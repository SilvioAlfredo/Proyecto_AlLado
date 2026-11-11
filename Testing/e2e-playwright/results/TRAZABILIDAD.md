# Trazabilidad E2E — Sprint 4

Filas listas para pegar en `Testing/Planilla de Test Cases v2(2).xlsx` (las 18 columnas respetan el orden de la planilla).

> Versión para pegar: `results/TRAZABILIDAD.tsv` (columnas separadas por tabulación).

| ID Caso | Trazabilidad | Estado | Resultado | Evidencia | Test automatizado |
|---|---|---|---|---|---|
| **TC-PW-FLOW-01** | HU-04 CA-01/CA-02 · RF-01, RF-11 | PASS | PASS — 1,3 s. Dashboard del trabajador visible y redirección a /login verificada. | `Testing/e2e-playwright/results/capturas-flujos/flow-01-01-dashboard-trabajador.png` | `Testing/e2e-playwright/tests/flow-01-registro-login.spec.ts::TC-PW-FLOW-01: Registro → Login → Dashboard → Logout` |
| **TC-PW-FLOW-02** | HU-01 CA-01, HU-02 CA-01/CA-02 · RF-04, RF-15 | PASS | PASS — 3,5 s. Solicitud publicada, postulación PENDIENTE, SELECCIONADA en candidatos e historial. | `Testing/e2e-playwright/results/capturas-flujos/flow-02-01-solicitud-publicada.png` | `Testing/e2e-playwright/tests/flow-02-publicar-postular-seleccionar.spec.ts::TC-PW-FLOW-02: Publicar → Postular → Seleccionar` |
| **TC-PW-FLOW-03** | HU-06 CA-01 · RF-05, RF-08 | PASS | PASS — 1,8 s. Valoración 5★ publicada, historial COMPLETADA y reseña visible para el trabajador. | `Testing/e2e-playwright/results/capturas-flujos/flow-03-01-valoracion-publicada.png` | `Testing/e2e-playwright/tests/flow-03-valorar-historial.spec.ts::TC-PW-FLOW-03: Valorar → Historial actualiza` |
| **TC-PW-FLOW-04** | HU-02 CA-02 · RF-04 | PASS | PASS — 1,3 s. Postulación retirada y estado RETIRADA verificado en el historial. | `Testing/e2e-playwright/results/capturas-flujos/flow-04-01-postulacion-pendiente.png` | `Testing/e2e-playwright/tests/flow-04-retirar-postulacion.spec.ts::TC-PW-FLOW-04: Retirar postulación → estado RETIRADA` |
| **TC-E2E-LOGIN-01** | HU-04 CA-02 · RF-01, RF-11 | PASS | PASS — 1,0 s. Login de empleador demo, dashboard y logout verificados. | `Testing/e2e-playwright/results/html-report/index.html` | `Testing/e2e-playwright/tests/p1-casos-complementarios.spec.ts::TC-E2E-LOGIN-01` |
| **TC-E2E-PROT-01** | HU-04 CA-02 · RF-01, RF-11 | PASS | PASS — 0,8 s. Redirección sin sesión y bloqueo de rol verificados. | `Testing/e2e-playwright/results/html-report/index.html` | `Testing/e2e-playwright/tests/p1-casos-complementarios.spec.ts::TC-E2E-PROT-01` |
| **TC-E2E-ADMIN-01** | HU-18 CA-03, HU-19 CA-01 · RF-19 | PASS | PASS — 1,5 s. Acceso administrador y métricas visibles. | `Testing/e2e-playwright/results/capturas-flujos/p1-admin-dashboard-metricas.png` | `Testing/e2e-playwright/tests/p1-casos-complementarios.spec.ts::TC-E2E-ADMIN-01` |
