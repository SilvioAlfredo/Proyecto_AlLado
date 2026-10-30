# Aportes por usuario — Mercurial + GitHub Issues

> Fuente hg: `hg log` (82 changesets, rev 0–81). Fuente Issues: `gh issue list --state all`
> (38 issues: 23 OPEN, 15 CLOSED). Fecha: 15/10/2026.

## 1. Mercurial — commits por usuario (normalizado)

| Usuario (hg) | Commits | % | Foco (por mensajes) |
|---|---|---|---|
| Silvio Riveros (`Riveros S.`) | 32 | 39 % | Backend (entity/DTO/service/repository), seguridad JWT, paginación, Swagger/OpenAPI, frontend Next.js, notificaciones mobile |
| Chiara Madera (`chiara_madera`) | 19 | 23 % | Frontend (Admin, Trabajador, Thymeleaf→tsx, Axios/CORS), controllers/mappers, docs JSDoc/JavaDoc/TypeDoc, tags 0.1/0.6.1 |
| Williams Gonzalez (`Williams Gonzalez`) | 16 | 20 % | Repository/DTO/mappers, mobile Expo, Diseño Sprint 2 (Penpot), merges |
| Julio Lasa (`JulioLasa`, `Julio Lasa`, `Lasa J.`, `julio`) | 15 | 18 % | Service/capas, Backend Sprint 3–5, frontend Empleador/Trabajador, tags v0.4.1/0.5.0 |

Detalle completo de mensajes por usuario en el Anexo §4.

## 2. GitHub Issues — por usuario

Convención: **Hechas** = CLOSED asignadas · **En desarrollo** = OPEN con asignado ·
**Pendientes** = OPEN sin asignar (bolsa común, mayormente automatización HU).

### 2.1 Resumen

| Usuario (GitHub) | Autor de | Hechas (CLOSED asignado) | En desarrollo (OPEN asignado) |
|---|---|---|---|
| ChiaraMadera | 34 | 9 | 3 (#40, #34, #4) |
| SilvioAlfredo | 4 | 12 | 3 (#39, #35, #4) |
| williams-gonzalez | 0 | 8 | 3 (#41, #37, #4) |
| juliolasa81 | 0 | 10 | 3 (#42, #36, #4) |
| (sin asignar) | — | 2 CLOSED | **14 pendientes OPEN** |

### 2.2 En desarrollo (OPEN con asignado)

| # | Título | Asignado | Autor |
|---|---|---|---|
| 42 | TKS 14 - Inicio del Sprint 3 | juliolasa81 | ChiaraMadera |
| 41 | TSK 13 - Archivos del Sprint 2 | williams-gonzalez | ChiaraMadera |
| 40 | TSK 12 - Archivos del Sprint 1 | ChiaraMadera | ChiaraMadera |
| 39 | TSK 11 - Inicio del Repositorio | SilvioAlfredo | ChiaraMadera |
| 37 | TSK 17 - Capa Service | williams-gonzalez | ChiaraMadera |
| 36 | TSK 16 - Capa Repository | juliolasa81 | ChiaraMadera |
| 35 | TSK 15 - Capa Entity | SilvioAlfredo | ChiaraMadera |
| 34 | TSK 18 - Capa Controller | ChiaraMadera | ChiaraMadera |
| 4 | RF-01: Desarrolo Backend | los 4 | SilvioAlfredo |

### 2.3 Pendientes (OPEN sin asignar, 14)

#38 Configuración Kallithea + #15–#29 (automatizaciones HU-01–HU-13, ej. #29 HU-13,
#28 HU-12, #27 HU-11, #26 HU-10, #25 HU-09, #24 HU-08, #23 HU-07, #21 HU-06,
#20 HU-05, #18 HU-04, #17 HU-03, #16 HU-02, #15 HU-01). Sin responsable:
candidatas a asignar en el próximo sprint.

### 2.4 Hechas (CLOSED, 15)

#22 HU-07, #19 HU-05, #14 TK-05 Modelo de Negocios, #13 TK-03 Historias de Usuario,
#12 TK-04 Requerimientos Funcionales, #11 TK-02 Perfiles, #10 TK-01 Descripción,
#9 TK-10 APIRest, #8 TK-09 Actividad, #7 TK-08 Clases, #6 TK-07 Casos de Uso,
#5 TK-06 Wireframe PenPot, #3 Diseño del sistema, #2 Documentación, #1 Wiki.
(Asignados por issue en §2.1; todas con autor ChiaraMadera salvo #1–#4 de SilvioAlfredo.)

## 3. Sincronización con sinc-issues

- Las 28 `AL-*` del backlog siguen en `Issues_Backlog.md`; `issues.json`
  agrupa 44 `AL-*` + 13 `GH-*` (57 en total) y el remoto tiene 95 issues.
- Las remotas se importan a `sinc-issues/issues.json` como `GH-<n>`
  con `autor`, `asignado`, `gh_state` (OPEN/CLOSED) y `estado` local
  (Cerrado / En desarrollo / Pendiente). Ver `sinc-issues/README.md` y
  `sync_github.py`. El ejecutable las lista, edita y re-sube vía `gh`.

## 4. Anexo — mensajes hg por usuario

### Chiara Madera (19)
- Configurar TypeDoc para la documentación del frontend
- Integrar vistas de Trabajador con panel Admin y documentación JavaDoc y JSDoc
- Completar panel Admin web y documentación JSDoc y JavaDoc
- AuthServiceImpl movido a service.auth.impl; eliminacion de templates Thymeleaf y assets estaticos del panel de admin
- Corrección de errores de compilación, JPQL y lógica en Backend
- Added tag 0.6.1 for changeset c55aea90e946 · Corrección 0.6.1
- merge: sincronizar con remoto · Agregar dahsboard Admin con Thymeleaf
- Corregir capa mapper y service. Se actualizo la capa controller.
- Renombrar carpeta Frontend a FrontPrueba, ajustar vista movil a 600px, editar .hgignore, id valido de trabajador para pruebas
- Migrar archivos a tsx en el front, integrar Axios y habilitar CORS en back
- Reestructuracion de carpetas · Agregar carpeta Frontend (Inicio, Nueva Postulacion, Postularse)
- fix: Actualizar capa controller · fix: corrige mappers, actualiza controllers y limpia estructura
- Agrega capa Controller · Added tag 0.1 · Archivos del SPRINT 1 en Especificaciones.

### Julio Lasa (15)
- feat: Integración de vistas, flujos y navegación del rol Trabajador en frontend web
- Agregar frontend del rol empleador
- refactor(service): modularizar capa service en paquetes usuario y trabajo
- Creacion de tag 0.5.0 de Sprint 5 · Cierre del Sprint 5: Ajustes finales del frontend
- Estructura final: Carpeta Backend y eliminación de temporales · Renombrando a Backend · Eliminando carpeta Sprint-3
- Creación de tag v0.4.1 · Finalizar Sprint 4: Backend completo, migración a PostgreSQL y servicios listos
- Actualizacion capa de service · Agregado de comandos Windows en Instrucciones Container
- Creado Sprint 3 y agregado el backend · Creada la capa Service · Agrego archivo de prueba

### Silvio Riveros (32)
- (mobile): notificaciones, propuestas PRO y retiro de postulaciones · sistema de notificaciones, propuestas PRO y retiro de postulaciones
- Reestructuración del frontend a nuevos diseños + sincronización backend
- nueva version de frontend y swagger · correcciones BD + Swagger/OpenAPI
- Paginación, N+1, 409 y revocación JWT · Tests de autorización y contratos + limpieza
- Seguridad, integridad y documentación del backend · paginación, métricas admin, perfiles
- Added tag 0.6.0 · Reestructuración frontend React→Next.js + sincronización backend
- Sistema desacoplado + administración · ajustes backend (security/exception) + base frontend web
- Revert commit 50 + modularización repository · Backed out changeset · chore: ignorar .idea
- Reestructuración entity/dto · revisión avances + required · Added tag v0.4
- correcciones mappers/service (postulaciones/oportunidades) · remover carpetas sin seguimiento
- capa dto completa · los dto · Added tag v0.3.1 · correcciones clase usuario
- Merge final unificado · Merge sprint 3 + etiquetas · cambios sprint 3 · Added tag v0.3 · config properties
- base del proyecto + entitys · README.md

### Williams Gonzalez (16)
- feat: modulo complementario movil con react native y expo
- feat(frontend): maquetación pantallas de trabajador y mock data sprint 6
- refactor: modularizar DTOs y paquetes por dominio y corregir relaciones JPA
- Agregado de repositorios restantes y eliminacion de entidades duplicadas
- Implementacion completa de repository y correccion de dependencias
- Agregar reglas de .hgignore · Merge con cambios del remoto · Agrego carpeta mappers
- Merge con cambios de Julio · Merge con cambios de Chiara · Elimino TrbajadorService.java
- Added tag 0.2 · Diseño Sprint 2 (bocetos/diagramas) · archivos Sprint 2 en Diseño
- repository completa + TrabajadorService

## 5. Reparto de issues abiertas por responsable (20/10/2026)

> Criterio: el responsable de una issue es el desarrollador con la propiedad
> actual del código que la issue toca, según `hg log` (último autor por
> archivo) y el foco de trabajo por capa. Se reparte ~10 issues por persona.
> 41 issues abiertas, 0 sin asignar.

### 5.1 Evidencia por capa (archivos como último toucher)

| Capa | Dueño principal | Secundarios |
|---|---|---|
| BE/controller, dto, entity, config | SilvioAlfredo (63-70 %) | chiara_madera, juliolasa81 |
| BE/mapper, repository, service | chiara_madera (43 %) | SilvioAlfredo, juliolasa81 |
| BE/otros (OpenApi, Cors, pom) | SilvioAlfredo | juliolasa81 |
| FE web (pages/features) | chiara_madera (63-88 %) | SilvioAlfredo |
| FE móvil (Expo) | SilvioAlfredo (85 %) | williams-gonzalez |
| FE capa de datos (mapper/DTO/repo) | williams-gonzalez | — |
| Diseño / Especificaciones / Testing | chiara_madera, williams-gonzalez | SilvioAlfredo |

### 5.2 Issues por responsable

**SilvioAlfredo (10)** — lead backend, seguridad, OpenAPI, cliente móvil
| # | Issue | Área | Prio |
|---|---|---|---|
| 74 | AL-SEC-001 JWT secret hardcodeado | Seguridad | P0 |
| 75 | AL-SEC-002 Credencial PostgreSQL hardcodeada | Seguridad | P0 |
| 80 | AL-SEC-004 Endurecer recuperación y verificación de cuenta | Seguridad | P0 |
| 81 | AL-SEC-005 Evitar enumeración de usuarios | Seguridad | P0 |
| 82 | AL-SEC-006 Restringir lecturas y proyecciones por audiencia | Seguridad | P0 |
| 93 | AL-SEC-007 Cerrar concesión directa de PRO | Seguridad | P0 |
| 88 | AL-API-005 Identidad autenticada mínima y sesión coherente | Integración | P1 |
| 47 | GH-47 [HU-04] Registro — SMS y política de contraseña | Producto | P2 |
| 52 | GH-52 [HU-09] Notificaciones al publicar (<30 s) | Producto | P2 |
| 66 | AL-DOC-002 Completar contrato OpenAPI como fuente de verdad | Documentación | P2 |

**ChiaraMadera (11)** — frontend web, documentación, plan de pruebas
| # | Issue | Área | Prio |
|---|---|---|---|
| 70 | AL-FE-004 Definir frontend oficial y matriz de alcance | Frontend | P1 |
| 90 | AL-FE-008 Coherencia de notificaciones: flags y contador | Frontend | P1 |
| 78 | AL-FE-005 Quitar datos ficticios del Home y formularios | Frontend | P2 |
| 72 | AL-FUNC-002 Implementar notificaciones del matching | Producto | P2 |
| 73 | AL-FUNC-003 Eliminar placeholders | Producto | P2 |
| 86 | AL-TEST-004 Crear seed_tc_db_005.sql y separar schema/seed | Testing | P2 |
| 87 | AL-TEST-005 Ejecutar el ciclo S1 del plan de pruebas | Testing | P2 |
| 97 | AL-FE-009 Contraste de texto WCAG AA (Lighthouse) | Frontend | P2 |
| 98 | AL-FE-010 Atributo alt a imagen sin describir (Lighthouse) | Frontend | P2 |
| 99 | AL-SEC-011 Headers de seguridad en next.config.ts | Seguridad | P2 |
| 100 | AL-FE-011 Optimizar rendimiento del frontend (Lighthouse) | Frontend | P3 |

**juliolasa81 (10)** — capa service, infraestructura backend, flujos de web
| # | Issue | Área | Prio |
|---|---|---|---|
| 95 | AL-SEC-009 Centralizar vigencia, cupo y renovación | Seguridad | P1 |
| 49 | GH-49 [HU-06] Valoración — gate por finalización y tip | Producto | P2 |
| 50 | GH-50 [HU-07] Búsqueda filtrada + UI (<500 ms) | Producto | P2 |
| 53 | GH-53 [HU-10] Disponibilidad — excluir de búsquedas | Producto | P2 |
| 55 | GH-55 [HU-12] Estadísticas del trabajador + candado Pro | Producto | P2 |
| 63 | AL-DB-001 Reemplazar ddl-auto=update | Backend | P2 |
| 64 | AL-DB-002 Separar configuración de producción | Backend | P2 |
| 71 | AL-FUNC-001 Implementar matching local real | Producto | P2 |
| 84 | AL-TEST-002 Agregar tests de integración API | Testing | P2 |
| 61 | AL-CLEAN-001 Eliminar artefactos generados | Repositorio | P3 |

**williams-gonzalez (10)** — capa de datos (mapper/DTO/repo), móvil, fixes FE
| # | Issue | Área | Prio |
|---|---|---|---|
| 94 | AL-SEC-008 Impedir IDs del cliente en altas | Seguridad | P0 |
| 89 | AL-FE-007 Uniformar errores HTTP: 403 ≠ 401 y parseo tolerante | Frontend | P1 |
| 91 | AL-MOB-001 Corregir las 14 divergencias móviles | Frontend/Móvil | P1 |
| 92 | AL-MOB-002 Aporte verificable completo entre clientes | Frontend/Móvil | P1 |
| 96 | AL-SEC-010 Atribuir acciones administrativas al principal | Seguridad | P1 |
| 56 | GH-56 [HU-13] Reportes + foro por sector | Producto | P2 |
| 62 | AL-CLEAN-002 Revisar .hg | Repositorio | P3 |
| 77 | AL-CLEAN-003 Normalizar estructura de la carpeta Diseño | Repositorio | P3 |
| 79 | AL-FE-006 Eliminar código muerto del frontend | Frontend | P3 |
| 85 | AL-TEST-003 Ampliar Postman/test_endpoints a módulos de comunidad | Testing | P3 |

## 6. Etiquetado (tags) de las issues

Las 95 issues del remoto quedaron etiquetadas con un esquema uniforme:

| Tipo de etiqueta | Valores | Origen |
|---|---|---|
| Área | Frontend, Backend, Mobile, Seguridad, Integración, Testing, Documentación, Diseño, Producto, Repositorio, Automatización | campo `area` de `issues.json` / `clasificar_areas.py` |
| Prioridad | P0, P1, P2, P3 | campo `priority` |
| Tipo | bug, enhancement, documentation, wontfix | naturaleza del trabajo (hallazgos MEJ-* = bug) |
| Temáticas | UML, OpenAPI, Lighthouse, Accesibilidad, Performance, Mobile | contexto de la issue |

Notas:

- Las 13 automatizaciones `HU-xx: Automatizar …` (#15–#29) y #38 (Kallithea)
  llevan `wontfix` + `Automatización`/`Repositorio`: se cerraron por limpieza
  (ver `Unificacion_issues.md` §3).
- Las #34–#42 (TSK/TKS por capas) llevan `Backend` + `enhancement`: el trabajo
  existe en el código, se cerraron sin seguimiento.
- Se conservaron las etiquetas preexistentes (`documentation`, `UML`,
  `good first issue`) y se agregaron las que faltaban; no se quitó ninguna.
