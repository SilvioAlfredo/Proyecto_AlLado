# Testing — Plan de Pruebas y Trazabilidad (IEEE 829)

> **Responde a:** Devolución Plan de Pruebas y Test Cases — AlLado Grupo D.
> Complementa a `Issues_Backlog.md`. Los casos manuales existentes se conservan;
> los resultados automatizados se registran por suite y los resultados de la
> planilla manual solo se marcan ejecutados con evidencia de esa corrida.

## 1. Unificación de IDs (trazabilidad)

- La devolución observó doble numeración (plan TEST-D-101…505 vs. planilla TEST-G/L/D/DB/M-00x).
- **Regla desde ahora:** ID único **`TC-<área>-<nnn>`** en plan, planilla, código Postman y bugs.
  - Equivalencia: `TEST-G-00x → TC-G-00x`, `TEST-L-00x → TC-L-00x`, etc.
  - Los casos nuevos usan el correlativo siguiente (`TC-SEC-001`, `TC-LIM-001`, …).
- Prohibidos los resultados con "o" (`401/404`, `400/409`, "impide… o maneja…").
  Cada caso fija **un** oráculo verificable (código HTTP exacto + estado en BD).

## 1.1 Inventario de suites y última validación

| Suite | Casos en código | Última validación | Resultado / límite |
|---|---:|---|---|
| Backend (JUnit/Mockito) | 218 | 29/10/2026 | 218 aprobados; incluye contexto H2 y autorización de notificaciones |
| Frontend web (Vitest/RTL) | 45 | 29/10/2026 | 45 aprobados; `npm test` |
| Frontend móvil (Jest/RNTL) | 27 | 29/10/2026 | 27 aprobados; `npm test -- --runInBand`; la suite previa de sesión aún emite warnings `act` |
| E2E web (Selenium + axe) | 32 en código | último runtime 22/10/2026 | 30 resultados: 28 PASS, 2 SKIP; `TC-NOTIF-003` y `TC-ACC-001` compilan, pero no se han ejecutado |
| Postman API | 41 requests / 31 casos API+BD | 29/10/2026 | ejecución manual registrada: 29 PASS, 2 FAIL; colección y environment versionados; sin reporte Newman |

**Informe consolidado actual:** 351 casos, 347 PASS, 2 FAIL, 0 ERROR y 2 SKIP
(29/10/2026). La planilla contiene los mismos IDs y estados. Los FAIL/SKIP están
ligados a defectos abiertos. La aprobación es 347/349 (99,4 %) de los ejecutados;
los SKIP no se cuentan como aprobados. Este corte no equivale al cierre de QA.

## 2. Criterios de entrada, salida, suspensión y reanudación

### Entrada
- Build backend + frontend compila; migraciones aplicadas; seed de QA cargado.
- Contrato API (OpenAPI) publicado para los endpoints bajo prueba.
- Datos de prueba definidos por caso (ver §7).

### Salida (aceptación del ciclo)
- 100 % de casos críticos (prioridad Crítica) ejecutados.
- 0 defectos bloqueantes abiertos.
- ≥ 95 % de casos aprobados; ningún P0/P1 abierto sin plan.
- Evidencia linkeada por caso (captura Postman + link a Issue si falla).

### Suspensión
- Defecto bloqueante que impide > 30 % de los casos pendientes.
- Caída del entorno (BD/API/front) o contrato API incompatible con la colección Postman.

### Reanudación
- Fix verificado en rama QA + re-ejecución de los casos bloqueados.
- Registro en el Issue correspondiente antes de retomar.

## 3. Umbrales de rendimiento (trasladados de las HU al plan)

La devolución observó que el plan mencionaba JMeter sin umbrales. Umbrales vigentes:

| HU | Umbral | Caso que lo verifica |
|---|---|---|
| HU-07 CA-01 | Búsqueda filtrada < 500 ms | TC-PERF-001 |
| HU-09 CA-01 | Alerta push/WhatsApp < 30 s | TC-PERF-002 |
| HU-04 CA-02 | SMS con token < 60 s (mock Twilio Sandbox) | TC-PERF-003 |
| HU-01 CA-01 | Persistencia de tarea < 1 s | TC-PERF-004 |

## 4. Riesgos (probabilidad × impacto + mitigación)

| Riesgo | Prob. | Impacto | Mitigación |
|---|---|---|---|
| Mock Twilio figura como "(pendiente)" | Alta | Alto | Congelar contrato del mock esta iteración; si no está, TC-PERF-003 se ejecuta contra stub local y se reporta como riesgo abierto |
| JWT secret / password BD hardcodeados (AL-SEC-001/002) | Alta | Crítico | Variables de entorno antes del ciclo; verificar con `grep` que no hay secretos en repo |
| Contratos Front↔Back divergentes (AL-API-001…004) | Alta | Alto | OpenAPI como fuente de verdad (AL-DOC-002); colección Postman versionada |
| Sin paginación (`findAll` en listados) | Media | Medio | Casos de volumen moderado; medir tiempos y reportar bug si > umbral |
| `ddl-auto=update` (AL-DB-001) | Media | Alto | Migraciones Flyway/Liquibase; seed `seed_tc_db_005.sql` versionado |
| Metadata `.hg` / artefactos en entrega (AL-CLEAN-001/002) | Media | Bajo | Limpiar antes de evidencias; `.gitignore` verificado |

## 5. Recursos, responsabilidades y cronograma

| Persona | Rol QA | Responsabilidad |
|---|---|---|
| Riveros Silvio | Líder proyecto / QA lead | Aprueba salida del ciclo, gestiona Issues P0 |
| Madera Chiara | Tester funcional | TC-G, TC-L, TC-SEC (autorización) |
| Lasa Julio | Tester API/BD | TC-D, TC-DB, TC-PERF |
| Gonzalez Williams | Tester comunidad/seguridad | TC-M, TC-LIM (valores límite), evidencias |

**Cronograma propuesto (2 semanas):**

| Semana | Actividad | Entregable |
|---|---|---|
| S1 d1–d2 | Cierre Sprint 0 seguridad + seeds y colección Postman | Entorno listo (criterios de entrada) |
| S1 d3–d5 | Ejecución TC-G/TC-L/TC-D + autorización | Planilla con Estado + evidencias |
| S2 d1–d3 | TC-DB/TC-M/nuevos (cobertura §6) + perf | Issues reportados con plantilla §8 |
| S2 d4–d5 | Re-test, matriz trazabilidad, informe final | Este doc + planilla completa |

**Entregables IEEE 829:** este plan, planilla de casos, logs de ejecución,
reportes de incidencias (GitHub Issues), evidencias (capturas/colección Postman).

## 6. Matriz de trazabilidad HU → CA → TC → Bug

La matriz original de devolución se conserva como punto de partida. La cobertura
automatizada fue ampliada desde esa revisión; los casos siguen separados entre
implementados, omitidos por defecto conocido y pendientes de ejecución real.

| HU / CA | RF | TC (existente o nuevo) | Bug |
|---|---|---|---|
| HU-01 CA-02 (3 publicaciones/mes freemium) | RF-13 | **TC-LIM-001** (3.ª OK / 4.ª bloqueada + modal Pro) — nuevo | (al ejecutar) |
| HU-02 CA-02 (exclusividad Pro 15 min) | RF-13 | **TC-LIM-002** (postulación 14:59 bloqueada / 15:00 permitida) — nuevo | (al ejecutar) |
| HU-03 CA-01 (perfil trabajador) | RF-02 | **TC-COB-001** (GET perfil: foto/bio/certificados; 404 si no existe) — nuevo | (al ejecutar) |
| HU-05 CA-01 (perfil empleador, oculta contacto) | RF-03 | TC-G-001 (extender: verificar teléfono enmascarado) | (al ejecutar) |
| HU-10 CA-01 (no disponible fuera de búsqueda) | RF-02 | **TC-COB-002** (trabajador no disponible no aparece en búsqueda) — nuevo | (al ejecutar) |
| HU-07 CA-01 (filtros < 500 ms) | RF-06 | **TC-PERF-001** + **TC-COB-003** (filtro Electricidad + ★ mín.) — nuevo | (al ejecutar) |
| HU-09 CA-01 (alerta < 30 s) | RF-11/RF-12 | **TC-PERF-002** + **TC-COB-004** (preferencia electricidad+zona dispara alerta) — nuevo | (al ejecutar) |
| HU-12 CA-01/CA-02 (métricas + candado Pro) | RF-12* | **TC-COB-005** (totales = SUM/COUNT; básico ve candado) — nuevo | (al ejecutar) |
| HU-04 CA-01 (pass 7 vs 8 caracteres) | RF-01 | **TC-LIM-003** (7 rechaza / 8 acepta) — nuevo | (al ejecutar) |
| HU-06 CA-01 (reseña 0/1/5/6 ★, inmutable) | RF-05 | **TC-LIM-004** (0 y 6 rechazan; PUT/DELETE reseña → 403/405) — nuevo | (al ejecutar) |
| HU-13 CA-02 (foro solo mismo barrio) | RF-13 | **TC-SEC-002** (usuario otro barrio no ve/lee hilo) — nuevo; TC-M-003 se extiende | (al ejecutar) |
| Autorización (no solo autenticación) | RF-10 | **TC-SEC-001** (`ROLE_TRABAJADOR → /admin/** = 403`) — nuevo | (al ejecutar) |
| Negativos gestión datos (hoja D toda happy path) | RF-04 | **TC-NEG-001** editar oportunidad ajena → 403; **TC-NEG-002** postularse a cerrada → 409; **TC-NEG-003** doble postulación → 409; **TC-NEG-004** remuneración ≤ 0 → 400 — nuevos | (al ejecutar) |
| HU-08 CA-01 (historial inalterable) | RF-08 | TC-DB-005 corregido (ver §7 ejemplo; borrado lógico, 6 puntos) | (al ejecutar) |
| HU-09 (notificación por postulación) | RF-11/RF-12* | **TC-NOTIF-001** lectura individual (200); **TC-NOTIF-002** lectura masiva (204 + contador 0); **TC-NOTIF-003** postulación crea aviso al empleador | AL-FE-008 / AL-TEST-005 |
| Notificaciones: listado y contador del titular | RF-11/RF-12* | **TC-NOTIF-004** listado 200; **TC-NOTIF-005** contador numérico; **TC-NOTIF-006** decremento exacto tras leer una; **TC-NOTIF-007** cero tras lectura masiva | AL-FE-008 |
| Aislamiento de notificaciones | RF-10/RF-11 | **TC-SEC-004** listado ajeno → 403; **TC-SEC-005** lectura de notificación ajena → 403 | AL-TEST-001 |
| Accesibilidad WCAG 2.2 AA | Transversal | **TC-ACC-001** axe-core sobre inicio, login y dashboard de trabajador; reporte `Testing/e2e/target/accesibilidad/axe-report.md` | AL-FE-009/010 |

### Estado de implementación por suite (29/10/2026)

| TC | Backend | Web | Móvil | E2E | Postman |
|---|---|---|---|---|---|
| TC-NOTIF-001 | Servicio: titular puede marcar leída | Lista: marca una y oculta la acción | Lista: marca una y oculta la acción | — | PATCH devuelve 200 y `leida=true` |
| TC-NOTIF-002 | Servicio: lectura masiva del titular | Lista: marca todas | Lista: marca todas | — | PATCH devuelve 204; verificación posterior exige contador 0 |
| TC-NOTIF-003 | — | — | — | Postulación → aviso al empleador → lectura masiva; compilado, pendiente de runtime | Requiere aviso pendiente |
| TC-NOTIF-004/005/006/007 | Conteo y propiedad verificados en servicio | — | — | — | Listado, conteo, decremento y cero con respuestas exactas |
| TC-SEC-004/005 | Listado y lectura de tercero rechazados | — | — | — | GET de usuario ajeno y PATCH de notificación ajena exigen 403 |
| TC-ACC-001 | — | — | — | axe-core sobre 3 pantallas; compilado y pendiente de runtime | — |

AL-FE-008 sigue abierto: los tests de lista verifican el estado local tras leer,
pero no verifican que el contador de la barra web se invalide en todas las rutas;
el móvil tampoco expone un contador global. No se considera corregido por estos
casos. Los casos API/BD ya tienen resultados manuales de Postman en la planilla;
AL-TEST-005 sigue abierto hasta ejecutar TC-NOTIF-003 contra QA y adjuntar su
evidencia E2E.

`TC-ACC-001` está integrado a Selenium con axe 4.13.0 y filtra tags WCAG 2.2 AA.
Genera un Markdown con violations, impacto, URL, target y HTML. La última
validación fue compilación solamente: frontend y API QA no estaban levantados,
por lo que no se declara resultado ni se genera un informe axe vacío.

\* Nota: el doc de RF define RF-11 como redes sociales y RF-12 como notificaciones;
la devolución los cita como Notificaciones/Dashboard. Se mantiene el mapeo HU→TC
que es lo auditable; revisar numeración RF en `Requerimientos Funcionales.md`.

## 7. Correcciones a casos observados (datos concretos, sin ambigüedad)

- **Datos de entrada obligatorios:** todo TC indica valores exactos
  (ej. TC-L-001: `trabajador@test.com / Clave123!`; TC-L-003: email nuevo
  `nuevo.trabajador@test.com`; TC-D-001: título/descripción/etiqueta/coordenadas fijas).
- **Precondiciones reproducibles:** con IDs y seed
  (ej. TC-M-001: reporte sobre `oportunidad id=10`; TC-DB-002: `trabajador id=3`,
  `empleador id=2`, trabajo en estado final definido por seed).
- **Sin "o":** TC-L-004 fija `409` para email duplicado; TC-G-004 fija `401`;
  TC-M-002 obtiene `moderadorId` del token (no de la URL — posible vulnerabilidad
  señalada en la devolución).
- **TC-DB-005 corregido:** aplica el formato de la devolución (título, trazabilidad
  RF-03·RF-04·HU-08 CA-01, precondiciones con `seed_tc_db_005.sql`,
  `empleador id=2`, 6 puntos de oráculo, `204 + activo=false + COUNT=2 +
  postulación CANCELADA + sin ConstraintViolationException`). Si el equipo no
  decidió borrado lógico vs `409`, se reporta como **defecto del requerimiento**,
  no se escribe "o".

### Entorno reproducible de TC-DB-005

El seed quedó versionado en `datos/seed_tc_db_005.sql`, y el esquema vigente en
`datos/schema_al_lado_db.sql`. `al_lado_db.sql` sigue en el repo como foto del
09/10/2026, pero no sirve para levantar el entorno: le faltan columnas que las
entidades ya declaran (`latitud`, `longitud`, `dia_semana_requerido`,
`hora_inicio_requerida`, `hora_fin_requerida`, `email_verification_token`).

```bash
createdb al_lado_tc
psql -d al_lado_tc -f datos/schema_al_lado_db.sql
psql -d al_lado_tc -f datos/seed_tc_db_005.sql
```

El seed deja al `empleador id=2` como dueño de la oferta `id=1`, con 2
postulaciones `PENDIENTE` de los trabajadores `id=3` e `id=4`. Las tres cuentas
usan `Clave123!`. Es idempotente: se puede volver a correr sin limpiar la base.

Dos hallazgos al armarlo, que se reportan como defecto en lugar de ajustar el
oráculo:

- **Estados:** el oráculo pide la postulación en `CANCELADA`, pero el modelo solo
  define `PENDIENTE | SELECCIONADA | RECHAZADA | COMPLETADA | RETIRADA`.
- **Borrado:** `OportunidadLaboralServiceImpl.eliminar` hace `DELETE` físico y
  responde `BusinessRuleException` ("No se puede eliminar la oferta porque tiene
  postulaciones asociadas") cuando la clave foránea lo impide. Con la
  precondición de 2 postulaciones el caso espera `204`, así que la ejecución va a
  fallar contra el código actual.

## 8. Gestión de defectos

- **Herramienta:** GitHub Issues ( Kanban para tratamiento; link del Issue en la
  columna Evidencia de cada TC fallido).
- **Severidad:** Bloqueante / Alta / Media / Baja.
- **Prioridad:** P0 / P1 / P2 / P3 (misma escala que `Issues_Backlog.md`).
- **Ciclo de vida:** Abierto → En análisis → En fix → En re-test → Cerrado
  (reabre si el re-test falla).
- **Plantilla de reporte:**

```text
Título: [TC-XXX-nnn] resumen
TC / HU-CA / RF:
Severidad / Prioridad:
Entorno (branch, seed, colección Postman):
Pasos:
Datos de entrada:
Resultado esperado:
Resultado real:
Evidencia (captura/log):
```

## 9. Evidencias (qué linkear por caso)

- Captura Postman (request + status + body) en la colección `AlLado-QA`.
- Query SQL de verificación (`SELECT activo ...`, `COUNT(*) ...`) donde aplique.
- Screenshot UI para TC-G/TC-L/TC-M.
- Link al Issue si el caso falla; link al re-test al cerrar.
- Corte 29/10/2026: 351 filas con resultado y evidencia; 347 PASS, 2 FAIL y 2
  SKIP. Los casos incorporados después de la corrida deben quedar señalados como
  pendientes, sin heredar el resultado de una corrida previa.

## 10. Ejecución de notificaciones en Postman

Importar `Postman_Collection.json` y `Postman_Environment_QA.json`; seleccionar
`AlLado QA PostgreSQL` y ejecutar `Comunidad / Notificaciones` en orden, contra
una base QA aislada:

1. `authToken` y `userId`: token e ID del empleador titular.
2. `otroUsuarioId`: ID de otro usuario autenticado, distinto del titular.
3. `notificacionAjenaId`: notificación que pertenece a un tercer usuario.
4. Debe existir al menos una notificación pendiente del titular, generada por
  una postulación creada durante la preparación del caso.

La primera petición elige `notificacionId` de la respuesta; las siguientes
verifican listado y conteo, lectura individual y decremento exacto, rechazo de
consulta/lectura ajena (`403`), lectura masiva (`204`) y contador final `0`.
Esta carpeta no crea ni limpia datos. Los 31 casos API/BD manuales tienen
resultados en la planilla; esta carpeta nueva de notificaciones aún no se ejecutó
contra QA. El informe Newman no se generó en este corte.
