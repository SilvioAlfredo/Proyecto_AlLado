import '@testing-library/jest-dom/vitest';
import { cleanup } from '@testing-library/react';
import { afterEach, beforeEach, vi } from 'vitest';

/**
 * Preparación común de la suite web.
 *
 * - Cada test arranca con el `localStorage` vacío: la sesión vive ahí y una sesión
 *   filtrada entre tests haría pasar casos que en producción fallan.
 * - `next/navigation` se mockea: el `AuthProvider` usa `useRouter`, que en el
 *   navegador real necesita un App Router montado.
 */

beforeEach(() => {
  window.localStorage.clear();
  window.sessionStorage.clear();
});

afterEach(() => {
  cleanup();
});

vi.mock('next/navigation', () => ({
  useRouter: () => ({
    push: vi.fn(),
    replace: vi.fn(),
    refresh: vi.fn(),
    back: vi.fn(),
    prefetch: vi.fn(),
  }),
  usePathname: () => '/',
  useSearchParams: () => new URLSearchParams(),
  redirect: vi.fn(),
}));

/**
 * jsdom no implementa la navegación del navegador: al asignar
 * `window.location.href` escribe "Not implemented: navigation" por consola. El
 * interceptor de axios lo hace a propósito (redirige al login), así que se filtra
 * ese mensaje puntual para que el reporte de la suite quede limpio.
 */
const errorOriginal = console.error;
console.error = (...argumentos: unknown[]) => {
  const texto = String(argumentos[0] ?? '');
  if (texto.includes('Not implemented: navigation')) return;
  errorOriginal(...argumentos);
};
