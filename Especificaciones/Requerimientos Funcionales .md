# Requerimientos Funcionales - AlLado

Este documento describe los requerimientos funcionales vigentes, alineados con la implementación actual del sistema. Las referencias a clases indican el DTO o entidad que materializa cada requerimiento y, cuando corresponde, el endpoint asociado.

---

### RF-01 | Registro de Usuarios

| Atributo | Detalle Técnico |
| :--- | :--- |
| **Identificador** | RF-01 |
| **Nombre** | Registro de Usuarios |
| **Descripción** | El sistema **debe permitir** a los visitantes crear una cuenta seleccionando el rol de Trabajador o Empleador mediante un formulario que valide los campos obligatorios: nombre, correo electrónico, teléfono y contraseña. |
| **Prioridad** | Alta (Must Have) |
| **Actor / Fuente** | Visitante / `RegisterRequestDTO` / `POST /auth/register` |
| **Restricciones de Datos** | • Nombre: obligatorio, máximo 100 caracteres.<br>• Correo electrónico: obligatorio, formato válido y único, máximo 150 caracteres.<br>• Teléfono: obligatorio, máximo 20 caracteres (incluye código de país).<br>• Contraseña: obligatoria, mínimo 8 caracteres.<br>• Rol: valores permitidos `TRABAJADOR` o `EMPLEADOR`. |
| **Criterio de Verificación** | El sistema crea la cuenta, devuelve un token JWT (`AuthResponseDTO`) con el correo y los roles, y emite un token de verificación de correo. La cuenta queda marcada con `emailVerificado = false` hasta que el usuario confirme mediante `GET /auth/verify-email`. La verificación de correo no impide iniciar sesión. |

---

### RF-02 | Perfil del Trabajador

| Atributo | Detalle Técnico |
| :--- | :--- |
| **Identificador** | RF-02 |
| **Nombre** | Perfil del Trabajador |
| **Descripción** | El sistema **debe permitir** al usuario con rol Trabajador configurar su vitrina digital: datos personales, fotografía, biografía, certificados, sector barrial y sus especialidades tomadas del catálogo de etiquetas. |
| **Prioridad** | Alta (Must Have) |
| **Actor / Fuente** | Trabajador / `TrabajadorDTO` / `PUT /trabajadores/{id}` |
| **Restricciones de Datos** | • Especialidades: deben pertenecer al catálogo de `Etiqueta`.<br>• Fotografía, biografía y certificados: opcionales.<br>• El campo `password` del DTO es de solo escritura y no se expone en las respuestas. |
| **Criterio de Verificación** | Al guardar, el perfil queda disponible en `GET /trabajadores/{id}` y en el directorio. El portafolio y el acceso a "Propuestas PRO" se rigen por RF-15. |

---

### RF-03 | Perfil del Empleador

| Atributo | Detalle Técnico |
| :--- | :--- |
| **Identificador** | RF-03 |
| **Nombre** | Perfil del Empleador |
| **Descripción** | El sistema **debe compilar y mostrar** la información del Empleador junto con el historial público de las oportunidades que ha publicado, sin exponer credenciales ni datos privados. |
| **Prioridad** | Media (Should Have) |
| **Actor / Fuente** | Empleador / `EmpleadorDTO` / `GET|PUT /empleadores/{id}` |
| **Restricciones de Datos** | • La contraseña nunca se incluye en las respuestas (`WRITE_ONLY`).<br>• El perfil empresarial asociado es opcional y depende de una suscripción PRO (RF-14). |
| **Criterio de Verificación** | La consulta recupera y renderiza correctamente la lista de oportunidades del Empleador y, si existe, su perfil empresarial. |

---

### RF-04 | Solicitud de Mano de Obra (Postulación Inversa)

| Atributo | Detalle Técnico |
| :--- | :--- |
| **Identificador** | RF-04 |
| **Nombre** | Solicitud de Mano de Obra (Postulación Inversa) |
| **Descripción** | El sistema **debe permitir** a un Empleador autenticado publicar una necesidad puntual del barrio, especificando título, descripción, ubicación, modalidad, rango salarial estimado y etiquetas de especialidad, y **debe permitir** a los Trabajadores postularse a ella. |
| **Prioridad** | Alta (Must Have) |
| **Actor / Fuente** | Empleador y Trabajador / `OportunidadLaboralDTO`, `PostulacionDTO` / `POST /oportunidades`, `POST /postulaciones` / UC1 |
| **Restricciones de Datos** | • `modalidad`: `PRESENCIAL`, `REMOTO` o `HIBRIDO`.<br>• `ubicacion`: texto libre (no se utiliza geolocalización ni coordenadas).<br>• Solo el propio Empleador (o un Administrador) puede crear, editar o eliminar su oferta.<br>• Al crear una oferta se exige ser el autor indicado; al editar no puede transferirse la titularidad. |
| **Criterio de Verificación** | Al confirmar la publicación, la oferta se persiste y queda visible en `GET /oportunidades`. Un Empleador en **Nivel Básico** solo puede mantener hasta **3 ofertas activas creadas en el mes**; al superar el límite el sistema bloquea la publicación con un mensaje que invita a pasar a PRO. Un Trabajador puede postularse con un mensaje (`contenido`) y una `remuneracionPretendida`. |

---

### RF-05 | Sistema de Valoraciones y Reseñas

| Atributo | Detalle Técnico |
| :--- | :--- |
| **Identificador** | RF-05 |
| **Nombre** | Sistema de Valoraciones y Reseñas |
| **Descripción** | El sistema **debe permitir** al Empleador calificar a un Trabajador asignando una puntuación de 1 a 5 estrellas y, opcionalmente, un comentario escrito, para construir la reputación vecinal. |
| **Prioridad** | Alta (Must Have) |
| **Actor / Fuente** | Empleador / `ValoracionDTO` / `POST /valoraciones` / UC5 |
| **Restricciones de Datos** | • `puntuacion`: obligatoria, entero entre 1 y 5.<br>• `comentario`: opcional.<br>• Un Empleador solo puede valorar **una vez** a un mismo Trabajador.<br>• El autor debe ser un Empleador y el trabajador valorado debe existir. |
| **Criterio de Verificación** | Al guardar, la reseña se persiste y se expone en `GET /valoraciones/trabajador/{id}`. El sistema calcula el promedio en `GET /valoraciones/trabajador/{id}/promedio` y un *nivel de recomendación* (1-5) dentro del panel de estadísticas (RF-09). Un intento de valoración duplicada es rechazado. |

---

### RF-06 | Categorización por Especialidad

| Atributo | Detalle Técnico |
| :--- | :--- |
| **Identificador** | RF-06 |
| **Nombre** | Categorización por Especialidad |
| **Descripción** | El sistema **debe proveer y mantener** un catálogo cerrado y estandarizado de etiquetas de especialidad (ej. Electricidad, Cuidado de personas, Programación) que indexa de forma unívoca los perfiles de los Trabajadores y las oportunidades. |
| **Prioridad** | Alta (Must Have) |
| **Actor / Fuente** | Administrador / `EtiquetaDTO` / `GET|POST|DELETE /etiquetas` / UC3 |
| **Restricciones de Datos** | • `nombre`: obligatorio y único.<br>• Una etiqueta asociada a Trabajadores u Oportunidades no puede eliminarse. |
| **Criterio de Verificación** | Los filtros del directorio utilizan exclusivamente este catálogo: los Trabajadores se buscan por `etiqueta` y `sector` (`GET /trabajadores/filtrar`), y las oportunidades por texto, modalidad, etiquetas y carácter destacado (`POST /oportunidades/filtrar`). |

---

### RF-07 | Favoritos

| Atributo | Detalle Técnico |
| :--- | :--- |
| **Identificador** | RF-07 |
| **Nombre** | Favoritos |
| **Descripción** | El sistema **debe permitir** a los Empleadores guardar perfiles de Trabajadores en una lista personalizada para facilitar accesos directos y contrataciones futuras. |
| **Prioridad** | Baja (Could Have) |
| **Actor / Fuente** | Empleador / `FavoritoDTO` / `POST|DELETE /favoritos/empleador/{id}/trabajador/{id}` / UC6 |
| **Restricciones de Datos** | • Relación única Empleador–Trabajador.<br>• Se registra la fecha de guardado. |
| **Criterio de Verificación** | Al marcar un favorito, este aparece en `GET /favoritos/empleador/{id}`; al quitarlo, desaparece de la lista. |

---

### RF-08 | Historial de Contratos

| Atributo | Detalle Técnico |
| :--- | :--- |
| **Identificador** | RF-08 |
| **Nombre** | Historial de Contratos |
| **Descripción** | El sistema **debe registrar** cada interacción contractual a través del ciclo de vida de las postulaciones, conservando los participantes, las fechas, las etiquetas del servicio y el estado de la transacción para el seguimiento de la actividad. |
| **Prioridad** | Alta (Must Have) |
| **Actor / Fuente** | Sistema AlLado / `Postulacion` / `PATCH /postulaciones/{id}/{accion}` / UC1 |
| **Restricciones de Datos** | • Estados válidos: `PENDIENTE`, `SELECCIONADA`, `RECHAZADA`, `COMPLETADA`, `RETIRADA`.<br>• Solo una postulación `SELECCIONADA` puede completarse.<br>• El Empleador selecciona/rechaza/completa; el Trabajador puede retirarse mientras la postulación siga abierta. |
| **Criterio de Verificación** | Cada cambio de estado se persiste y queda disponible en el listado de postulaciones. El Empleador cuenta con un historial cronológico de los servicios contratados y el Trabajador con el suyo. |

---

### RF-09 | Panel de Estadísticas

| Atributo | Detalle Técnico |
| :--- | :--- |
| **Identificador** | RF-09 |
| **Nombre** | Panel de Estadísticas |
| **Descripción** | El sistema **debe calcular y desplegar** datos de rendimiento del Trabajador: ingresos totales y de los últimos 3 meses, postulaciones enviadas, seleccionadas y completadas, cantidad y promedio de valoraciones y nivel de recomendación. |
| **Prioridad** | Media (Should Have) |
| **Actor / Fuente** | Trabajador / `EstadisticasTrabajadorDTO` / `GET /trabajadores/{id}/estadisticas` / UC9 |
| **Restricciones de Datos** | • Las métricas se calculan sobre las postulaciones y valoraciones registradas.<br>• Se indica además si el perfil está completo y si la suscripción está activa. |
| **Criterio de Verificación** | Los valores devueltos coinciden con las agregaciones sobre las postulaciones completadas, seleccionadas y las valoraciones recibidas. |

---

### RF-10 | Panel de Reportes

| Atributo | Detalle Técnico |
| :--- | :--- |
| **Identificador** | RF-10 |
| **Nombre** | Panel de Reportes |
| **Descripción** | El sistema **debe proveer** un módulo de seguridad para que cualquier usuario autenticado reporte comportamientos inapropiados o incumplimientos, almacenando la alerta para su auditoría y moderación. |
| **Prioridad** | Alta (Must Have) |
| **Actor / Fuente** | Usuario autenticado y Administrador / `ReporteDTO` / `POST /reportes`, `PUT /reportes/{id}/resolver` / UC8 y UC12 |
| **Restricciones de Datos** | • `motivo` y `descripcion`: obligatorios.<br>• Estado inicial `PENDIENTE`; estados de resolución válidos: `EN_REVISION`, `RESUELTO`, `DESCARTADO`.<br>• Un reporte ya moderado no puede volver a resolverse. |
| **Criterio de Verificación** | El envío crea un reporte en estado `PENDIENTE`. El Administrador puede listarlos por estado y resolverlos, registrando el moderador que actuó. |

---

### RF-11 | Autenticación, Sesión y Gestión de Contraseña

| Atributo | Detalle Técnico |
| :--- | :--- |
| **Identificador** | RF-11 |
| **Nombre** | Autenticación, Sesión y Gestión de Contraseña |
| **Descripción** | El sistema **debe permitir** iniciar sesión con correo y contraseña, cerrar sesión de forma segura, cambiar la contraseña conociendo la actual y recuperar el acceso mediante un token enviado al correo. |
| **Prioridad** | Alta (Must Have) |
| **Actor / Fuente** | Usuario / `AuthRequestDTO`, `AuthResponseDTO`, `ChangePasswordDTO`, `ForgotPasswordRequestDTO`, `ResetPasswordRequestDTO` / `POST /auth/*` |
| **Restricciones de Datos** | • El login devuelve un JWT con los roles.<br>• El cambio de contraseña exige la contraseña actual correcta.<br>• El token de recuperación tiene vencimiento. |
| **Criterio de Verificación** | `POST /auth/login` autentica y devuelve el token; `POST /auth/logout` revoca el token (se registra en `TokenRevocado`); `POST /auth/password` cambia la contraseña; `POST /auth/password/forgot` genera el token y `POST /auth/password/reset` la restablece. |

---

### RF-12 | Notificaciones y Preferencias

| Atributo | Detalle Técnico |
| :--- | :--- |
| **Identificador** | RF-12 |
| **Nombre** | Notificaciones y Preferencias |
| **Descripción** | El sistema **debe registrar y despachar** notificaciones dentro de la aplicación a los usuarios ante eventos relevantes (nueva postulación recibida, cambio de estado de una postulación) y **debe permitir** al Trabajador configurar sus preferencias de aviso. |
| **Prioridad** | Alta (Must Have) |
| **Actor / Fuente** | Usuario / `NotificacionDTO`, `PreferenciaNotificacionDTO` / `GET /notificaciones/usuario/{id}`, `PATCH /notificaciones/{id}/leer`, `PUT /preferencias/trabajador/{id}` / UC4 |
| **Restricciones de Datos** | • `titulo` y `mensaje`: obligatorios.<br>• Tipos de notificación: sistema, postulación recibida y cambio de estado de postulación.<br>• Una notificación puede marcarse como leída. |
| **Criterio de Verificación** | Al recibir una postulación se genera una notificación para el Empleador; cada cambio de estado genera una notificación para el Trabajador. El usuario consulta las no leídas (`GET /notificaciones/usuario/{id}/no-leidas`) y las marca como leídas. Las preferencias del Trabajador (`notificarEmail`, `notificarPush`, `alertasNuevasOfertas`, `alertasNuevasPostulaciones`) se persisten. |

---

### RF-13 | Comunicados y Foro Vecinal

| Atributo | Detalle Técnico |
| :--- | :--- |
| **Identificador** | RF-13 |
| **Nombre** | Comunicados y Foro Vecinal |
| **Descripción** | El sistema **debe habilitar** comunicados institucionales emitidos por la Administración y un espacio comunitario (foro) donde los usuarios publicados puedan compartir recomendaciones, consejos y experiencias. |
| **Prioridad** | Baja (Could Have) |
| **Actor / Fuente** | Usuario y Administrador / `ComunicadoDTO`, `PublicacionForoDTO` / `GET|POST /comunicados`, `GET|POST|DELETE /foro` / UC8 |
| **Restricciones de Datos** | • Los comunicados solo los crea/elimina un Administrador y pueden clasificarse por `alcance`.<br>• Las publicaciones del foro tienen `titulo` y `contenido` obligatorios y son **globales** (no se restringen por zona). |
| **Criterio de Verificación** | Los comunicados se listan para su lectura y admiten filtrado por alcance. Cualquier usuario autenticado puede publicar en el foro; el autor o un Administrador puede eliminar su publicación. |

---

### RF-14 | Perfil Empresarial y Sello de Confianza

| Atributo | Detalle Técnico |
| :--- | :--- |
| **Identificador** | RF-14 |
| **Nombre** | Perfil Empresarial y Sello de Confianza |
| **Descripción** | El sistema **debe permitir** al Empleador con suscripción PRO publicar un perfil empresarial (nombre de la empresa, CUIT, descripción, sitio web y logo) y **debe permitir** al Administrador verificarlo, otorgándole el Sello de Confianza. |
| **Prioridad** | Media (Should Have) |
| **Actor / Fuente** | Empleador y Administrador / `PerfilEmpresarialDTO` / `PUT /perfiles-empresariales/empleador/{id}`, `PUT /perfiles-empresariales/{id}/verificar` |
| **Restricciones de Datos** | • Requiere **suscripción PRO vigente**.<br>• Relación uno a uno con el Empleador; el CUIT es único.<br>• Solo el Administrador puede marcar `verificado`. |
| **Criterio de Verificación** | Un Empleador sin PRO recibe un error al guardar el perfil. Con PRO vigente el perfil se almacena; cuando el Administrador lo verifica, el perfil queda marcado como `verificado` (Sello de Confianza). |

---

### RF-15 | Portafolio y Propuestas PRO del Trabajador

| Atributo | Detalle Técnico |
| :--- | :--- |
| **Identificador** | RF-15 |
| **Nombre** | Portafolio y Propuestas PRO del Trabajador |
| **Descripción** | El sistema **debe permitir** al Trabajador con suscripción PRO armar un portafolio de trabajos (título, descripción y archivo/enlace) y acceder a las "Propuestas PRO". |
| **Prioridad** | Media (Should Have) |
| **Actor / Fuente** | Trabajador / `PortafolioItemDTO`, `OportunidadLaboralDTO` / `POST /portafolio-items/trabajador/{id}`, `GET /oportunidades/propuestas-pro` |
| **Restricciones de Datos** | • Ambas capacidades requieren **suscripción PRO vigente**.<br>• El portafolio pertenece al Trabajador (relación uno a muchos). |
| **Criterio de Verificación** | Un Trabajador sin PRO recibe un error al agregar ítems de portafolio o al consultar Propuestas PRO. Con PRO vigente, ambos accesos funcionan. |

---

### RF-16 | Publicidad de Cercanía

| Atributo | Detalle Técnico |
| :--- | :--- |
| **Identificador** | RF-16 |
| **Nombre** | Publicidad de Cercanía |
| **Descripción** | El sistema **debe permitir** al Empleador con suscripción PRO publicar anuncios de su comercio (título, contenido, imagen y enlace) con una vigencia determinada, y **debe exponer** los anuncios vigentes en la plataforma. |
| **Prioridad** | Media (Should Have) |
| **Actor / Fuente** | Empleador y Administrador / `PublicidadDTO` / `POST|PUT|DELETE /publicidades`, `GET /publicidades/vigentes` |
| **Restricciones de Datos** | • Requiere **suscripción PRO vigente**.<br>• El anunciante debe ser un Empleador.<br>• Posee `fechaInicio`, `fechaFin` y estado `activa`. |
| **Criterio de Verificación** | El Empleador PRO crea y administra sus anuncios; la plataforma lista los anuncios vigentes. El Administrador puede gestionar todas las publicidades. |

---

### RF-17 | Suscripciones, Aportes y Activación de PRO

| Atributo | Detalle Técnico |
| :--- | :--- |
| **Identificador** | RF-17 |
| **Nombre** | Suscripciones, Aportes y Activación de PRO |
| **Descripción** | El sistema **debe gestionar** el nivel de suscripción del usuario (`BASICO` o `PRO`), **debe registrar** los aportes y donaciones con los datos de la transferencia, y **debe permitir** al Administrador verificar el aporte para activar el PRO por 1 año. |
| **Prioridad** | Alta (Must Have) |
| **Actor / Fuente** | Usuario y Administrador / `SuscripcionDTO`, `DonacionDTO` / `POST /donaciones`, `POST /donaciones/{id}/verificar`, `POST /suscripciones`, `PUT /suscripciones/cancelar/{id}` |
| **Restricciones de Datos** | • El monto debe ser mayor a cero.<br>• Un aporte por transferencia/suscripción se registra en estado `PENDIENTE` con banco, tipo y asunto.<br>• La verificación exige que **asunto y monto coincidan** con el registro bancario; de lo contrario, permanece pendiente.<br>• El nivel PRO se otorga por 1 año desde la verificación. |
| **Criterio de Verificación** | El aporte verificado correctamente activa (o renueva) el nivel PRO del usuario. `PUT /suscripciones/cancelar/{id}` desactiva la suscripción y el usuario vuelve al límite del Nivel Básico. |

---

### RF-18 | Disponibilidad Horaria del Trabajador

| Atributo | Detalle Técnico |
| :--- | :--- |
| **Identificador** | RF-18 |
| **Nombre** | Disponibilidad Horaria del Trabajador |
| **Descripción** | El sistema **debe permitir** al Trabajador registrar sus franjas de disponibilidad por día de la semana (hora de inicio y de fin). |
| **Prioridad** | Baja (Could Have) |
| **Actor / Fuente** | Trabajador / `HorarioDisponibilidadDTO` / `POST|DELETE /horarios`, `GET /horarios/trabajador/{id}` / UC11 |
| **Restricciones de Datos** | • El día de la semana es obligatorio.<br>• Una franja no puede solaparse con otra ya registrada para el mismo día. |
| **Criterio de Verificación** | Las franjas se persisten y se listan por Trabajador. Un intento de registrar un horario solapado es rechazado. |

---

### RF-19 | Gestión Administrativa y Panel de Métricas

| Atributo | Detalle Técnico |
| :--- | :--- |
| **Identificador** | RF-19 |
| **Nombre** | Gestión Administrativa y Panel de Métricas |
| **Descripción** | El sistema **debe proveer** un panel administrativo para gestionar usuarios (habilitar/deshabilitar), etiquetas, reportes, comunicados, foro, valoraciones, aportes, suscripciones, publicidades y empresas, y **debe exponer** métricas agregadas de la plataforma. |
| **Prioridad** | Media (Should Have) |
| **Actor / Fuente** | Administrador / `DashboardMetricsDTO` / `/api/admin/**`, `GET /api/admin/dashboard/metrics` / UC12 |
| **Restricciones de Datos** | • Todos los endpoints exigen `ROLE_ADMIN`.<br>• Las métricas de ingresos son **estimaciones** (suma de aportes más una valuación por plan PRO activo), no cobros auditados. |
| **Criterio de Verificación** | El Administrador accede a los listados paginados por sección y al dashboard, cuyos indicadores (contratos, trabajadores, promedio de remuneración, usuarios activos/inactivos, postulaciones por estado y tendencias) se calculan sobre los datos persistidos. |
