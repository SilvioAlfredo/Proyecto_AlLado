import { test, expect } from '@playwright/test';
import AxeBuilder from '@axe-core/playwright';

test('Inicio: accesibilidad WCAG 2.2 AA', async ({ page }, testInfo) => {
  await page.goto('http://localhost:3000/');

  await expect(page.locator('main')).toBeVisible();

  const resultados = await new AxeBuilder({ page })
    .withTags([
      'wcag2a',
      'wcag2aa',
      'wcag21a',
      'wcag21aa',
      'wcag22aa',
    ])
    .analyze();

  await testInfo.attach('resultado-axe.json', {
    body: JSON.stringify(resultados, null, 2),
    contentType: 'application/json',
  });

  await testInfo.attach('pantalla-auditada.png', {
    body: await page.screenshot({ fullPage: true }),
    contentType: 'image/png',
  });

  expect(resultados.violations).toEqual([]);
});
