import { expect, type Page } from '@playwright/test';
import { BasePage } from './BasePage';

export interface NuevaSolicitud {
  titulo: string;
  descripcion: string;
  especialidad: string;
  ubicacion: string;
  presupuesto: string;
}

/** Alta de una solicitud desde el panel del empleador. */
export class EmployerNewRequestPage extends BasePage {
  constructor(page: Page) {
    super(page);
  }

  async open(): Promise<void> {
    await this.visit('/empleador/solicitudes/nueva');
    await this.expectHeading('Contanos qué necesitás.');
  }

  /** Publica la solicitud y devuelve el id de la oferta creada. */
  async publish(data: NuevaSolicitud): Promise<number> {
    await this.page.getByLabel('Título del trabajo').fill(data.titulo);
    await this.page.getByLabel('Descripción').fill(data.descripcion);
    await this.page.getByLabel('Especialidad').selectOption({ label: data.especialidad });
    await this.page.getByLabel('Ubicación').fill(data.ubicacion);
    await this.page.getByLabel('Presupuesto (opcional)').fill(data.presupuesto);

    await this.page.getByRole('button', { name: 'Publicar solicitud' }).click();

    await expect(this.page).toHaveURL(/\/empleador\/solicitudes\/\d+$/);
    const match = this.page.url().match(/\/empleador\/solicitudes\/(\d+)$/);
    expect(match, 'la publicación debe redirigir al detalle con su id').not.toBeNull();
    return Number(match![1]);
  }
}
