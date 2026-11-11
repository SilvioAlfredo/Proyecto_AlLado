import { expect, type Page } from '@playwright/test';
import { BasePage } from './BasePage';

/** Historial de postulaciones del trabajador (`/trabajador/postulaciones`). */
export class WorkerHistoryPage extends BasePage {
  constructor(page: Page) {
    super(page);
  }

  async open(): Promise<void> {
    await this.visit('/trabajador/postulaciones');
    await this.expectHeading('Historial de Postulaciones');
  }

  async expectEstado(title: string, estado: string): Promise<void> {
    const row = this.page.locator('tbody tr').filter({ hasText: title }).first();
    await expect(row).toBeVisible();
    await expect(row).toContainText(estado);
  }
}
