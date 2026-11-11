import { expect, test } from '@playwright/test';
import { env } from '../src/support/env';
import { LoginPage } from '../src/pages/LoginPage';
import { WorkerDashboardPage } from '../src/pages/WorkerDashboardPage';
import { AdminDashboardPage } from '../src/pages/AdminDashboardPage';

test.describe('Casos E2E complementarios', () => {
  test('TC-E2E-LOGIN-01: Login → dashboard → logout con cuenta sembrada', async ({ page }) => {
    const login = new LoginPage(page);
    await login.open();
    await login.loginAndWaitForDashboard(
      env.demoEmployerEmail,
      env.defaultPassword,
      /\/empleador\/dashboard/,
    );

    await expect(page.getByRole('heading', { level: 1 })).toContainText('Hola');

    await page.getByRole('button', { name: 'Cerrar sesión' }).click();
    await expect(page).toHaveURL(/\/login$/);
  });

  test('TC-E2E-PROT-01: rutas protegidas y separación de roles', async ({ page }) => {
    // Sin sesión, el panel del trabajador redirige al login.
    await page.goto('/trabajador/dashboard');
    await expect(page).toHaveURL(/\/login$/);

    // Un trabajador no puede entrar al panel de administración.
    const login = new LoginPage(page);
    await login.open();
    await login.loginAndWaitForDashboard(
      env.demoWorkerEmail,
      env.defaultPassword,
      /\/trabajador\/dashboard/,
    );

    await new WorkerDashboardPage(page).expectLoaded();
    await page.goto('/admin/dashboard');
    await expect(page).toHaveURL(/\/trabajador\/dashboard$/);
  });

  test('TC-E2E-ADMIN-01: acceso de administración y métricas', async ({ page }) => {
    const login = new LoginPage(page);
    await login.openAdminAccess();
    await login.loginAndWaitForDashboard(
      env.adminEmail,
      env.adminPassword,
      /\/admin\/dashboard$/,
    );

    const dashboard = new AdminDashboardPage(page);
    await dashboard.expectMetrics();
    await dashboard.capture('p1-admin-dashboard-metricas');
  });
});
