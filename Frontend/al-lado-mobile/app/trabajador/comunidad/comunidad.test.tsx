import { render, screen, waitFor } from '@testing-library/react-native';
import React from 'react';
import { Text } from 'react-native';
import { AuthProvider, useAuth } from '../../../src/auth/AuthContext';
import { comunidadService } from '../../../src/api/services';

/**
 * AL-SEC-006: en el foro público no se expone el email del autor, así que la
 * pantalla detecta "mi publicación" por identificador y no por email.
 *
 * Es la regresión de la semana 1 en el móvil: la comparación por email dejó de
 * funcionar cuando el backend pasó a devolver la proyección pública.
 */

function ConsumidorConId() {
  const { userId } = useAuth();
  return <Text testID="userId">{userId ?? 'null'}</Text>;
}

describe('pantalla de comunidad', () => {
  const fetchOriginal = global.fetch;

  afterEach(() => {
    global.fetch = fetchOriginal;
    jest.restoreAllMocks();
  });

  it('el servicio de foro devuelve publicaciones sin el email del autor', async () => {
    const mock = jest.fn().mockResolvedValue({
      ok: true,
      status: 200,
      text: jest.fn().mockResolvedValue(
        JSON.stringify([
          { id: 1, titulo: 'Mi publicación', contenido: 'hola', autor: { id: 3, nombre: 'Demo' } },
          { id: 2, titulo: 'Otra', contenido: 'hola', autor: { id: 4, nombre: 'Ana' } },
        ]),
      ),
    });
    global.fetch = mock as unknown as typeof fetch;

    const publicaciones = await comunidadService.foro();

    expect(publicaciones).toHaveLength(2);
    expect(publicaciones[0].autor?.email).toBeUndefined();
    expect(publicaciones[0].autor?.id).toBe(3);
  });

  it('la sesión expone el identificador para comparar la autoría', async () => {
    const SecureStore = require('expo-secure-store');
    const base64 = (valor: object) =>
      Buffer.from(JSON.stringify(valor)).toString('base64').replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '');
    const token = `${base64({ alg: 'HS256' })}.${base64({ uid: 3 })}.firma`;
    await SecureStore.setItemAsync('auth_token', token);
    await SecureStore.setItemAsync('user_email', 'demo@mail.com');
    await SecureStore.setItemAsync('user_roles', JSON.stringify(['ROLE_TRABAJADOR']));

    render(
      <AuthProvider>
        <ConsumidorConId />
      </AuthProvider>,
    );

    await waitFor(() => expect(screen.getByTestId('userId')).toHaveTextContent('3'));
  });
});
