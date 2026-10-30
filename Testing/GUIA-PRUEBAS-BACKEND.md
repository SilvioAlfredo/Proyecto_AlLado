# Guía paso a paso: suite de pruebas del backend (AlLado)

> Ruta destino: `Backend/src/test/java/com/al_lado/al_lado_project`
> Stack de pruebas: **JUnit 5 + Mockito** (más `@WebMvcTest` para la capa web).
> Referencia de cobertura: `Especificaciones/Testing.md` y la planilla oficial de test cases.

---

## 1. Convenciones que ya usa el proyecto

Antes de escribir un test, hay que respetar las convenciones de las 18 clases de prueba
existentes. No son estilos distintos: son **el** estilo del proyecto.

### 1.1 Tres niveles de prueba

| Nivel | Anotación | Qué se prueba | Dependencias |
|---|---|---|---|
| **Regla de negocio** | `@ExtendWith(MockitoExtension.class)` | Servicios: reglas, transiciones de estado, validaciones | Repositorios `@Mock`, mappers reales |
| **Autorización** | `@ExtendWith(MockitoExtension.class)` + `SecurityContextHolder` | Quién puede operar cada recurso | Entidad `Usuario` real en el contexto |
| **Contrato HTTP** | `@WebMvcTest(Controlador.class)` | Código de estado, JSON, paginación, matriz por rol | Servicio `@MockBean` |

**Nunca** se levanta el contexto completo de Spring para probar lógica de negocio:
eso se hace en la suite E2E (`Testing/e2e`), no acá.

### 1.2 Reglas de escritura

1. **Los tests son de clase de paquete** (`class`, no `public class`).
2. **Nombres de método en español, en formato `accionSujeto_resultadoEsperado`**:
   `crearPostulacion_deberiaGuardarPostulacionCuandoDatosSonValidos`.
3. **Comentarios `// Dado` / `// Cuando` / `// Entonces`** dentro del test.
4. **Aserciones de JUnit** (`assertEquals`, `assertThrows`, `assertNotNull`), no de AssertJ
   ni Hamcrest, para no sumar dependencias.
5. **El servicio se construye a mano** en `@BeforeEach`, no con `@InjectMocks`:
   ```java
   service = new PostulacionServiceImpl(postulacionRepository, trabajadorRepository, ...);
   ```
   Así el test no depende del orden de inyección por nombre.
6. **Los mappers son reales**, no mocks: no dependen de Spring y son parte de la lógica
   que se quiere verificar.
7. **Limpiar el contexto de seguridad** siempre:
   ```java
   @AfterEach
   void limpiarContexto() { SecurityContextHolder.clearContext(); }
   ```
8. **Un oráculo por caso**: si el caso verifica dos códigos HTTP distintos, son dos tests.

### 1.3 Plantilla de test de servicio (Mockito)

```java
@ExtendWith(MockitoExtension.class)
class MiServicioTest {

    @Mock private Repositorio repository;
    @Mock private OtroServicio otroServicio;

    private final MiMapper mapper = new MiMapper();      // mapper real
    private MiServicioImpl service;

    @BeforeEach
    void setUp() {
        service = new MiServicioImpl(repository, mapper, otroServicio);
    }

    @AfterEach
    void limpiarContexto() { SecurityContextHolder.clearContext(); }

    @Test
    void accion_deberiaHacerXCuandoY() {
        // Dado
        when(repository.findById(1L)).thenReturn(Optional.of(entidad));
        // Cuando
        Resultado result = service.operacion(dto);
        // Entonces
        assertEquals("esperado", result.getCampo());
        verify(repository, times(1)).save(any(Entidad.class));
    }
}
```

### 1.4 Plantilla de test web (`@WebMvcTest`)

```java
@WebMvcTest(MiController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class MiControllerSecurityTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private MiService service;
    @MockBean private JwtUtils jwtUtils;
    @MockBean private UserDetailsServiceImpl userDetailsService;
    @MockBean private TokenRevocadoRepository tokenRevocadoRepository;

    @Test
    void anonimoNoPuedeAcceder() throws Exception {
        mockMvc.perform(get("/ruta")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "TRABAJADOR")
    void trabajadorNoPuedeAcceder() throws Exception {
        mockMvc.perform(get("/ruta")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminSiPuedeAcceder() throws Exception {
        when(service.metodo(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(dto)));
        mockMvc.perform(get("/ruta"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].campo").value("valor"));
    }
}
```

Los cuatro `@MockBean` (servicio + `JwtUtils` + `UserDetailsServiceImpl` +
`TokenRevocadoRepository`) son **obligatorios**: sin ellos el contexto de seguridad
no levanta.

---

## 2. Los cinco pasos para crear un test

### Paso 1 — Localizar la regla y su oráculo
Abrir el servicio y listar las reglas que aplica. Cada regla que hoy no está cubierta
es un test candidate. Anotar en el encabezado del test el identificador de trazabilidad
(`AL-TEST-00x` cuando exista en el plan, o `RF-xx / HU-xx CA-xx`).

### Paso 2 — Ubicar el archivo según la capa
- Servicio → `service/<area>/impl/<Clase>Test.java` (misma ruta que la clase real).
- Controlador → `web/<Clase>ControllerSecurityTest.java` o `web/<Clase>ControllerTest.java`.
- Regla transversal de autorización → `security/`.
- Base de datos → no se prueba acá (ver §5).

### Paso 3 — Escribir el escenario con mocks
- Mockear **solo** los repositorios y servicios externos que el caso necesita.
- `when(...).thenReturn(...)` para cada lectura; `thenAnswer(invocation -> getArgument(0))`
  para `save(...)` cuando el test necesita el objeto guardado.
- Para verificar que no se persistió nada: `verify(repository, never()).save(any())`.

### Paso 4 — Verificar una sola cosa
Cada test debe responder sí o no a una pregunta. Si el caso necesita dos respuestas,
se parte en dos.

### Paso 5 — Ejecutar y leer el resultado
```bash
cd Backend
./mvnw test                                   # toda la suite
./mvnw test -Dtest=ClaseDePrueba              # una clase
./mvnw test -Dtest=ClaseDePrueba#metodo       # un método
```
El informe HTML queda en `Backend/target/surefire-reports/site/index.html`.

---

## 3. Catálogo de pruebas a crear

Estado a la fecha del informe: **27 clases / 162 tests, todos en verde** (la suite
completa corre sin PostgreSQL: el test de arranque usa el perfil `e2e-h2`).
Faltan las siguientes. La prioridad va de mayor a menor riesgo.

> Los ítems 1 a 4 ya se crearon (21/10) y los de seguridad AL-SEC-001/004/005/006/007
> y AL-API-005 se cerraron el 22/10 con 4 clases nuevas
> (`JwtUtilsTest`, `AuthServiceSeguridadTest`, `SuscripcionConcesionProTest`,
> `TrabajadorProyeccionTest`). Ver `Testing/INFORME-ESTRUCTURA-PRUEBAS-BACKEND.md`.

### 3.1 Servicios — reglas de negocio y valores límite

| # | Clase a crear | Qué cubre | Trazabilidad | Estado |
|---|---|---|---|---|
| 1 | `service/auth/impl/AuthServiceImplTest` | Contraseña de 7 caracteres rechazada y de 8 aceptada; email duplicado; registro por rol; login inválido | RF-01 / HU-04 CA-01 | **Hecho** |
| 2 | `service/comunidad/impl/ValoracionServiceImplTest` | Puntuación 0, 1, 5 y 6; reseña inmutable (no se actualiza ni se borra) | RF-05 / HU-06 | **Hecho** |
| 3 | `service/trabajo/impl/PostulacionLimiteTest` | Remuneración 0 y negativa: el backend **no** valida `@Positive` | RF-04 / TC-POS-004 | **Hecho** |
| 4 | `service/comunidad/impl/PublicacionForoServiceImplTest` | Autor obligatorio; no se puede borrar la publicación de otro; **el foro no filtra por barrio** | RF-13 / TC-SEC-002 | **Hecho** |
| 5 | `service/comunidad/impl/FavoritoServiceImplTest` | No se puede favoritar dos veces; no se borra el favorito de otro | RF-03 | Pendiente |
| 6 | `service/comunidad/impl/NotificacionServiceImplTest` | Marcar como leída solo la propia notificación | RF-12 | Pendiente |
| 7 | `service/usuario/impl/UsuarioServiceImplTest` | Cambio de contraseña con la actual correcta; no se cambia la de otro | RF-01 | Pendiente |
| 8 | `service/perfil/impl/HorarioDisponibilidadServiceImplTest` | Validación de día y hora; solapamiento de horarios | RF-02 | Pendiente |
| 9 | `service/trabajo/impl/EtiquetaServiceImplTest` | Etiqueta en uso no se puede eliminar; normalización del nombre | RF-06 | Pendiente |
| 10 | `service/comunidad/impl/PerfilEmpresarialServiceImplTest` | Perfil sin plan PRO no puede publicar; contacto enmascarado | RF-03 / HU-05 | Pendiente |
| 11 | `service/auth/impl/AuthServiceSeguridadTest` | Propósito y uso único de los tokens; recuperación sin enumeración | RF-01 / HU-04 CA-02 | **Hecho** (22/10) |
| 12 | `service/usuario/impl/SuscripcionConcesionProTest` | Solo ADMIN concede PRO; el alta propia es `BASICO` | RF-09 / HU-12 | **Hecho** (22/10) |
| 13 | `service/usuario/impl/TrabajadorProyeccionTest` | Proyección pública (sin email/teléfono) vs. completa (titular/ADMIN) | RF-02/RF-03 | **Hecho** (22/10) |

### 3.2 Web — contrato HTTP y matriz por rol

| # | Clase a crear | Qué cubre | Trazabilidad | Estado |
|---|---|---|---|---|
| 11 | `web/ValoracionControllerSecurityTest` | 401 anónimo, 403 para roles sin permiso, 200 para ADMIN/EMPLEADOR | RF-05 / RF-10 | Pendiente |
| 12 | `web/FavoritoControllerSecurityTest` | 401/403/200 según rol; idem por recurso | RF-03 | Pendiente |
| 13 | `web/SuscripcionControllerSecurityTest` | Solo el titular o ADMIN cancela; ADMIN ve todas | RF-09 / HU-12 | Pendiente |
| 14 | `web/EtiquetaControllerSecurityTest` | Listado público; escritura restringida a ADMIN | RF-06 | Pendiente |
| 15 | `web/UsuarioControllerSecurityTest` | Perfil propio; el de otro devuelve 403; contraseña | RF-01 | Pendiente |
| 16 | `web/ComunicadoControllerTest` | Contract de comunicados; alcance GLOBAL vs GENERAL | RF-11 | Pendiente |
| 17 | `web/NotificacionControllerTest` | Listado paginado; marcar leída | RF-12 | Pendiente |
| 18 | `web/PerfilEmpresarialControllerTest` | Perfil del empleador; contacto enmascarado para el resto | RF-03 | Pendiente |
| 19 | `web/PublicidadControllerTest` | Vigencia de la publicidad; solo ADMIN gestiona | RF-11 | Pendiente |
| 20 | `web/HorarioDisponibilidadControllerTest` | CRUD del horario del trabajador | RF-02 | Pendiente |
| 21 | `web/PortafolioItemControllerTest` | CRUD del portafolio; solo el titular | RF-02 | Pendiente |
| 22 | `web/PreferenciaNotificacionControllerTest` | Preferencias de notificación del trabajador | RF-12 | Pendiente |
| 23 | `web/TrabajadorControllerTest` | Perfil y estadísticas del trabajador | RF-02 | Pendiente |
| 24 | `web/EmpleadorControllerTest` | Perfil del empleador | RF-03 | Pendiente |
| 25 | `web/AdminRestControllerTest` | Métricas del dashboard solo para ADMIN | RF-12 | Pendiente |
| 26 | `web/AuthControllerTest` (casos nuevos) | `GET /auth/me`; `forgot` con respuesta genérica; `login` con identidad mínima | RF-01 / AL-API-005 / AL-SEC-005 | **Hecho** (22/10) |
| 27 | `web/OportunidadControllerContractTest` (casos nuevos) | `GET /oportunidades/{id}/contacto`: 401 anónimo, 200 con sesión | RF-04 / AL-SEC-006 | **Hecho** (22/10) |

### 3.3 Casos de la planilla oficial que aún no tienen soporte

| Caso de la planilla | Qué falta | Dónde resolverlo |
|---|---|---|
| TC-POS-004 (remuneración ≤ 0) | El backend acepta el monto | Test #3 de §3.1, que documenta el defecto |
| TC-DB-002 (integridad referencial) | La BD no bloquea la referencia rota | Test de repositorio o verificación SQL manual (ver §5) |
| TC-DB-003 (email único) | Constraint UNIQUE | Test de repositorio o verificación SQL manual |
| TC-SEC-002 (foro por barrio) | No hay filtro por sector | Test #4 de §3.1, que documenta el defecto |

---

## 4. Reglas de seguridad que toda clase web debe cubrir

Para cada endpoint protegido, la matriz mínima es:

| Rol | Código esperado |
|---|---|
| Anónimo (sin `@WithMockUser`) | `401 Unauthorized` |
| `ROLE_TRABAJADOR` | `403 Forbidden` o `200 OK` según el caso |
| `ROLE_EMPLEADOR` | `403 Forbidden` o `200 OK` según el caso |
| `ROLE_ADMIN` | `200 OK` |

Cuando el recurso pertenece a alguien (oportunidad, postulación, suscripción,
favorito, publicación), agregar el caso de **tercero**: un usuario autenticado que
no es el titular debe recibir `403`.

---

## 5. Lo que NO va en esta suite

- **No** se prueban reglas de persistencia (constraints, índices, cascadas). Eso exige
  una base real y se verifica con las consultas SQL de la planilla (`SELECT ... WHERE ...`)
  o desde la suite E2E, que corre contra PostgreSQL de verdad.
- **No** se levanta el contexto completo de Spring (`@SpringBootTest`) salvo el test de
  arranque `AlLadoProjectApplicationTests`, que existe para detectar que la aplicación
  arranca.
- **No** se prueba la interfaz: eso es la suite E2E de Selenium en `Testing/e2e`.

---

## 6. Errores frecuentes y cómo evitarlos

| Error | Cómo se evita |
|---|---|
| `UnnecessaryStubbingException` | Mockear solo lo que el caso usa; `lenient()` solo si es justificado |
| Test que pasa por el motivo equivocado | Verificar con `verify(...)` lo que el caso afirma, no solo el resultado |
| Estado de seguridad filtrado entre tests | `@AfterEach` con `SecurityContextHolder.clearContext()` |
| `@MockBean` faltante y contexto que no levanta | Recordar los cuatro beans obligatorios de §1.4 |
| Test dependiente del orden de ejecución | Cada test arma su propio estado en `@BeforeEach` |
| Caso que verifica dos oráculos | Partirlo en dos tests (§2, paso 4) |

---

## 6b. La planilla oficial de test cases (22/10/2026)

La planilla vive **en el repositorio**: `Testing/Planilla de Test Cases v2(2).xlsx`
(hoja `AlLado - Planilla de Test Cases`, 13 columnas: las 10 de definición más
`Resultado Real`, `Estado` y `Evidencia`). Es el artefacto de QA; el informe
consolidado (PDF/XML/XLSX) se **deriva** de ella.

Antes vivía solo en `~/Descargas` y el repo tenía una copia vieja
(`Testing/Test Case.xlsx`, numeración `TEST-xx` previa a la unificación de IDs).
Hoy la versión vigente está versionada y los scripts la leen desde el repo
(con la copia de Descargas como respaldo).

### Cómo se actualiza

```bash
# 1. Regenerar evidencias (API/BD reales + fichas del backend) e informe
python3 Testing/e2e/scripts/generar_evidencias.py
python3 Testing/e2e/scripts/generar_informe_pruebas.py

# 2. Volcar los resultados de la corrida a la planilla
python3 Testing/e2e/scripts/actualizar_planilla.py
```

`actualizar_planilla.py` escribe `Resultado Real`, `Estado` y `Evidencia` en las
filas que comparten identificador y **anexa** los casos de la corrida que todavía
no estaban (E2E y regresión del backend), con su trazabilidad y su evidencia.
Es idempotente: correrlo dos veces no duplica filas ni mueve resultados.

Para ver qué haría sin escribir: `--simular`. Para no anexar casos nuevos:
`--sin-agregar`.

> `Testing/Test Case.xlsx` es la versión **histórica** anterior a la unificación de
> identificadores (`TEST-xx` → `TC-xx`); no se usa en el flujo actual.

---

## 7. Trazabilidad con la planilla

Al cerrar el ciclo de pruebas, cada fila de la planilla oficial
(`AlLado - Planilla de Test Cases v2(2).xlsx`) debe quedar con:

- **Resultado Real**: la observación concreta del test (mensaje de error, código HTTP).
- **Estado**: `PASS`, `FAIL` o `SKIP`.
- **Evidencia**: ruta del reporte de Surefire o de la captura de la suite E2E.

El generador `Testing/e2e/scripts/generar_informe_pruebas.py` arma el informe
consolidado en PDF, XML y hoja de cálculo a partir de estos mismos resultados.
