---
markmap:
  maxWidth: 340
  initialExpandLevel: 2
  colorFreezeLevel: 2
---

# AlLado Backend Original — Arquitectura en capas (atómico)

## controller (21, REST por dominio)
### auth (/auth)
- POST login {email,password} → {token,email,roles} (🔓)
- POST register {nombre,email,password min8,telefono,rol TRABAJADOR|EMPLEADOR} (🔓)
- GET verify-email?token= (🔓)
- POST password y password/verify (sesión + email propio)
- POST password/forgot {email} y password/reset {token,newPassword} (🔓)
- POST logout con Bearer (🔒, revoca jti, 204)
### trabajo
- OportunidadLaboralController (/oportunidades): GET / (público, ?filtro=&page=&size=), GET /{id} (público), POST /filtrar (🔒), POST/PUT/DELETE (💼👑 titular, Básico 3/mes, sin transferencias)
- PostulacionController (/postulaciones): POST (🔧 titular, nace PENDIENTE), GET (🔒), PATCH seleccionar|rechazar|completar (💼👑 dueño, máquina de estados)
- EtiquetaController (/etiquetas): GET (🔒), POST/DELETE (👑, get-or-create con trim + retry)
### usuario
- UsuarioController (/usuarios): GET lista/baja 👑, GET/PUT perfil titular (id del path prevalece)
- TrabajadorController (/trabajadores): GET 🔒, POST registro 🔓, PUT titular, DELETE 👑, GET estadísticas titular
- EmpleadorController (/empleadores): igual (POST registro 🔓)
- AdministradorController (/administradores): todo 👑 por URL
- SuscripcionController (/suscripciones): lista/baja 👑, resto titular (crear, cancelar, por-usuario)
- AdminRestController (/api/admin, 👑): GET dashboard/metrics
### comunidad
- PublicacionForoController (/foro): GET público (?page=&size=), POST titular, DELETE dueño
- ComunicadoController (/comunicados): GET público (?page=&size=), POST/DELETE 👑
- ReporteController (/reportes): GET + resolver 👑 (?page=&size=), POST titular (nace PENDIENTE)
- ValoracionController (/valoraciones): GET 🔒 (?page=&size=), POST 💼 titular (1–5), DELETE dueño/👑
- DonacionController (/donaciones): GET 👑 (?page=&size=), GET/{id} titular, POST titular (PENDIENTE, compareTo en montos), POST /{id}/verificar?asunto=&monto= 👑 (otorga PRO)
- FavoritoController (/favoritos/empleador/{id}): GET/POST/DELETE 💼 titular
- PublicidadController (/publicidades): GET público, POST/PUT/DELETE 💼👑 titular (nueva siempre activa)
- PerfilEmpresarialController: GET/PUT /empleador/{id} titular (cuit único), PUT /{id}/verificar 👑 idempotente
### perfil
- HorarioDisponibilidadController + PortafolioItemController: GET/POST /trabajador/{id}, DELETE /{id} (🔧👑 titular; día LUNES..DOMINGO, sin solapes)
- PreferenciaNotificacionController: GET/PUT /trabajador/{id} (🔧👑 titular)

## service (interfaces + impl/, orquestación + reglas)
### trabajo/impl
- OportunidadLaboralServiceImpl: guardar (lock pesimista + cuota + etiquetas gestionadas + anti-transferencia), cerrar, eliminar (FK-check), spec de búsqueda, obtenerTrabajadoresPorEtiqueta (JOIN+FETCH)
- PostulacionServiceImpl: crear (titular), seleccionar (solo PENDIENTE), rechazar (PENDIENTE|SELECCIONADA), completar (solo SELECCIONADA), estadísticas (conteos reales, suscripcionActiva con isActiva())
- EtiquetaServiceImpl: obtenerOCrearPorNombre (trim + retry ante carrera)
### usuario/impl
- AuthServiceImpl (BCrypt, rol validado, reset 24h un solo uso, logout revoca jti + purga)
- Trabajador/EmpleadorServiceImpl (update con teléfono+perfil, email con Objects.equals, delete con FK-check)
- SuscripcionServiceImpl (PRO inactiva/vencida → cupo básico, trim case-insensitive)
- AdministradorServiceImpl (CRUD + obtenerMetricasDashboard con agregaciones)
- UsuarioServiceImpl (delega por tipo, ownership)
### comunidad/impl + perfil/impl
- Crear resuelve FK gestionadas + requireSelfOrAdmin; resolver/verificar con guards e idempotencia; eliminar con titularidad; horarios anti-solape; preferencias persisten los 4 flags

## repository (Spring Data, solo queries)
- trabajo: specs + @EntityGraph/@JOIN FETCH, AVG/SUM/COUNT, findTendenciasPorEtiqueta, findByNombreIgnoreCase, countByAutorId...(paginados donde hay lista masiva)
- comunidad: listados paginados con fetch (autor, emisor, reportante/reportado/moderador, usuario, anunciante, empleador), sumTotalMontos
- perfil/usuario/seguridad: finders por dueño, countActivasPro, findByEspecialidadNombreIgnoreCase, TokenRevocadoRepository (existsByJti, purge)

## entity (JPA, PostgreSQL al_lado_db, 23 tablas)
- Usuario abstracto JOINED (nombre/email unique/telefono NOT NULL/password, habilitado, emailVerificado, equals/hashCode)
- Trabajador (postulaciones, especialidades M:N + helpers, portafolio, horarios, preferencia, valoraciones, sector, tokenPush)
- Empleador (oportunidades, favoritos, publicidades, valoracionesOtorgadas) · Administrador (nivelAcceso) · Suscripcion (usuario unique, nivel, vigencia)
- PostBase abstracto JOINED (titulo, contenido, fechaCreacion @PrePersist, autor NOT NULL)
- OportunidadLaboral (ubicacion, modalidad, rangoSalarial, activa, etiquetas, add/removeEtiqueta, helpers empleador)
- Postulacion (estado + constantes, remuneracion Double, oportunidad NOT NULL, helpers trabajador)
- Etiqueta (nombre unique) · TokenRevocado (jti unique, expira)
- Comunidad: Comunicado, Donacion (MONEDA_POR_DEFECTO, estado), Favorito (unique par), PerfilEmpresarial (empleador unique, cuit unique, verificado), PublicacionForo, Publicidad (anunciante, vigencia, activa), Reporte (whitelist estados), Valoracion (puntuacion @NotNull 1–5)
- Perfil: HorarioDisponibilidad, PortafolioItem, PreferenciaNotificacion (4 flags default true)
- PrePersist con null-check · Sin soft delete (activa/habilitado como estado)

## dto (contratos + Bean Validation)
- auth: login/register/change/forgot/reset + AuthResponse{token,email,roles}
- trabajo: PostBaseDTO abstracto, Oportunidad(DTO+Resumen), Postulacion(DTO+Resumen), EtiquetaDTO, Filtros, TendenciaDTO
- usuario: UsuarioDTO abstracto WRITE_ONLY password, resúmenes, Trabajador/Empleador/Suscripcion/Estadisticas/AdminDTO
- comunidad/perfil: uno por entidad; @Pattern en estados/rol/nivel/modalidad/día; fechas server-generated READ_ONLY; @Size acordes a columnas
- ErrorResponse + DashboardMetricsDTO (admin)

## mappers (manuales @Component, sin MapStruct)
- Copian atributos propios + resumen; relaciones FK las pone el service (documentado por mapper)
- PostBaseMapper base; PostulacionMapper delega resumen; password solo en toEntity (hash en services)
- MONEDA_POR_DEFECTO y ESTADO_PENDIENTE como constantes

## security + config + exception
- SecurityConfig (públicos: auth, registro, GET catálogos, Swagger; entryPoint 401; CSRF off; STATELESS)
- JwtUtils (HS256, jti UUID, 24h) + JwtAuthenticationFilter (firma+expiración+revocados → 401) + UserDetailsServiceImpl (rechaza deshabilitados) + SecurityUtils (requireSelfOrAdmin/isAdmin/currentUserId)
- Handlers: 404/409-duplicado/400/401/403/409-integridad/500 sin filtrar + CorsConfig (orígenes por property) + OpenApiConfig (bearer-jwt global)

## Transversal
- @Transactional en todos los writes; @Valid en todos los RequestBody; BCrypt siempre
- Tests: unitarios puros + Mockito titularidad + slices MockMvc 401/403/200/201 (55/56; solo falla contexto Spring sin PostgreSQL)
- Deuda: ddl-auto=update (sin Flyway), secretos en properties, placeholders en estadísticas, lecturas cruzadas trabajo→valoraciones, sin tests de integración con BD
