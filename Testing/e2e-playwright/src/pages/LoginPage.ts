import { expect, type Page } from '@playwright/test';
import { BasePage } from './BasePage';

/** Formulario de acceso compartido por `/login` y `/acceso-admin`. */
export class LoginPage extends BasePage {
  constructor(page: Page) {
    super(page);
  }

  async open(): Promise<void> {
    await this.visit('/login');
    await expect(this.page.locator('#login-email')).toBeVisible();
  }

  async openAdminAccess(): Promise<void> {
    await this.visit('/acceso-admin');
    await expect(this.page.locator('#login-email')).toBeVisible();
  }

  async login(email: string, password: string): Promise<void> {
    await this.page.locator('#login-email').fill(email);
    await this.page.locator('#login-password').fill(password);
    await this.page.locator("form button[type='submit']").click();
  }

  /** Inicia sesión y espera el panel del rol. */
  async loginAndWaitForDashboard(
    email: string,
    password: string,
    destination: RegExp = /\/dashboard/,
  ): Promise<void> {
    await this.login(email, password);
    await expect(this.page).toHaveURL(destination);
  }

  errorBanner() {
    return this.page.getByRole('alert');
  }
}
