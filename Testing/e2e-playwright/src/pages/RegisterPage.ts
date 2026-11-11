import { expect, type Page } from '@playwright/test';
import { BasePage } from './BasePage';
import type { TestUserInput } from '../support/testData';

/** Formulario de alta de cuentas (`/register`). */
export class RegisterPage extends BasePage {
  constructor(page: Page) {
    super(page);
  }

  async open(): Promise<void> {
    await this.visit('/register');
    await expect(this.page.getByLabel('Nombre', { exact: true })).toBeVisible();
  }

  async selectRole(rol: 'TRABAJADOR' | 'EMPLEADOR'): Promise<void> {
    const label = rol === 'EMPLEADOR' ? /Quiero contratar/ : /Quiero trabajar/;
    const button = this.page.getByRole('button', { name: label });
    await button.click();
    await expect(button).toHaveAttribute('aria-pressed', 'true');
  }

  async fill(user: TestUserInput): Promise<void> {
    await this.page.getByLabel('Nombre', { exact: true }).fill(user.nombre);
    await this.page.getByLabel('Apellido', { exact: true }).fill(user.apellido);
    await this.page.getByLabel('Correo electrónico', { exact: true }).fill(user.email);
    await this.page.getByLabel('Teléfono', { exact: true }).fill(user.telefono);
    // El campo de contraseña está dentro de un contenedor con el botón de
    // visibilidad; su <label> apunta al contenedor, no al input.
    await this.page.locator('#register-password').fill(user.password);
  }

  async submit(): Promise<void> {
    await this.page.getByRole('button', { name: 'Crear mi cuenta' }).click();
  }

  /** Completa el alta y espera la confirmación visible. */
  async register(user: TestUserInput): Promise<void> {
    await this.selectRole(user.rol);
    await this.fill(user);
    await this.submit();
    await expect(this.page.getByText('Cuenta creada correctamente.')).toBeVisible();
  }
}
