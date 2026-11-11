# Suite E2E de AlLado — Playwright Test + TypeScript + POM

Suite de pruebas de punta a punta de la aplicación web de AlLado. Cubre los
**3 flujos críticos CRUD** exigidos por la consigna del Sprint 4 más casos
complementarios de autenticación, autorización y administración.

- **Ruta:** `Testing/e2e-playwright/`
- **Stack:** Playwright Test 1.64.1 · TypeScript · Page Object Model
- **Navegador:** Chromium (desktop)
- **Responsable:** Julio Lasa (Capa Cúmula)
- **Evidencias:** reporte HTML, JUnit, JSON + screenshot, video y trace por fallo

---

## 1. Cobertura

### 1.1 Flujos críticos (P0)

| Caso | RF / HU | Archivo de prueba | Qué valida |
|---|---|---|---|
| **TC-PW-FLOW-01** | RF-01/11 · HU-04 | `tests/flow-01-registro-login.spec.ts` | Registro → Login → Dashboard del trabajador → Logout → ruta protegida |
| **TC-PW-FLOW-02** | RF-04/15 · HU-01/02 | `tests/flow-02-publicar-postular-seleccionar.spec.ts` | Empleador publica → trabajador postula → empleador selecciona → historial del trabajador |
| **TC-PW-FLOW-03** | RF-05/08 · HU-06 | `tests/flow-03-valorar-historial.spec.ts` | Trabajo completado → valoración del empleador → historial y valoraciones del trabajador |
| **TC-PW-FLOW-04** | RF-04 · HU-02 | `tests/flow-04-retirar-postulacion.spec.ts` | **Delete:** el trabajador retira su postulación → estado RETIRADA |

### 1.2 Casos complementarios (P1)

| Caso | RF / HU | Archivo | Qué valida |
|---|---|---|---|
| **TC-E2E-LOGIN-01** | RF-01/11 · HU-04 | `tests/p1-casos-complementarios.spec.ts` | Login con cuenta sembrada → dashboard → logout |
| **TC-E2E-PROT-01** | RF-01/11 · HU-04 | `tests/p1-casos-complementarios.spec.ts` | Ruta protegida sin sesión y separación de roles (trabajador ≠ admin) |
| **TC-E2E-ADMIN-01** | RF-19 · HU-18/19 | `tests/p1-casos-complementarios.spec.ts` | Acceso de administración y métricas del dashboard |

Los casos P1 restantes de la planilla (foro, favoritos, publicidad, portafolio,
suscripción, etc.) quedan listados como deseables y se apoyan en los mismos page
objects; se priorizó la pirámide: la lógica ya está cubierta por backend y API.

### 1.3 Evidencia de fallo deliberado

`evidencia/fallo-deliberado.spec.ts` fuerza un fallo real (credenciales inválidas
contra una aserción incorrecta) y genera screenshot, video y trace. No se ejecuta
en la corrida normal: usa `playwright.evidencia.config.ts`.

---

## 2. Estructura

```
Testing/e2e-playwright/
├── package.json
├── playwright.config.ts              Configuración de la corrida normal
├── playwright.evidencia.config.ts    Configuración del fallo deliberado
├── tsconfig.json
├── src/
│   ├── pages/                        Page Object Model (13 clases)
│   │   ├── BasePage.ts
│   │   ├── LoginPage.ts
│   │   ├── RegisterPage.ts
│   │   ├── WorkerDashboardPage.ts
│   │   ├── OpportunitiesPage.ts
│   │   ├── OpportunityDetailPage.ts
│   │   ├── EmployerNewRequestPage.ts
│   │   ├── RequestDetailPage.ts
│   │   ├── CandidatesPage.ts
│   │   ├── RatingPage.ts
│   │   ├── WorkerHistoryPage.ts
│   │   ├── WorkerRatingsPage.ts
│   │   ├── WorkerApplicationDetailPage.ts
│   │   └── AdminDashboardPage.ts
│   └── support/
│       ├── api.ts                    Precondiciones por API (cuentas y datos)
│       ├── env.ts                    Variables de entorno
│       └── testData.ts               Datos únicos por corrida
├── tests/                            Flujos críticos y casos P1
├── evidencia/                        Fallo deliberado (AUT-E2E-07)
├── scripts/
│   └── generar_trazabilidad.py       Genera TRAZABILIDAD.tsv / .md
└── results/                          Reportes, capturas y evidencias versionadas
```

**Convenciones**

- Cada caso declara un oráculo visible (URL, encabezado o mensaje de estado).
- Los page objects encapsulan selectores y esperas; los tests no usan CSS suelto.
- Las cuentas y publicaciones de precondición se crean por API con datos únicos,
  de modo que la suite no dependa de datos acumulados de corridas anteriores.
- El flujo que se valida se ejecuta siempre por la interfaz.

---

## 3. Requisitos

- Node.js ≥ 20 y npm.
- Chromium de Playwright (`npx playwright install chromium`).
- PostgreSQL con una base **exclusiva de QA** (no usar la base de desarrollo).
- Backend Spring Boot con el perfil `e2e`, apuntando a esa base.
- Frontend Next.js compilado con `NEXT_PUBLIC_API_URL` apuntando al backend QA.

> La suite crea usuarios reales (`@allado.test`) y publicaciones reales en la base
> de QA. Por eso la base debe ser descartable.

---

## 4. Puesta en marcha del entorno

### 4.1 Base de QA

```bash
# Base exclusiva para la suite (se recrea en cada preparación)
psql -U postgres -c "DROP DATABASE IF EXISTS al_lado_e2e;"
psql -U postgres -c "CREATE DATABASE al_lado_e2e;"
```

El esquema lo crea **Flyway** (`Backend/src/main/resources/db/migration/V1__baseline.sql`)
al arrancar el backend. El seed de cuentas y especialidades se carga después con
`Backend/src/main/resources/seed-e2e.sql`.

### 4.2 Backend

```bash
cd Backend
java -jar target/al_lado_project-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=e2e \
  --spring.datasource.url=jdbc:postgresql://127.0.0.1:5432/al_lado_e2e \
  --spring.datasource.username=postgres \
  --spring.datasource.password="$DB_PASSWORD" \
  --spring.sql.init.mode=always \
  --spring.sql.init.data-locations=classpath:seed-e2e.sql \
  --spring.jpa.defer-datasource-initialization=true
```

El backend queda en `http://localhost:8081`. Las cuentas sembradas usan la
contraseña `Clave123!`:

| Rol | Email |
|---|---|
| ADMIN | `admin@allado.com` |
| EMPLEADOR | `empleador@demo.com` |
| TRABAJADOR | `trabajador@demo.com` |

### 4.3 Frontend

```bash
cd Frontend/al_lado-app
npm install
NEXT_PUBLIC_API_URL=http://localhost:8081 npm run build
NEXT_PUBLIC_API_URL=http://localhost:8081 npx next start -p 3001
```

`NEXT_PUBLIC_API_URL` se incrusta **al compilar**: si cambia la URL del backend,
hay que recompilar.

---

## 5. Ejecución

```bash
cd Testing/e2e-playwright
npm install
npx playwright install chromium

# Toda la suite
npm test

# Solo los 3 flujos críticos
npm run test:flow

# Un caso puntual
npx playwright test tests/flow-02-publicar-postular-seleccionar.spec.ts

# Con navegador visible
npm run test:headed

# Capturas de los flujos en verde + log de la corrida
$env:E2E_CAPTURAS="1"; npm run test:capturas

# Abrir el reporte HTML
npm run report
```

### Variables de entorno

| Variable | Valor por defecto | Uso |
|---|---|---|
| `E2E_BASE_URL` | `http://localhost:3001` | URL del frontend |
| `E2E_API_URL` | `http://localhost:8081` | URL del backend (precondiciones) |
| `E2E_ADMIN_EMAIL` | `admin@allado.com` | Cuenta administradora sembrada |
| `E2E_ADMIN_PASSWORD` | `Clave123!` | Contraseña de la cuenta administradora |
| `E2E_EMPLOYER_EMAIL` | `empleador@demo.com` | Cuenta empleador sembrada |
| `E2E_WORKER_EMAIL` | `trabajador@demo.com` | Cuenta trabajador sembrada |
| `E2E_DEFAULT_PASSWORD` | `Clave123!` | Contraseña de las cuentas sembradas |

---

## 6. Reportes y evidencias

Cada corrida deja en `results/`:

| Artefacto | Ruta | Contenido |
|---|---|---|
| Reporte HTML | `results/html-report/index.html` | Resultado interactivo por caso y paso |
| JUnit XML | `results/junit.xml` | Integrable con CI |
| JSON | `results/results.json` | Datos crudos de la corrida |
| Traces | `results/test-results/**/trace.zip` | Solo en fallos |
| Screenshots | `results/test-results/**/*.png` | Solo en fallos |
| Videos | `results/test-results/**/*.webm` | Solo en fallos |
| Capturas por flujo | `results/capturas-flujos/*.png` | 12 hitos de los flujos en verde (con `E2E_CAPTURAS=1`) |
| Log de la corrida | `results/ejecucion.log` | Salida de consola completa |
| Trazabilidad | `results/TRAZABILIDAD.tsv` / `.md` | Filas para la planilla |

### 6.1 Capturas de los flujos en verde

```bash
$env:E2E_CAPTURAS="1"
npm run test:capturas
```

Genera en `results/capturas-flujos/` una captura por hito:

| Archivo | Momento |
|---|---|
| `flow-01-01-dashboard-trabajador.png` | Dashboard tras registro y login |
| `flow-01-02-ruta-protegida-redirige.png` | Ruta protegida redirigiendo al login |
| `flow-02-01-solicitud-publicada.png` | Solicitud publicada por el empleador |
| `flow-02-02-postulacion-enviada.png` | Postulación confirmada |
| `flow-02-03-postulante-seleccionado.png` | Postulante en estado SELECCIONADA |
| `flow-02-04-historial-trabajador.png` | Historial del trabajador actualizado |
| `flow-03-01-valoracion-publicada.png` | Valoración publicada por el empleador |
| `flow-03-02-valoraciones-trabajador.png` | Reseña visible en el perfil del trabajador |
| `flow-04-01-postulacion-pendiente.png` | Postulación en estado PENDIENTE |
| `flow-04-02-postulacion-retirada.png` | Postulación retirada (RETIRADA) |
| `flow-04-03-historial-retirada.png` | Historial con el estado RETIRADA |
| `p1-admin-dashboard-metricas.png` | Dashboard de administración |
| `contact-sheet.png` | Hoja de contacto con las 12 capturas anteriores |

### 6.2 Trazabilidad para la planilla

```bash
python scripts/generar_trazabilidad.py
```

`results/TRAZABILIDAD.tsv` trae 7 filas con las **18 columnas de la planilla**
(`ID Caso`, trazabilidad HU/CA, tipo, técnica, precondiciones, datos, pasos,
resultado esperado, prioridad, resultado real, estado, evidencia, etiquetas, capa,
automatizable, justificación y herramienta/ruta del test). Se pega directamente
en `Testing/Planilla de Test Cases v2(2).xlsx`.

### Fallo deliberado (AUT-E2E-07)

```bash
npm run test:fallo
```

Genera `results/fallo-deliberado/` con `trace.zip`, video y screenshot del fallo,
más su propio reporte HTML en `results/fallo-deliberado/html-report/`.

---

## 7. Resultado de la última ejecución

Corrida del 10/11/2026 contra `http://localhost:3001` (frontend) y
`http://localhost:8081` (backend QA con PostgreSQL):

```
Running 7 tests using 1 worker
  7 passed (12.1s)
```

| Caso | Resultado | Duración |
|---|---|---|
| TC-PW-FLOW-01 | ✅ passed | 1.3 s |
| TC-PW-FLOW-02 | ✅ passed | 3.5 s |
| TC-PW-FLOW-03 | ✅ passed | 1.8 s |
| TC-PW-FLOW-04 | ✅ passed | 1.3 s |
| TC-E2E-LOGIN-01 | ✅ passed | 1.0 s |
| TC-E2E-PROT-01 | ✅ passed | 0.8 s |
| TC-E2E-ADMIN-01 | ✅ passed | 1.5 s |

Artefactos versionados en este repositorio:

- `results/html-report/index.html` — reporte HTML de la corrida.
- `results/junit.xml` y `results/results.json` — resultados para CI.
- `results/ejecucion.log` — log completo de la corrida.
- `results/capturas-flujos/` — 12 capturas de los flujos en verde y su hoja de contacto.
- `results/TRAZABILIDAD.tsv` / `.md` — filas para la planilla.
- `results/fallo-deliberado/` — screenshot, video, trace y reporte del fallo
  deliberado (AUT-E2E-07).

> `results/test-results/` (artefactos intermedios de cada caso) queda fuera de
> Git; se regenera en cada corrida.

---

## 8. Trazabilidad

| Requisito de la consigna | Dónde se cumple |
|---|---|
| Suite E2E con Playwright y POM | `playwright.config.ts`, `src/pages/` |
| Al menos 3 flujos críticos CRUD | `tests/flow-01/02/03` (+ `flow-04` para el Delete) |
| Reporte de ejecución | `results/html-report/` + JUnit + JSON |
| Evidencia de al menos un fallo | `evidencia/` + `results/fallo-deliberado/` |
| Capturas de los flujos en verde | `results/capturas-flujos/` |
| Trazabilidad en la planilla | `results/TRAZABILIDAD.tsv` (18 columnas) |
| Casos negativos visibles | TC-E2E-PROT-01 (ruta protegida y rol), validaciones del registro |

---

## 9. Relación con las otras suites

| Suite | Ruta | Qué cubre |
|---|---|---|
| Backend | `Backend/src/test/java/...` | Reglas de negocio, seguridad y contrato (458 tests) |
| API | `Postman_Collection.json` + `Postman_Environment_QA.json` | Contrato HTTP y negativos |
| Frontend unitarias | `Frontend/al_lado-app/src/**/*.test.*` | Componentes React aislados |
| **E2E** | `Testing/e2e-playwright/` | Flujos visibles de punta a punta |
| Accesibilidad | `Testing/Accesibilidad/` | WCAG 2.2 AA (axe-core, Lighthouse, manual) |

La suite E2E **no re-prueba** reglas de negocio ni contrato HTTP: solo verifica
que el flujo visible del usuario funcione sobre la aplicación real.
