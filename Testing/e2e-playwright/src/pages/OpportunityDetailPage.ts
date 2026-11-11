import { expect, type Page } from '@playwright/test';
import { BasePage } from './BasePage';

interface Propuesta {
  remuneracion: string;
  plazo: string;
  mensaje: string;
}

/** Detalle de una oportunidad y modal de postulación. */
export class OpportunityDetailPage extends BasePage {
  constructor(page: Page) {
    super(page);
  }

  async apply(propuesta: Propuesta): Promise<void> {
    await this.page.getByRole('button', { name: /Postularme ahora/ }).click();
    await expect(this.page.getByRole('heading', { name: 'Enviar Postulación' })).toBeVisible();

    await this.page.locator('#postulacion-cotizacion').fill(propuesta.remuneracion);
    await this.page.locator('#postulacion-plazo').fill(propuesta.plazo);
    await this.page.locator('#postulacion-mensaje').fill(propuesta.mensaje);

    await this.page.getByRole('button', { name: 'Confirmar Postulación' }).click();

    await expect(
      this.page.getByRole('status').filter({ hasText: 'Postulación enviada correctamente.' }),
    ).toBeVisible();
    await expect(this.page.getByRole('button', { name: 'Enviada' })).toBeVisible();
  }
}
