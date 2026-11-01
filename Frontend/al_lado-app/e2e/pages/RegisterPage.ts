import { Locator, Page } from '@playwright/test';
import { BasePage } from './BasePage';

export class RegisterPage extends BasePage {
  readonly nombreInput: Locator;
  readonly apellidoInput: Locator;
  readonly emailInput: Locator;
  readonly telefonoInput: Locator;
  readonly passwordInput: Locator;
  readonly rolTrabajadorButton: Locator;
  readonly rolEmpleadorButton: Locator;
  readonly submitButton: Locator;

  constructor(page: Page) {
    super(page);
    this.nombreInput = page.locator('input[placeholder="Nombre"]');
    this.apellidoInput = page.locator('input[placeholder="Apellido"]');
    this.emailInput = page.locator('input[placeholder="Correo electrónico"]');
    this.telefonoInput = page.locator('input[placeholder="Teléfono"]');
    this.passwordInput = page.locator('#register-password');
    this.rolTrabajadorButton = page.getByRole('button', { name: /quiero trabajar/i });
    this.rolEmpleadorButton = page.getByRole('button', { name: /quiero contratar/i });
    this.submitButton = page.getByRole('button', { name: /crear mi cuenta|cuenta creada/i });
  }

  async goto() {
    await super.goto('/register');
  }

  async register(
    nombre: string,
    apellido: string,
    email: string,
    password: string,
    telefono: string,
    rol: 'TRABAJADOR' | 'EMPLEADOR' = 'TRABAJADOR',
  ) {
    if (rol === 'EMPLEADOR') {
      await this.rolEmpleadorButton.click();
    } else {
      await this.rolTrabajadorButton.click();
    }

    await this.nombreInput.fill(nombre);
    await this.apellidoInput.fill(apellido);
    await this.emailInput.fill(email);
    await this.telefonoInput.fill(telefono);
    await this.passwordInput.fill(password);
    await this.submitButton.click();
  }

}
