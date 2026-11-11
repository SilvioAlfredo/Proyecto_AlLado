import { expect, type Page } from '@playwright/test';
import { mkdirSync } from 'node:fs';
import { join } from 'node:path';

/** Comportamiento común a todos los page objects de la suite. */
export abstract class BasePage {
  protected constructor(protected readonly page: Page) {}

  /**
   * Captura una evidencia con nombre propio cuando `E2E_CAPTURAS=1`.
   * Se usa para documentar los flujos en verde (la config normal solo
   * fotografía los fallos).
   */
  async capture(name: string): Promise<void> {
    if (process.env.E2E_CAPTURAS !== '1') return;
    const dir = join(process.cwd(), 'results', 'capturas-flujos');
    mkdirSync(dir, { recursive: true });
    await this.page.screenshot({ path: join(dir, `${name}.png`), fullPage: true });
  }

  /** Navega a una ruta absoluta del frontend. */
  protected async visit(path: string): Promise<void> {
    await this.page.goto(path);
  }

  async expectPath(pattern: RegExp | string): Promise<void> {
    await expect(this.page).toHaveURL(pattern);
  }

  async expectHeading(name: string | RegExp): Promise<void> {
    await expect(this.page.getByRole('heading', { level: 1, name })).toBeVisible();
  }

  async fillByLabel(label: string | RegExp, value: string): Promise<void> {
    await this.page.getByLabel(label).fill(value);
  }

  async clickButton(name: string | RegExp): Promise<void> {
    await this.page.getByRole('button', { name }).click();
  }

  /** Borra la sesión del navegador sin pasar por la UI (cambio de rol). */
  async clearSession(): Promise<void> {
    await this.page.goto('/login');
    await this.page.evaluate(() => window.localStorage.clear());
  }
}
