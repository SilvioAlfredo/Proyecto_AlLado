import { describe, expect, it } from 'vitest';
import api from './api';

/**
 * Capa HTTP del cliente web: inyección del token y limpieza de la sesión.
 *
 * Un 401/403 tiene que borrar la sesión completa (incluido el identificador del
 * usuario) y mandar al login: si queda el `user_id` guardado, la siguiente pantalla
 * muestra datos de la cuenta anterior.
 */

type Handler = { fulfilled?: (valor: unknown) => unknown; rejected?: (error: unknown) => unknown };

function interceptores() {
  const peticiones = (api.interceptors.request as unknown as { handlers: Handler[] }).handlers;
  const respuestas = (api.interceptors.response as unknown as { handlers: Handler[] }).handlers;
  return {
    request: (config: unknown) => peticiones[0].fulfilled!(config),
    response: (error: unknown) => respuestas[0].rejected!(error),
  };
}

describe('api', () => {
  it('inyecta el bearer token en cada petición', async () => {
    localStorage.setItem('auth_token', 'jwt-de-prueba');
    const config: { headers: Record<string, string> } = { headers: {} };

    await interceptores().request(config);

    expect(config.headers.Authorization).toBe('Bearer jwt-de-prueba');
  });

  it('no inyecta cabecera si no hay token', async () => {
    const config: { headers: Record<string, string> } = { headers: {} };

    await interceptores().request(config);

    expect(config.headers.Authorization).toBeUndefined();
  });

  it('borra la sesión completa al recibir 401', async () => {
    localStorage.setItem('auth_token', 'jwt');
    localStorage.setItem('user_email', 'ana@mail.com');
    localStorage.setItem('user_roles', '["ROLE_TRABAJADOR"]');
    localStorage.setItem('user_id', '7');
    localStorage.setItem('user_nombre', 'Ana');
    const error = { config: { url: '/trabajadores' }, response: { status: 401 } };

    await expect(interceptores().response(error)).rejects.toBe(error);

    expect(localStorage.getItem('auth_token')).toBeNull();
    expect(localStorage.getItem('user_email')).toBeNull();
    expect(localStorage.getItem('user_roles')).toBeNull();
    expect(localStorage.getItem('user_id')).toBeNull();
    expect(localStorage.getItem('user_nombre')).toBeNull();
  });

  it('borra la sesión también con 403', async () => {
    localStorage.setItem('auth_token', 'jwt');
    localStorage.setItem('user_id', '7');
    const error = { config: { url: '/suscripciones' }, response: { status: 403 } };

    await expect(interceptores().response(error)).rejects.toBe(error);

    expect(localStorage.getItem('auth_token')).toBeNull();
    expect(localStorage.getItem('user_id')).toBeNull();
  });

  it('no borra la sesión si el fallo es del login mismo', async () => {
    localStorage.setItem('auth_token', 'jwt');
    const error = { config: { url: '/auth/login' }, response: { status: 401 } };

    await expect(interceptores().response(error)).rejects.toBe(error);

    expect(localStorage.getItem('auth_token')).toBe('jwt');
  });

  it('deja la sesión intacta con un error que no es 401 ni 403', async () => {
    localStorage.setItem('auth_token', 'jwt');
    const error = { config: { url: '/oportunidades' }, response: { status: 500 } };

    await expect(interceptores().response(error)).rejects.toBe(error);

    expect(localStorage.getItem('auth_token')).toBe('jwt');
  });
});
