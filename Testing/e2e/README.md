# Suite E2E de AlLado (Selenium 4 + JUnit 5)

Pruebas end-to-end de la interfaz de AlLado: verifican los flujos reales del
usuario contra el frontend Next.js y el backend Spring Boot desplegados.

Ruta: `Testing/e2e`

---

## 1. Qué cubre

| Área | Casos | Identificadores |
|---|---|---|
| Autenticación | 13 | login, registro, cierre de sesión y guards por rol |
| Comunidad / foro | 4 | `TC-M-001` … `TC-M-003`, `TC-SEC-002` |
| Notificaciones | 1 | `TC-NOTIF-003` |
| Accesibilidad | 1 | `TC-ACC-001` (axe-core, WCAG 2.2 AA) |
| Oportunidades y postulación | 11 | `TC-COB-*`, `TC-NEG-*`, `TC-LIM-*` |
| Trabajador | 2 | perfil y rutas protegidas |
| **Total en código** | **32** | Último runtime: 30 casos, 28 PASS/2 SKIP; notificaciones y axe pendientes de runtime |

---

## 2. Estructura

```
Testing/e2e/
├── pom.xml
├── scripts/
│   ├── preparar_base_qa.py        Levanta la base de QA con el dump del proyecto
│   └── generar_informe_pruebas.py Genera el informe consolidado (PDF, XML, XLSX)
└── src/test/java/com/al_lado/e2e/
    ├── core/
    │   ├── E2EConfig.java          Configuración por variables de entorno
    │   ├── DriverFactory.java      Chrome / Firefox
    │   ├── EvidenceExtension.java  Ciclo de vida del navegador y evidencia
    │   ├── EvidenceCapture.java    Captura PNG + HTML + URL de cada caso
    │   ├── Waits.java              Esperas explícitas (nunca Thread.sleep)
    │   ├── ApiClient.java          Cliente HTTP para contrastar la UI con la API
    │   └── BaseE2ETest.java        Clase base con los atajos de autenticación
    ├── pages/                      Page objects (Login, Opportunities, Community…)
    └── tests/                      Casos agrupados por área, incluida notificaciones
```

**Convenciones**

- Page objects con esperas explícitas; los datos de prueba no están en el código.
- Cada caso declara **un** oráculo verificable (la URL de destino, el mensaje de
  error o el estado del botón).
- Las expectativas del listado y del foro se **derivan de la API**, no de un
  conjunto de datos fijo: los casos siguen siendo válidos si cambia el seed.
- Los casos que documentan un defecto abierto se marcan `@Disabled` con la
  referencia al caso de la planilla, para no tener la suite en rojo.
- `NotificationFlowTest` crea una oportunidad con UUID, envía una postulación y
  comprueba el aviso recibido por el empleador y la lectura masiva.
- `AccessibilityAxeTest` audita inicio, login y dashboard de trabajador con los
  tags `wcag22aa`; genera `target/accesibilidad/axe-report.md` y falla si axe
  encuentra violations. Requiere el runtime QA activo.

---

## 3. Requisitos

- **JDK 21** (el backend compila con `release 21`).
- **Maven 3.9+**.
- **Chrome** (o Firefox) con su driver. Selenium Manager resuelve el driver si
  está en el `PATH`; si el navegador es una instalación portátil, seindican las
  rutas por variable de entorno.
- **PostgreSQL** con la base de QA (el backend usa bloqueos pesimistas que H2 no
  soporta) y el **frontend** en modo producción o desarrollo.
- La clase E2E compila, pero el flujo `TC-NOTIF-003` requiere ejecutar la suite
  contra API, frontend y PostgreSQL QA activos para generar evidencia de runtime.
- `TC-ACC-001` está compilado, pero requiere navegador y entorno QA activos; no
  se considera ejecutado hasta generar un reporte axe con resultados reales.

---

## 4. Puesta en marcha del entorno

```bash
# 1) Base de QA: carga datos/al_lado_db.sql y fija las contrasenas de las cuentas de rol
python3 Testing/e2e/scripts/preparar_base_qa.py

# 2) Backend en el puerto 8081, contra la base de QA
cd Backend && java -jar target/al_lado_project-0.0.1-SNAPSHOT.jar --spring.profiles.active=e2e

# 3) Frontend en el puerto 3001, apuntando al backend de QA
cd Frontend/al_lado-app
NEXT_PUBLIC_API_URL=http://localhost:8081 npx next build
NEXT_PUBLIC_API_URL=http://localhost:8081 npx next start -p 3001
```

> `NEXT_PUBLIC_API_URL` se fija **al compilar**: Next.js la incrusta en el bundle
> del cliente.

### Si la base de QA está en el contenedor de PostgreSQL del proyecto

El contenedor `al_lado_db` (postgres:18, usuario `postgres`; la **contraseña se toma del
entorno**, nunca del repositorio: véase `Backend/.env.example`) publica
el puerto **5432**, mientras que el perfil `e2e` espera el 5433. Se resuelve por
variables de entorno, sin tocar el archivo de propiedades:

```bash
# Base QA (el script acepta el puerto por variable de entorno)
# DB_PASSWORD vive en el entorno: no se escribe en el repositorio (AL-SEC-002)
PGPASSWORD="$DB_PASSWORD" QA_PG_PORT=5432 python3 Testing/e2e/scripts/preparar_base_qa.py

# Backend contra esa base
E2E_DB_URL=jdbc:postgresql://localhost:5432/al_lado_db \
E2E_DB_USERNAME=postgres E2E_DB_PASSWORD="$DB_PASSWORD" \
java -jar Backend/target/al_lado_project-0.0.1-SNAPSHOT.jar --spring.profiles.active=e2e
```

| Variable del perfil `e2e` | Defecto | Uso |
|---|---|---|
| `E2E_DB_URL` | `jdbc:postgresql://localhost:5433/al_lado_db` | URL de la base de QA |
| `E2E_DB_USERNAME` | `postgres` | Usuario de la base de QA |
| `E2E_DB_PASSWORD` | *(vacío)* | Contraseña de la base de QA |
| `E2E_BACKEND_PORT` | `8081` | Puerto del backend de QA |

---

## 5. Ejecutar la suite

```bash
cd Testing/e2e

export E2E_BASE_URL=http://localhost:3001
export E2E_API_URL=http://localhost:8081
export E2E_BROWSER=chrome

mvn test                                   # toda la suite
mvn test -Dtest=LoginTest                  # una clase
mvn test -Dtest=LoginTest#loginEsValido    # un método
```

### Variables de entorno

| Variable | Defecto | Uso |
|---|---|---|
| `E2E_BASE_URL` | `http://localhost:3000` | Raíz del frontend |
| `E2E_API_URL` | `http://localhost:8080` | Backend Spring Boot |
| `E2E_BROWSER` | `chrome` | `chrome` o `firefox` |
| `E2E_HEADLESS` | `true` | `false` abre el navegador visible |
| `E2E_TIMEOUT` | `15` | Espera explícita máxima, en segundos |
| `E2E_CHROME_BINARY` | — | Ruta del ejecutable si no está en el `PATH` |
| `E2E_CHROMEDRIVER` | — | Ruta del driver si no está en el `PATH` |
| `E2E_WORKER_EMAIL` / `E2E_WORKER_PASSWORD` | `trabajador@demo.com` / `Clave123!` | Cuenta rol TRABAJADOR |
| `E2E_EMPLOYER_EMAIL` / `E2E_EMPLOYER_PASSWORD` | `empleador@demo.com` / `Clave123!` | Cuenta rol EMPLEADOR |
| `E2E_ADMIN_EMAIL` / `E2E_ADMIN_PASSWORD` | `admin@allado.com` / `Clave123!` | Cuenta rol ADMIN |
| `E2E_CAPTURE_ONLY_FAILURES` | `false` | `true` limita la evidencia a los casos que fallan |
| `E2E_EVIDENCE_DIR` | `target/e2e-evidencias` | Destino de las capturas |

---

## 6. Evidencia

Cada caso deja en `target/e2e-evidencias/<Clase>.<metodo>/`:

| Archivo | Contenido |
|---|---|
| `captura.png` | Captura de pantalla en el momento de la ejecución |
| `pagina.html` | HTML de la página (para inspeccionar el DOM) |
| `url.txt` | URL final |
| `paso.txt` | Nombre del caso |

La captura se genera **siempre**, tanto si el caso pasa como si falla. El último
runtime guardado fue del 22/10: 30 casos, 28 capturas y 2 omitidos. El código
actual tiene 31 métodos; `TC-NOTIF-003` se agregó después y está compilado, pero
pendiente de ejecución. Al correr los 31 actuales se esperan 29 carpetas con
captura si los dos casos deshabilitados continúan omitidos.

Para las corridas rápidas de diagnóstico se puede limitar la evidencia a los
fallos:

```bash
E2E_CAPTURE_ONLY_FAILURES=true mvn test
```

---

## 6.1 Navegador: una instancia de Chrome por clase

`EvidenceExtension` abre **un solo Chrome por clase de prueba** y lo reutiliza
para todos sus casos; entre casos borra las cookies y el almacenamiento local
para que cada uno arranque en un estado limpio. Al terminar la clase lo cierra.

| Estrategia | Lanzamientos de Chrome | Tiempo de la suite |
|---|---|---|
| Un navegador por caso (anterior) | 30 | 1 min 58 s |
| **Un navegador por clase (actual)** | **8** | **1 min 05 s** |

Además hay un *shutdown hook* que cierra el navegador aunque la corrida se
interrumpa, de modo que no quedan procesos de Chrome huérfanos.

---

## 7. Informe consolidado

```bash
python3 Testing/e2e/scripts/generar_evidencias.py    # primero las evidencias
python3 Testing/e2e/scripts/generar_informe_pruebas.py # despues el informe
```

El orden importa: `generar_evidencias.py` produce las capturas de API, base de
datos y backend; `generar_informe_pruebas.py` las vincula a cada caso y arma los
tres entregables.

Genera en `/home/Alfredo/Documentos/AlLado/plan_de_pruebas/`:

- `AlLado - Informe de Resultados de Pruebas.pdf` — incluye un **anexo con una
  página por caso y su captura a tamaño completo**.
- `AlLado - Informe de Resultados de Pruebas.xml` — incluye la ruta de la
  evidencia en cada `testcase`.
- `AlLado - Informe de Resultados de Pruebas.xlsx` — la **captura va incrustada
  dentro de la celda "Evidencia"** de cada fila.
- `evidencias/` — las capturas del último consolidado, para que la ruta de la celda resuelva desde
  la carpeta del informe.

### De dónde sale cada evidencia

| Prefijo de la carpeta | Origen | Qué muestra la captura |
|---|---|---|
| `<Clase>.<metodo>` | Suite E2E (Selenium) | La pantalla de la aplicación en el momento del caso |
| `API-<TC-...>` | Planilla, casos de API | El intercambio HTTP real: método, URL, cabeceras, cuerpo, código de respuesta y respuesta |
| `DB-<TC-...>` | Planilla, casos de base de datos | La cadena de llamadas (alta + borrado) y la comprobación posterior |
| `BACK-BE-...` | Suite de backend (JUnit + Mockito) | Ficha del caso: clase, método, estado, duración y referencia al reporte de Surefire |

Los casos de API y base de datos **se re-ejecutan de verdad** contra el backend
de QA antes de capturar: la evidencia no es una maqueta, es el resultado
observado en esa corrida. Por eso cada caso que necesita un estado limpio (una
oportunidad sin postular, por ejemplo) crea su propia oportunidad y lo deja
constar en la nota de ejecución de la ficha.

Consolida esta suite, la suite de backend del proyecto y los casos de la
planilla oficial, separados por tipo de prueba y categorizados por área técnica.

---

## 8. Relación con las otras suites

| Suite | Ruta | Qué cubre |
|---|---|---|
| Backend | `Backend/src/test/java/...` | Reglas de negocio, autorización y contrato HTTP (JUnit 5 + Mockito) |
| Frontend E2E | `Testing/e2e` | Flujos de usuario en navegador (Selenium 4 + JUnit 5) |
| API / BD | Colección Postman `AlLado-QA` | Contrato de endpoints y verificación SQL |

Guía de la suite de backend: `Testing/GUIA-PRUEBAS-BACKEND.md`.
