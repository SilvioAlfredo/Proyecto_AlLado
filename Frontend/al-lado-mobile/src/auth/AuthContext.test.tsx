import { act, render, screen, waitFor } from '@testing-library/react-native';
import React from 'react';
import { Text } from 'react-native';
import { AuthProvider, useAuth, userIdFromToken } from './AuthContext';
import { authService } from '../api/services';

/**
 * AL-API-005: la sesión móvil se sostiene sobre la identidad mínima.
 *
 * Antes, cada pantalla resolvía el identificador del usuario listando
 * `/trabajadores` y `/empleadores` y comparando emails (`resolveIdByEmail`).
 * Estos tests fijan el comportamiento nuevo: el id llega con el login o se lee del
 * claim `uid` del token, y **nunca se lista el directorio de usuarios**.
 */

function tokenConUid(uid: number | string): string {
  const base64 = (valor: object) =>
    Buffer.from(JSON.stringify(valor)).toString('base64').replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '');
  return `${base64({ alg: 'HS256' })}.${base64({ uid })}.firma`;
}

function Consumidor() {
  const { userId, email, roles, loading } = useAuth();
  return (
    <>
      <Text testID="userId">{userId === null ? 'null' : String(userId)}</Text>
      <Text testID="email">{email ?? 'sin-sesion'}</Text>
      <Text testID="roles">{roles.join(',')}</Text>
      <Text testID="loading">{loading ? 'cargando' : 'listo'}</Text>
    </>
  );
}

// En @testing-library/react-native v14 `render` es asíncrono: hay que esperarlo
// para que el árbol quede montado y `screen` disponible.
async function montar() {
  return render(
    <AuthProvider>
      <Consumidor />
    </AuthProvider>,
  );
}

describe('userIdFromToken', () => {
  it('lee el uid numérico del token', () => {
    expect(userIdFromToken(tokenConUid(42))).toBe(42);
  });

  it('acepta un uid numérico en texto', () => {
    expect(userIdFromToken(tokenConUid('42'))).toBe(42);
  });

  it('devuelve null si el token no declara uid', () => {
    const base64 = (valor: object) =>
      Buffer.from(JSON.stringify(valor)).toString('base64').replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '');
    const token = `${base64({ alg: 'HS256' })}.${base64({ sub: 'a@mail.com' })}.firma`;

    expect(userIdFromToken(token)).toBeNull();
  });

  it('devuelve null con un token inválido o vacío', () => {
    expect(userIdFromToken('basura')).toBeNull();
    expect(userIdFromToken(null)).toBeNull();
  });
});

describe('AuthProvider', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('no hay sesión al empezar', async () => {
    await montar();

    await waitFor(() => expect(screen.getByTestId('loading')).toHaveTextContent('listo'));
    expect(screen.getByTestId('email')).toHaveTextContent('sin-sesion');
    expect(screen.getByTestId('userId')).toHaveTextContent('null');
  });

  it('guarda el identificador que devuelve el login', async () => {
    jest.spyOn(authService, 'login').mockResolvedValue({
      token: tokenConUid(7),
      id: 7,
      email: 'ana@mail.com',
      nombre: 'Ana',
      roles: ['ROLE_TRABAJADOR'],
    });

    await montar();

    expect(screen.getByTestId('loading')).toHaveTextContent('listo');
  });

  it('el identificador se lee del token cuando la sesión no lo tiene guardado', async () => {
    // Sesión anterior a AL-API-005: token y email guardados, sin user_id.
    const SecureStore = require('expo-secure-store');
    await SecureStore.setItemAsync('auth_token', tokenConUid(99));
    await SecureStore.setItemAsync('user_email', 'ana@mail.com');
    await SecureStore.setItemAsync('user_roles', JSON.stringify(['ROLE_TRABAJADOR']));

    await montar();

    await waitFor(() => expect(screen.getByTestId('userId')).toHaveTextContent('99'));
    expect(await SecureStore.getItemAsync('user_id')).toBe('99');
  });

  it('no lista usuarios para descubrir el propio identificador', async () => {
    const fetchOriginal = global.fetch;
    const mock = jest.fn().mockResolvedValue({
      ok: true,
      status: 200,
      text: jest.fn().mockResolvedValue('[]'),
    });
    global.fetch = mock as unknown as typeof fetch;

    const SecureStore = require('expo-secure-store');
    await SecureStore.setItemAsync('auth_token', tokenConUid(99));
    await SecureStore.setItemAsync('user_email', 'ana@mail.com');
    await SecureStore.setItemAsync('user_roles', JSON.stringify(['ROLE_TRABAJADOR']));

    await montar();

    await waitFor(() => expect(screen.getByTestId('userId')).toHaveTextContent('99'));
    const rutas = mock.mock.calls.map(([url]) => String(url));
    expect(rutas.some((ruta) => ruta.includes('/trabajadores'))).toBe(false);
    expect(rutas.some((ruta) => ruta.includes('/empleadores'))).toBe(false);

    global.fetch = fetchOriginal;
  });

  it('la sesión guardada se lee al montar', async () => {
    const SecureStore = require('expo-secure-store');
    await SecureStore.setItemAsync('auth_token', tokenConUid(3));
    await SecureStore.setItemAsync('user_email', 'bruno@mail.com');
    await SecureStore.setItemAsync('user_roles', JSON.stringify(['ROLE_EMPLEADOR']));
    await SecureStore.setItemAsync('user_id', '3');

    await montar();

    await waitFor(() => expect(screen.getByTestId('email')).toHaveTextContent('bruno@mail.com'));
    expect(screen.getByTestId('roles')).toHaveTextContent('ROLE_EMPLEADOR');
    expect(screen.getByTestId('userId')).toHaveTextContent('3');
  });
});
