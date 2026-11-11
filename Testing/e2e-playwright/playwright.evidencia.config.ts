import { defineConfig, devices } from '@playwright/test';

/**
 * Configuración exclusiva para generar la evidencia de un fallo deliberado
 * (AUT-E2E-07). No se ejecuta con `npm test`.
 */
export default defineConfig({
  testDir: './evidencia',
  outputDir: 'results/fallo-deliberado',
  workers: 1,
  retries: 0,
  timeout: 60_000,
  reporter: [
    ['list'],
    ['html', { outputFolder: 'results/fallo-deliberado/html-report', open: 'never' }],
  ],
  use: {
    baseURL: process.env.E2E_BASE_URL ?? 'http://localhost:3001',
    locale: 'es-AR',
    timezoneId: 'America/Argentina/Buenos_Aires',
    trace: 'on',
    screenshot: 'on',
    video: 'on',
    actionTimeout: 15_000,
    navigationTimeout: 30_000,
  },
  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'], viewport: { width: 1440, height: 900 } },
    },
  ],
});
