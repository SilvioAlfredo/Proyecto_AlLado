# Testing — Plan de Pruebas y Trazabilidad (IEEE 829)

> **Responde a:** Devolución Plan de Pruebas y Test Cases — AlLado Grupo D.
> Complementa a `Issues_Backlog.md`. Los 25 casos existentes (TEST-G/L/D/DB/M-00x)
> se conservan; aquí se corrigen los puntos observados. Nada está marcado como
> ejecutado: `Resultado Real / Estado / Evidencia` se completa al ejecutar.

## 1. Unificación de IDs (trazabilidad)

- La devolución observó doble numeración (plan TEST-D-101…505 vs. planilla TEST-G/L/D/DB/M-00x).
- **Regla desde ahora:** ID único **`TC-<área>-<nnn>`** en plan, planilla, código Postman y bugs.
  - Equivalencia: `TEST-G-00x → TC-G-00x`, `TEST-L-00x → TC-L-00x`, etc.
  - Los casos nuevos usan el correlativo siguiente (`TC-SEC-001`, `TC-LIM-001`, …).
- Prohibidos los resultados con "o" (`401/404`, `400/409`, "impide… o maneja…").
  Cada caso fija **un** oráculo verificable (código HTTP exacto + estado en BD).

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

## 6. Matriz de trazabilidad HU → CA → TC → Bug (cubre los 5 RF sin casos)

RF sin casos según devolución: RF-02/03, RF-06, RF-11, RF-12, RF-13.
Reglas prometidas sin prueba: límite 3 publicaciones/mes, exclusividad Pro 15 min,
enmascaramiento de teléfonos, foro geográfico, reseña inmutable.

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
- Columnas `Resultado Real / Estado / Evidencia`: vacías = no ejecutado
  (la devolución observó las 25 vacías; se llenan en el ciclo S1–S2).
