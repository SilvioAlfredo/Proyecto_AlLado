import { vi, type Mock } from 'vitest';

/**
 * Tipos de los dobles de prueba del cliente HTTP.
 *
 * `vi.mock` sustituye el módulo en tiempo de ejecución, pero TypeScript sigue
 * viendo los tipos reales de axios (cuyos métodos no son mocks). Estos helpers
 * castean el módulo simulado a su forma de mock para que los tests se lean
 * sin `as any`.
 */

export interface ApiMock {
  get: Mock;
  post: Mock;
  put: Mock;
  patch: Mock;
  delete: Mock;
}

/** Convierte el módulo de axios simulado en un doble con métodos verificables. */
export function comoApiMock(modulo: unknown): ApiMock {
  return modulo as ApiMock;
}

/** Respuestasuccessful de axios, ya resuelta. */
export function respuesta<T>(data: T) {
  return { data, status: 200, statusText: 'OK', headers: {}, config: {} };
}

/** Error de axios con el código HTTP indicado. */
export function errorAxios(status: number, data: unknown = { message: 'Error' }) {
  return {
    isAxiosError: true,
    response: { status, data },
    request: {},
    message: `Request failed with status code ${status}`,
  };
}

export { vi };
