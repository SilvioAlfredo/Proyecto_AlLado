import { expect, Locator, Page } from '@playwright/test';
import { BasePage } from './BasePage';

export class LoginPage extends BasePage {
  readonly emailInput: Locator;
  readonly passwordInput: Locator;
  readonly submitButton: Locator;

  constructor(page: Page) {
    super(page);
    this.emailInput = page.locator('#login-email');
    this.passwordInput = page.locator('#login-password');
    this.submitButton = page.locator('main').getByRole('button', { name: /^ingresar$/i });
  }

  async goto() {
    await super.goto('/login');
  }

  async login(email: string, password: string) {
    await this.emailInput.fill(email);
    await this.passwordInput.fill(password);
    await this.submitButton.click();
  }


  async logout() {
    const button = this.page.getByRole('button', { name: 'Cerrar sesión', exact: true });
    await expect(button).toBeVisible();
    await button.click();
    await expect(this.page).toHaveURL('/login');
  }
}
