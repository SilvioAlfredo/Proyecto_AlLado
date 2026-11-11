# Informe E2E — AlLado · Sprint 4

**Proyecto:** AlLado — Fortaleciendo el Vínculo Laboral Local
**Sprint:** 4 — Automatización de Pruebas
**Capa:** Cúmula — UI / E2E (flujos críticos)
**Responsable:** Julio Lasa
**Fecha de ejecución:** 10/11/2026
**Herramienta:** Playwright Test 1.64.1 · TypeScript · Page Object Model

---

## 1. Resumen ejecutivo

Se implementó desde cero la suite E2E de la aplicación web de AlLado en
`Testing/e2e-playwright/`, con Playwright Test, TypeScript y Page Object Model.

| Métrica | Valor |
|---|---|
| Casos automatizados | **7** |
| Resultado | **7/7 PASS** |
| Duración total | **12,1 s** |
| Flujos críticos CRUD | 4 (3 exigidos + borrado) |
| Page objects | 13 |
| Navegador | Chromium (headless) |
| Reporte | HTML + JUnit XML + JSON |
| Evidencia de fallo | screenshot + video + trace |
| Capturas de flujos en verde | 12 |

La suite valida el flujo visible del usuario contra el frontend Next.js y el
backend Spring Boot reales, sobre una base PostgreSQL exclusiva de QA.

---

## 2. Alcance y estrategia

La suite respeta la pirámide de pruebas: **no re-prueba** reglas de negocio ni el
contrato HTTP (eso ya está cubierto por backend y API). Se limita a los flujos
críticos visibles de punta a punta.

| Nivel | Suite | Herramienta | Responsable |
|---|---|---|---|
| Base | Frontend unitarias | Vitest + RTL | Complementario |
| Media | API | Postman + Newman | Willy |
| **Cima** | **E2E** | **Playwright + TS + POM** | **Julio** |
| Transversal | Accesibilidad | axe-core + Lighthouse | Chiara |

**Regla aplicada:** mínima cantidad de E2E, máximo valor de negocio. Las
precondiciones (cuentas y publicaciones) se crean por API para que los casos sean
independientes y deterministas; **todas las acciones que se validan se ejecutan
por la interfaz**.

---

## 3. Casos y resultados

| ID | RF / HU | Descripción | Resultado | Duración |
|---|---|---|---|---|
| **TC-PW-FLOW-01** | RF-01/11 · HU-04 | Registro → Login → Dashboard → Logout → ruta protegida | ✅ PASS | 1,3 s |
| **TC-PW-FLOW-02** | RF-04/15 · HU-01/02 | Publicar → Postular → Seleccionar → historial | ✅ PASS | 3,5 s |
| **TC-PW-FLOW-03** | RF-05/08 · HU-06 | Valorar → Historial y valoraciones del trabajador | ✅ PASS | 1,8 s |
| **TC-PW-FLOW-04** | RF-04 · HU-02 | Retirar postulación (Delete) → estado RETIRADA | ✅ PASS | 1,3 s |
| **TC-E2E-LOGIN-01** | RF-01/11 · HU-04 | Login con cuenta sembrada → dashboard → logout | ✅ PASS | 1,0 s |
| **TC-E2E-PROT-01** | RF-01/11 · HU-04 | Ruta protegida sin sesión y separación de roles | ✅ PASS | 0,8 s |
| **TC-E2E-ADMIN-01** | RF-19 · HU-18/19 | Acceso administrador y métricas del dashboard | ✅ PASS | 1,5 s |

### Cobertura CRUD

| Operación | Dónde se valida |
|---|---|
| **Create** | FLOW-01 (cuenta), FLOW-02 (publicación y postulación) |
| **Read** | FLOW-01 (dashboard), FLOW-02 (listado e historial), FLOW-03 (valoraciones) |
| **Update** | FLOW-02 (selección), FLOW-03 (completar y valorar) |
| **Delete** | FLOW-04 (retirar postulación) |

---

## 4. Evidencias

### 4.1 Reportes de ejecución

| Evidencia | Archivo |
|---|---|
| Reporte HTML interactivo | `results/html-report/index.html` |
| JUnit XML (CI) | `results/junit.xml` |
| JSON crudo | `results/results.json` |
| Log de consola | `results/ejecucion.log` |

### 4.2 Capturas de los flujos en verde

En `results/capturas-flujos/` (12 archivos):

| Archivo | Momento |
|---|---|
| `flow-01-01-dashboard-trabajador.png` | Dashboard tras registro y login |
| `flow-01-02-ruta-protegida-redirige.png` | Ruta protegida redirigiendo a /login |
| `flow-02-01-solicitud-publicada.png` | Solicitud publicada |
| `flow-02-02-postulacion-enviada.png` | Postulación confirmada |
| `flow-02-03-postulante-seleccionado.png` | Postulante en SELECCIONADA |
| `flow-02-04-historial-trabajador.png` | Historial actualizado |
| `flow-03-01-valoracion-publicada.png` | Valoración 5★ publicada |
| `flow-03-02-valoraciones-trabajador.png` | Reseña visible en el perfil |
| `flow-04-01-postulacion-pendiente.png` | Postulación PENDIENTE |
| `flow-04-02-postulacion-retirada.png` | Postulación RETIRADA |
| `flow-04-03-historial-retirada.png` | Historial con RETIRADA |
| `p1-admin-dashboard-metricas.png` | Dashboard de administración |
| `contact-sheet.png` | Hoja de contacto con las 12 capturas |

### 4.3 Evidencia de fallo deliberado (AUT-E2E-07)

En `results/fallo-deliberado/`:

| Evidencia | Archivo |
|---|---|
| Screenshot | `…/test-failed-1.png` |
| Video | `…/video.webm` |
| Trace interactivo | `…/trace.zip` |
| Reporte HTML del fallo | `html-report/index.html` |

El caso usa credenciales inválidas y luego exige haber llegado al dashboard: el
fallo es real, reproducible y deja traza completa.

### 4.4 Trazabilidad

| Evidencia | Archivo |
|---|---|
| Filas para la planilla (18 columnas) | `results/TRAZABILIDAD.tsv` |
| Versión legible | `results/TRAZABILIDAD.md` |

---

## 5. Trazabilidad resumida

| ID | Evidencia principal | Test automatizado |
|---|---|---|
| TC-PW-FLOW-01 | `results/capturas-flujos/flow-01-01-dashboard-trabajador.png` | `tests/flow-01-registro-login.spec.ts::TC-PW-FLOW-01` |
| TC-PW-FLOW-02 | `results/capturas-flujos/flow-02-03-postulante-seleccionado.png` | `tests/flow-02-publicar-postular-seleccionar.spec.ts::TC-PW-FLOW-02` |
| TC-PW-FLOW-03 | `results/capturas-flujos/flow-03-02-valoraciones-trabajador.png` | `tests/flow-03-valorar-historial.spec.ts::TC-PW-FLOW-03` |
| TC-PW-FLOW-04 | `results/capturas-flujos/flow-04-02-postulacion-retirada.png` | `tests/flow-04-retirar-postulacion.spec.ts::TC-PW-FLOW-04` |
| TC-E2E-LOGIN-01 | `results/html-report/index.html` | `tests/p1-casos-complementarios.spec.ts::TC-E2E-LOGIN-01` |
| TC-E2E-PROT-01 | `results/html-report/index.html` | `tests/p1-casos-complementarios.spec.ts::TC-E2E-PROT-01` |
| TC-E2E-ADMIN-01 | `results/capturas-flujos/p1-admin-dashboard-metricas.png` | `tests/p1-casos-complementarios.spec.ts::TC-E2E-ADMIN-01` |

La versión completa con las 18 columnas de la planilla está en
`results/TRAZABILIDAD.tsv`.

---

## 6. Cómo reproducir

### 6.1 Requisitos

- Node.js ≥ 20 y npm.
- Chromium de Playwright.
- PostgreSQL 18 con base `al_lado_e2e`.
- Backend Spring Boot (perfil `e2e`) en `http://localhost:8081`.
- Frontend Next.js compilado en `http://localhost:3001`.

### 6.2 Preparación

```bash
# Base
psql -U postgres -c "DROP DATABASE IF EXISTS al_lado_e2e;"
psql -U postgres -c "CREATE DATABASE al_lado_e2e;"

# Backend (Flyway crea el esquema)
cd Backend
java -jar target/al_lado_project-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=e2e \
  --spring.datasource.url=jdbc:postgresql://127.0.0.1:5432/al_lado_e2e \
  --spring.datasource.username=postgres \
  --spring.datasource.password="$DB_PASSWORD"

# Seed de cuentas y especialidades
psql -U postgres -d al_lado_e2e -f src/main/resources/seed-e2e.sql

# Frontend
cd ../Frontend/al_lado-app
NEXT_PUBLIC_API_URL=http://localhost:8081 npm run build
NEXT_PUBLIC_API_URL=http://localhost:8081 npx next start -p 3001
```

### 6.3 Ejecución

```bash
cd Testing/e2e-playwright
npm install
npx playwright install chromium

# Suite completa
E2E_BASE_URL=http://localhost:3001 E2E_API_URL=http://localhost:8081 npx playwright test

# Con capturas de los flujos en verde
$env:E2E_CAPTURAS="1"; npm run test:capturas

# Evidencia de fallo deliberado
npm run test:fallo

# Reporte HTML
npm run report
```

### 6.4 Cuentas sembradas

| Rol | Email | Contraseña |
|---|---|---|
| ADMIN | `admin@allado.com` | `Clave123!` |
| EMPLEADOR | `empleador@demo.com` | `Clave123!` |
| TRABAJADOR | `trabajador@demo.com` | `Clave123!` |

---

## 7. Estructura de la suite

```
Testing/e2e-playwright/
├── playwright.config.ts
├── playwright.evidencia.config.ts
├── src/
│   ├── pages/          13 page objects (POM)
│   └── support/        api.ts · env.ts · testData.ts
├── tests/              flow-01 … flow-04 + p1-casos-complementarios
├── evidencia/          fallo-deliberado.spec.ts
├── scripts/            generar_trazabilidad.py
└── results/            reportes, capturas, log, trazabilidad y fallo
```

---

## 8. Observaciones y pendientes

1. La suite quedó **sin commit**: `git status` muestra `?? Testing/e2e-playwright/`.
   Para que la evidencia quede atada al código, corresponde commit + PR revisado.
2. Las 7 filas de `TRAZABILIDAD.tsv` deben pegarse en
   `Testing/Planilla de Test Cases v2(2).xlsx` y vincularse en `Testing.md`.
3. El reporte HTML y la evidencia del fallo conviene publicarlos en el Drive o
   en los artifacts del pipeline, como pide la consigna.
4. Los casos P1 restantes (foro, favoritos, publicidad, portafolio, suscripción)
   son deseables y reutilizan los page objects existentes; no son bloqueantes.

---

*Fin del informe E2E — Sprint 4.*
