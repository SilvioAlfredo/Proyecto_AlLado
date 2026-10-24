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
- **RF/HU:** RF-01 · HU-04 CA-01/CA-02 | **Estado:** Cerrado (21/10/2026)
- **Resolución:** `JwtUtils` lee `jwt.secret` **sin valor por defecto** y valida que tenga al menos 32 bytes
  (HS256); si falta o es corta, la aplicación no arranca. `application.properties` usa
  `jwt.secret=${JWT_SECRET}`; el ejemplo está en `Backend/.env.example` y los `.env` quedaron
  ignorados por git. Los tests usan un valor ficticio propio (`src/test/resources/application.properties`).
- **Evidencia:** `Backend/src/test/java/com/al_lado/al_lado_project/security/jwt/JwtUtilsTest.java`
  (`sinSecretoNoSeFirmaNingunToken`, `unSecretoCortoSeRechaza`, `unTokenFirmadoConOtraClaveNoSeAcepta`).

### AL-SEC-002 — Credencial PostgreSQL hardcodeada | P0 | Seguridad/Configuración

- **Problema:** `application.properties` contiene contraseña de BD.
- **Impacto:** exposición potencial de credenciales.
- **Solución:** usar `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` por entorno; eliminar credenciales del repo.
- **Criterio de aceptación:** el repositorio no contiene passwords.
- **RF/HU:** Transversal | **Estado:** Cerrado (21/10/2026)
- **Resolución:** `spring.datasource.password=${DB_PASSWORD}` (obligatoria: sin ella la app no arranca),
  `url` y `username` configurables por entorno. Ejemplo en `Backend/.env.example`; `.env` ignorado.
  El repositorio no contiene ninguna contraseña.
- **Evidencia:** `grep -rn "password" Backend/src/main/resources/` → solo la referencia a la variable de entorno.

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
- **RF/HU:** RF-01 · HU-04 CA-02 | **Estado:** Cerrado (21/10/2026)
- **Resolución:** nuevo `TokenPurpose` (`SESSION`, `RECOVERY`, `VERIFY_EMAIL`) en el claim `purpose` de
  cada token. La recuperación y la verificación emiten tokens propios (`jwtUtils.generateRecoveryToken` /
  `generateVerificationToken`) con expiración configurable (`auth.token.recovery.ttl-minutes`,
  `auth.token.verification.ttl-minutes`) y registran su `jti` en la base: se registran **los `jti`, no
  los tokens**, y se borran al consumirse (uso único). `GET /auth/verify-email` y `POST /auth/password/reset` rechazan tokens
  vencidos, ya usados o de otro propósito con el mismo mensaje genérico. **Complemento (22/10/2026):**
  el filtro `JwtAuthenticationFilter` sólo autentica tokens con `purpose == SESSION`; un token de
  recuperación o de verificación (o uno antiguo sin el claim) ya no sirve como `Bearer` de sesión.
- **Evidencia:** `AuthServiceSeguridadTest` (`resetPassword_deberiaRechazarTokenDeSesion`,
  `resetPassword_deberiaRechazarTokenYaConsumido`, `resetPassword_deberiaRechazarTokenVencido`,
  `verifyEmail_deberiaVerificarElCorreoYConsumirElToken`, `verifyEmail_deberiaRechazarTokenDeSesion`),
  `JwtAuthenticationFilterTest` (`tokenDeRecuperacionNoAutentica`, `tokenDeVerificacionNoAutentica`,
  `tokenAntiguoSinPropositoNoAutentica`, `tokenDeSesionValidoSiAutentica`,
  `tokenDeSesionRevocadoNoAutentica`) y `JwtUtilsTest`.

### AL-SEC-005 — Evitar enumeración de usuarios | P0 | Seguridad

- **Problema:** el flujo de recuperación diferencia emails existentes e inexistentes.
- **Impacto:** permite descubrir cuentas registradas.
- **Solución:** responder siempre con mensaje genérico.
- **Criterio de aceptación:** respuestas públicas indistinguibles.
- **RF/HU:** RF-01 · HU-04 | **Estado:** Cerrado (21/10/2026)
- **Resolución:** `POST /auth/password/forgot` responde siempre `200` con el mismo mensaje
  ("Si la cuenta está registrada, te enviaremos las instrucciones..."), exista o no la cuenta;
  cuando el email no existe se ejecuta un hash descartable para que el tiempo de respuesta no
  delate la diferencia (ahora la codificación corre en **los dos** caminos). `GET /trabajadores/por-email/estadisticas`
  responde `403` tanto si el email no existe como si es de otra cuenta. El login ya respondía `401` con mensaje genérico.
- **Evidencia:** `AuthServiceSeguridadTest` (`forgotPassword_deberiaNoFallarConEmailInexistente`,
  `forgotPassword_haceElMismoTrabajoConEmailInexistente`, `forgotPassword_haceElMismoTrabajoConEmailExistente`)
  y `PostulacionAlcanceTest` (`lasEstadisticasPorEmailResponden403TantoSiNoExisteSiEsDeOtro`,
  `lasEstadisticasPorEmailPropiasSiSePuedenConsultar`).

### AL-SEC-006 — Restringir lecturas y proyecciones por audiencia (MEJ-001) | P0 | Seguridad

- **Problema:** las lecturas públicas devolvían el perfil completo de los usuarios (email, teléfono,
  CUIT, postulaciones, preferencias) y cualquier usuario autenticado veía todas las postulaciones.
- **Impacto:** exposición de datos de contacto y de actividad de terceros sin relación entre ellos.
- **Solución:** proyección por audiencia: el titular y ADMIN reciben la ficha completa; el resto, una
  proyección pública.
- **Criterio de aceptación:** las lecturas públicas no devuelven datos de contacto nipostulaciones ajenas.
- **RF/HU:** RF-02/RF-03 · HU-03/HU-05 | **Estado:** Cerrado (21/10/2026)
- **Resolución:** los mappers suman `toDTOPublic(...)` (sin email, teléfono, CUIT, supervisor ni
  colecciones privadas) y los servicios eligen la proyección según quién pregunta
  (`SecurityUtils.canViewPrivateDataOf`): directorio de trabajadores y empleadores, ofertas (además sin
  el detalle de postulantes, solo el conteo `postulacionesCount`), valoraciones, foro, anuncios y
  comunicados. `GET /postulaciones` y `GET /postulaciones/{id}` quedan acotados al alcance del lector
  (el trabajador sus postulaciones, el empleador las de sus ofertas, ADMIN todas). Las preferencias de
  notificación y la lista de favoritos dejaron de ser públicas. El contacto del empleador se expone
  en `GET /oportunidades/{id}/contacto`, solo para el titular, ADMIN y quien se postuló.
- **Evidencia:** `TrabajadorProyeccionTest` (`toDTOPublic_noDebeExponerEmailNiTelefono`,
  `toDTOPublic_debeConservarLosDatosDelDirectorio`, `obtenerPorId_debeDevolverLaProyeccionPublicaATerceros`,
  `obtenerPorId_debeDevolverElPerfilCompletoAlTitular`, `obtenerPorId_debeDevolverElPerfilCompletoAlAdministrador`),
  `OportunidadProyeccionTest` (proyección de la oferta y `GET /oportunidades/{id}/contacto`) y
  `PostulacionAlcanceTest` (alcance de `GET /postulaciones` y `/postulaciones/{id}`).

### AL-SEC-007 — Cerrar concesión directa de PRO (MEJ-002) | P0 | Seguridad

- **Problema:** `POST /suscripciones` con `nivel: PRO` lo podía hacer el propio usuario sobre su cuenta.
- **Impacto:** cualquier usuario se auto-otorgaba el plan PRO (ofertas ilimitadas, perfil empresarial,
  anuncios, propuestas PRO).
- **Solución:** el nivel PRO solo lo concede un administrador tras verificar el aporte.
- **Criterio de aceptación:** un usuario no autenticado como ADMIN recibe 403 al pedir PRO.
- **RF/HU:** RF-09 · HU-12 | **Estado:** Cerrado (21/10/2026)
- **Resolución:** `SuscripcionServiceImpl.crear` exige `ROLE_ADMIN` para `nivel: PRO` (403 con mensaje
  explícito en el resto de los casos); el alta propia queda siempre en `BASICO` y sin vencimiento, y
  un `BASICO` existente no se puede promover a `PRO` por el propio usuario. La promoción sigue
  disponible para el administrador (y por la verificación de transferencias, que otorga PRO).
- **Evidencia:** `SuscripcionConcesionProTest` (`unEmpleadorNoPuedeAutoOtorgarsePro`,
  `unEmpleadorNoPuedePasarseDeBasicoAPro`, `unAdministradorSiPuedeConcederPro`,
  `unEmpleadorPuedeCrearSuPlanBasico`).

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

### AL-API-005 — Identidad autenticada mínima y sesión coherente (MEJ-006) | P1 | Integración

- **Problema:** el cliente no conocía su propio identificador: lo resolvía listando usuarios y
  comparando emails (`/trabajadores`, `/empleadores`, `/administradores`), y esas lecturas son
  públicas. Además, el JWT no llevaba identidad y la sesión se armaba con datos sueltos.
- **Impacto:** cada pantalla hacía un listado completo de usuarios para descubrir el propio
  (fuga de datos de terceros y requests innecesarios); la sesión podía quedar incoherente
  (email sin id, o id desactualizado).
- **Solución:** el backend devuelve la identidad mínima y la sesión del cliente se apoya en ella.
- **Criterio de aceptación:** el cliente obtiene su id del token o de la respuesta del login, sin
  listar usuarios; la sesión web y la móvil coinciden con la del backend.
- **RF/HU:** RF-01 · HU-04 | **Estado:** Cerrado (21/10/2026)
- **Resolución:** `POST /auth/login` y `POST /auth/register` devuelven `id`, `email`, `nombre` y
  `roles`; el token de sesión incluye el claim `uid`; se agrega `GET /auth/me` para rehidratar la
  sesión. En el cliente web, `AuthProvider` guarda `user_id`/`user_nombre` y los usa como `userId`
  (las sesiones viejas los recuperan del claim `uid` y, si no está, de `/auth/me`); se eliminaron
  `resolverIdPorEmail`, `obtenerRefPorEmail` y el listado de usuarios para armar la sesión. En el
  cliente móvil, `AuthContext` toma el id de la respuesta (con backfill desde el token) y se
  eliminó `resolveIdByEmail` de las 19 pantallas que lo usaban.
- **Evidencia:** `AuthServiceImplTest` (`registrar_deberiaDevolverLaIdentidadMinimaYElTokenDeVerificacion`),
  `JwtUtilsTest` (`generarToken_deberiaIncluirLaIdentidadDelUsuario`,
  `generarToken_deberiaDeclararElPropositoSession`), `tsc --noEmit` sin errores y las suites de
  `vitest` (`Frontend/al_lado-app`, 43/43) y `jest` (`Frontend/al-lado-mobile`, 25/25).

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

### AL-FE-009 — Corregir contraste de texto para WCAG AA | P2 | Accesibilidad

- **Problema:** 7 elementos con `text-zinc-500` (`#71717a`) sobre `bg-[#0b0b0c]` quedan en ratio ~4.14:1, por debajo del mínimo WCAG AA de 4.5:1 para texto pequeño. Detectado por Lighthouse 13.4.1 (19/10/2026): Navbar "SERVICIOS LOCALES", "¿Ya sos parte? Iniciá sesión", "Explorá las publicaciones sin crear una cuenta.", "PASO 1/2/3" (×3), footer "Personas de tu barrio…".
- **Solución:** cambiar `text-zinc-500` → `text-zinc-400` en esos 7 elementos (ratio ~6.9:1). Revisar también `placeholder:text-zinc-500` en inputs.
- **Criterio de aceptación:** Lighthouse Accessibility ≥ 95 sin fallos de contraste.
- **RF/HU:** Transversal | **Estado:** Abierto

### AL-FE-010 — Agregar atributo `alt` a imagen sin describir | P2 | Accesibilidad/SEO

- **Problema:** Lighthouse reporta un `<img>` sin atributo `alt` en la homepage (`localhost:3000`), lo que baja Accessibility y SEO. En el código fuente solo existe un `<img>` en `PublicidadAdminDetail.tsx:237` (admin); el detectado puede ser favicon o elemento del overlay de dev.
- **Solución:** agregar `alt=""` si es decorativo o `alt="descriptivo"` si aporta información; verificar que no haya otros `<img>` sin `alt` en componentes públicos.
- **Criterio de aceptación:** Lighthouse Accessibility y SEO sin fallos de `alt`.
- **RF/HU:** Transversal | **Estado:** Abierto

### AL-SEC-011 — Configurar headers de seguridad en next.config.ts | P2 | Seguridad

- **Problema:** `next.config.ts` vacío, sin headers de seguridad HTTP. Lighthouse reporta 5 avisos manuales (severidad Alta): sin CSP, sin HSTS, sin COOP, sin X-Frame-Options, sin Trusted Types.
- **Solución:** agregar bloque `headers()` en `next.config.ts` con `Content-Security-Policy`, `Strict-Transport-Security`, `Cross-Origin-Opener-Policy: same-origin`, `X-Frame-Options: DENY`. CSP con al menos `default-src 'self'`, `script-src 'self'`, `style-src 'self' 'unsafe-inline'` (Tailwind).
- **Criterio de aceptación:** respuesta HTTP incluye los 4 headers; Lighthouse Best Practices sin avisos de seguridad.
- **RF/HU:** Transversal | **Estado:** Abierto

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

### AL-FE-011 — Optimizar rendimiento del frontend | P3 | Performance

- **Problema:** Lighthouse Performance 78 (Slow 4G, Moto G Power). CSS render-blocking `src_app_globals_*.css` (11.2 KiB, 160 ms); ~9 KiB de legacy JS (polyfills `Array.at`, `.flat`, `.flatMap`, `Object.fromEntries`, `.hasOwn`, `String.trimEnd/Start`); sin preconnect a orígenes. TBT 1,010 ms.
- **Solución:** agregar `<link rel="preconnect">` en `layout.tsx` para orígenes externos; revisar `next.config.ts` para reducir transpilación innecesaria (target moderno si el soporte lo permite); evaluar `font-display: swap`.
- **Criterio de aceptación:** Lighthouse Performance ≥ 90 en Slow 4G sin extensiones.
- **RF/HU:** Transversal | **Estado:** Abierto

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

## Cierre de la semana 1 — 21/10/2026 (SilvioAlfredo)

Cierre de las 7 issues de la primera semana del plan de dos semanas (6 P0 + 1 P1).
Las 3 restantes de la semana 2 (AL-DOC-002, GH-47, GH-52) siguen abiertas.

| # local | ID | Issue | Prio | Estado | Evidencia principal |
|---|---|---|---|---|---|
| 74 | AL-SEC-001 | JWT secret hardcodeado | P0 | Cerrado | `JwtUtilsTest` · `jwt.secret=${JWT_SECRET}` sin fallback |
| 75 | AL-SEC-002 | Credencial PostgreSQL hardcodeada | P0 | Cerrado | `DB_PASSWORD` obligatoria · `Backend/.env.example` · `Testing/e2e` sin credencial (22/10) |
| 80 | AL-SEC-004 | Recuperación y verificación de cuenta | P0 | Cerrado | `AuthServiceSeguridadTest` · `JwtAuthenticationFilterTest` (el filtro exige `purpose=SESSION`) |
| 81 | AL-SEC-005 | Evitar enumeración de usuarios | P0 | Cerrado | `forgot` con respuesta genérica + tiempo equivalente |
| 82 | AL-SEC-006 | Lecturas y proyecciones por audiencia (MEJ-001) | P0 | Cerrado | `TrabajadorProyeccionTest` · `OportunidadProyeccionTest` · `PostulacionAlcanceTest` |
| 93 | AL-SEC-007 | Concesión directa de PRO (MEJ-002) | P0 | Cerrado | `SuscripcionConcesionProTest` · PRO solo para ADMIN |
| 88 | AL-API-005 | Identidad mínima y sesión coherente (MEJ-006) | P1 | Cerrado | claim `uid` + `GET /auth/me` · sin listados para armar la sesión |

Verificaciones ejecutadas:
- Backend: `./mvnw test` (JDK 21) → el conteo que constaba aquí (162) no se correspondía con el
  árbol real ni con los nombres de tests citados; la verificación vigente (con cifras y nombres
  reales) está en **«Verificación y correcciones — 22/10/2026»** más abajo.
- Frontend web: `tsc --noEmit` sin errores y `next build` correcto.
- Frontend móvil: `tsc --noEmit` sin errores.

Pendiente para la semana 2 (sin tocar): AL-DOC-002 (contrato OpenAPI), GH-47 (registro: SMS y
política de contraseñas) y GH-52 (notificaciones al publicar, < 30 s).

---

## Verificación y correcciones — 22/10/2026 (SilvioAlfredo)

Revisión de las 17 issues (7 de la semana 1 + 10 asignadas a williams-gonzalez) con ejecución real
de las suites. Varias issues declaradas «Cerradas» no lo estaban en el código; éstas son las
correcciones aplicadas en esta pasada:

| # | ID | Qué faltaba realmente en el código | Corrección aplicada |
|---|---|---|---|
| 80 | AL-SEC-004 | el filtro autenticaba cualquier token con firma válida: un token `RECOVERY` o `VERIFY_EMAIL` servía como `Bearer` de sesión | `JwtAuthenticationFilter` exige `purpose == SESSION` |
| 81 | AL-SEC-005 | el hash descartable sólo corría con el email inexistente y `por-email/estadisticas` devolvía 404 si el email no existía | misma codificación BCrypt en ambos caminos + `403` en ambos |
| 82 | AL-SEC-006 | no existía `toDTOPublic`: sólo había un test que no compilaba y la descripción de la issue | `toDTOPublic` en 9 mappers, elección por audiencia en los servicios, `GET /postulaciones` y `/postulaciones/{id}` acotados, `GET /oportunidades/{id}/contacto`, `postulacionesCount` |
| 88 | AL-API-005 | web y móvil resolvían el id de sesión listando el directorio de usuarios (`/trabajadores`, `/empleadores`) | id desde la respuesta de login/registro, claim `uid` o `GET /auth/me`; `obtenerRefPorEmail` y `resolveIdByEmail` eliminados |
| 94 | AL-SEC-008 | `entity.setId(dto.getId())` en 16 mappers de altas: una petición con un ID ajeno sobrescribía un registro | `entity.setId(null)` en todos; `UsuarioServiceImpl` delega en el servicio de cada tipo (antes guardaba la contraseña en claro) |
| 96 | AL-SEC-010 | el comunicado tomaba el emisor del cuerpo de la petición | emisor = principal de la sesión (mismo criterio que `ReporteServiceImpl.resolver`) |
| 56 | GH-56 | los repositorios ya tenían `findBySectorIgnoreCase`, pero no estaba expuesto en ningún endpoint | `GET /foro/sector/{sector}` y `GET /reportes/sector/{sector}` |
| 75 | AL-SEC-002 | quedaban la credencial de la base (`1234`) en `Testing/e2e/README.md` y en `generar_evidencias.py` | credencial tomada del entorno; el script falla si falta `PGPASSWORD` |

Issues revisadas y confirmadas sin cambios: 74, 77, 79, 85, 89, 92, 93.
Pendientes: 62 (merge de `hg` sin resolver, sin tocar) y 91 (AL-MOB-001, divergencias móviles
restantes: quedan `console.error` de `act(...)` cosméticos en `comunidad.test.tsx`).

Verificaciones ejecutadas el 22/10/2026:
- Backend (JDK 21, Maven 3.9.16): `mvn -o test` → **201 tests, 0 fallos, 1 error**:
  `AlLadoProjectApplicationTests.contextLoads`, que necesita PostgreSQL activo en
  `localhost:5432` (no instalado en esta máquina; falla igual antes de estos cambios).
- Frontend web: `vitest run` → **43/43** y `tsc --noEmit` sin errores.
- Frontend móvil: `jest --ci` → **25/25** y `tsc --noEmit` sin errores.

Tests nuevos que quedan como evidencia (sin commitear): `JwtAuthenticationFilterTest`,
`OportunidadProyeccionTest`, `PostulacionAlcanceTest`, `AltasSinIdDelClienteTest`,
`SectorFiltroTest`, `ComunicadoEmisorPrincipioTest`, más los tests ya existentes sin trackear
(`TrabajadorProyeccionTest`, `AuthServiceSeguridadTest`, `AuthControllerValidationTest`,
`PublicacionForoServiceImplTest`, `ValoracionServiceImplTest`, `PostulacionLimiteTest`,
`SuscripcionConcesionProTest`, `JwtUtilsTest`, `AuthServiceImplTest`) y los de ambos frontends.

---

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
