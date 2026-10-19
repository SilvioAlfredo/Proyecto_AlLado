# AlLado — Frontend Desktop (`al_lado-app`)

Next.js 16 + React 19 + Tailwind v4. Tema oscuro `#0b0b0c / #1a1a1c` + amarillo `#ffc107`, según `diseños/desktop` (empleador / trabajador / admin).

Base API: `NEXT_PUBLIC_API_URL || http://localhost:8080`. JWT en `localStorage: auth_token`. 401 → `/login`.

## Rutas implementadas (51 páginas)

### Públicas
| Ruta | Diseño | Endpoints que consume |
|---|---|---|
| `/` Inicio | Inicio empleador | `POST /oportunidades/filtrar` |
| `/publicar` Nueva solicitud | Nueva solicitud | `POST /oportunidades` + `autor` resuelto (EMPLEADOR/ADMIN) |
| `/foro` | Comunidad / Foro | `GET /foro` · `GET /foro/autor/{autorId}` |
| `/login` `/register` `/forgot-password` `/reset-password` `/verify-email` | Iniciar sesión / Registro | `POST /auth/login` · `POST /auth/register` · `POST /auth/password/forgot` · `POST /auth/password/reset` · `GET /auth/verify-email?token=` |

### Empleador (`/empleador/*`)
| Ruta | Diseño | Endpoints |
|---|---|---|
| `/dashboard` Inicio | Inicio empleador (KPIs + CTA + Explorar) | `POST /oportunidades/filtrar` · `GET /postulaciones` |
| `/solicitudes` | Mis solicitudes (Todas/Activas/Cerradas) | `GET /oportunidades` · `PUT /oportunidades/{id}` (cerrar/reactivar) |
| `/solicitudes/[id]` | Gestión de solicitud | `GET /oportunidades/{id}` · `GET /postulaciones` · `PATCH /postulaciones/{id}/completar` · `PUT /oportunidades/{id}` |
| `/oportunidades` | Mis Ofertas | `POST /oportunidades/filtrar` · `POST /oportunidades` · `PUT /oportunidades/{id}` · `DELETE /oportunidades/{id}` |
| `/candidatos?oportunidadId=` | Postulantes recibidos | `GET /postulaciones` · `GET /oportunidades/{id}` · `PATCH /postulaciones/{id}/seleccionar\|rechazar` |
| `/trabajadores` | Buscar trabajadores | `GET /trabajadores/filtrar?etiqueta=&sector=` |
| `/notificaciones` | Notificaciones | `GET /notificaciones/usuario/{id}` · `PATCH /notificaciones/{id}/leer` · `PATCH /notificaciones/usuario/{id}/leer-todas` |
| `/trabajadores/[id]` | Perfil de trabajador | `GET /trabajadores/{id}` · `GET /valoraciones/trabajador/{id}` · `GET /portafolio-items/trabajador/{id}` · `GET /horarios/trabajador/{id}` · `POST-DELETE /favoritos/empleador/{e}/trabajador/{t}` |
| `/favoritos` | Favoritos | `GET /favoritos/empleador/{empleadorId}` · `DELETE /favoritos/empleador/{e}/trabajador/{t}` |
| `/contratos` | Contratos y trabajos | `GET /postulaciones` · `PATCH /postulaciones/{id}/completar` |
| `/valoraciones` | Valorar trabajador | `POST /valoraciones` (EMPLEADOR/ADMIN) · `GET /valoraciones/autor/{autorId}` |
| `/anuncios` | Mis anuncios / Crear o editar | `GET /publicidades` · `GET /publicidades/anunciante/{id}` · `POST /publicidades` · `PUT /publicidades/{id}` · `DELETE /publicidades/{id}` |
| `/empresa` | Perfil empresarial | `GET /perfiles-empresariales/empleador/{id}` · `PUT /perfiles-empresariales/empleador/{id}` |
| `/plan` | Mi plan (PRO) | `GET /suscripciones/usuario/{id}` · `POST /suscripciones` · `PUT /suscripciones/cancelar/{id}` |
| `/transferencias` | Transferencia e historial | `GET /donaciones/usuario/{id}` · `POST /donaciones` |
| `/perfil` | Mi perfil y cuenta | `GET /empleadores` → resolve id por email → `GET /empleadores/{id}` · `PUT /empleadores/{id}` |
| `/seguridad` | Mi cuenta / Seguridad | `POST /auth/password` · `POST /auth/logout` |

### Trabajador (`/trabajador/*`)
| Ruta | Diseño | Endpoints |
|---|---|---|
| `/oportunidades` | Oportunidades | `POST /oportunidades/filtrar` |
| `/oportunidades/[id]` | Detalle de oportunidad | `GET /oportunidades/{id}` |
| `/oportunidades/[id]/postular` | Enviar postulación | `GET /oportunidades/{id}` · `POST /postulaciones` (TRABAJADOR)
- `PATCH /postulaciones/{id}/retirar` (TRABAJADOR autor, desde PENDIENTE/SELECCIONADA) |
| `/filtros` | Filtros de oportunidades | `GET /etiquetas` · `POST /oportunidades/filtrar` |
| `/postulaciones` | Mis postulaciones | `GET /postulaciones` |
| `/propuestas` | Propuestas PRO | `GET /oportunidades/propuestas-pro?trabajadorId=` (requiere PRO) |
| `/postulaciones/[id]` | Detalle de postulación | `GET /postulaciones/{id}` · `PATCH /postulaciones/{id}/retirar` (PENDIENTE/SELECCIONADA) |
| `/dashboard` | Mi Panel | `GET /postulaciones` · `GET /trabajadores/por-email/estadisticas?email=` |
| `/perfil` | Mi perfil / Editar perfil | `GET /trabajadores` → resolve id → `GET /trabajadores/{id}` · `PUT /trabajadores/{id}` |
| `/portafolio` | Mi portafolio | `GET /portafolio-items/trabajador/{id}` · `POST /portafolio-items/trabajador/{id}` · `DELETE /portafolio-items/{id}` |
| `/disponibilidad` | Disponibilidad semanal | `GET /horarios/trabajador/{id}` · `POST /horarios/trabajador/{id}` · `DELETE /horarios/{id}` |
| `/valoraciones` | Valoraciones recibidas | `GET /valoraciones/trabajador/{id}` · `GET /valoraciones/trabajador/{id}/promedio` |
| `/actividad` | Mi actividad | `GET /postulaciones` |
| `/suscripcion` | Mi suscripción y aportes | `GET /suscripciones/usuario/{id}` · `POST /suscripciones` · `GET /donaciones/usuario/{id}` · `POST /donaciones` |
| `/preferencias` | Preferencias | `GET /preferencias/trabajador/{id}` · `PUT /preferencias/trabajador/{id}` |
| `/comunidad` | Comunidad | `GET /foro` · `GET /foro/autor/{autorId}` · `POST /foro` · `DELETE /foro/{id}` · `POST /reportes` |
| `/seguridad` | Mi cuenta / Seguridad | `POST /auth/password` · `POST /auth/logout` |

### Admin (`/admin/*`)
| Ruta | Diseño | Endpoints |
|---|---|---|
| `/dashboard` | 01_dashboard | `GET /api/admin/dashboard/metrics` (ADMIN) |
| `/personas` | 02_personas | `GET /trabajadores` · `GET /empleadores` · `GET /administradores` · `POST /administradores` · `DELETE /administradores/{id}` |
| `/usuarios` | Personas (tabla) | `GET /trabajadores` · `GET /empleadores` |
| `/usuarios/[id]` | Ficha de usuario | `GET /usuarios/{id}` · `PUT /usuarios/{id}` · `DELETE /usuarios/{id}` (ADMIN) |
| `/oportunidades` | 08_oportunidades_activas | `GET /oportunidades` · `GET /oportunidades/{id}` |
| `/reportes` | 10_reportes | `GET /reportes` · `GET /reportes/estado/{estado}` · `PUT /reportes/{id}/resolver` (ADMIN) |
| `/publicaciones` | Moderación | `GET /reportes` · `GET /foro` · `GET /comunicados` |
| `/foro` | 12_foro_y_valoraciones | `GET /foro` · `DELETE /foro/{id}` · `GET /valoraciones/trabajador/{id}` · `DELETE /valoraciones/{id}` (ADMIN) |
| `/donaciones` | 13_transferencias | `GET /donaciones` (ADMIN) · `GET /donaciones/{id}` · `POST /donaciones/{id}/verificar` (ADMIN) |
| `/suscripciones` | 15_suscripciones | `GET /suscripciones` (ADMIN) · `GET /suscripciones/{id}` · `DELETE /suscripciones/{id}` (ADMIN) |
| `/publicidad` | 17_publicidad | `GET /publicidades` · `POST /publicidades` · `PUT /publicidades/{id}` · `DELETE /publicidades/{id}` |
| `/empresas` | 19_empresas | `GET /empleadores` · `GET /perfiles-empresariales/empleador/{id}` · `PUT /perfiles-empresariales/{id}/verificar` (ADMIN) |
| `/etiquetas` | 21_etiquetas | `GET /etiquetas` · `GET /etiquetas/{id}` · `POST /etiquetas` (ADMIN) · `DELETE /etiquetas/{id}` (ADMIN) |
| `/comunicados` | 06_comunicados | `GET /comunicados` · `GET /comunicados/alcance/{alcance}` · `POST /comunicados` (ADMIN) · `DELETE /comunicados/{id}` (ADMIN) |

## Endpoints backend resultantes (98, base `http://localhost:8080`)

### Auth `/auth` (público salvo indicado)
- `POST /auth/login` `{email,password}` → `{token,email,roles}`
- `POST /auth/register` `{nombre,email,password,telefono,rol}` → `{token,...}`
- `GET /auth/verify-email?token=`
- `POST /auth/password/forgot` · `POST /auth/password/reset` (público)
- `POST /auth/password` · `POST /auth/password/verify` · `POST /auth/logout` (auth)

### Trabajo
- `GET /oportunidades?filtro=&page=&size=` · `GET /oportunidades/{id}` · `POST /oportunidades/filtrar` (público)
- `POST /oportunidades` · `PUT /oportunidades/{id}` · `DELETE /oportunidades/{id}` (EMPLEADOR/ADMIN)
- `GET /postulaciones` · `GET /postulaciones/{id}` (auth)
- `POST /postulaciones` (TRABAJADOR)
- `PATCH /postulaciones/{id}/retirar` (TRABAJADOR autor, desde PENDIENTE/SELECCIONADA)
- `PATCH /postulaciones/{id}/seleccionar|rechazar|completar` (EMPLEADOR/ADMIN)
- `GET /etiquetas` · `GET /etiquetas/{id}` · `POST /etiquetas` · `DELETE /etiquetas/{id}` (POST/DELETE ADMIN)

### Usuarios / Perfil
- `GET /trabajadores` · `GET /trabajadores/{id}` · `GET /trabajadores/filtrar?etiqueta=&sector=` · `POST /trabajadores` · `PUT /trabajadores/{id}` · `DELETE /trabajadores/{id}` (DELETE ADMIN)
- `GET /trabajadores/por-email/estadisticas?email=` · `GET /trabajadores/{id}/estadisticas`
- `GET /empleadores` · `GET /empleadores/{id}` · `POST /empleadores` · `PUT /empleadores/{id}` · `DELETE /empleadores/{id}` (DELETE ADMIN)
- `GET /usuarios` · `DELETE /usuarios/{id}` (ADMIN) · `GET /usuarios/{id}` · `PUT /usuarios/{id}` · `PUT /usuarios/{id}/deshabilitar|habilitar` (ADMIN)
- `GET /administradores` · `POST /administradores` · `GET /administradores/{id}` · `PUT /administradores/{id}` · `DELETE /administradores/{id}`
- `GET /suscripciones` · `GET /suscripciones/{id}` · `DELETE /suscripciones/{id}` (ADMIN) · `GET /suscripciones/usuario/{usuarioId}` · `POST /suscripciones` · `PUT /suscripciones/cancelar/{id}`
- `GET /portafolio-items/trabajador/{id}` · `POST /portafolio-items/trabajador/{id}` · `DELETE /portafolio-items/{id}` (POST/DELETE TRABAJADOR/ADMIN)
- `GET /horarios/trabajador/{id}` · `POST /horarios/trabajador/{id}` · `DELETE /horarios/{id}` (ídem)
- `GET /preferencias/trabajador/{id}` · `PUT /preferencias/trabajador/{id}` (PUT TRABAJADOR/ADMIN)

### Notificaciones
- `GET /notificaciones/usuario/{id}` · `GET /notificaciones/usuario/{id}/no-leidas` · `PATCH /notificaciones/{id}/leer` · `PATCH /notificaciones/usuario/{id}/leer-todas` (titular)

### Comunidad / Moderación
- `GET /foro` · `GET /foro/{id}` · `GET /foro/autor/{autorId}` · `POST /foro` · `DELETE /foro/{id}` (DELETE TRABAJADOR/EMPLEADOR/ADMIN)
- `GET /valoraciones/trabajador/{id}` · `GET /valoraciones/trabajador/{id}/promedio` · `GET /valoraciones/autor/{autorId}` · `POST /valoraciones` (EMPLEADOR/ADMIN) · `DELETE /valoraciones/{id}` (ADMIN)
- `GET /reportes` · `GET /reportes/{id}` · `GET /reportes/estado/{estado}` · `PUT /reportes/{id}/resolver?moderadorId=&estado=` (ADMIN) · `POST /reportes`
- `GET /publicidades` · `GET /publicidades/{id}` · `GET /publicidades/anunciante/{id}` · `GET /publicidades/vigentes` · `POST /publicidades` · `PUT /publicidades/{id}` · `DELETE /publicidades/{id}` (POST/PUT/DELETE EMPLEADOR/ADMIN)
- `GET /perfiles-empresariales/empleador/{id}` · `PUT /perfiles-empresariales/empleador/{id}` · `PUT /perfiles-empresariales/{id}/verificar?supervisorId=` (verificar ADMIN)
- `GET /favoritos/empleador/{id}` · `POST /favoritos/empleador/{e}/trabajador/{t}` · `DELETE /favoritos/empleador/{e}/trabajador/{t}` (POST/DELETE EMPLEADOR/ADMIN)
- `GET /comunicados` · `GET /comunicados/{id}` · `GET /comunicados/alcance/{alcance}` · `POST /comunicados` (ADMIN) · `DELETE /comunicados/{id}` (ADMIN)
- `GET /donaciones` (ADMIN) · `GET /donaciones/{id}` · `GET /donaciones/usuario/{usuarioId}` · `POST /donaciones` · `POST /donaciones/{id}/verificar?asunto=&monto=` (verificar ADMIN)
- `GET /api/admin/dashboard/metrics` (ADMIN)

## Notas de integración
- No existe `GET /trabajadores/perfil` ni `GET /empleadores/perfil`: el frontend resuelve `id` listando por `email` (`GET /trabajadores|/empleadores|/administradores` → find, ver `lib/usuarioRef.ts`) y luego usa `GET|PUT /{id}`.
- Lectura pública sin JWT: `POST /oportunidades/filtrar` y `GET` de `/etiquetas /trabajadores|/empleadores (solo base y /{id}) /valoraciones /horarios /portafolio-items /preferencias /perfiles-empresariales /favoritos` son `permitAll` en `SecurityConfig` (la home los consume anónima; las mutaciones siguen exigiendo rol por `@PreAuthorize`).
- `PUT /reportes/{id}/resolver`: `moderadorId` opcional; si se omite se usa el admin autenticado.
- `POST /suscripciones` con usuario existente reactiva la suscripción (nivel/fechas/activa) en vez de 409 duplicado.
- `POST /donaciones/{id}/verificar` compara `asunto` exacto y `monto` con `compareTo` (ignora escala): el alta debe declarar `asuntoTransferencia` para que la verificación pueda coincidir.
- `PUT /oportunidades/{id}` con `{activa:false}` implementa cerrar/reactivar (no hay `/cerrar`).
- `PATCH` (no PUT) para seleccionar/rechazar/completar postulaciones.
- Estados postulación backend: `PENDIENTE|SELECCIONADA|RECHAZADA|COMPLETADA`.
- Build backend: requiere Lombok ≥1.18.36 en JDK 23+ (`lombok.version` fijada a 1.18.46 en `pom.xml`); el target sigue siendo Java 21.
