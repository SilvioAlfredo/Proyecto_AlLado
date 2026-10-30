import { defineConfig, type Plugin } from 'vitest/config';
import react from '@vitejs/plugin-react';
import tsconfigPaths from 'vite-tsconfig-paths';

/**
 * Suite unitaria del frontend web (AlLado).
 *
 * - `jsdom` para el DOM: los componentes de sesión (AuthProvider) y los interceptores
 *   de axios corren en el navegador.
 * - `vite-tsconfig-paths` para respetar el alias `@/*` de `tsconfig.json`.
 * - `setupFiles` carga los matchers de `@testing-library/jest-dom`.
 *
 * Los tests van junto al código (`*.test.ts` / `*.test.tsx`) para que la ruta del
 * archivo que se prueba y la del test coincidan.
 */
export default defineConfig({
  // Hay dos copias de vite instaladas (la raíz, que usan los plugins, y la que
  // trae vitest anidada): los tipos de Plugin no coinciden entre sí, pero en
  // runtime son compatibles. El cast solo calma a TypeScript.
  plugins: [tsconfigPaths(), react()] as unknown as Plugin[],
  test: {
    environment: 'jsdom',
    globals: true,
    setupFiles: ['./src/test/setup.ts'],
    include: ['src/**/*.{test,spec}.{ts,tsx}'],
    exclude: ['node_modules/**', '.next/**', 'docs/**'],
    clearMocks: true,
    restoreMocks: true,
  },
});
