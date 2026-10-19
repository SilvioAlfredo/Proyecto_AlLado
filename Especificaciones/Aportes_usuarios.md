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

- Las 24 `AL-*` locales siguen en `Issues_Backlog.md` / `issues.json`.
- Las 38 de GitHub se importan a `sinc-issues/issues.json` como `GH-<n>`
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
