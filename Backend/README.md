# AlLado Backend

## Stack Tecnológico

- **Lenguaje:** Java 21 (OpenJDK)
- **Framework:** Spring Boot 3.2.5
- **Base de Datos:** PostgreSQL (contenedor Docker)
- **Persistencia:** Spring Data JPA con Hibernate
- **Seguridad:** Spring Security con JWT (jjwt 0.12.5, HS256) + `@PreAuthorize` por roles
- **Build Tool:** Maven
- **Testing:** JUnit 5, Mockito
- **Mapeo:** Manual (mappers `@Component`, sin MapStruct)
- **Lombok:** `@Data`, `@RequiredArgsConstructor`, etc.
- **Docs API:** springdoc-openapi (Swagger UI)

## Arquitectura

Capas separadas por responsabilidad:

```
com.al_lado.al_lado_project/
├── controller/  endpoints REST
├── service/     lógica de negocio (interfaces + impl)
├── repository/  acceso a datos JPA
├── entity/      clases mapeadas a BD
├── dto/         transferencia (oculta la entidad)
├── mappers/     conversión entity ↔ dto (manual)
├── security/    JWT, filtro auth, UserDetailsService, SecurityUtils
├── config/      CORS y demás configuración Spring
└── exception/   excepciones + GlobalExceptionHandler
```

Flujo de request: Controller (valida con `@Valid`) → Service (reglas + titularidad vía `SecurityUtils`) → Repository → BD. Los `toEntity` solo mapean atributos propios: las relaciones se resuelven en el service con entidades gestionadas (nunca transient).

### Patrones y decisiones

- **Herencia `JOINED`**: `Usuario` → Trabajador/Empleador/Administrador, `PostBase` → OportunidadLaboral/Postulacion, con `@PrimaryKeyJoinColumn`.
- **Polimorfismo Jackson** (`@JsonTypeInfo/@JsonSubTypes`, propiedad `type`) en entidades y DTOs base.
- **Titularidad**: todo recurso de usuario exige ser titular o `ADMIN` (`SecurityUtils.requireSelfOrAdmin`); roles vía `@PreAuthorize`.
- **Ofertas**: límite 3/mes plan Básico (con bloqueo pesimista), Pro ilimitado; no se transfieren entre empleadores.
- **Postulaciones inmutables**: sin borrado físico; máquina de estados `PENDIENTE → SELECCIONADA → COMPLETADA` (rechazo desde `PENDIENTE`/`SELECCIONADA`, retiro por el trabajador desde `PENDIENTE`/`SELECCIONADA` → `RETIRADA`).
- **Reportes**: `PENDIENTE/EN_REVISION/RESUELTO/DESCARTADO`; `resolver` no reabre finales.
- **Etiquetas**: se resuelven contra el repositorio (sin duplicados por `unique`); `obtenerOCrearPorNombre` con trim y retry ante carrera.
- **Módulo de notificaciones push/WhatsApp eliminado**: solo existe la consulta `obtenerTrabajadoresPorEtiqueta`.

## Seguridad y roles

- `ROLE_ADMIN`: administración, comunicados, reportes, verificaciones.
- `ROLE_EMPLEADOR`: ofertas, publicidades, valoraciones, favoritos.
- `ROLE_TRABAJADOR`: postulaciones, perfil/portafolio/horarios/preferencias.
- Endpoints sensibles adicionales: `PUT /usuarios/{id}` asigna el id del path; `POST /auth/password` y `/password/verify` exigen sesión y email propio.
- Lecturas públicas: `GET /oportunidades/**`, `/comunicados/**`, `/publicidades/**`, `/foro/**`, `POST /trabajadores|/empleadores` (registro), `POST /auth/login|/register`, recuperación de contraseña y `verify-email`.
- Contraseñas con BCrypt; `password` es `WRITE_ONLY` en JSON; el 500 genérico no filtra mensajes internos.

## API REST — referencia completa

Leyenda: 🔓 público · 🔒 solo autenticado · 👑 `ADMIN` · 💼 `EMPLEADOR` · 🔧 `TRABAJADOR` · 🧑 titular del recurso o admin. Respuestas: `200 OK` · `201 Created` · `204 No Content` · `400` regla/validación · `401` sin token · `403` sin rol/titularidad · `404` no encontrado · `409` duplicado. Violaciones de integridad imprevistas → `409` genérico (sin filtrar SQL).

### Auth — `/auth`
| Método | Ruta | Auth | Body / params |
|---|---|---|---|
| POST | `/auth/login` | 🔓 | `{email, password}` → `{token, email, roles}` |
| POST | `/auth/register` | 🔓 | `{nombre, email, password (min 8), telefono, rol: TRABAJADOR\|EMPLEADOR}` → `{token, email, roles}` |
| GET | `/auth/verify-email?token=` | 🔓 | Activa `emailVerificado` |
| POST | `/auth/password` | 🔒🧑 | `{email, currentPassword, newPassword}` (email = propio) |
| POST | `/auth/password/verify` | 🔒🧑 | Verifica clave actual del propio usuario |
| POST | `/auth/password/forgot` | 🔓 | `{email}` (token válido 24 h) |
| POST | `/auth/password/reset` | 🔓 | `{token, newPassword}` |
| POST | `/auth/logout` | 🔒🧑 | Header `Authorization: Bearer <token>` → `204` (revoca el JWT actual) |

### Usuarios — `/usuarios`
| Método | Ruta | Auth |
|---|---|---|
| GET | `/usuarios` | 👑 |
| GET | `/usuarios/{id}` | 🔒 |
| PUT | `/usuarios/{id}` | 🔒🧑 (el `id` del path prevalece) |
| DELETE | `/usuarios/{id}` | 👑 |
| PUT | `/usuarios/{id}/deshabilitar` · `/usuarios/{id}/habilitar` | 👑 (suspender/rehabilitar sin borrar) |

### Trabajadores — `/trabajadores` (idem `/empleadores`; `/administradores` todo 👑 por URL)
| Método | Ruta | Auth |
|---|---|---|
| GET | `/` · `/{id}` · `/filtrar?etiqueta=&sector=` | 🔒 |
| POST | `/` | 🔓 (registro) |
| PUT | `/{id}` | 🔒🧑 |
| DELETE | `/{id}` | 👑 |
| GET | `/{id}/estadisticas` · `/por-email/estadisticas?email=` | 🔒🧑 (contadores reales + placeholders en ingresos/nivel/login) |

### Suscripciones — `/suscripciones`
| Método | Ruta | Auth |
|---|---|---|
| GET | `/` · `/{id}` | 👑 |
| GET | `/usuario/{usuarioId}` | 🔒🧑 |
| POST | `/` | 🔒🧑 `{usuario{id}, nivel: BASICO\|PRO, fechaInicio, fechaFin?, activa}` |
| PUT | `/cancelar/{id}` | 🔒🧑 |
| DELETE | `/{id}` | 👑 |

### Oportunidades — `/oportunidades`
| Método | Ruta | Auth |
|---|---|---|
| GET | `/` (`?filtro=&page=0&size=10`) · `/{id}` | 🔓 (solo activas) |
| POST | `/filtrar` | 🔒 `{filtro, modalidad, etiquetas[]}` |
| POST | `/` | 💼👑🧑 (Básico: máx 3/mes; nueva siempre `activa=true`; `destacada` exige PRO) |
| PUT | `/{id}` | 💼👑🧑 (dueño; no transferible) |
| DELETE | `/{id}` | 💼👑🧑 (falla si tiene postulaciones) |
| GET | `/propuestas-pro?trabajadorId=` | 🔧🧑 PRO (compatibles con especialidades, destacadas primero) |

### Postulaciones — `/postulaciones`
| Método | Ruta | Auth |
|---|---|---|
| POST | `/` | 🔧🧑 `{titulo, contenido, remuneracionPretendida?, estado?, oportunidadLaboral{id}, autor{id}}` (nace `PENDIENTE`) |
| GET | `/` · `/{id}` | 🔒 |
| PATCH | `/{id}/seleccionar` | 💼👑 dueño (solo desde `PENDIENTE`) |
| PATCH | `/{id}/rechazar` | 💼👑 dueño (desde `PENDIENTE`/`SELECCIONADA`) |
| PATCH | `/{id}/completar` | 💼👑 dueño (solo desde `SELECCIONADA`) |
| PATCH | `/{id}/retirar` | 🔧🧑 autor (desde `PENDIENTE`/`SELECCIONADA` → `RETIRADA`) |
| DELETE | `/{id}` | ❌ eliminado (historial inmutable; antes siempre 400) |

### Etiquetas — `/etiquetas`
| Método | Ruta | Auth |
|---|---|---|
| GET | `/` · `/{id}` | 🔒 |
| POST | `/` | 👑 `{nombre}` (get-or-create con trim) |
| DELETE | `/{id}` | 👑 |

### Perfil trabajador
| Método | Ruta | Auth |
|---|---|---|
| GET · PUT | `/preferencias/trabajador/{trabajadorId}` | 🔧👑🧑 |
| GET | `/portafolio-items/trabajador/{id}` · `/horarios/trabajador/{id}` | 🔒 |
| POST | `/portafolio-items/trabajador/{id}` · `/horarios/trabajador/{id}` | 🔧👑🧑 (`diaSemana: LUNES..DOMINGO`, `horaInicio < horaFin`, sin solapes) |
| DELETE | `/portafolio-items/{id}` · `/horarios/{id}` | 🔧👑🧑 (dueño) |

| GET | `/notificaciones/usuario/{id}` · `/notificaciones/usuario/{id}/no-leidas` | 🔒🧑 |
| PATCH | `/notificaciones/{id}/leer` · `/notificaciones/usuario/{id}/leer-todas` | 🔒🧑 (dueño) |

### Comunidad
| Método | Ruta | Auth |
|---|---|---|
| GET | `/comunicados` · `/comunicados/{id}` · `/comunicados/alcance/{alcance}` (`?page=&size=`, default 0/10) | 🔓 |
| POST · DELETE | `/comunicados` · `/comunicados/{id}` | 👑 (emisor = admin) |
| GET | `/donaciones` (`?page=&size=`) | 👑 |
| GET | `/donaciones/{id}` · `/donaciones/usuario/{usuarioId}` (`?page=&size=` en el segundo) | 🔒🧑 |
| POST | `/donaciones` | 🔒🧑 (nace `PENDIENTE`; `monto>0`) |
| POST | `/donaciones/{id}/verificar?asunto=&monto=` | 👑 (marca `VERIFICADO`; `SUSCRIPCION` otorga PRO 1 año) |
| GET · POST · DELETE | `/favoritos/empleador/{empleadorId}[/trabajador/{trabajadorId}]` | 💼👑🧑 |
| GET · PUT | `/perfiles-empresariales/empleador/{empleadorId}` | 🔒🧑 (`cuit` único; guardar exige PRO) |
| PUT | `/perfiles-empresariales/{id}/verificar?supervisorId=` | 👑 (idempotente) |
| GET | `/foro` · `/foro/{id}` · `/foro/autor/{autorId}` (`?page=&size=`) | 🔓 |
| POST · DELETE | `/foro` · `/foro/{id}` | 🔒 (crear titular; borrar dueño) |
| GET | `/publicidades` · `/publicidades/{id}` · `/publicidades/anunciante/{id}` · `/publicidades/vigentes` | 🔓 |
| POST · PUT · DELETE | `/publicidades` · `/publicidades/{id}` | 💼👑🧑 (nueva siempre activa; `inicio<=fin`; crear exige PRO) |
| GET | `/reportes` · `/reportes/{id}` · `/reportes/estado/{estado}` (`?page=&size=`) | 👑 |
| POST | `/reportes` | 🔒🧑 (nace `PENDIENTE`) |
| PUT | `/reportes/{id}/resolver?moderadorId=&estado=` | 👑 (`EN_REVISION\|RESUELTO\|DESCARTADO`; finales no reabren) |
| GET | `/valoraciones/trabajador/{id}` · `/valoraciones/trabajador/{id}/promedio` · `/valoraciones/autor/{id}` (`?page=&size=` salvo promedio) | 🔒 |
| POST | `/valoraciones` | 💼👑🧑 (`puntuacion` 1–5) |
| DELETE | `/valoraciones/{id}` | 👑 (dueño) |

### Admin — `/api/admin` (👑)
| Método | Ruta | Notas |
|---|---|---|
| GET | `/api/admin/dashboard/metrics` | `{totalIngresos, metricContratos, metricTrabajadores, metricAvg, dineroComunidad, trabajosCompletados, tendencias[5], usuariosActivos, usuariosInactivos, postulacionesPorEstado}` con agregaciones SQL |

### Funciones PRO (requieren suscripción `PRO` activa y vigente)
Perfil empresarial, publicidades, portafolio, ofertas destacadas y propuestas pro.
Sin PRO → `400` con mensaje que indica el plan requerido.

### Valores cerrados (validados con `@Pattern`)
`rol`: `TRABAJADOR|EMPLEADOR` · `nivel`: `BASICO|PRO` · `modalidad`: `PRESENCIAL|REMOTO|HIBRIDO` · `diaSemana`: `LUNES..DOMINGO` · postulación: `PENDIENTE|SELECCIONADA|RECHAZADA|COMPLETADA|RETIRADA` · reporte: `PENDIENTE|EN_REVISION|RESUELTO|DESCARTADO` · donación: `PENDIENTE|VERIFICADO|RECHAZADO`. Las fechas generadas por servidor (`fechaCreacion`, `fechaEmision`, `fechaDonacion`, `fechaGuardado`) son `READ_ONLY`: si se envían, se ignoran.

### Swagger
- `/swagger-ui.html`, `/v3/api-docs` (sin anotaciones `@Operation` en controllers)

## Configuración

### Perfiles de configuración
- `application.properties` contiene valores comunes conservadores: `ddl-auto=validate`, `show-sql=false` y `open-in-view=false`.
- Desarrollo local: activar `SPRING_PROFILES_ACTIVE=dev`. El perfil `dev` usa PostgreSQL local y habilita `ddl-auto=update`, el log SQL y Open Session in View. Ver `src/main/resources/instrucciones_container` para iniciar PostgreSQL.
- Producción: activar `SPRING_PROFILES_ACTIVE=prod` y definir `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` y `JWT_SECRET` en el entorno. El perfil valida el esquema, mantiene apagados los logs SQL y no expone mensajes ni stack traces en errores.
- No guardar credenciales reales en los archivos de propiedades ni activar `dev` en producción.

### JWT
- `jwt.secret` / `jwt.expiration` (default 86400000 ms = 24 h). **Definir `jwt.secret` en cada entorno**: el default del código es compartido.
- Header `Authorization: Bearer <token>`.
- Revocación: `POST /auth/logout` guarda el `jti` en blacklist (`tokens_revocados`, purga de vencidos incluida); el filtro rechaza revocados con 401. Anónimos denegados → 401, autenticados sin permiso → 403.

### CORS
- Orígenes vía `app.cors.allowed-origins` (default `localhost:3000,5173,5174`).

## Tests
- Unitarios sin Spring: `SecurityUtilsTest` (titular/admin/terceros/anónimo),
  `PostulacionServiceImplTest`, `PostulacionAuthorizationTest`, `OportunidadAuthorizationTest` (Mockito).
- Slices MockMvc (`@WebMvcTest` + seguridad real): `Reporte/Postulacion/Oportunidad/AuthController*Test`
  (matriz 401/403/200/201 por rol + contratos JSON).
- `AlLadoProjectApplicationTests` (contexto completo) requiere PostgreSQL activo.
- Sin Maven en este entorno: compilar con `javac -parameters` y ejecutar con el
  launcher JUnit de `~/.m2` (ver script de verificación usado en esta sesión).
