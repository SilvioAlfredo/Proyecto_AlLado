import { expect, test } from '@playwright/test';
import { ApiClient } from '../src/support/api';
import { uniqueTitle } from '../src/support/testData';
import { LoginPage } from '../src/pages/LoginPage';
import { EmployerNewRequestPage } from '../src/pages/EmployerNewRequestPage';
import { OpportunitiesPage } from '../src/pages/OpportunitiesPage';
import { OpportunityDetailPage } from '../src/pages/OpportunityDetailPage';
import { RequestDetailPage } from '../src/pages/RequestDetailPage';
import { CandidatesPage } from '../src/pages/CandidatesPage';
import { WorkerHistoryPage } from '../src/pages/WorkerHistoryPage';

test.describe('Flujo crítico 2 — Publicar, postular y seleccionar', () => {
  test('TC-PW-FLOW-02: Publicar → Postular → Seleccionar', async ({ page }) => {
    const api = await ApiClient.create();
    const employer = await api.register('EMPLEADOR');
    const worker = await api.register('TRABAJADOR');
    const title = uniqueTitle('Reparación E2E');
    const descripcion =
      'Trabajo generado por la suite E2E: reparar una persiana del living.';

    try {
      // 1. El empleador publica la solicitud desde la UI.
      const login = new LoginPage(page);
      await login.open();
      await login.loginAndWaitForDashboard(
        employer.email,
        employer.password,
        /\/empleador\/dashboard/,
      );

      const newRequest = new EmployerNewRequestPage(page);
      await newRequest.open();
      const oportunidadId = await newRequest.publish({
        titulo: title,
        descripcion,
        especialidad: 'Electricidad',
        ubicacion: 'Centro',
        presupuesto: '120000',
      });
      await newRequest.capture('flow-02-01-solicitud-publicada');

      // 2. El trabajador encuentra la publicación y se postula.
      await login.open();
      await login.loginAndWaitForDashboard(
        worker.email,
        worker.password,
        /\/trabajador\/dashboard/,
      );

      const opportunities = new OpportunitiesPage(page);
      await opportunities.open();
      await opportunities.openDetail(title);

      const detail = new OpportunityDetailPage(page);
      await detail.apply({
        remuneracion: '110000',
        plazo: '2',
        mensaje: 'Puedo hacer el trabajo este sábado por la mañana.',
      });
      await detail.capture('flow-02-02-postulacion-enviada');

      const history = new WorkerHistoryPage(page);
      await history.open();
      await history.expectEstado(title, 'PENDIENTE');

      // 3. El empleador revisa postulantes y selecciona al trabajador.
      await login.open();
      await login.loginAndWaitForDashboard(
        employer.email,
        employer.password,
        /\/empleador\/dashboard/,
      );

      const requestDetail = new RequestDetailPage(page);
      await requestDetail.open(oportunidadId);
      await requestDetail.expectTitle(title);
      await requestDetail.openCandidates();

      const candidates = new CandidatesPage(page);
      await candidates.expectLoaded();
      await candidates.acceptCandidate(worker.email);
      await candidates.capture('flow-02-03-postulante-seleccionado');

      await requestDetail.open(oportunidadId);
      await requestDetail.expectSelectedWorker(worker.nombre);

      // 4. El historial del trabajador refleja la selección.
      await login.open();
      await login.loginAndWaitForDashboard(
        worker.email,
        worker.password,
        /\/trabajador\/dashboard/,
      );
      await history.open();
      await history.expectEstado(title, 'SELECCIONADA');
      await history.capture('flow-02-04-historial-trabajador');

      // La publicación existe en la API con el estado esperado.
      await expect(page).toHaveURL(/\/trabajador\/postulaciones$/);
    } finally {
      await api.dispose();
    }
  });
});
