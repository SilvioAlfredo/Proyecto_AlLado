import { test, expect } from '@playwright/test';

// Worker-scoped recording options must be declared at file level.
test.use({ trace: 'on', video: 'on' });

test('Timeout de selector detectado y generación automática de traza y video', async ({ page }) => {
  await page.goto('/login');
  await expect(page.getByRole('heading', { name: 'Iniciá sesión.' })).toBeVisible();

  // Mark only the deliberate selector timeout as an expected failure.
  test.fail(true, 'El selector inexistente debe agotar su tiempo de espera.');
  await expect(page.locator('#elemento-inexistente-para-generar-evidencia-consigna'))
    .toBeVisible({ timeout: 3000 });
});
