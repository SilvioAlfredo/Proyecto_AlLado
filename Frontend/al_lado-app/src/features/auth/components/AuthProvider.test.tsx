import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { AuthProvider } from './AuthProvider';
import { useAuth } from '../hooks/useAuth';
import api from '@/lib/api';
import { authService } from '../services/authService';
import { comoApiMock } from '@/test/mocks';

/**
 * AL-API-005: la sesión web se sostiene sobre la identidad mínima.
 *
 * Antes, el identificador del usuario se buscaba listando `/trabajadores`,
 * `/empleadores` o `/administradores` y comparando emails. Estos tests fijan el
 * comportamiento nuevo: el id viene del login o del claim `uid` del token, y si
 * falta se pide a `GET /auth/me`. **Nunca se lista el directorio de usuarios.**
 */

vi.mock('@/lib/api', () => ({
  default: { get: vi.fn(), post: vi.fn(), put: vi.fn(), patch: vi.fn(), delete: vi.fn() },
}));

vi.mock('../services/authService', () => ({
  authService: { login: vi.fn(), register: vi.fn(), logout: vi.fn() },
}));

const getApi = comoApiMock(api);
const getAuthService = authService as unknown as Record<'login' | 'logout', ReturnType<typeof vi.fn>>;

function tokenConUid(uid: number): string {
  const base64 = (valor: object) =>
    btoa(JSON.stringify(valor)).replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '');
  return `${base64({ alg: 'HS256' })}.${base64({ uid })}.firma`;
}

function montar() {
  const cliente = new QueryClient({ defaultOptions: { queries: { retry: false } } });
  return render(
    <QueryClientProvider client={cliente}>
      <AuthProvider>
        <Consumidor />
      </AuthProvider>
    </QueryClientProvider>,
  );
}

function Consumidor() {
  const { user, userId, loading, login, logout } = useAuth();
  return (
    <div>
      <span data-testid="email">{user?.email ?? 'sin-sesion'}</span>
      <span data-testid="roles">{user?.roles.join(',') ?? ''}</span>
      <span data-testid="userId">{userId ?? 'null'}</span>
      <span data-testid="loading">{loading ? 'cargando' : 'listo'}</span>
      <button onClick={() => void login({ email: 'ana@mail.com', contrasena: 'Clave123!' })}>
        entrar
      </button>
      <button onClick={() => void logout()}>salir</button>
    </div>
  );
}

describe('AuthProvider', () => {
  beforeEach(() => {
    getApi.get.mockReset();
    getAuthService.login.mockReset();
    getAuthService.logout.mockReset();
  });

  it('no hay sesión al empezar', async () => {
    montar();

    await waitFor(() => expect(screen.getByTestId('loading')).toHaveTextContent('listo'));
    expect(screen.getByTestId('email')).toHaveTextContent('sin-sesion');
    expect(screen.getByTestId('userId')).toHaveTextContent('null');
  });

  it('guarda la identidad mínima que devuelve el login', async () => {
    getAuthService.login.mockResolvedValue({
      token: tokenConUid(7),
      id: 7,
      email: 'ana@mail.com',
      nombre: 'Ana Gomez',
      roles: ['ROLE_TRABAJADOR'],
    });

    montar();
    await userEvent.click(screen.getByText('entrar'));

    await waitFor(() => expect(screen.getByTestId('email')).toHaveTextContent('ana@mail.com'));
    expect(screen.getByTestId('userId')).toHaveTextContent('7');
    expect(screen.getByTestId('roles')).toHaveTextContent('ROLE_TRABAJADOR');
    expect(localStorage.getItem('auth_token')).toBe(tokenConUid(7));
    expect(localStorage.getItem('user_email')).toBe('ana@mail.com');
    expect(localStorage.getItem('user_id')).toBe('7');
    expect(localStorage.getItem('user_nombre')).toBe('Ana Gomez');
  });

  it('rehidrata el id desde el claim del token sin llamar a la API', async () => {
    // Sesión anterior a AL-API-005: hay token y email, pero no user_id.
    localStorage.setItem('auth_token', tokenConUid(99));
    localStorage.setItem('user_email', 'ana@mail.com');
    localStorage.setItem('user_roles', JSON.stringify(['ROLE_TRABAJADOR']));

    montar();

    await waitFor(() => expect(screen.getByTestId('userId')).toHaveTextContent('99'));
    expect(getApi.get).not.toHaveBeenCalled();
    expect(localStorage.getItem('user_id')).toBe('99');
  });

  it('rehidrata el id con GET /auth/me cuando el token no trae uid', async () => {
    localStorage.setItem('auth_token', 'token-sin-uid');
    localStorage.setItem('user_email', 'ana@mail.com');
    localStorage.setItem('user_roles', JSON.stringify(['ROLE_TRABAJADOR']));
    getApi.get.mockResolvedValue({ data: { id: 55, email: 'ana@mail.com', nombre: 'Ana', roles: [] } });

    montar();

    await waitFor(() => expect(screen.getByTestId('userId')).toHaveTextContent('55'));
    expect(getApi.get).toHaveBeenCalledWith('/auth/me');
    expect(localStorage.getItem('user_id')).toBe('55');
  });

  it('nunca lista usuarios para descubrir el propio identificador', async () => {
    localStorage.setItem('auth_token', tokenConUid(99));
    localStorage.setItem('user_email', 'ana@mail.com');
    localStorage.setItem('user_roles', JSON.stringify(['ROLE_TRABAJADOR']));

    montar();

    await waitFor(() => expect(screen.getByTestId('userId')).toHaveTextContent('99'));
    const rutasLlamadas = getApi.get.mock.calls.map((llamada) => String(llamada[0]));
    expect(rutasLlamadas).not.toContain('/trabajadores');
    expect(rutasLlamadas).not.toContain('/empleadores');
    expect(rutasLlamadas).not.toContain('/administradores');
  });

  it('la sesión guardada se lee al montar', async () => {
    localStorage.setItem('auth_token', tokenConUid(3));
    localStorage.setItem('user_email', 'bruno@mail.com');
    localStorage.setItem('user_roles', JSON.stringify(['ROLE_EMPLEADOR']));
    localStorage.setItem('user_id', '3');

    montar();

    await waitFor(() => expect(screen.getByTestId('email')).toHaveTextContent('bruno@mail.com'));
    expect(screen.getByTestId('roles')).toHaveTextContent('ROLE_EMPLEADOR');
    expect(screen.getByTestId('userId')).toHaveTextContent('3');
  });

  it('ignora una sesión incompleta (sin roles válidos)', async () => {
    localStorage.setItem('auth_token', tokenConUid(3));
    localStorage.setItem('user_email', 'bruno@mail.com');
    localStorage.setItem('user_roles', 'no-es-una-lista');

    montar();

    await waitFor(() => expect(screen.getByTestId('loading')).toHaveTextContent('listo'));
    expect(screen.getByTestId('email')).toHaveTextContent('sin-sesion');
  });

  it('el logout borra toda la sesión del navegador', async () => {
    getAuthService.login.mockResolvedValue({
      token: tokenConUid(7),
      id: 7,
      email: 'ana@mail.com',
      nombre: 'Ana',
      roles: ['ROLE_TRABAJADOR'],
    });
    getAuthService.logout.mockResolvedValue(undefined);

    montar();
    await userEvent.click(screen.getByText('entrar'));
    await waitFor(() => expect(screen.getByTestId('userId')).toHaveTextContent('7'));

    await userEvent.click(screen.getByText('salir'));

    await waitFor(() => expect(screen.getByTestId('email')).toHaveTextContent('sin-sesion'));
    expect(localStorage.getItem('auth_token')).toBeNull();
    expect(localStorage.getItem('user_email')).toBeNull();
    expect(localStorage.getItem('user_roles')).toBeNull();
    expect(localStorage.getItem('user_id')).toBeNull();
    expect(localStorage.getItem('user_nombre')).toBeNull();
  });
});
