import '@testing-library/jest-dom/vitest';
import { cleanup } from '@testing-library/react';
import { afterEach, beforeEach, vi } from 'vitest';

/**
 * Relleno de almacenamiento para Node 26.
 *
 * Node 26 declara `localStorage` y `sessionStorage` en `globalThis` aunque no
 * estén habilitados (quedan en `undefined` salvo que se pase
 * `--localstorage-file`). Vitest no copia los de jsdom porque la clave ya existe
 * en el global, así que `window.localStorage` llega `undefined` y los tests
 * fallan al limpiar la sesión. Si jsdom ya los expone, este bloque no hace nada.
 */
class MemoryStorage implements Storage {
  private readonly data = new Map<string, string>();

  get length(): number {
    return this.data.size;
  }

  clear(): void {
    this.data.clear();
  }

  getItem(key: string): string | null {
    return this.data.get(key) ?? null;
  }

  key(index: number): string | null {
    return [...this.data.keys()][index] ?? null;
  }

  removeItem(key: string): void {
    this.data.delete(key);
  }

  setItem(key: string, value: string): void {
    this.data.set(key, String(value));
  }
}

for (const name of ['localStorage', 'sessionStorage'] as const) {
  if (!window[name]) {
    Object.defineProperty(window, name, { value: new MemoryStorage(), configurable: true });
  }
}

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
