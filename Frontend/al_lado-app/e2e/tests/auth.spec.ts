import { test, expect, type Page } from '@playwright/test';
import { LoginPage } from '../pages/LoginPage';
import { RegisterPage } from '../pages/RegisterPage';

async function evidencia(page: Page, nombre: string) {
  await test.info().attach(nombre, { body: await page.screenshot({ fullPage: true }), contentType: 'image/png' });
}

test.describe('AUT-E2E-01: Registro, autenticación y cierre de sesión', () => {
  test('Create: Registro exitoso de nuevo trabajador con validación de campos obligatorios', async ({ page }) => {
    const registerPage = new RegisterPage(page);
    const email = `e2e.worker.${Date.now()}@demo.com`;
    await registerPage.goto();
    for (const input of [registerPage.nombreInput, registerPage.apellidoInput, registerPage.emailInput, registerPage.telefonoInput, registerPage.passwordInput]) {
      await expect(input).toHaveJSProperty('required', true);
    }
    await registerPage.submitButton.click();
    await expect.poll(() => registerPage.nombreInput.evaluate((input: HTMLInputElement) => input.validity.valueMissing)).toBe(true);
    await expect(page).toHaveURL('/register');
    await evidencia(page, 'Campos obligatorios impiden registro vacio');
    await registerPage.register('Usuario E2E', 'Trabajador', email, 'Clave123!', '+5493510000000', 'TRABAJADOR');
    await expect(page.getByRole('status').filter({ hasText: 'Cuenta creada correctamente.' })).toBeVisible();
    await expect(registerPage.emailInput).toHaveValue(email);
    await evidencia(page, 'Registro exitoso del trabajador');
  });

  test('Create (Negativo): Registro rechazado con contraseña menor a 8 caracteres (valor límite: 7 chars)', async ({ page }) => {
    const registerPage = new RegisterPage(page);
    await registerPage.goto();
    await registerPage.register('Usuario Corto', 'Validez', `e2e.corto.${Date.now()}@demo.com`, 'abc1234', '+5493510000000', 'TRABAJADOR');
    await expect.poll(() => registerPage.passwordInput.evaluate((input: HTMLInputElement) => input.validity.tooShort)).toBe(true);
    await expect(page).toHaveURL('/register');
    await expect(page.getByRole('status').filter({ hasText: 'Cuenta creada correctamente.' })).toHaveCount(0);
    await expect(registerPage.submitButton).toBeEnabled();
    await evidencia(page, 'Registro rechazado por contrasena de 7 caracteres');
  });

  test('Read: Inicio de sesión válido, redirección a dashboard y persistencia de sesión JWT', async ({ page }) => {
    const registerPage = new RegisterPage(page);
    const loginPage = new LoginPage(page);
    const email = `e2e.login.${Date.now()}@demo.com`;
    await registerPage.goto();
    await registerPage.register('Usuario Login', 'Persistente', email, 'Clave123!', '+5493510000000', 'TRABAJADOR');
    await expect(page.getByRole('status').filter({ hasText: 'Cuenta creada correctamente.' })).toBeVisible();

    await loginPage.goto();
    await loginPage.login(email, 'Clave123!');
    await expect(page).toHaveURL('/trabajador/dashboard');
    await expect(page.getByText(email, { exact: true })).toBeVisible();
    const token = await page.evaluate(() => ({
      authToken: localStorage.getItem('auth_token'),
      email: localStorage.getItem('user_email'),
      roles: JSON.parse(localStorage.getItem('user_roles') || '[]') as string[],
    }));
    expect(token.authToken?.split('.')).toHaveLength(3);
    expect(token.email).toBe(email);
    expect(token.roles).toContain('ROLE_TRABAJADOR');
    await page.reload();
    await expect(page).toHaveURL('/trabajador/dashboard');
    await expect(page.getByText(email, { exact: true })).toBeVisible();
    await evidencia(page, 'Sesion del trabajador persistente tras recarga');
  });

  test('Delete/Logout: Cierre de sesión seguro y eliminación de tokens de sesión', async ({ page }) => {
    const registerPage = new RegisterPage(page);
    const loginPage = new LoginPage(page);
    const email = `e2e.logout.${Date.now()}@demo.com`;
    await registerPage.goto();
    await registerPage.register('Usuario Logout', 'Session', email, 'Clave123!', '+5493510000000', 'TRABAJADOR');
    await expect(page.getByRole('status').filter({ hasText: 'Cuenta creada correctamente.' })).toBeVisible();
    await loginPage.goto();
    await loginPage.login(email, 'Clave123!');
    await expect(page).toHaveURL('/trabajador/dashboard');
    expect(await page.evaluate(() => localStorage.getItem('auth_token'))).toBeTruthy();

    await loginPage.logout();
    await expect(page.getByRole('heading', { name: 'Iniciá sesión.' })).toBeVisible();
    const storage = await page.evaluate(() => {
      const keys = ['auth_token', 'user_email', 'user_roles', 'user_id', 'user_nombre'];
      return Object.fromEntries(keys.map((key) => [key, localStorage.getItem(key)]));
    });
    for (const value of Object.values(storage)) expect(value).toBeNull();
    await evidencia(page, 'Cierre de sesion sin datos almacenados');
  });
});
