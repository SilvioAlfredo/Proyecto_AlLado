import { expect, type Page } from '@playwright/test';
import { BasePage } from './BasePage';

/** Panel de administración (`/admin/dashboard`). */
export class AdminDashboardPage extends BasePage {
  constructor(page: Page) {
    super(page);
  }

  async open(): Promise<void> {
    await this.visit('/admin/dashboard');
    await this.expectHeading('La comunidad, en números');
  }

  async expectMetrics(): Promise<void> {
    await expect(this.page.getByText('Trabajadores', { exact: true })).toBeVisible();
    await expect(this.page.getByText('Postulaciones', { exact: true })).toBeVisible();
  }
}
