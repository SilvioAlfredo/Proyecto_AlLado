import { expect, type Page } from '@playwright/test';
import { BasePage } from './BasePage';

/** Valoraciones recibidas por el trabajador (`/trabajador/valoraciones`). */
export class WorkerRatingsPage extends BasePage {
  constructor(page: Page) {
    super(page);
  }

  async open(): Promise<void> {
    await this.visit('/trabajador/valoraciones');
    await this.expectHeading('Mis Valoraciones');
  }

  async expectReview(comentario: string, autor: string): Promise<void> {
    await expect(this.page.getByText(comentario)).toBeVisible();
    await expect(this.page.getByRole('heading', { name: autor, level: 4 })).toBeVisible();
  }
}
