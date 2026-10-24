# Informe: modificaciones y estructuración de las pruebas del backend (AlLado)

> Ruta: `Backend/src/test/java/com/al_lado/al_lado_project`
> Stack: **JUnit 5 + Mockito** (+ `@WebMvcTest` para la capa web)
> Fecha: 21/10/2026 · Salida de las tres exportaciones: PDF, XML y XLSX

---

## 1. Resumen ejecutivo

| Métrica | Antes | Después (21/10) | Δ tras cierre seguridad (22/10) |
|---|---|---|---|
| Clases de prueba | 18 | **23** | **27** (+4) |
| Casos de prueba | 86 | **122** | **162** (+40) |
| Suites en verde | 18/18 | **23/23** | **27/27** |
| Fallos | 0 | **0** | **0** |
| Casos que documentan defectos abiertos | 0 | **9** | +9 |

La suite del backend pasó de 86 a **162 casos**, todos en verde. No se modificó
ninguna clase de prueba preexistente: se agregaron clases nuevas y el
resto del árbol quedó intacto.

> **Actualización 22/10/2026 (cierre de seguridad de la semana 1):** 4 clases
> nuevas (`JwtUtilsTest`, `AuthServiceSeguridadTest`, `SuscripcionConcesionProTest`,
> `TrabajadorProyeccionTest`) y 7 casos agregados en dos clases web existentes.
> Además, `AlLadoProjectApplicationTests` pasó a correr con el perfil `e2e-h2`:
> **la suite completa queda en verde sin PostgreSQL instalado** (antes fallaba ese
> único caso por falta de base de datos).

---

## 2. Qué se agregó

| Clase nueva | Ubicación | Casos | Cubre |
|---|---|---|---|
| `AuthServiceImplTest` | `service/auth/impl/` | 8 | RF-01 / HU-04 CA-01 — email duplicado, rol inválido, alta por rol, login con roles, credenciales inválidas, el password nunca sale en la respuesta |
| `AuthControllerValidationTest` | `web/` | 5 | RF-01 / **TC-LIM-003** — contraseña de 7 caracteres rechazada y de 8 aceptada, email inválido, rol inválido, teléfono ausente |
| `ValoracionServiceImplTest` | `service/comunidad/impl/` | 14 | RF-05 / HU-06 — puntuación 0, 1, 5, 6 y nula; autor y trabajador obligatorios; duplicado; tercero no puede valorar ni borrar; borrado de la propia |
| `PublicacionForoServiceImplTest` | `service/comunidad/impl/` | 6 | RF-13 / HU-13 CA-02 — autor obligatorio, no se borra la publicación de otro, **el listado no filtra por barrio** |
| `PostulacionLimiteTest` | `service/trabajo/impl/` | 3 | RF-04 / **TC-POS-004** — remuneración 0 y negativa: el backend las persiste |

### Clases agregadas en el cierre de seguridad (22/10/2026)

| Clase | Ubicación | Casos | Cubre |
|---|---|---|---|
| `JwtUtilsTest` | `security/jwt/` | 10 | RF-01 · AL-SEC-001/004 · AL-API-005 — sin secreto no se firma; clave corta se rechaza; token de otra clave no se acepta; claim `purpose`; claim `uid`; expiración de 30 min; `jti` distinto por token |
| `AuthServiceSeguridadTest` | `service/auth/impl/` | 13 | RF-01 · AL-SEC-004/005 — token de sesión no recupera contraseñas ni verifica correos; token vencido, ya consumido o de otro propósito se rechazan; el token se consume al usarlo; `forgot` no falla ni filtra si el email existe |
| `SuscripcionConcesionProTest` | `service/usuario/impl/` | 4 | RF-09 · AL-SEC-007 (MEJ-002) — un usuario no se auto-otorga PRO ni se promueve de `BASICO` a `PRO`; el administrador sí; el alta propia queda en `BASICO` sin vencimiento |
| `TrabajadorProyeccionTest` | `service/usuario/impl/` | 5 | RF-02/RF-03 · AL-SEC-006 (MEJ-001) — la proyección pública no expone email ni teléfono; tercero ve la pública; titular y ADMIN ven la completa |

Casos agregados en clases web existentes: `GET /auth/me` (200 con identidad, 401 sin
sesión), `POST /auth/password/forgot` (respuesta genérica idéntica con y sin cuenta),
`login` devuelve la identidad mínima y `GET /oportunidades/{id}/contacto` (401 anónimo,
200 con sesión).

### Detalle de los dos casos que resuelven la planilla

- **TC-LIM-003 (7 vs 8 caracteres)**: la regla vive en el Bean Validation de
  `RegisterRequestDTO` (`@Size(min = 8)`), que se dispara en el controlador con
  `@Valid`. Por eso el caso está en la capa web (`AuthControllerValidationTest`)
  y no en el servicio: en el servicio la longitud no se valida.
- **TC-POS-004 (remuneración ≤ 0)**: el test documenta el comportamiento
  **actual** (se persiste) con el nombre explícito
  `crearPostulacion_deberiaAceptarRemuneracionCeroHoy`. Cuando se corrija el
  defecto, esos dos tests tienen que pasar a exigir el rechazo.

---

## 3. Estructuración aplicada

La suite quedó organizada en **tres niveles**, cada uno con una responsabilidad
distinta y sin solapamiento:

```
Backend/src/test/java/com/al_lado/al_lado_project/
│
├── AlLadoProjectApplicationTests.java     Contexto de Spring: la app arranca
│
├── security/
│   └── SecurityUtilsTest.java              Reglas transversales de autorización
│
├── service/<area>/impl/
│   ├── trabajo/impl/                       Oportunidades, postulaciones, etiquetas
│   ├── comunidad/impl/                     Valoraciones, foro, reportes, transferencias
│   ├── usuario/impl/                       Suscripciones
│   └── auth/impl/                          Registro e inicio de sesión      ← NUEVO
│
└── web/                                    Contrato HTTP y matriz por rol
    ├── AuthControllerValidationTest.java                            ← NUEVO
    └── (AuthControllerTest, OportunidadControllerContractTest, ...)
```

### Nivel 1 — Regla de negocio (Mockito puro)

```java
@ExtendWith(MockitoExtension.class)
class ValoracionServiceImplTest {
    @Mock private ValoracionRepository repository;
    private final ValoracionMapper mapper = new ValoracionMapper(new UsuarioResumenMapper());
    private ValoracionServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ValoracionServiceImpl(repository, empleadorRepository, trabajadorRepository, mapper);
    }
}
```

Convenciones aplicadas de forma consistente:

- **El servicio se construye a mano**, no con `@InjectMocks`: el test no depende
  del orden de inyección por nombre.
- **Los mappers son reales**: no dependen de Spring y son parte de la lógica que
  se quiere verificar.
- **Solo se mockea lo que el caso usa** (evita `UnnecessaryStubbingException`).
- **`SecurityContextHolder` + `clearContext()` en `@AfterEach`** para el actor.
- **Nombres en `accionSujeto_resultadoEsperado`** y comentarios `// Dado / Cuando / Entonces`.

### Nivel 2 — Autorización

La matriz por rol vive en la capa web, con `@WebMvcTest`:

| Rol | Código esperado |
|---|---|
| Anónimo | `401 Unauthorized` |
| Rol sin permiso | `403 Forbidden` |
| Titular o ADMIN | `200 OK` |

Para recursos con dueño (oportunidad, postulación, valoración, publicación,
favorito) se agrega el caso de **tercero**: usuario autenticado que no es el
titular → `403`.

### Nivel 3 — Contrato HTTP

`@WebMvcTest(Controlador.class)` + `@Import({SecurityConfig.class, JwtAuthenticationFilter.class})`
+ los cuatro beans obligatorios (`servicio`, `JwtUtils`, `UserDetailsServiceImpl`,
`TokenRevocadoRepository`).

---

## 4. Mapa de cobertura actual

| Área | Requisito | Estado |
|---|---|---|
| Autenticación | RF-01 / HU-04 | **Cubierto** (servicio + web + tokens con propósito) |
| Recuperación y verificación de cuenta | RF-01 / HU-04 CA-02 | **Cubierto** (AL-SEC-004: propósito, expiración, uso único) |
| Sin enumeración de cuentas | RF-01 | **Cubierto** (AL-SEC-005: respuesta genérica) |
| Identidad de la sesión | RF-01 / HU-04 | **Cubierto** (AL-API-005: `id` en la respuesta, claim `uid`, `GET /auth/me`) |
| Proyección por audiencia | RF-02/RF-03 | **Cubierto** (AL-SEC-006: proyección pública vs. completa) |
| Plan PRO | RF-09 / HU-12 | **Cubierto** (AL-SEC-007: solo ADMIN concede PRO) |
| Perfil trabajador | RF-02 | Parcial (dashboard y E2E) |
| Perfil empleador | RF-03 | Parcial (enmascarado de contacto pendiente) |
| Publicación de oportunidades | RF-04 | **Cubierto** (autorización, estados, límites) |
| Valoraciones | RF-05 | **Cubierto** |
| Etiquetas | RF-06 | Parcial (existe `EtiquetaEliminarTest`) |
| Publicación en el foro | RF-13 | **Cubierto**, con el defecto de filtro por barrio documentado |
| Suscripciones / plan PRO | RF-12 | Parcial (vigencia y cancelación) |
| Panel de administración | RF-12 | **Cubierto** (14 casos de gestión + 4 de métricas) |
| Notificaciones | RF-11/RF-12 | Pendiente |
| Historial inalterable | RF-08 | Pendiente |

---

## 5. Defectos abiertos documentados por la suite

La suite no solo verifica: también deja constancia de lo que **no** cumple el
requerimiento, para que el equipo lo trackee.

| Defecto | Qué hace hoy la app | Test que lo documenta |
|---|---|---|
| **TC-POS-004** | Persiste una remuneración 0 o negativa | `PostulacionLimiteTest.crearPostulacion_deberiaAceptarRemuneracionCeroHoy` |
| **TC-SEC-002** | El foro devuelve publicaciones de otros barrios | `PublicacionForoServiceImplTest.obtenerTodas_noDeberiaFiltrarPorBarrio` |

Además, el caso E2E `TC-SEC-002` está deshabilitado en la suite de Selenium por
el mismo motivo, y la planilla oficial mantiene `TC-POS-004` y `TC-DB-002` en
estado `FAIL`.

---

## 6. Cómo ejecutar

```bash
cd Backend

# Suite completa (no necesita PostgreSQL: el test de arranque usa el perfil e2e-h2)
JAVA_HOME=~/.jdks/ms-21.0.12.1 ./mvnw test

# Una clase
./mvnw test -Dtest=ValoracionServiceImplTest

# Un método
./mvnw test -Dtest=ValoracionServiceImplTest#crearPostulacion_deberiaAceptarRemuneracionCeroHoy

# Informe HTML
./mvnw test && open target/surefire-reports/site/index.html
```

> En este entorno el `mvnw` del repositorio no resuelve su
> `.mvn/wrapper/maven-wrapper.properties`. Se usó el Maven local con JDK 21:
> `JAVA_HOME=~/.jdks/ms-21.0.12.1 ~/.m2/wrapper/dists/apache-maven-3.9.16/56ba1f9f/bin/mvn test`
>
> Con el JDK 25 del sistema la suite falla: Mockito/Byte Buddy de Spring Boot 3.2.5
> no instrumenta clases de Java 25. El proyecto fija `release 21`, así que hace
> falta un JDK 21 (en esta máquina: `~/.jdks/ms-21.0.12.1` o
> `~/.local/jdk-21.0.12.1+1`).

---

## 6b. Suites unitarias de los frontends (22/10/2026)

Los frontends no tenian ni un test unitario: lo reformulado en la semana 1 (la
sesion: `AuthProvider`, `AuthContext`, `usuarioRef`, `serviciosPerfil`) se
apoyaba solo en `tsc`, `lint`, `build` y la suite E2E. Se agregaron dos suites:

| Suite | Ruta | Herramienta | Casos | Documentacion |
|---|---|---|---|---|
| Web | `Frontend/al_lado-app/src` | Vitest 3 + React Testing Library (jsdom) | 43 | `Testing/GUIA-PRUEBAS-FRONTEND.md` |
| Movil | `Frontend/al-lado-mobile/{src,app}` | jest-expo 57 + @testing-library/react-native | 25 | `Testing/GUIA-PRUEBAS-FRONTEND.md` |

Archivos nuevos (web): `vitest.config.mts`, `src/test/setup.ts`, `src/test/mocks.ts`,
`src/lib/jwt.test.ts`, `src/lib/usuarioRef.test.ts`, `src/lib/api.test.ts`,
`src/features/auth/components/AuthProvider.test.tsx`,
`src/features/usuario/services/serviciosPerfil.test.ts`,
`src/features/oportunidades/services/oportunidadService.test.ts`,
`src/features/postulaciones/components/CandidatosList.test.tsx`.

Archivos nuevos (movil): `jest.config.js`, `jest.setup.js`,
`src/auth/AuthContext.test.tsx`, `src/api/client.test.ts`, `src/api/services.test.ts`,
`app/trabajador/comunidad/comunidad.test.tsx`.

Las tres regresiones que estas suites dejan bloqueadas:

1. Ningun cliente lista usuarios para descubrir su propio identificador (AL-API-005).
2. La sesion se rehidrata con el claim `uid` del token o con `GET /auth/me`.
3. El foro y el directorio publicos no exponen el email del autor (AL-SEC-006), y el
   empleador si sigue viendo el contacto de quien se postulo a su oferta.

---

## 7. Lo que falta (catálogo priorizado)

La guía `Testing/GUIA-PRUEBAS-BACKEND.md` tiene el catálogo completo con los
25 casos pendientes. En orden de prioridad:

1. `service/comunidad/impl/FavoritoServiceImplTest` — no duplicar favoritos, no borrar los ajenos.
2. `service/comunidad/impl/NotificacionServiceImplTest` — marcar leída solo la propia.
3. `service/usuario/impl/UsuarioServiceImplTest` — cambio de contraseña con la actual correcta.
4. `web/ValoracionControllerSecurityTest`, `web/FavoritoControllerSecurityTest`, `web/SuscripcionControllerSecurityTest`, `web/EtiquetaControllerSecurityTest`, `web/UsuarioControllerSecurityTest` — matriz 401/403/200.
5. `web/ComunicadoControllerTest`, `web/NotificacionControllerTest`, `web/PerfilEmpresarialControllerTest`, `web/PublicidadControllerTest` — contrato HTTP.
6. `service/perfil/impl/HorarioDisponibilidadServiceImplTest` — solapamiento de horarios.
7. `service/trabajo/impl/EtiquetaServiceImplTest` — normalización del nombre de la etiqueta.

Las reglas de persistencia (constraints, índices, cascadas) **no** van en esta
suite: se verifican con las consultas SQL de la planilla o desde la suite E2E,
que corre contra PostgreSQL real.

---

## 8. Cambios colaterales en el backend (fuera de `src/test`)

| Archivo | Cambio | Motivo |
|---|---|---|
| `Backend/pom.xml` | Se agregó la dependencia `com.h2database:h2` (scope `runtime`) | Perfil de QA sin PostgreSQL instalado |
| `Backend/src/main/resources/application-e2e.properties` | Perfil E2E sobre PostgreSQL real (puerto 5433) | La app usa bloqueos pesimistas (`FOR NO KEY UPDATE`) que H2 no soporta |
| `Backend/src/main/resources/application-e2e-h2.properties` | Perfil alternativo sobre H2 en memoria (puerto 8082) | Máquinas sin PostgreSQL |
| `Backend/src/main/resources/seed-e2e.sql` | Seed determinista para el perfil H2 | Datos mínimos para QA |
| `Testing/e2e/scripts/preparar_base_qa.py` | Carga `datos/al_lado_db.sql` en la base de QA y fija las contraseñas de las cuentas de rol | Entorno reproducible |

Ninguno de estos cambios altera el comportamiento de la aplicación en su perfil
por defecto (`application.properties`): solo agregan perfiles de QA.

---

## 9. Exportaciones del informe

Los resultados de esta suite se consolidan con los de la suite E2E y con la
planilla oficial en tres formatos, todos en
`/home/Alfredo/Documentos/AlLado/plan_de_pruebas/`:

| Formato | Archivo | Contenido |
|---|---|---|
| PDF | `AlLado - Informe de Resultados de Pruebas.pdf` | Portada, resumen por categoría, defectos abiertos, entorno, tabla general por tipo de prueba y **anexo con una página por caso y su captura a tamaño completo** |
| XML | `AlLado - Informe de Resultados de Pruebas.xml` | Formato de suite de pruebas, un `testsuite` por categoría y un `testcase` por caso con la trazabilidad completa y la ruta de la evidencia |
| Hoja de cálculo | `AlLado - Informe de Resultados de Pruebas.xlsx` | Una hoja por categoría con las 13 columnas de la planilla oficial, **con la captura incrustada dentro de la celda "Evidencia"**, más una hoja de resumen |

### Dónde quedan las imágenes

| Ubicación | Qué hay | Cómo se ve |
|---|---|---|
| `/plan_de_pruebas/evidencias/<carpeta>/captura.png` | Las **210 capturas**: 30 de la suite E2E, 20 de API/BD y 162 del backend | La celda "Evidencia" apunta a `evidencias/.../captura.png`, ruta que **resuelve desde la carpeta del informe** |
| Celda "Evidencia" del XLSX | La imagen incrustada dentro de la celda | Se ve sin abrir nada, al abrir la hoja |
| Anexo del PDF (sección 5) | Una página por caso con la captura a tamaño completo | Se imprime y se adjunta al Issue |
| Atributo `evidenciaRutaAbsoluta` del XML | Ruta en disco de la imagen | Para pipelines que la consumes |

Resultado consolidado de la última corrida (22/10/2026, contra el PostgreSQL del
contenedor `al_lado_db`): **280 casos · 276 PASS · 2 FAIL (los defectos abiertos
documentados) · 2 SKIP · 0 ERROR**.

La **planilla oficial** (`Testing/Planilla de Test Cases v2(2).xlsx`, ahora en el
repositorio) quedó con los mismos casos y su `Estado`, `Resultado Real` y
`Evidencia`: **280 filas, 276 PASS · 2 FAIL · 2 SKIP, sin duplicados**. Se actualiza
con `python3 Testing/e2e/scripts/actualizar_planilla.py` (idempotente).

> Ese consolidado (PDF/XML/XLSX) cubre **backend + E2E + API/BD**. Las suites
> unitarias de los frontends (43 web + 25 móvil) se reportan por separado, con sus
> propios comandos (`npm test` en cada front); el generador del informe todavía no
> las incluye.

Se regeneran con:

```bash
python3 Testing/e2e/scripts/generar_evidencias.py
python3 Testing/e2e/scripts/generar_informe_pruebas.py
```

### Qué muestra cada tipo de evidencia

| Tipo de evidencia | Contenido de la captura |
|---|---|
| **E2E (Selenium)** | La pantalla real de la aplicacion en el momento del caso |
| **API** | El intercambio HTTP real: metodo, URL, cabeceras, cuerpo de la peticion, codigo de respuesta y cuerpo de la respuesta. Contrasta con el resultado esperado de la planilla |
| **Base de datos** | La cadena de llamadas del caso (por ejemplo, alta + borrado) y la comprobacion posterior de no persistencia |
| **Backend (JUnit + Mockito)** | Ficha del caso: clase, metodo, tipo, estado, duracion, detalle y la referencia al reporte de Surefire |

Los casos de API y base de datos **se re-ejecutan de verdad** contra el backend de
QA antes de capturar. La evidencia no es una maqueta: es el resultado observado
en esa corrida. Por eso los casos que necesitan un estado limpio crean su propio
recurso y lo dejan constar en la nota de ejecucion.
