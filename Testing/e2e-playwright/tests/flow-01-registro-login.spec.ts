import { expect, test } from '@playwright/test';
import { LoginPage } from '../src/pages/LoginPage';
import { RegisterPage } from '../src/pages/RegisterPage';
import { WorkerDashboardPage } from '../src/pages/WorkerDashboardPage';
import { buildWorkerRegistration } from '../src/support/testData';

test.describe('Flujo crítico 1 — Autenticación', () => {
  test('TC-PW-FLOW-01: Registro → Login → Dashboard → Logout', async ({ page }) => {
    const user = buildWorkerRegistration();

    // 1. Alta de una cuenta nueva por la interfaz.
    const register = new RegisterPage(page);
    await register.open();
    await register.register(user);

    // 2. Login con la cuenta recién creada.
    const login = new LoginPage(page);
    await login.open();
    await login.loginAndWaitForDashboard(
      user.email,
      user.password,
      /\/trabajador\/dashboard/,
    );

    // 3. El panel del trabajador queda operativo y muestra la identidad.
    const dashboard = new WorkerDashboardPage(page);
    await dashboard.expectLoaded();
    await dashboard.capture('flow-01-01-dashboard-trabajador');

    // 4. Logout: vuelve al login y la ruta protegida ya no es accesible.
    await dashboard.logout();
    await page.goto('/trabajador/dashboard');
    await expect(page).toHaveURL(/\/login$/);
    await dashboard.capture('flow-01-02-ruta-protegida-redirige');
  });
});
