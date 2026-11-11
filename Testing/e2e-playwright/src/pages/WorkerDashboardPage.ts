import { expect, type Page } from '@playwright/test';
import { BasePage } from './BasePage';

/** Panel del trabajador (`/trabajador/dashboard`). */
export class WorkerDashboardPage extends BasePage {
  constructor(page: Page) {
    super(page);
  }

  async expectLoaded(): Promise<void> {
    await this.expectHeading(/Hola,/);
    await expect(this.page.getByText('postulaciones activas')).toBeVisible();
  }

  async logout(): Promise<void> {
    await this.page.getByRole('button', { name: 'Cerrar sesión' }).click();
    await expect(this.page).toHaveURL(/\/login$/);
  }
}
