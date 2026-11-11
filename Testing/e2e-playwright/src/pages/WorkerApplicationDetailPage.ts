import { expect, type Page } from '@playwright/test';
import { BasePage } from './BasePage';

/**
 * Seguimiento de una postulación del trabajador
 * (`/trabajador/postulaciones/:id`).
 */
export class WorkerApplicationDetailPage extends BasePage {
  constructor(page: Page) {
    super(page);
  }

  async open(id: number): Promise<void> {
    await this.visit(`/trabajador/postulaciones/${id}`);
    await this.expectHeading('Seguimiento de postulación');
  }

  async expectStatus(estado: string): Promise<void> {
    await expect(this.page.getByText(`Estado: ${estado}`)).toBeVisible();
  }

  /**
   * Retira la postulación (borrado lógico) aceptando el diálogo de confirmación.
   * El botón solo existe mientras la postulación está PENDIENTE.
   */
  async retire(): Promise<void> {
    this.page.once('dialog', (dialog) => void dialog.accept());
    await this.page.getByRole('button', { name: 'Retirar postulación' }).click();

    await this.expectStatus('RETIRADA');
    await expect(
      this.page.getByRole('button', { name: 'Retirar postulación' }),
    ).toHaveCount(0);
  }
}
