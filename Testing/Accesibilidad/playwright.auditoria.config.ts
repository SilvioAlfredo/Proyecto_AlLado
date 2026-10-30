import { defineConfig } from '@playwright/test';

export default defineConfig({
  testDir: './auditoria',
  testMatch: 'pantallas.spec.ts',
  timeout: 90_000,
  expect: { timeout: 20_000 },
  workers: 1,
  retries: 0,
  maxFailures: 0,
  outputDir: './resultados-accesibilidad',
  reporter: [
    ['list'],
    ['html', { outputFolder: 'reporte-accesibilidad', open: 'never' }],
    ['./auditoria/resumen.ts'],
  ],
  use: {
    baseURL: process.env.BASE_URL || 'http://localhost:3000',
    browserName: 'chromium',
    screenshot: 'only-on-failure',
    trace: 'retain-on-failure',
  },
  projects: [
    { name: 'escritorio', use: { viewport: { width: 1280, height: 720 } } },
    { name: 'movil-web', use: {
      viewport: { width: 390, height: 844 },
      isMobile: true, hasTouch: true, deviceScaleFactor: 1,
    } },
  ],
});
