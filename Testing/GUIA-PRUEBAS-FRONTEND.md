# Guía paso a paso: suite de pruebas de los frontends (AlLado)

> Rutas: `Frontend/al_lado-app` (web, Next.js 16) y `Frontend/al-lado-mobile` (móvil, Expo 57 / React Native 0.86).
> Stack: **Vitest + React Testing Library** (web) y **jest-expo + @testing-library/react-native** (móvil).
> Actualizado: 29/10/2026.

---

## 1. Qué cubre cada suite

| Capa | Ubicación | Herramienta | Casos | Qué protege |
|---|---|---|---|---|
| Backend | `Backend/src/test` | JUnit 5 + Mockito | 218 | reglas de negocio, autorización, contrato HTTP |
| Web | `Frontend/al_lado-app/src` | Vitest + RTL | 45 | sesión, capa HTTP, servicios, datos de contacto y notificaciones |
| Móvil | `Frontend/al-lado-mobile/{src,app}` | jest-expo + RNTL | 27 | sesión, capa HTTP, servicios, comunidad y notificaciones |
| Interfaz completa | `Testing/e2e` | Selenium 4 + axe-core | 32 en código | último runtime 30: 28 PASS/2 SKIP; 2 flujos nuevos pendientes de runtime |

Las suites de frontend también entran al **informe consolidado** y a la **planilla
oficial**: cada runner deja un JUnit XML (`test-results/junit.xml`) que el generador
consume, y los casos aparecen como `FEW-*` (web) y `FEM-*` (móvil).

La brecha que estas suites cubren: hasta el 22/10/2026 los frontends **no tenían ni un
test unitario**. Lo que se reformuló en la semana 1 (la sesión: `AuthProvider`,
`AuthContext`, `usuarioRef`, `serviciosPerfil`) se apoyaba solo en `tsc`, `lint`, `build`
y la suite E2E. Ahora hay una red de regresión que corre en segundos, sin navegador y
sin base de datos.

---

## 2. Convenciones

Las mismas que usa la suite del backend (ver `GUIA-PRUEBAS-BACKEND.md` §1.2):

1. Los tests van **junto al código** (`*.test.ts` / `*.test.tsx`), con la misma ruta.
2. Nombres en español, formato `accionSujeto_resultadoEsperado`.
3. Comentarios `// Dado` / `// Cuando` / `// Entonces`.
4. Aserciones de la librería de testing (`expect`), sin `as any`.
5. Un oráculo por caso: si verifica dos cosas, son dos tests.
6. Cada test arma su propio estado: en la web, `localStorage` se limpia antes de cada
   test; en el móvil, el almacén de `expo-secure-store` es un `Map` en memoria.

---

## 3. Suite web (`Frontend/al_lado-app`)

### 3.1 Puesta en marcha

```bash
cd Frontend/al_lado-app
npm install          # instala vitest, @testing-library/* y jsdom
npm test             # corre la suite una vez
npm run test:watch   # modo interactivo
```

Configuración: `vitest.config.mts` (entorno `jsdom`, alias `@/*` con
`vite-tsconfig-paths`, `setupFiles` con los matchers de `@testing-library/jest-dom`).
Preparación común: `src/test/setup.ts` (limpia el almacenamiento, mockea
`next/navigation` y filtra el aviso de navegación de jsdom).

### 3.2 Qué hay testeado

| Archivo | Casos | Qué fija |
|---|---|---|
| `src/lib/jwt.test.ts` | 9 | Lectura del claim `uid` del token (AL-API-005) |
| `src/lib/usuarioRef.test.ts` | 6 | La referencia del usuario sale de la sesión, no de listar usuarios |
| `src/lib/api.test.ts` | 6 | Inyección del bearer y limpieza completa de la sesión ante 401/403 |
| `src/features/auth/components/AuthProvider.test.tsx` | 8 | Ciclo de vida de la sesión: login, rehidratación desde el token, fallback a `/auth/me`, logout |
| `src/features/usuario/services/serviciosPerfil.test.ts` | 7 | El perfil propio se pide con el id de la sesión; **no** se lista el directorio |
| `src/features/oportunidades/services/oportunidadService.test.ts` | 4 | Endpoint de contacto del empleador (AL-SEC-006) y cierre de oferta |
| `src/features/postulaciones/components/CandidatosList.test.tsx` | 3 | El empleador puede contactar al postulante (el email sigue visible en su lista) |
| `src/features/notificaciones/components/NotificacionesList.test.tsx` | 2 | TC-NOTIF-001/002: marcar una o todas como leídas |

### 3.3 Regresiones que la suite deja bloqueadas

- **No listar usuarios para descubrir el propio id.** Si alguien vuelve a resolver el
  usuario en sesión listando `/trabajadores` o `/empleadores`, el test falla.
- **La sesión se rehidrata con el token**, no con un listado completo.
- **Un 401/403 borra toda la sesión**, incluido el `user_id` (si queda, la pantalla
  siguiente muestra datos de la cuenta anterior).

---

## 4. Suite móvil (`Frontend/al-lado-mobile`)

### 4.1 Puesta en marcha

```bash
cd Frontend/al-lado-mobile
npm install          # instala jest-expo, jest, @testing-library/react-native
npm test
npm run test:watch
```

Configuración: `jest.config.js` (preset `jest-expo`, alias `@/*`,
`src/**` y `app/**` como raíces de test). Preparación común: `jest.setup.js`
(`IS_REACT_ACT_ENVIRONMENT` para React 19, almacén de `expo-secure-store` en memoria
y mock de `expo-router`).

### 4.2 Qué hay testeado

| Archivo | Casos | Qué fija |
|---|---|---|
| `src/auth/AuthContext.test.tsx` | 10 | `userIdFromToken` (con base64 propio) y rehidratación de la sesión desde el token |
| `src/api/client.test.ts` | 9 | Cabecera `Authorization`, contrato del login/registro, traducción de errores |
| `src/api/services.test.ts` | 4 | Rutas de los servicios y **ausencia de `resolveIdByEmail`** (AL-API-005) |
| `app/trabajador/comunidad/comunidad.test.tsx` | 2 | El foro público no expone el email del autor: la autoría se compara por id (AL-SEC-006) |
| `src/components/notificaciones.test.tsx` | 2 | TC-NOTIF-001/002: lectura individual y masiva en la lista móvil |

### 4.3 Ojo con la API de `render`

En `@testing-library/react-native` **v14, `render` es asíncrono**: hay que esperarlo
(`await render(...)`) para que el árbol quede montado y `screen` esté disponible.

---

## 5. Cómo se ejecuta todo junto

```bash
# Backend (no necesita PostgreSQL: el test de arranque usa el perfil e2e-h2)
cd Backend && JAVA_HOME=<jdk 21> ./mvnw test

# Frontends
cd Frontend/al_lado-app && npm test
cd Frontend/al-lado-mobile && npm test

# Tipos y build (los tests también se type-chequean)
cd Frontend/al_lado-app && npx tsc --noEmit && npm run build
cd Frontend/al-lado-mobile && npx tsc --noEmit

# Suite de interfaz (necesita backend + frontend levantados)
cd Testing/e2e && mvn test
```

---

## 6. Qué sigue sin cubrir

- **Los dos frontends no tienen tests de componentes de página.** Se prueban la sesión,
  la capa HTTP, los servicios y los componentes con datos sensibles; el resto de las
  51 páginas web y 45 pantallas móviles sigue cubierta por la suite E2E.
- **No hay tests de contrato entre front y back** salvo los que fija el backend
  (OpenAPI es la fuente de verdad pendiente de la issue AL-DOC-002, semana 2).
- La suite móvil corre en un solo runtime (Node + preset de Expo). Probar en un
  dispositivo real sigue siendo tarea de QA.
