import { expect, test } from '@playwright/test';

/**
 * FALLO DELIBERADO — evidencia exigida por AUT-E2E-07.
 *
 * Este caso NO forma parte de la corrida normal (la config principal ignora
 * `evidencia/`). Se ejecuta aparte con `npm run test:fallo` para generar
 * screenshot, video y trace de un fallo real y reproducible:
 *
 *   credenciales inválidas → la app muestra el error y permanece en /login,
 *   pero el caso exige por error que haya llegado al dashboard.
 */
test('TC-E2E-EVIDENCIA-01 (fallo deliberado): credenciales inválidas no deben entrar', async ({
  page,
}) => {
  await page.goto('/login');
  await page.locator('#login-email').fill('cuenta.invalida@allado.test');
  await page.locator('#login-password').fill('ClaveIncorrecta!');
  await page.locator("form button[type='submit']").click();

  await expect(page.getByRole('alert')).toBeVisible();

  // Aserción deliberadamente incorrecta: produce el fallo que se documenta.
  await expect(page).toHaveURL(/\/dashboard$/, { timeout: 5_000 });
});
