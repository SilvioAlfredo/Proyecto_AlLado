# Diseño de la API REST

Documentación de los objetos que viajan entre el frontend y los controladores de Spring, y de los endpoints principales expuestos por cada controlador. Los DTOs aíslan las entidades JPA del exterior y usan `UsuarioResumenDTO` para referenciar usuarios sin exponer datos sensibles.

## 1. Data Transfer Objects (DTOs)

### Autenticación y seguridad

| DTO | Atributos principales | Propósito |
|---|---|---|
| **AuthRequestDTO** | `email`, `password` | Credenciales de inicio de sesión. |
| **AuthResponseDTO** | `token`, `email`, `roles` | Token JWT y datos de la sesión. |
| **RegisterRequestDTO** | `nombre`, `email`, `password`, `telefono`, `rol` | Registro de Empleador o Trabajador. |
| **ChangePasswordDTO** | `email`, `currentPassword`, `newPassword` | Cambio de contraseña autenticado. |
| **ForgotPasswordRequestDTO** | `email` | Solicitud de recuperación de contraseña. |
| **ResetPasswordRequestDTO** | `token`, `newPassword` | Restablecimiento mediante token. |

### Usuarios y perfiles

| DTO | Atributos principales | Propósito |
|---|---|---|
| **UsuarioDTO** *(abstracto)* | `id`, `nombre`, `email`, `telefono`, `habilitado`, `password`*(solo escritura)* | Base de los perfiles de usuario. |
| **UsuarioResumenDTO** | `id`, `nombre`, `email` | Referencia compacta a un usuario. |
| **TrabajadorDTO** | extiende `UsuarioDTO` + `fotografia`, `biografia`, `certificados`, `sector`, `especialidades`, `portafolioItems`, `horarios`, `preferenciaNotificacion`, `postulaciones` | Perfil completo del Trabajador. |
| **EmpleadorDTO** | extiende `UsuarioDTO` + `perfilEmpresarial`, `oportunidades` | Perfil del Empleador y su historial. |
| **AdministradorDTO** | extiende `UsuarioDTO` + `nivelAcceso` | Perfil del Administrador. |
| **EstadisticasTrabajadorDTO** | `ingresosTotales`, `ingresosUltimos3Meses`, `valoracionesCount`, `promedioValoraciones`, `ofertasPublicadas`, `postulacionesEnviadas/Seleccionadas/Completadas`, `nivelRecomendacion`, `perfilCompleto`, `suscripcionActiva` | Panel de estadísticas del Trabajador. |

### Perfil ampliado

| DTO | Atributos principales | Propósito |
|---|---|---|
| **PortafolioItemDTO** | `id`, `titulo`, `descripcion`, `urlArchivo` | Ítem de portafolio (PRO). |
| **HorarioDisponibilidadDTO** | `id`, `diaSemana`, `horaInicio`, `horaFin` | Franja de disponibilidad. |
| **PreferenciaNotificacionDTO** | `notificarEmail`, `notificarPush`, `alertasNuevasOfertas`, `alertasNuevasPostulaciones` | Preferencias de aviso del Trabajador. |

### Trabajo (oportunidades y postulaciones)

| DTO | Atributos principales | Propósito |
|---|---|---|
| **PostBaseDTO** *(abstracto)* | `id`, `titulo`, `contenido`, `fechaCreacion`, `autor` | Base de publicaciones con autor. |
| **OportunidadLaboralDTO** | extiende `PostBaseDTO` + `ubicacion`, `modalidad`, `rangoSalarial`, `activa`, `destacada`, `etiquetas`, `postulaciones` | Solicitud de mano de obra (Postulación Inversa). |
| **OportunidadLaboralResumenDTO** | `id`, `titulo`, `contenido`, `ubicacion`, `modalidad`, `rangoSalarial`, `activa`, `fechaCreacion` | Versión resumida para listados y referencias. |
| **PostulacionDTO** | extiende `PostBaseDTO` + `estado`, `remuneracionPretendida`, `oportunidadLaboral` | Postulación de un Trabajador. |
| **PostulacionResumenDTO** | `id`, `titulo`, `estado`, `remuneracionPretendida`, `fechaCreacion`, `autor` | Versión resumida de una postulación. |
| **EtiquetaDTO** | `id`, `nombre` | Etiqueta del catálogo de especialidades. |
| **FiltrosOportunidadesDTO** | `filtro`, `modalidad`, `etiquetas`, `soloDestacadas` | Criterios de búsqueda de oportunidades. |

### Comunidad y administración

| DTO | Atributos principales | Propósito |
|---|---|---|
| **ValoracionDTO** | `id`, `puntuacion` (1-5), `comentario`, `fechaCreacion`, `autor`, `trabajador` | Reseña del Empleador al Trabajador. |
| **FavoritoDTO** | `id`, `empleador`, `trabajador`, `fechaGuardado` | Profesional guardado como favorito. |
| **NotificacionDTO** | `id`, `destinatario`, `titulo`, `mensaje`, `tipo`, `referenciaId`, `leida`, `fechaCreacion` | Aviso dentro de la aplicación. |
| **ReporteDTO** | `id`, `motivo`, `descripcion`, `estado`, `fechaCreacion`, `reportante`, `reportado`, `moderador` | Denuncia para moderación. |
| **PublicidadDTO** | `id`, `titulo`, `contenido`, `urlImagen`, `urlDestino`, `fechaInicio`, `fechaFin`, `activa`, `anunciante` | Anuncio de cercanía (PRO). |
| **ComunicadoDTO** | `id`, `titulo`, `contenido`, `alcance`, `fechaEmision`, `emisor` | Comunicado institucional. |
| **PublicacionForoDTO** | `id`, `titulo`, `contenido`, `fechaCreacion`, `autor` | Publicación del foro vecinal. |
| **DonacionDTO** | `id`, `monto`, `moneda`, `mecanismoPago`, `fechaDonacion`, `usuario`, `estado`, `banco`, `tipoTransferencia`, `asuntoTransferencia` | Aporte o pago de suscripción PRO. |
| **PerfilEmpresarialDTO** | `id`, `nombreEmpresa`, `cuit`, `descripcion`, `sitioWeb`, `urlLogo`, `verificado`, `empleador`, `supervisadoPor` | Perfil del comercio (Sello de Confianza). |
| **SuscripcionDTO** | `id`, `usuario`, `nivel`, `fechaInicio`, `fechaFin`, `activa` | Nivel de suscripción del usuario. |
| **DashboardMetricsDTO** | `totalIngresos`, `metricContratos`, `metricTrabajadores`, `metricAvg`, `dineroComunidad`, `trabajosCompletados`, `tendencias`, `usuariosActivos`, `usuariosInactivos`, `postulacionesPorEstado` | Métricas del panel administrativo. |
| **TendenciaDTO** | `nombre`, `cantidad`, `porcentaje`, `colorClass` | Serie de tendencia del dashboard. |
| **EmpresaAdminDTO** | Datos de la empresa para el panel admin | Listado de empresas. |
| **PaginaDTO<T>** | Envoltorio genérico de paginación | Respuestas paginadas del panel admin. |
| **ErrorResponse** | `status`, `error`, `message`, `path`, `timestamp`, `errors` | Errores uniformes de la API. |

## 2. Endpoints principales por controlador

### Autenticación (`/auth`)

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| POST | `/auth/register` | Público | Registro de usuario. |
| POST | `/auth/login` | Público | Inicio de sesión (JWT). |
| GET | `/auth/verify-email` | Público | Verificación de correo por token. |
| POST | `/auth/logout` | Autenticado | Revoca el token actual. |
| POST | `/auth/password` | Autenticado | Cambio de contraseña. |
| POST | `/auth/password/verify` | Autenticado | Verificación de la contraseña actual. |
| POST | `/auth/password/forgot` | Público | Solicitud de recuperación. |
| POST | `/auth/password/reset` | Público | Restablecimiento con token. |

### Oportunidades (`/oportunidades`)

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| GET | `/oportunidades` | Público | Listado de oportunidades. |
| GET | `/oportunidades/{id}` | Público | Detalle de una oportunidad. |
| POST | `/oportunidades` | Empleador / Admin | Crear oportunidad (valida plan). |
| PUT | `/oportunidades/{id}` | Empleador / Admin | Editar oportunidad. |
| DELETE | `/oportunidades/{id}` | Empleador / Admin | Eliminar oportunidad. |
| POST | `/oportunidades/filtrar` | Público | Búsqueda por filtros. |
| GET | `/oportunidades/propuestas-pro` | Trabajador (PRO) | Oportunidades reservadas para PRO. |

### Postulaciones (`/postulaciones`)

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| GET | `/postulaciones` | Autenticado | Listado de postulaciones. |
| GET | `/postulaciones/{id}` | Autenticado | Detalle. |
| POST | `/postulaciones` | Trabajador | Crear postulación. |
| PATCH | `/postulaciones/{id}/seleccionar` | Empleador / Admin | Pasar a `SELECCIONADA`. |
| PATCH | `/postulaciones/{id}/rechazar` | Empleador / Admin | Pasar a `RECHAZADA`. |
| PATCH | `/postulaciones/{id}/completar` | Empleador / Admin | Pasar a `COMPLETADA`. |
| PATCH | `/postulaciones/{id}/retirar` | Trabajador | Pasar a `RETIRADA`. |

### Valoraciones (`/valoraciones`)

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| GET | `/valoraciones/trabajador/{id}` | Autenticado | Reseñas de un Trabajador. |
| GET | `/valoraciones/trabajador/{id}/promedio` | Autenticado | Promedio de puntuaciones. |
| GET | `/valoraciones/autor/{id}` | Autenticado | Reseñas emitidas por un Empleador. |
| POST | `/valoraciones` | Empleador / Admin | Crear valoración (una por par). |
| DELETE | `/valoraciones/{id}` | Admin | Eliminar valoración. |

### Comunidad y perfiles

| Controlador | Rutas destacadas |
|---|---|
| **Favoritos** | `GET /favoritos/empleador/{id}`, `POST/DELETE /favoritos/empleador/{id}/trabajador/{id}` |
| **Notificaciones** | `GET /notificaciones/usuario/{id}`, `GET /notificaciones/usuario/{id}/no-leidas`, `PATCH /notificaciones/{id}/leer`, `PATCH /notificaciones/usuario/{id}/leer-todas` |
| **Reportes** | `POST /reportes`, `GET /reportes`, `GET /reportes/estado/{estado}`, `PUT /reportes/{id}/resolver` (admin) |
| **Publicidades** | `GET /publicidades/vigentes`, `GET /publicidades/anunciante/{id}`, `POST/PUT/DELETE /publicidades` (empleador PRO / admin) |
| **Comunicados** | `GET /comunicados`, `GET /comunicados/alcance/{alcance}`, `POST/DELETE /comunicados` (admin) |
| **Foro** | `GET /foro`, `GET /foro/autor/{id}`, `POST /foro`, `DELETE /foro/{id}` |
| **Donaciones** | `POST /donaciones`, `GET /donaciones/usuario/{id}`, `POST /donaciones/{id}/verificar` (admin) |
| **Perfil empresarial** | `GET/PUT /perfiles-empresariales/empleador/{id}`, `PUT /perfiles-empresariales/{id}/verificar` (admin) |

### Perfil, taxonomía y suscripciones

| Controlador | Rutas destacadas |
|---|---|
| **Trabajadores** | `GET /trabajadores`, `GET /trabajadores/{id}`, `GET /trabajadores/filtrar`, `PUT /trabajadores/{id}`, `GET /trabajadores/{id}/estadisticas` |
| **Empleadores** | `GET /empleadores`, `GET /empleadores/{id}`, `PUT /empleadores/{id}` |
| **Etiquetas** | `GET /etiquetas`, `POST /etiquetas` (admin), `DELETE /etiquetas/{id}` (admin) |
| **Horarios** | `GET /horarios/trabajador/{id}`, `POST /horarios/trabajador/{id}`, `DELETE /horarios/{id}` |
| **Portafolio** | `GET /portafolio-items/trabajador/{id}`, `POST /portafolio-items/trabajador/{id}`, `DELETE /portafolio-items/{id}` |
| **Preferencias** | `GET/PUT /preferencias/trabajador/{id}` |
| **Suscripciones** | `GET /suscripciones/usuario/{id}`, `POST /suscripciones`, `PUT /suscripciones/cancelar/{id}` |
| **Usuarios** | `GET /usuarios` (admin), `PUT /usuarios/{id}`, `PUT /usuarios/{id}/deshabilitar|habilitar` (admin) |

### Administración (`/api/admin`, `ROLE_ADMIN`)

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/admin/dashboard/metrics` | Métricas del panel. |
| GET | `/api/admin/comunicados` | Listado de comunicados. |
| GET | `/api/admin/oportunidades` | Listado de oportunidades. |
| GET | `/api/admin/oportunidades/{id}/postulaciones` | Postulaciones de una oportunidad. |
| GET | `/api/admin/reportes` | Reportes para moderación. |
| GET | `/api/admin/foro` | Publicaciones del foro. |
| GET | `/api/admin/valoraciones` | Valoraciones por Trabajador. |
| GET | `/api/admin/donaciones` | Aportes registrados. |
| GET | `/api/admin/suscripciones` | Suscripciones. |
| GET | `/api/admin/publicidades` | Publicidades. |
| GET | `/api/admin/empresas` | Empresas registradas. |

## 3. Convenciones

- **Autenticación:** JWT Bearer en la cabecera `Authorization`. Las autoridades son `ROLE_ADMIN`, `ROLE_EMPLEADOR` y `ROLE_TRABAJADOR`.
- **Autorización fina:** además de la configuración de seguridad, los controladores usan `@PreAuthorize` para restringir operaciones por rol.
- **Paginación:** los listados del panel administrativo se devuelven como `PaginaDTO<T>`.
- **Errores:** todas las respuestas de error usan `ErrorResponse` (`status`, `error`, `message`, `path`, `timestamp`, `errors`).
- **Acceso público:** registro, login, verificación y recuperación de contraseña; lectura de oportunidades, comunicados, publicidades, foro, trabajadores, empleadores, etiquetas y perfiles empresariales. El resto requiere autenticación.
