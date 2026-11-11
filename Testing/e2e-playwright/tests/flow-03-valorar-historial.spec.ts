import { test } from '@playwright/test';
import { ApiClient } from '../src/support/api';
import { uniqueTitle } from '../src/support/testData';
import { LoginPage } from '../src/pages/LoginPage';
import { RatingPage } from '../src/pages/RatingPage';
import { WorkerRatingsPage } from '../src/pages/WorkerRatingsPage';
import { WorkerHistoryPage } from '../src/pages/WorkerHistoryPage';

test.describe('Flujo crítico 3 — Valorar y actualizar el historial', () => {
  test('TC-PW-FLOW-03: Valorar → Historial actualiza', async ({ page }) => {
    const api = await ApiClient.create();
    const employer = await api.register('EMPLEADOR');
    const worker = await api.register('TRABAJADOR');
    const title = uniqueTitle('Trabajo valorado E2E');
    const comentario = `Trabajo impecable y puntual ${Date.now()}.`;

    try {
      // Precondición: trabajo publicado, postulado, seleccionado y completado.
      const oportunidad = await api.createOportunidad(employer, {
        titulo: title,
        contenido: 'Trabajo preparado por la suite E2E para el flujo de valoración.',
      });
      const postulacion = await api.createPostulacion(worker, oportunidad);
      await api.seleccionar(employer, postulacion.id);
      await api.completar(employer, postulacion.id);

      // 1. El empleador valora el trabajo desde la interfaz.
      const login = new LoginPage(page);
      await login.open();
      await login.loginAndWaitForDashboard(
        employer.email,
        employer.password,
        /\/empleador\/dashboard/,
      );

      const rating = new RatingPage(page);
      await rating.open(worker.id);
      await rating.rate({ trabajadorNombre: worker.nombre, comentario });
      await rating.capture('flow-03-01-valoracion-publicada');

      // 2. El historial del trabajador refleja el trabajo completado.
      await login.open();
      await login.loginAndWaitForDashboard(
        worker.email,
        worker.password,
        /\/trabajador\/dashboard/,
      );

      const history = new WorkerHistoryPage(page);
      await history.open();
      await history.expectEstado(title, 'COMPLETADA');

      // 3. La valoración aparece publicada en el perfil del trabajador.
      const ratings = new WorkerRatingsPage(page);
      await ratings.open();
      await ratings.expectReview(comentario, employer.nombre);
      await ratings.capture('flow-03-02-valoraciones-trabajador');
    } finally {
      await api.dispose();
    }
  });
});
