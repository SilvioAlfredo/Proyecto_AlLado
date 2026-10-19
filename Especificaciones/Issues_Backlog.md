# AlLado — Backlog de Issues

> **Fuente:** `AlLado_ISSUES_Backlog.pdf` v1.0 (movido a `Especificaciones/` para trazabilidad documental).
> **Fecha de traslado:** 15/10/2026.
> **Objetivo:** consolidar problemas, riesgos y tareas de corrección antes de nuevas funcionalidades.
> Las prioridades P0/P1 deben resolverse antes de producción; P2/P3 después de estabilizar.

## Criterio de prioridades

| Prioridad | Significado | Objetivo |
|---|---|---|
| P0 — Crítica | Seguridad, pérdida de control o bloqueo grave | Resolver inmediatamente |
| P1 — Alta | Funcionalidad rota o integración inconsistente | Resolver antes de nuevas features |
| P2 — Media | Deuda técnica o funcionalidad incompleta | Siguiente iteración |
| P3 — Baja | Mejoras, documentación y limpieza | Durante estabilización |

## Trazabilidad con especificaciones

Columna `RF/HU` agregada al trasladar (no existía en el PDF) para responder a la devolución
de QA (sin trazabilidad HU/CA). `Estado` queda en `Abierto` hasta ejecución con evidencia.

---

## P0 — Seguridad (Sprint 0)

### AL-SEC-001 — JWT secret hardcodeado | P0 | Seguridad

- **Problema:** secreto JWT por defecto en código/configuración.
- **Impacto:** una instalación puede usar clave conocida y permitir falsificación de tokens.
- **Solución:** mover `JWT_SECRET` a variables de entorno/secret manager, eliminar fallback inseguro.
- **Criterio de aceptación:** no hay secretos JWT reales en código; producción usa configuración externa.
- **RF/HU:** RF-01 · HU-04 CA-01/CA-02 | **Estado:** Abierto

### AL-SEC-002 — Credencial PostgreSQL hardcodeada | P0 | Seguridad/Configuración

- **Problema:** `application.properties` contiene contraseña de BD.
- **Impacto:** exposición potencial de credenciales.
- **Solución:** usar `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` por entorno; eliminar credenciales del repo.
- **Criterio de aceptación:** el repositorio no contiene passwords.
- **RF/HU:** Transversal | **Estado:** Abierto

### AL-SEC-003 — Verificar titularidad antes de modificar oportunidades | P0 | Autorización

- **Problema:** la actualización debe verificar el propietario original antes de mutar.
- **Impacto:** riesgo de manipulación de titularidad / bypass de autorización.
- **Solución:** capturar propietario actual, autorizar primero y luego modificar; impedir cambiar autor desde DTO.
- **Criterio de aceptación:** propietario y ADMIN acceden; terceros reciben 403; autor no modificable.
- **RF/HU:** RF-04 · HU-01 / HU-02 | **Estado:** Abierto

### AL-SEC-004 — Endurecer recuperación y verificación de cuenta | P0 | Autenticación

- **Problema:** tokens de recuperación/verificación necesitan finalidad, expiración y consumo seguro.
- **Impacto:** token reutilizable o de propósito incorrecto permite abuso.
- **Solución:** tokens con `purpose`, expiración y consumo único.
- **Criterio de aceptación:** tokens vencidos, usados o de otro propósito son rechazados.
- **RF/HU:** RF-01 · HU-04 CA-02 | **Estado:** Abierto

### AL-SEC-005 — Evitar enumeración de usuarios | P0 | Seguridad

- **Problema:** el flujo de recuperación diferencia emails existentes e inexistentes.
- **Impacto:** permite descubrir cuentas registradas.
- **Solución:** responder siempre con mensaje genérico.
- **Criterio de aceptación:** respuestas públicas indistinguibles.
- **RF/HU:** RF-01 · HU-04 | **Estado:** Abierto

---

## P1 — Integración Front ↔ Back + Frontend estable (Sprint 1)

### AL-API-001 — Corregir seleccionar/rechazar postulación | P1 | Integración

- **Problema:** frontend y backend tenían método/ruta distintos.
- **Solución:** unificar en `PATCH /postulaciones/{id}/seleccionar` y `/rechazar`.
- **Criterio de aceptación:** acciones desde UI producen 2xx y actualizan estado.
- **RF/HU:** RF-04 · HU-02 CA-01 | **Estado:** Abierto

### AL-API-002 — Unificar registro | P1 | Integración

- **Problema:** flujos diferentes; el backend exige teléfono.
- **Solución:** un único `POST /auth/register` y un DTO único.
- **Criterio de aceptación:** registro desde UI y API funciona con el mismo contrato.
- **RF/HU:** RF-01 · HU-04 | **Estado:** Abierto

### AL-API-003 — Alinear endpoints de perfil | P1 | Integración

- **Problema:** el frontend usaba rutas `/perfil` no alineadas con controladores.
- **Solución:** definir endpoints autenticados de perfil y usarlos desde frontend.
- **Criterio de aceptación:** trabajador y empleador consultan y editan su perfil.
- **RF/HU:** RF-02/RF-03 · HU-03/HU-05/HU-10 | **Estado:** Abierto

### AL-API-004 — Cerrar oportunidad: contrato único | P1 | Integración

- **Problema:** frontend y backend deben compartir exactamente el mismo endpoint.
- **Solución:** definir único método, path, autorización y respuesta.
- **Criterio de aceptación:** cerrar desde UI cambia correctamente el estado.
- **RF/HU:** RF-04/RF-08 · HU-08 | **Estado:** Abierto

### AL-FE-001 — Corregir imports de componentes inexistentes | P1 | Frontend

- **Problema:** referencias a Card, Checkbox, Select y Tooltip ausentes en el árbol revisado.
- **Solución:** crear/importar componentes o reemplazar referencias.
- **Criterio de aceptación:** `npm run build` sin errores de módulos.
- **RF/HU:** Transversal UI | **Estado:** Abierto

### AL-FE-002 — Corregir IncomeWidget / IngresosWidget | P1 | Frontend

- **Problema:** archivo e import con nombres diferentes.
- **Solución:** unificar archivo, export e import.
- **Criterio de aceptación:** build limpio en Linux (case-sensitive).
- **RF/HU:** RF-09 · HU-12 | **Estado:** Abierto

### AL-FE-003 — Corregir errores TypeScript en Admin | P1 | Frontend

- **Problema:** errores de sintaxis en publicaciones y usuarios.
- **Solución:** corregir y ejecutar typecheck/lint/build.
- **Criterio de aceptación:** 0 errores TypeScript.
- **RF/HU:** Transversal | **Estado:** Abierto

### AL-FE-004 — Definir frontend oficial | P1 | Frontend

- **Problema:** varios frentes: web, mobile y FrontPrueba.
- **Solución:** documentar apps oficiales y archivar legacy.
- **Criterio de aceptación:** README identifica apps soportadas.
- **RF/HU:** Transversal | **Estado:** Abierto

---

## P2 — Tests, BD y diferencial de producto (Sprint 2–3)

### AL-TEST-001 — Ampliar tests de autorización | P2 | Testing

- **Problema:** cobertura insuficiente de reglas de negocio.
- **Solución:** tests de propietario/admin/terceros para recursos críticos.
- **Criterio de aceptación:** suite cubre 401/403/200 según contexto.
- **RF/HU:** RF-04/RF-10 · HU-02/HU-13 (autorización: falta `ROLE_TRABAJADOR → /admin/** = 403`) | **Estado:** Abierto

### AL-TEST-002 — Agregar tests de integración API | P2 | Testing

- **Problema:** faltan pruebas de contratos frontend/backend.
- **Solución:** tests de login, registro, oportunidades, postulaciones y perfiles.
- **Criterio de aceptación:** endpoints críticos con tests de integración.
- **RF/HU:** Transversal (ver `Testing.md`, matriz HU→CA→TC) | **Estado:** Abierto

### AL-DB-001 — Reemplazar ddl-auto=update | P2 | Base de datos

- **Problema:** `update` no controla de forma reproducible la evolución del esquema.
- **Solución:** Flyway/Liquibase + `ddl-auto=validate`.
- **Criterio de aceptación:** esquema reconstruible con migraciones versionadas.
- **RF/HU:** Transversal (incluye `seed_tc_db_005.sql` para TC-DB-005) | **Estado:** Abierto

### AL-DB-002 — Separar configuración de producción | P2 | Configuración

- **Problema:** `show-sql` y `open-in-view` orientados a desarrollo.
- **Solución:** separar perfiles dev/prod.
- **Criterio de aceptación:** producción usa configuración segura y explícita.
- **RF/HU:** Transversal | **Estado:** Abierto

### AL-FUNC-001 — Implementar matching local real | P2 | Producto

- **Problema:** el diferencial requiere ubicación, radio, habilidades y disponibilidad.
- **Solución:** filtros/scoring por etiquetas + distancia.
- **Criterio de aceptación:** una oportunidad devuelve candidatos relevantes y ordenados.
- **RF/HU:** RF-04/RF-06/RF-12 · HU-02/HU-07/HU-09 | **Estado:** Abierto

### AL-FUNC-002 — Implementar notificaciones del matching | P2 | Producto

- **Problema:** diseño contempla notificaciones pero el módulo fue retirado.
- **Solución:** notificación interna primero; luego email/push/WhatsApp.
- **Criterio de aceptación:** un trabajador recibe notificación ante coincidencia (< 30 s, HU-09).
- **RF/HU:** RF-12 · HU-09 CA-01 | **Estado:** Abierto

### AL-FUNC-003 — Eliminar placeholders | P2 | Estadísticas

- **Problema:** métricas con valores fijos, cero o null.
- **Solución:** implementar cálculos o marcar como no disponibles.
- **Criterio de aceptación:** ningún indicador productivo muestra datos ficticios.
- **RF/HU:** RF-09 · HU-12 | **Estado:** Abierto

---

## P3 — Documentación y limpieza (Sprint 4)

### AL-DOC-001 — Actualizar UML | P3 | Documentación

- **Problema:** diagramas con componentes que no coinciden con implementación.
- **Solución:** regenerar desde arquitectura vigente.
- **Criterio de aceptación:** UML, README y código representan el mismo sistema.
- **RF/HU:** — | **Estado:** Abierto

### AL-DOC-002 — Crear contrato API como fuente de verdad | P3 | Documentación

- **Problema:** frontend, backend y README pueden divergir.
- **Solución:** OpenAPI versionado.
- **Criterio de aceptación:** cada endpoint crítico tiene método, path, request, response y errores.
- **RF/HU:** — | **Estado:** Abierto

### AL-CLEAN-001 — Eliminar artefactos generados | P3 | Repositorio

- **Problema:** `.next`, `target`, `.class`, mapas en el repo.
- **Solución:** limpiar y actualizar ignore.
- **Criterio de aceptación:** la entrega contiene solo fuentes necesarias.
- **RF/HU:** — | **Estado:** Abierto

### AL-CLEAN-002 — Revisar .hg | P3 | Repositorio

- **Problema:** metadata de Mercurial en el proyecto.
- **Solución:** decidir si forma parte de la entrega; eliminarla si no.
- **Criterio de aceptación:** solo metadata intencional.
- **RF/HU:** — | **Estado:** Abierto

---

## Orden recomendado de ejecución

| Sprint | Issues |
|---|---|
| Sprint 0 — Seguridad crítica | AL-SEC-001 a AL-SEC-005 |
| Sprint 1 — Contrato Front ↔ Back | AL-API-001 a AL-API-004 |
| Sprint 1 — Frontend estable | AL-FE-001 a AL-FE-004 |
| Sprint 2 — Tests y calidad | AL-TEST-001, AL-TEST-002 |
| Sprint 2 — BD/configuración | AL-DB-001, AL-DB-002 |
| Sprint 3 — Diferencial de producto | AL-FUNC-001 a AL-FUNC-003 |
| Sprint 4 — Documentación y limpieza | AL-DOC-001, AL-DOC-002, AL-CLEAN-001, AL-CLEAN-002 |

## Definition of Done (del documento fuente)

- Front y back comparten un contrato API único y verificable.
- `npm run build` y typecheck/lint sin errores; backend compila y sus tests pasan.
- Endpoints críticos con tests de integración.
- Sin secretos ni credenciales en el repo.
- Escrituras verifican propietario/rol antes de mutar.
- Recuperación/verificación con tokens con propósito, expiración y consumo seguro.
- Documentación y diagramas corresponden al código vigente.
- Sin `.next`, `target`, `.class` ni artefactos generados.
- Placeholders implementados o marcados como no disponibles.
