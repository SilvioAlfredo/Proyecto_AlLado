import { defineConfig, devices } from '@playwright/test';

/**
 * Configuración de la suite E2E de AlLado.
 *
 * - Un solo worker: los escenarios comparten la base de QA y crean datos reales.
 * - Traza, captura y video se conservan solo cuando un caso falla (evidencia AUT-E2E-07).
 * - El reporte HTML queda en `results/html-report`.
 */
export default defineConfig({
  testDir: './tests',
  outputDir: 'results/test-results',
  fullyParallel: false,
  workers: 1,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 1 : 0,
  timeout: 90_000,
  expect: { timeout: 15_000 },

  reporter: [
    ['list'],
    ['html', { outputFolder: 'results/html-report', open: 'never' }],
    ['junit', { outputFile: 'results/junit.xml' }],
    ['json', { outputFile: 'results/results.json' }],
  ],

  use: {
    baseURL: process.env.E2E_BASE_URL ?? 'http://localhost:3001',
    locale: 'es-AR',
    timezoneId: 'America/Argentina/Buenos_Aires',
    trace: 'retain-on-failure',
    screenshot: 'only-on-failure',
    video: 'retain-on-failure',
    actionTimeout: 15_000,
    navigationTimeout: 30_000,
  },

  projects: [
    {
      name: 'chromium',
      use: {
        ...devices['Desktop Chrome'],
        viewport: { width: 1440, height: 900 },
      },
    },
  ],
});
