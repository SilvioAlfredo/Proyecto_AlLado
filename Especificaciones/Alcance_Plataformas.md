# Alcance de plataformas — qué se soporta y qué no

> **Responde a:** AL-FE-004 (MEJ-030) · R19 · T41/T46.
>
> Este documento es la fuente de verdad sobre **qué aplicación es oficial**, qué
> función está disponible en cada plataforma y qué se declara fuera de alcance.
> Si una función no figura acá, no está soportada.

## Resumen

- Hay **dos aplicaciones oficiales**: web (`al_lado-app`) y mobile (`al-lado-mobile`).
  Las dos consumen el mismo backend Spring Boot.
- `FrontPrueba` **no es parte del producto**: solo existe en la rama
  `origin/prueba_front`, nunca llegó a `main` y no se mantiene.
- Cuatro funciones están anunciadas en la documentación pero **no tienen recorrido
  alcanzable** en ninguna app. Cada una tiene una decisión propuesta abajo.
- De las tres ambigüedades del backlog, **dos ya están resueltas por el código**
  (reportes y etiquetas) y **una requiere decisión de negocio** (propuestas PRO).

## Aplicaciones soportadas

| Aplicación | Stack | Alcance | Cantidad | Estado |
|---|---|---|---|---|
| `Frontend/al_lado-app` | Next.js 16.3.6 · React 19 · Tailwind 4 | Web responsive, roles visitante / trabajador / empleador / admin | 73 páginas | **Soportada** |
| `Frontend/al-lado-mobile` | Expo SDK 57 · expo-router · React Native 0.86 | App móvil, roles trabajador / empleador / admin (sin navegación pública) | 43 pantallas | **Soportada** |
| `Backend` | Spring Boot · PostgreSQL | API REST compartida (98 endpoints) | — | **Soportado** |
| `FrontPrueba` | React legacy | Tres pantallas del sprint inicial | 3 vistas | **Archivado** — rama `origin/prueba_front` |

### Diferencias estructurales entre las dos apps

| Tema | Web | Mobile |
|---|---|---|
| Acceso sin cuenta | Sí: Home y foro | No: `app/index.tsx` redirige a login |
| Registro | Completo (login, register, forgot, reset, verify-email) | Parcial (login, register, forgot; sin reset ni verify) |
| Cambio de contraseña | Trabajador y empleador (`/seguridad`) | Solo empleador (`/cuenta`) |
| Panel admin | 25 rutas | 5 pantallas (dashboard, personas, moderación, aportes, gestión) |

## Cómo leer la matriz

- **Sí** — la función tiene pantalla, contrato y flujo completo.
- **Parcial** — existe pero le falta un paso del criterio de aceptación (se indica cuál).
- **No** — no hay pantalla ni recorrido.
- **—** — la plataforma no tiene ese rol por diseño.

## Matriz función × rol × plataforma

### Visitante

| Función | Web | Mobile | Contrato | Nota |
|---|---|---|---|---|
| Explorar oportunidades | Sí `/` | No | `POST /oportunidades/filtrar` | Mobile exige sesión |
| Ver detalle de oportunidad | No | No | `GET /oportunidades/{id}` | El Home muestra tarjetas sin enlace; el detalle exige rol TRABAJADOR |
| Publicar necesidad sin cuenta | No | No | `POST /oportunidades` | `/publicar` es accesible pero `obtenerRefSesion()` exige sesión |
| Registro con verificación de email | Sí | Parcial | `POST /auth/register` · `GET /auth/verify-email` | Mobile no tiene pantalla de verificación |
| Recuperar contraseña | Sí (forgot + reset) | Parcial | `POST /auth/password/forgot` · `/reset` | Mobile solo pide el token |
| Leer foro | Sí `/foro` | No | `GET /foro` | |

### Trabajador

| Función | Web | Mobile | Contrato | Nota |
|---|---|---|---|---|
| Buscar y filtrar oportunidades | Sí | Sí | `POST /oportunidades/filtrar` | |
| Postularse | Sí | Sí | `POST /postulaciones` | |
| Retirar postulación | Sí | Sí | `PATCH /postulaciones/{id}/retirar` | |
| Mis postulaciones | Sí | Sí | `GET /postulaciones` | |
| Propuestas PRO | Sí | Sí | `GET /oportunidades/propuestas-pro` | Requiere suscripción vigente |
| Vitrina: perfil, portafolio, disponibilidad, preferencias | Sí | Sí | `/trabajadores` · `/portafolio-items` · `/horarios` · `/preferencias` | |
| Valoraciones recibidas | Sí | Sí | `GET /valoraciones/trabajador/{id}` | |
| **Ver ficha del empleador desde la oferta** | **No** | **No** | `GET /empleadores/{id}` público | Ver brecha 1 |
| Comunidad: publicar y reportar | Sí | Sí | `GET/POST/DELETE /foro` · `POST /reportes` | |
| Suscripción PRO y aportes | Sí | Sí | `/suscripciones` · `/donaciones` | |
| **Leer comunicados institucionales** | **No** | **No** | `GET /comunicados` | Ver brecha 3 |
| Notificaciones | Sí | Sí | `GET /notificaciones/usuario/{id}` | |
| Cambiar contraseña | Sí | **No** | `POST /auth/password` | Mobile solo el empleador |

### Empleador

| Función | Web | Mobile | Contrato | Nota |
|---|---|---|---|---|
| Publicar y editar solicitud | Sí | Sí | `POST/PUT /oportunidades` | |
| Gestionar postulantes | Sí | Sí | `GET /postulaciones` · `PATCH .../seleccionar\|rechazar` | |
| Directorio de profesionales | Sí | Sí | `GET /trabajadores/filtrar` | |
| Favoritos | Sí | Sí | `/favoritos/empleador/{e}/trabajador/{t}` | |
| Contratos y completar trabajo | Sí | Sí | `PATCH /postulaciones/{id}/completar` | |
| Valorar al trabajador | Sí | Sí | `POST /valoraciones` | Única por trabajador |
| **Donación posterior a la valoración** | **No** | **No** | `POST /donaciones` | Ver brecha 2 |
| Perfil empresarial | Sí | Sí | `/perfiles-empresariales` | Verificación solo en web admin |
| Plan PRO | Sí | Sí | `/suscripciones` | |
| Publicar publicidad | Sí | Sí | `POST/PUT/DELETE /publicidades` | Requiere PRO |
| **Ver publicidad vigente como audiencia** | **No** | **No** | `GET /publicidades/vigentes` | Ver brecha 4 |
| Transferencias y aportes | Sí | Sí | `/donaciones` | |

### Administrador

| Función | Web | Mobile | Contrato | Nota |
|---|---|---|---|---|
| Dashboard y métricas | Sí | Sí | `GET /api/admin/dashboard/metrics` | |
| Personas y usuarios | Sí | Parcial | `/trabajadores` · `/empleadores` · `/usuarios` | Mobile no tiene ficha de usuario |
| Moderar reportes | Sí | Sí | `GET /reportes` · `PUT /reportes/{id}/resolver` | Estados finales cerrados |
| Moderar foro y valoraciones | Sí | No | `DELETE /foro/{id}` · `DELETE /valoraciones/{id}` | |
| Donaciones y verificación | Sí | Sí | `GET /donaciones` · `POST /donaciones/{id}/verificar` | |
| Suscripciones | Sí | No | `GET/DELETE /suscripciones` | |
| Publicidad | Sí | No | `/publicidades` | Mobile no administra anuncios |
| Empresas y verificación | Sí | No | `PUT /perfiles-empresariales/{id}/verificar` | |
| Etiquetas | Sí | Sí | `POST/DELETE /etiquetas` | Catálogo cerrado |
| Emitir comunicados | Sí | Sí | `POST /comunicados` | Lectura por miembros: brecha 3 |
| Oportunidades activas | Sí | No | `GET /oportunidades` | |

## Cobertura de prueba

Esta matriz verifica **ruta alcanzable y contrato**. La cobertura de prueba por
función no se duplica acá: vive en `Especificaciones/Testing.md` (casos `TC-*`) y
en `Testing/e2e/casos_api.json`. Una función nueva se considera soportada recién
cuando tiene las tres cosas; hoy las filas marcadas **No** son justamente las que
no tienen recorrido ni caso ejecutable.

## Brechas: funciones anunciadas sin recorrido

Las cuatro aparecen en `Historia de usuario.md` y en `Requerimientos Funcionales.md`,
pero ninguna app las expone. **Decisión propuesta por función**, con responsable.

| # | Función | Evidencia | Decisión propuesta | Costo | Responsable |
|---|---|---|---|---|---|
| 1 | Ficha del empleador desde la oferta (HU-05 CA-01) | Web `/trabajador/oportunidades/[id]` nunca resuelve el autor; mobile muestra `Publicado por {nombre}` como texto plano. El contrato ya existe: `GET /empleadores/{id}` devuelve `perfilEmpresarial` + `oportunidades`, y `GET /oportunidades/{id}/contacto` restringe el contacto. | **Completar en web** (ruta pública `/empleador/[id]` y enlace desde el detalle). **Declarar fuera en mobile** para esta iteración. | Bajo: reutiliza contrato existente | Chiara |
| 2 | Donación posterior a la valoración (HU-06 CA-02) | Ni `/empleador/valoraciones` ni `contratos-valorar` ofrecen el paso; la donación existe suelta en `/empleador/transferencias` y `perfil/suscripcion`. | **Completar en web**: CTA al terminar de valorar que lleve a transferencias con monto precargado. **Declarar fuera en mobile**. | Bajo | Chiara |
| 3 | Comunicados hacia miembros (HU-15 CA-01) | Solo existe emisión (web `/admin/comunicados`, mobile `admin/gestion`). Ninguna pantalla de lectura para trabajador o empleador. | **Completar en ambas**: ruta `/comunicados` en web y sección dentro de Notificaciones en mobile. `GET /comunicados` ya está disponible. | Medio | Chiara |
| 4 | Publicidad exhibida a la audiencia (HU-18 CA-02) | `GET /publicidades/vigentes` existe y `moderacionService.vigentes()` lo consume, pero **ningún componente lo usa**. | **Decidir**: completar (slot en Home web) o declarar fuera y borrar el método muerto. | Bajo si se completa en web | Silvio (producto) |

> Mientras una brecha no se resuelva, se declara como limitación visible: se quita
> la promesa de la documentación pública o se muestra como "próximamente" en la app.
> No alcanza con dejarla en este documento.

## Decisiones de negocio

| # | Tema | Qué hace el código hoy | Decisión | Responsable |
|---|---|---|---|---|
| 1 | Propuestas PRO: reservadas vs priorizadas | `propuestasProParaTrabajador` filtra las oportunidades activas por las especialidades del trabajador y ordena las destacadas primero. **No** hay oportunidades reservadas ni excluidas para no-PRO: cualquier usuario ve todas en el buscador. | **Documentar como "priorizadas por especialidad"** y corregir la redacción de `Modelo de negocio.md` y HU-03 CA-03, que dicen "reservadas". Alternativa: implementar reserva real (mayor costo, requiere campo nuevo). | Silvio |
| 2 | Reporte: revisión → final | `PENDIENTE → EN_REVISION → RESUELTO\|DESCARTADO`. Los dos últimos son finales y no se reabren; `EN_REVISION` sí admite avance. Web y mobile muestran los cuatro estados. | **Cerrado**: el código coincide con el criterio. Solo falta que quede escrito acá. | — |
| 3 | Catálogo de etiquetas: abierto vs cerrado | `RF-05` exige catálogo cerrado; `POST/DELETE /etiquetas` es exclusivo de ADMIN. Los usuarios eligen del catálogo, no crean. | **Cerrado con curaduría del admin.** No requiere cambio; se documenta para cerrar la ambigüedad. | — |

## Regla de alcance para nuevas funciones

1. Toda función anunciada necesita **ruta + contrato + prueba** antes de considerarse soportada.
2. Si no se implementa, se agrega a la tabla de brechas con decisión **declarar fuera**
   y se elimina la promesa de la documentación.
3. La matriz se actualiza en el mismo commit que el cambio de alcance.

## Verificación

- [x] Web identificada como app oficial (`Frontend/al_lado-app`).
- [x] Mobile identificada como app oficial (`Frontend/al-lado-mobile`).
- [x] `FrontPrueba` declarado archivado (solo en `origin/prueba_front`).
- [x] Las cuatro funciones anunciadas tienen evidencia y decisión propuesta.
- [x] Las tres ambigüedades tienen estado y responsable.
- [ ] Decisiones de brechas 1–4 y PRO aprobadas por producto.
- [ ] README raíz enlaza este documento.
