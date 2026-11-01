import { expect, Locator, Page } from '@playwright/test';

export class BasePage {
  readonly page: Page;

  constructor(page: Page) {
    this.page = page;
  }

  async goto(path = '/') {
    const target = path.startsWith('http')
      ? path
      : path.startsWith('/')
        ? path
        : `/${path}`;

    await this.page.goto(target, { waitUntil: 'domcontentloaded' });
  }

  async esperarCarga() {
    await expect(this.page.getByText(/^Cargando/i)).toHaveCount(0, { timeout: 15000 });
  }

  async esperarToast(): Promise<Locator> {
    const toast = this.page
      .locator('[role="alert"], [role="status"], .toast, .alert')
      .first();

    await toast.waitFor({ state: 'visible', timeout: 10000 });
    return toast;
  }
}