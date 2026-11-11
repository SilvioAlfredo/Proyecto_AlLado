import { expect, type Page } from '@playwright/test';
import { BasePage } from './BasePage';

/** Listado de postulantes de una solicitud (`/empleador/candidatos`). */
export class CandidatesPage extends BasePage {
  constructor(page: Page) {
    super(page);
  }

  async expectLoaded(): Promise<void> {
    await this.expectHeading('Postulantes');
  }

  /** Acepta la propuesta del trabajador identificado por su email. */
  async acceptCandidate(email: string): Promise<void> {
    const candidate = this.page.locator('article').filter({ hasText: email }).first();
    await expect(candidate).toBeVisible();
    await candidate.getByRole('button', { name: 'Aceptar' }).click();
    await expect(candidate.getByRole('button', { name: 'SELECCIONADA' })).toBeVisible();
  }
}
