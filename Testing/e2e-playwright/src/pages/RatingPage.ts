import { expect, type Page } from '@playwright/test';
import { BasePage } from './BasePage';

export interface Valoracion {
  trabajadorNombre: string;
  comentario: string;
}

/** Formulario de valoración del empleador (`/empleador/valoraciones`). */
export class RatingPage extends BasePage {
  constructor(page: Page) {
    super(page);
  }

  async open(workerId: number): Promise<void> {
    await this.visit(`/empleador/valoraciones?trabajadorId=${workerId}`);
    await this.expectHeading('¿Cómo fue tu trabajo?');
  }

  async rate(data: Valoracion): Promise<void> {
    await this.page.getByLabel('Trabajador').selectOption({ label: data.trabajadorNombre });
    await this.page.getByRole('button', { name: '5 estrellas' }).click();
    await this.page
      .getByLabel('Comentario (opcional)')
      .fill(data.comentario);

    await this.page.getByRole('button', { name: 'Publicar valoración' }).click();

    await expect(this.page.getByText('Gracias. Tu valoración fue publicada.')).toBeVisible();
    await expect(
      this.page.locator('article').filter({ hasText: data.comentario }),
    ).toBeVisible();
  }
}
