import { test } from '@playwright/test';
import { ApiClient } from '../src/support/api';
import { uniqueTitle } from '../src/support/testData';
import { LoginPage } from '../src/pages/LoginPage';
import { WorkerApplicationDetailPage } from '../src/pages/WorkerApplicationDetailPage';
import { WorkerHistoryPage } from '../src/pages/WorkerHistoryPage';

test.describe('Flujo de borrado — Retirar postulación', () => {
  test('TC-PW-FLOW-04: Retirar postulación → estado RETIRADA', async ({ page }) => {
    const api = await ApiClient.create();
    const employer = await api.register('EMPLEADOR');
    const worker = await api.register('TRABAJADOR');
    const title = uniqueTitle('Trabajo para retirar E2E');

    try {
      // Precondición: publicación del empleador y postulación PENDIENTE del trabajador.
      const oportunidad = await api.createOportunidad(employer, {
        titulo: title,
        contenido: 'Trabajo preparado para validar el retiro de una postulación.',
      });
      const postulacion = await api.createPostulacion(worker, oportunidad);

      // 1. El trabajador abre el seguimiento de su postulación.
      const login = new LoginPage(page);
      await login.open();
      await login.loginAndWaitForDashboard(
        worker.email,
        worker.password,
        /\/trabajador\/dashboard/,
      );

      const detalle = new WorkerApplicationDetailPage(page);
      await detalle.open(postulacion.id);
      await detalle.expectStatus('PENDIENTE');
      await detalle.capture('flow-04-01-postulacion-pendiente');

      // 2. Retira la postulación y confirma el diálogo.
      await detalle.retire();
      await detalle.capture('flow-04-02-postulacion-retirada');

      // 3. El historial refleja el borrado lógico.
      const history = new WorkerHistoryPage(page);
      await history.open();
      await history.expectEstado(title, 'RETIRADA');
      await history.capture('flow-04-03-historial-retirada');
    } finally {
      await api.dispose();
    }
  });
});
