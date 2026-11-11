import { expect, type Page } from '@playwright/test';
import { BasePage } from './BasePage';

/** Detalle de una solicitud del empleador (`/empleador/solicitudes/:id`). */
export class RequestDetailPage extends BasePage {
  constructor(page: Page) {
    super(page);
  }

  async open(id: number): Promise<void> {
    await this.visit(`/empleador/solicitudes/${id}`);
    await expect(this.page.getByText('Gestión de solicitud')).toBeVisible();
  }

  async expectTitle(title: string): Promise<void> {
    await this.expectHeading(title);
  }

  async openCandidates(): Promise<void> {
    await this.page.getByRole('link', { name: 'Ver postulantes' }).click();
    await expect(this.page).toHaveURL(/\/empleador\/candidatos\?oportunidadId=\d+/);
  }

  async expectSelectedWorker(nombre: string): Promise<void> {
    await expect(this.page.getByRole('heading', { name: 'Profesional seleccionado' })).toBeVisible();
    await expect(this.page.getByRole('link', { name: new RegExp(nombre) })).toBeVisible();
  }

  /** Marca el trabajo como completado desde el detalle de la solicitud. */
  async completeJob(): Promise<void> {
    await this.page.getByRole('button', { name: 'Marcar como completado' }).click();
    await expect(this.page.getByRole('link', { name: 'Valorar trabajo' })).toBeVisible();
  }
}
