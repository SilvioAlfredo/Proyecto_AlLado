import { expect, type Locator, type Page } from '@playwright/test';
import { BasePage } from './BasePage';

export class OportunidadPage extends BasePage {
  constructor(page: Page) {
    super(page);
  }

  solicitud(titulo: string): Locator {
    return this.page.locator('article').filter({ has: this.page.getByRole('heading', { name: titulo, exact: true }) });
  }

  tarjeta(titulo: string): Locator {
    return this.page.locator('div.flex.flex-col.justify-between').filter({
      has: this.page.getByRole('heading', { name: titulo, exact: true }),
    });
  }

  async publicar(titulo: string, descripcion: string, presupuesto: string) {
    await this.goto('/publicar');
    await this.page.getByPlaceholder('Describí el problema o trabajo que necesitás...', { exact: false })
      .fill(`${titulo}\n${descripcion}`);
    await this.page.locator('form select').first().selectOption({ label: 'Electricidad' });
    await this.page.getByPlaceholder('Ingresá tu ubicación o barrio').fill('Córdoba');
    await this.page.getByPlaceholder('Ej: $15.000 - $20.000').fill(presupuesto);
    await this.page.getByRole('button', { name: /publicar solicitud/i }).click();

    // /publicar navigates home only after the create request resolves.
    await expect(this.page).toHaveURL('/');
    await this.goto('/empleador/solicitudes');
    await expect(this.solicitud(titulo)).toBeVisible();
    await expect(this.solicitud(titulo)).toContainText(descripcion);
    await expect(this.solicitud(titulo)).toContainText('Activa');

    // The list card does not display the budget; the detail page does.
    await this.solicitud(titulo).getByRole('link', { name: 'Gestionar' }).click();
    await expect(this.page).toHaveURL(/\/empleador\/solicitudes\/\d+$/);
    await expect(this.page.getByRole('heading', { name: titulo, exact: true })).toBeVisible();
    const trabajo = this.page.getByRole('heading', { name: 'El trabajo' }).locator('..');
    await expect(trabajo.getByText(presupuesto, { exact: true })).toBeVisible();
    await this.goto('/empleador/solicitudes');
    await expect(this.solicitud(titulo)).toBeVisible();
  }

  async abrirOportunidad(titulo: string) {
    const tarjeta = this.tarjeta(titulo);
    await expect(tarjeta).toBeVisible();
    await tarjeta.getByRole('link', { name: /ver detalle y postularme/i }).click();
    await expect(this.page.getByRole('heading', { name: titulo, exact: true })).toBeVisible();
  }

  async postular(monto: string, mensaje: string) {
    await this.page.getByRole('button', { name: 'Postularme ahora' }).click();
    await this.page.getByPlaceholder('Ej. 18000').fill(monto);
    await this.page.getByPlaceholder('Explica tu experiencia previa', { exact: false }).fill(mensaje);
    await this.page.getByRole('button', { name: /confirmar postulación/i }).click();
  }
}
