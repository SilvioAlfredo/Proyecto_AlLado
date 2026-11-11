import { expect, type Page } from '@playwright/test';
import { BasePage } from './BasePage';

/** Listado de oportunidades del trabajador (`/trabajador/oportunidades`). */
export class OpportunitiesPage extends BasePage {
  constructor(page: Page) {
    super(page);
  }

  async open(): Promise<void> {
    await this.visit('/trabajador/oportunidades');
    await this.expectHeading('Oportunidades Laborales');
  }

  /** Filtra por el título único y abre el detalle de esa publicación. */
  async openDetail(title: string): Promise<void> {
    await this.page
      .getByPlaceholder('Buscar por oficio o tarea...')
      .fill(title);

    const card = this.page
      .locator('div.bg-allado-card')
      .filter({ has: this.page.getByRole('heading', { name: title, exact: true }) })
      .first();

    await expect(card).toBeVisible();
    await card.getByRole('link', { name: /Ver detalle y postularme/ }).click();
    await this.expectHeading(title);
  }
}
