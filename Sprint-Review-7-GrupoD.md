# 📋 Sprint Review 7 — Grupo D

| | |
|:---|:---|
| **Equipo (Grupo D)** | Chiara Madera · Silvio Riveros (SilvioAlfredo) · Williams Gonzalez (williams-gonzalez) · Julio Lasa (juliolasa81) |
| **Duración del sprint** | 28/09/2026 → 16/10/2026 (~2,5 semanas) |
| **Referencia técnica** | tag `0.6.1` (rev 64) → tag `0.7.0` (rev 88) · 23 changesets de trabajo |
| **Backlog del sprint** | 26 issues asignadas a Sprint 7 → **15 cerradas (58 %)**, 11 pasan a Sprint 8 |
| **Entregable** | Producto incrementado: plataforma web consolidada (frontend unificado) + backend endurecido (seguridad, API, tests) + base móvil |

---

## 🎯 Objetivo del sprint (ejecutado)

> Unificar los contratos frontend↔backend que quedaban divergentes tras S6, consolidar la plataforma web
> (trabajador / empleador / admin), blindar seguridad e integridad del backend, incorporar la base del
> cliente móvil y dejar lista la trazabilidad de especificaciones y testing para la entrega.

---

## ✅ Lo completado (demo)

### 1. Integración API — contratos unificados (Silvio)
- **AL-API-001 (#57)** — Seleccionar/rechazar postulación: frontend y backend unificados en
  `PATCH /postulaciones/{id}/seleccionar` y `/rechazar` (impl. `PostulacionController`).
- **AL-API-002 (#58)** — Registro unificado: un único `POST /auth/register` con DTO validado
  (nombre, email, contraseña, teléfono, rol).
- **AL-API-003 (#59)** — Endpoints de perfil alineados: el frontend consume los endpoints reales
  (`/trabajadores/{id}`, `/empleadores/{id}`, `/perfiles-empresariales/...`).
- **AL-API-004 (#60)** — Cierre de oportunidad con contrato único (`PUT /oportunidades/{id}` + `activa=false`).

### 2. Seguridad e integridad (Silvio)
- **AL-SEC-003 (#76)** — Verificación de titularidad previa a modificar oportunidades: propietario/ADMIN
  sí acceden, terceros reciben 403; autor no modificable desde DTO.
- **AL-TEST-001 (#83)** — Tests de autorización ampliados: `AdminDashboardSecurityTest` cubre
  401 anónimo, 403 para TRABAJADOR/EMPLEADOR, 200 para ADMIN.
- Infraestructura asociada (rev 70–73): revocación de JWT por `jti`, respuestas 409 de integridad,
  paginación consistente, eliminación de consultas N+1, BCrypt y validación de roles.

### 3. Reglas de negocio (Silvio)
- **GH-44 (#44)** — Límite freemium 3 ofertas/mes para cuentas Básicas (`puedePublicarOferta`
  con lock pesimista; PRO vigente = ilimitadas).
- **GH-45 (#45)** — Acceso PRO a propuestas reservadas: `GET /oportunidades/propuestas-pro`
  + UI con gate de suscripción (HU-02 CA-03). *(Título original hablaba de "exclusividad 15 min";
  la implementación real corresponde a HU-02 CA-03.)*
- **GH-51 (#51)** — Historial de contratos mediante máquina de estados de la `Postulacion`
  (PENDIENTE → SELECCIONADA → COMPLETADA) + vista `/empleador/contratos`.

### 4. Frontend web — UI y funcionalidad (Chiara · Julio · Williams)
- **AL-FE-001 (#67)** (Chiara) — Creación de componentes UI faltantes (`card.tsx`, `checkbox.tsx`,
  `select.tsx`, `tooltip.tsx`); `npm run build` sin errores de módulos.
- **AL-FE-003 (#69)** (Chiara) — Corrección de errores TypeScript en Admin → 0 errores.
- **GH-46 (#46)** (Chiara) — Perfil de trabajador: UI completa con datos reales del backend.
- **GH-48 (#48)** (Chiara) — Perfil de empleador visible al trabajador.
- **GH-54 (#54)** (Julio) — UI de favoritos del empleador.
- **AL-FE-002 (#68)** (Williams) — Fix de hidratación en `IncomeWidget`/`IngresosWidget`
  (case-sensitive en Linux) e interceptor de API manejando 403 sin bucle de peticiones.
- Trabajo estructural: reestructuración completa del frontend web adaptado a los diseños Figma,
  flujos y navegación del rol Trabajador (Julio, rev 78), panel Admin web integrado con vistas
  de Trabajador (Chiara, rev 79–80).

### 5. Móvil (Williams)
- Base de la app móvil con **React Native + Expo** (rev 66) con módulos complementarios.
- Paridad de features con el frontend web: notificaciones, propuestas PRO y retiro de
  postulaciones (rev 77). **AuthGuard** para proteger rutas privadas (rev 84).

### 6. Documentación y testing (Chiara · Silvio)
- JavaDoc/JSDoc del backend + **TypeDoc configurado** para el frontend (rev 79–81).
- Diagramas y especificaciones actualizados (rev 86); creación de la carpeta `Testing/`
  (Plan de Pruebas + matriz de Test Cases) (rev 87).
- Swagger / OpenAPI integrado con springdoc (rev 73–74).
- **Limpieza del repositorio**: eliminación de templates Thymeleaf, assets del panel admin,
  `node_modules` fuera del tracking y la carpeta `FrontPrueba` (rev 69, 78–79, 88).

---

## 📊 Métricas del sprint

### Por changesets (trabajo entre 0.6.1 y 0.7.0)

| Integrante | Changesets | Contenido principal |
|:---|:---:|:---|
| **Riveros Silvio (SilvioAlfredo)** | 9 | Backend: seguridad, API unificada, tests, OpenAPI, notificaciones/PRO (≈ 25.100 líneas netas en ~740 archivos) |
| **Madera Chiara (ChiaraMadera)** | 9 | Frontend web Admin/Usuario, UI components, docs JavaDoc/TypeDoc, Testing, limpieza (≈ 22.500 líneas netas + ~264.000 en limpieza de binarios) |
| **Gonzalez Williams (williams-gonzalez)** | 4 | App móvil React Native/Expo, AuthGuard, interceptor 403, fix IncomeWidget (≈ 6.500 en ~21 archivos) |
| **Lasa Julio (juliolasa81)** | 1 | Integración de vistas y navegación del rol Trabajador en web, limpieza de tracking (≈ 1.800 en ~330 archivos) |

**Total: 23 changesets · ~55.400 líneas insertadas · ~525.000 líneas eliminadas (mayormente artefactos limpiados del tracking).**

### Por issues del backlog

| Integrante | Asignadas S7 | Cerradas | % | Num. issues cerradas |
|:---|:---:|:---:|:---:|:---|
| Silvio Riveros | 14 | 9 | 64 % | #57, #58, #59, #60, #76, #83, #44, #45, #51 |
| Chiara Madera | 5 | 4 | 80 % | #67, #69, #46, #48 |
| Williams Gonzalez | 1 | 1 | 100 % | #68 |
| Julio Lasa | 3 | 1 | 33 % | #54 |
| **Total** | **26** | **15** | **58 %** | |

> Flujo interno S7: 12 issues P1 (API/Frontend/Seguridad) — 8 cerradas; los P0 de seguridad
> pendientes (#74, #75 secretos) se traspasan a Sprint 8 con prioridad máxima.

---

## ❌ No se completó (carry-over a Sprint 8)

| Issue | Anti-título | Estado actual | Nota |
|:---|:---|:---|:---|
| AL-DOC-002 (#66) | Completar contrato OpenAPI (springdoc ya integra Swagger UI) | En desarrollo | Falta spec versionado en el repo |
| AL-FE-004 (#70) | Definir frontend oficial (web vs móvil) | En desarrollo | Falta decidir app oficial y archivar legacy en README |
| AL-SEC-004 (#80) | Tokens de recuperación con `purpose` + uso único | En desarrollo | Hoy el token de reset tiene expiración 24h y un solo uso; falta separación de propósito |
| AL-SEC-005 (#81) | Evitar enumeración de usuarios | En desarrollo | `forgotPassword` difiere emails existentes/inexistentes |
| AL-TEST-002 (#84) | Tests de integración API end-to-end | En desarrollo | Existen slices MockMvc; falta cobertura de comunidad |
| GH-47 (#47) | Registro — SMS y política de contraseña | En desarrollo | Teléfono ya obligatorio; falta SMS |
| GH-49 (#49) | Valoración — gate por finalización y tip | En desarrollo | Falta validar COMPLETADA en `ValoracionServiceImpl` |
| GH-50 (#50) | Búsqueda filtrada + UI (<500 ms) | En desarrollo | Backend `POST /filtrar` listo; falta medir/perfilar en UI |
| GH-53 (#53) | Disponibilidad — excluir de búsquedas | En desarrollo | Horarios sin solape ya implementados; falta integrar en matching |
| GH-55 (#55) | Estadísticas del trabajador + candado Pro | En desarrollo | Endpoints `/estadísticas` listos; faltan widgets UI con candado Pro |
| GH-56 (#56) | Reportes + foro por sector | En desarrollo | Módulos reportes y foro funcionales; falta filtrado por sector |

---

## 🔁 Decisiones de backlog tomadas en la review

1. Las 11 issues no terminadas se **replanifican a Sprint 8**, manteniendo asignados actuales.
2. Se suman al Sprint 8 los issues ya identificados: limpieza de artefactos (`.next`, `target`),
   migraciones Flyway/Liquibase (reemplazo de `ddl-auto=update`), separación de perfiles
   de configuración dev/prod y secretos JWT/DB fuera del repo (**P0** #74, #75).
3. Nuevos issues de Sprint 8 surgidos de esta review: `AL-FE-005` (datos ficticios en Home/`/publicar`),
   `AL-FE-006` (código muerto UI), `AL-SEC-006` (lectura cruzada por rol), `AL-TEST-003/004/005`
   (cobertura de Postman por módulo, seed TC-DB-005, corrida de ciclo S1 con evidencia).
4. **Verificación documental aplicada durante la review**: #59, #60 y #44 fueron confirmadas con
   evidencia en código y se marcan cerradas; el alcance de #66 se redefine como "completar la
   documentación OpenAPI existente" (springdoc + Swagger UI ya integrados).

---

## 🎤 Aportes por integrante (resumen narrativo)

- **Riveros Silvio** — Motor del backend en S7: unidad de contratos API, transición de seguridad
  (JWT revocable, 403 por titularidad, BCrypt), suite de tests de autorización/contratos, OpenAPI,
  integración frontend↔backend con Web, y features de negocio (cuota freemium, propuestas PRO,
  notificaciones, máquina de estados de postulación). El mayor volumen de changesets del sprint.
- **Chiara Madera** — Dueña de la UI web y la calidad del frontend: componentes UI base, panel Admin
  completo, gestión de errores TS, JavaDoc/TypeDoc, diagramas y especificaciones al día, carpeta de
  Testing y limpieza de repo (FrontPrueba retirado en rev 88). Completitud del 80 % de sus issues.
- **Williams Gonzalez** — Levantó la **apuesta móvil** (React Native + Expo) y blindó el frontend web
  (AuthGuard + interceptor 403 + fix de hidratación case-sensitive). Aseguró paridad de features web/mobile.
- **Julio Lasa** — Integró el flujo y navegación del rol **Trabajador** en el frontend web y la UI de
  favoritos del empleador; lideró el saneamiento del tracking de `node_modules` en el frontend web.

---

## 🧭 Próximos pasos (acuerdo del equipo)

1. **Sprint 8**: seguridad P0 (secretos fuera del repo), plan de pruebas ejecutado con evidencia,
   migraciones versionadas y cierre de las 11 issues carry-over.
2. Cierre de la definición de frontend oficial (AL-FE-004) antes de la demo final.
3. Continuar con el módulo de matching local (AL-FUNC-001) como preparación del diferencial del producto.
