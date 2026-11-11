# Registro de ejecución E2E

**Fecha:** 10/11/2026 · 23:23 (America/Argentina/Buenos_Aires)
**Responsable:** Julio Lasa — Capa Cúmula

## Entorno

| Componente | Valor |
|---|---|
| Frontend | `http://localhost:3001` (Next.js 16.3.6, build de producción) |
| Backend | `http://localhost:8081` (Spring Boot 3.2.5, perfil `e2e`) |
| Base de datos | PostgreSQL 18 local, base exclusiva `al_lado_e2e` |
| Navegador | Chromium (Playwright 1.64.1, headless) |

## Comando

```bash
cd Testing/e2e-playwright
E2E_BASE_URL=http://localhost:3001 E2E_API_URL=http://localhost:8081 \
E2E_CAPTURAS=1 npx playwright test | tee results/ejecucion.log
```

La corrida con `E2E_CAPTURAS=1` genera además las 12 capturas de
`results/capturas-flujos/` y el log completo en `results/ejecucion.log`.

## Resultado

```
Running 7 tests using 1 worker
  7 passed (12.1s)
```

| Caso | Resultado |
|---|---|
| TC-PW-FLOW-01 — Registro → Login → Dashboard | ✅ passed |
| TC-PW-FLOW-02 — Publicar → Postular → Seleccionar | ✅ passed |
| TC-PW-FLOW-03 — Valorar → Historial actualiza | ✅ passed |
| TC-PW-FLOW-04 — Retirar postulación (Delete) | ✅ passed |
| TC-E2E-LOGIN-01 — Login/logout con cuenta sembrada | ✅ passed |
| TC-E2E-PROT-01 — Rutas protegidas y roles | ✅ passed |
| TC-E2E-ADMIN-01 — Acceso administrador y métricas | ✅ passed |

## Evidencia de fallo deliberado (AUT-E2E-07)

```bash
npm run test:fallo
```

Generó, en `results/fallo-deliberado/`:

- `test-failed-1.png` (screenshot)
- `video.webm` (video)
- `trace.zip` (trace interactivo)
- `html-report/index.html` (reporte del fallo)

El caso falla **a propósito**: usa credenciales inválidas y luego exige haber
llegado al dashboard, de modo que el fallo es real y reproducible.

## Artefactos de la corrida

- `results/html-report/index.html`
- `results/junit.xml`
- `results/results.json`
- `results/ejecucion.log`
- `results/capturas-flujos/` (12 capturas)
- `results/TRAZABILIDAD.tsv` y `results/TRAZABILIDAD.md` (7 filas × 18 columnas)
