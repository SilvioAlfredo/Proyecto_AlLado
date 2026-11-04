import { test, expect, type Page } from '@playwright/test';
import { LoginPage } from '../pages/LoginPage';
import { OportunidadPage } from '../pages/OportunidadPage';
import { RegisterPage } from '../pages/RegisterPage';

async function evidencia(page: Page, nombre: string) {
  await test.info().attach(nombre, { body: await page.screenshot({ fullPage: true }), contentType: 'image/png' });
}

async function crearCuenta(page: Page, email: string, rol: 'TRABAJADOR' | 'EMPLEADOR') {
  const registerPage = new RegisterPage(page);
  await registerPage.goto();
  await registerPage.register('Usuario E2E', rol === 'EMPLEADOR' ? 'Empleador' : 'Trabajador', email, 'Demo1234*', '+5493510000000', rol);
  await expect(page.getByRole('status').filter({ hasText: 'Cuenta creada correctamente.' })).toBeVisible();
}

async function ingresar(page: Page, email: string, rol: 'trabajador' | 'empleador') {
  const loginPage = new LoginPage(page);
  await loginPage.goto();
  await loginPage.login(email, 'Demo1234*');
  await expect(page).toHaveURL(`/` + rol + '/dashboard');
}

async function crearOportunidad(page: Page, titulo: string, descripcion: string, presupuesto: string) {
  await new OportunidadPage(page).publicar(titulo, descripcion, presupuesto);
}

test.describe('AUT-E2E-02: CRUD Oportunidad Laboral', () => {
  test('Empleador crea y publica nueva oportunidad laboral en /publicar', async ({ page }) => {
    const email = `e2e.emp.${Date.now()}@demo.com`;
    const titulo = `Reparación Eléctrica E2E ${Date.now()}`;
    await crearCuenta(page, email, 'EMPLEADOR');
    await ingresar(page, email, 'empleador');
    await crearOportunidad(page, titulo, 'Instalación de disyuntor y circuito eléctrico.', '22000');
    await evidencia(page, 'Solicitud publicada y activa en Mis solicitudes');
  });

  test('Listado de oportunidades para trabajador y filtrado dinámico por oficio/rubro', async ({ page }) => {
    const email = `e2e.emp.list.${Date.now()}@demo.com`;
    const titulo = `Instalación Eléctrica ${Date.now()}`;
    await crearCuenta(page, email, 'EMPLEADOR');
    await ingresar(page, email, 'empleador');
    await crearOportunidad(page, titulo, 'Necesito instalar un circuito eléctrico en una vivienda.', '24000');

    const workerEmail = `e2e.trab.list.${Date.now()}@demo.com`;
    await crearCuenta(page, workerEmail, 'TRABAJADOR');
    await ingresar(page, workerEmail, 'trabajador');
    const oportunidades = new OportunidadPage(page);
    await oportunidades.goto('/trabajador/oportunidades');
    await expect(oportunidades.tarjeta(titulo)).toBeVisible();
    await page.getByPlaceholder('Buscar por oficio o tarea...').fill(titulo);
    await expect(oportunidades.tarjeta(titulo)).toBeVisible();
    await evidencia(page, 'Oportunidad creada filtrada por titulo');

    await page.getByPlaceholder('Buscar por oficio o tarea...').clear();
    await page.getByRole('button', { name: 'Electricidad', exact: true }).click();
    await expect(oportunidades.tarjeta(titulo)).toBeVisible();
    await evidencia(page, 'Oportunidad creada filtrada por rubro Electricidad');
  });

  test('Empleador edita presupuesto y descripción de oportunidad laboral activa', async ({ page }) => {
    const email = `e2e.emp.edit.${Date.now()}@demo.com`;
    const titulo = `Reparación Editada E2E ${Date.now()}`;
    await crearCuenta(page, email, 'EMPLEADOR');
    await ingresar(page, email, 'empleador');
    await crearOportunidad(page, titulo, 'Revisión y ajuste de instalación eléctrica.', '20000');

    const oportunidades = new OportunidadPage(page);
    await oportunidades.solicitud(titulo).getByRole('link', { name: 'Editar' }).click();
    await expect(page.getByRole('heading', { name: 'Editar solicitud' })).toBeVisible();
    await page.getByLabel('Presupuesto (opcional)').fill('25000');
    const descripcion = 'Necesito revisión final con presupuesto actualizado y descripción mejorada.';
    await page.getByLabel('Descripción').fill(descripcion);
    await page.getByRole('button', { name: 'Guardar cambios' }).click();
    await expect(page).toHaveURL(/\/empleador\/solicitudes\/\d+$/);
    await expect(page.getByRole('heading', { name: titulo, exact: true })).toBeVisible();
    await expect(page.getByText(descripcion, { exact: true })).toBeVisible();
    await expect(page.getByText('25000', { exact: true })).toBeVisible();
    await evidencia(page, 'Solicitud editada con descripcion y presupuesto nuevos');
  });

  test('Baja lógica / Cancelación de oportunidad laboral activa', async ({ page }) => {
    const email = `e2e.emp.delete.${Date.now()}@demo.com`;
    const titulo = `Reparación Baja E2E ${Date.now()}`;
    await crearCuenta(page, email, 'EMPLEADOR');
    await ingresar(page, email, 'empleador');
    await crearOportunidad(page, titulo, 'Necesito cerrar y dar de baja una solicitud de prueba.', '18000');

    const oportunidades = new OportunidadPage(page);
    const solicitud = oportunidades.solicitud(titulo);
    await solicitud.getByRole('button', { name: 'Cerrar solicitud' }).click();
    await expect(solicitud).toContainText('Cerrada');
    await expect(solicitud.getByRole('button', { name: 'Reactivar solicitud' })).toBeVisible();
    await page.reload();
    await expect(oportunidades.solicitud(titulo)).toContainText('Cerrada');
    await evidencia(page, 'Solicitud creada cerrada luego de recargar el listado');
  });
});

test.describe('AUT-E2E-03: Alta y validación de postulaciones', () => {
  async function prepararPostulacion(page: Page, prefijo: string) {
    const employerEmail = `e2e.emp.${prefijo}.${Date.now()}@demo.com`;
    const titulo = `Trabajo Postulación ${prefijo} ${Date.now()}`;
    await crearCuenta(page, employerEmail, 'EMPLEADOR');
    await ingresar(page, employerEmail, 'empleador');
    await crearOportunidad(page, titulo, 'Instalaciones eléctricas para una vivienda.', '18000');

    const workerEmail = `e2e.trab.${prefijo}.${Date.now()}@demo.com`;
    await crearCuenta(page, workerEmail, 'TRABAJADOR');
    await ingresar(page, workerEmail, 'trabajador');
    const oportunidades = new OportunidadPage(page);
    await oportunidades.goto('/trabajador/oportunidades');
    await oportunidades.abrirOportunidad(titulo);
    return { titulo, oportunidades };
  }

  test('Trabajador se postula a una oportunidad disponible', async ({ page }) => {
    const { titulo, oportunidades } = await prepararPostulacion(page, 'post');
    await oportunidades.postular('18000', 'Tengo experiencia en instalaciones eléctricas y puedo resolverlo en 3 días.');
    await expect(page.getByRole('status').filter({ hasText: 'Postulación enviada correctamente.' })).toBeVisible();
    await expect(page.getByRole('heading', { name: titulo, exact: true })).toBeVisible();
    await expect(page.getByRole('button', { name: 'Enviada' })).toBeDisabled();
    await evidencia(page, 'Postulacion enviada para la oportunidad creada');
  });

  test('Rechazo de doble postulación a la misma oportunidad por el mismo usuario', async ({ page }) => {
    const { titulo, oportunidades } = await prepararPostulacion(page, 'dup');
    await oportunidades.postular('18000', 'Primera postulación.');
    await expect(page.getByRole('status').filter({ hasText: 'Postulación enviada correctamente.' })).toBeVisible();
    await page.reload();
    await expect(page.getByRole('heading', { name: titulo, exact: true })).toBeVisible();
    const respuesta = page.waitForResponse((r) => r.url().includes('/postulaciones') && r.request().method() === 'POST');
    await oportunidades.postular('18000', 'Segunda postulación a la misma oportunidad.');
    expect((await respuesta).ok()).toBe(false);
    await expect(page.locator('form .text-red-300')).toBeVisible();
    await expect(page.getByRole('button', { name: 'Postularme ahora' })).toBeVisible();
    await evidencia(page, 'Rechazo visible de la segunda postulacion');
  });
});
