import { describe, expect, it, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react-native';
import React from 'react';
import { Login } from './login';
import { useAuth } from '../../src/auth/AuthContext';
import { errMsg } from '../../src/api/client';

/**
 * HU-04: Trabajador se registra y valida correo electrónico.
 *
 * Pruebas del componente Login: envío de credenciales, manejo de errores
 * y redirección tras login exitoso.
 */
vi.mock('../../src/auth/AuthContext', () => ({
  useAuth: vi.fn(),
  roleHome: vi.fn((roles: string[]) => roles.includes('ROLE_ADMIN') ? '/admin/dashboard' : '/empleador'),
}));

vi.mock('../../src/api/client', () => ({
  errMsg: vi.fn((e: unknown) => String(e)),
}));

vi.mock('expo-router', () => ({
  Link: ({ children, href }: { children: React.ReactNode; href: string }) => (
    <Pressable testID={`link-${href}`}>{children}</Pressable>
  ),
  router: { replace: vi.fn() },
  useRouter: () => ({ replace: vi.fn(), push: vi.fn() }),
}));

const Pressable = ({ children, testID }: { children: React.ReactNode; testID?: string }) =>
  React.createElement('Pressable', { testID }, children);

const mockUseAuth = useAuth as unknown as ReturnType<typeof vi.fn>;

beforeEach(() => {
  mockUseAuth.mockReset();
  vi.mocked(errMsg).mockReset();
});

function renderLogin() {
  return render(<Login />);
}

describe('Login', () => {
  it('envía email y contraseña al hacer login', async () => {
    const loginMock = vi.fn().mockResolvedValue({
      token: 'jwt',
      id: 7,
      email: 'ana@mail.com',
      roles: ['ROLE_TRABAJADOR'],
    });
    mockUseAuth.mockReturnValue({ login: loginMock });

    renderLogin();

    const emailInput = screen.getByPlaceholderText('juan@mail.com');
    const passwordInput = screen.getByPlaceholderText('••••••••');
    fireEvent.changeText(emailInput, 'ana@mail.com');
    fireEvent.changeText(passwordInput, 'Clave123!');
    fireEvent.press(screen.getByText('Ingresar'));

    await waitFor(() => expect(loginMock).toHaveBeenCalledWith('ana@mail.com', 'Clave123!'));
  });

  it('muestra error cuando el login falla con 401', async () => {
    const loginMock = vi.fn().mockRejectedValue({ status: 401 });
    mockUseAuth.mockReturnValue({ login: loginMock });

    renderLogin();

    fireEvent.changeText(screen.getByPlaceholderText('juan@mail.com'), 'ana@mail.com');
    fireEvent.changeText(screen.getByPlaceholderText('••••••••'), 'clave');
    fireEvent.press(screen.getByText('Ingresar'));

    await waitFor(() =>
      expect(screen.getByText('Revisá tu email y contraseña.')).toBeInTheDocument(),
    );
  });

  it('redirige al home del trabajador tras login exitoso', async () => {
    const loginMock = vi.fn().mockResolvedValue({
      token: 'jwt',
      id: 7,
      email: 'ana@mail.com',
      roles: ['ROLE_TRABAJADOR'],
    });
    const replaceMock = vi.fn();
    mockUseAuth.mockReturnValue({ login: loginMock });
    vi.mocked(require('expo-router').router).replace = replaceMock;

    renderLogin();

    fireEvent.changeText(screen.getByPlaceholderText('juan@mail.com'), 'ana@mail.com');
    fireEvent.changeText(screen.getByPlaceholderText('••••••••'), 'Clave123!');
    fireEvent.press(screen.getByText('Ingresar'));

    await waitFor(() => expect(replaceMock).toHaveBeenCalledWith('/empleador'));
  });

  it('muestra enlace para crear cuenta', () => {
    renderLogin();

    expect(screen.getByTestId('link-/(auth)/register')).toBeInTheDocument();
  });
});
