# AlLado Frontend

## Stack Tecnológico

- **Lenguaje:** TypeScript 5.x
- **Framework:** React 18 con Hooks
- **Build Tool:** Vite 5.x (rápido HMR y optimizaciones nativas)
- **Ruteo:** React Router DOM
- **Estilización:** CSS Modules / CSS tradicional (estilos inline en algunos componentes)
- **HTTP Client:** Axios (para llamadas a API REST)
- **Linter:** Oxlint con plugins react, typescript, oxc
- **Formato de fecha/hora:** Day.js o manejo nativo de Date
- **Testing:** Jest + React Testing Library (pendiente de expansión)

## Arquitectura

### Patrón
Componente-based architecture con separación de responsabilidades:

```
src/
├── pages/        Componentes de página completas (rutas)
├── components/   Componentes reutilizables (UI atoms/molecules)
├── api/          Cliente Axios y funciones de llamadas a API
├── App.tsx       Componente raíz con enrutamiento y estado global
├── main.tsx      Punto de entrada ReactDOM.createRoot
├── pages/        Vistas por ruta (Feed, CrearOferta, Perfil, etc.)
└── App.css       Estilos globales
```

### Flujo de Datos
1. **App.tsx:** Estado global (screen, ofertaSeleccionada, trabajadorId) y enrutamiento
2. **Pages:** Cada página consume API y gestiona su propio estado local (useState, useEffect)
3. **Components:** Componentes puros que reciben props y emiten eventos onCallback
4. **api/client:** Axios instance con baseURL `http://localhost:8080`
5. **API Responses:** Datos transformados y directamente usados en JSX

### Patrones Aplicados
- **Custom Hooks:** lógica reutilizable (por implementar: useFetch, useAuth)
- **Container/Presentational separation:** páginas conectan API, componentes solo renderizan
- **Elevated State:** eventos callback pasados de padre a hijo para acciones
- **Error Handling:** try/catch en handlers de evento + alertas user-friendly
- **Loading States:** indicadores visuales durante llamadas API (.map con loading)

### Rutas Definidas
- `/` - Pantalla de inicio (FeedPage con ofertas y buscador)
- `/nueva` - Pantalla crear oferta (CrearOfertaPage)
- `/postulacion/:id` - Pantalla postularse a oferta (CrearPostulacionPage)
- `/perfil-trabajador/:id` - Perfil de trabajador (PerfilTrabajadorPage) - **Nuevo**

## Estructura de Carpetas

```
/src/
├── api/
│   └── client.ts  Axios instance con baseURL http://localhost:8080
├── components/
│   ├── BottomNav.tsx        (componente placeholder)
│   ├── FavoriteButton.tsx   (nuevo - botón de favoritos)
│   ├── Footer.tsx           (pie de página)
│   ├── OportunidadCard.tsx  (tarjeta de oferta en vitrinas)
│   └── ...                  (otros componentes por crear)
├── pages/
│   ├── AppShell.tsx         (layout wrapper - aún no implementado)
│   ├── FeedPage.tsx         (inicio, ofertas, filtros, favs)
│   ├── CrearOfertaPage.tsx  (formulario publicar oferta)
│   ├── CrearPostulacionPage.tsx  (formulario postularse)
│   ├── PerfilTrabajadorPage.tsx  (nuevo - perfil completo trabajador)
│   └── ...                  (otras por crear: login, registro, admin)
├── App.tsx                  enrutamiento y estado global
├── main.tsx                 ReactDOM.createRoot
└── App.css                  estilos globales

/public/
├── index.html               plantilla HTML
└── vite.svg                 favicon/assets

/**
 * package.json define dependencias:
 * - react, react-dom
 * - @vitejs/react, @vitejs/plugin-react (o react-swc)
 * - axios
 * - typescript, @types/react, @types/react-dom
 * - vite
 * - oxlint (para linting type-aware)
 */

## API REST Consumo

### Configuración Base
- **Base URL:** `http://localhost:8080` (desarrollo)
- **Headers:** `Content-Type: application/json`
- **Autenticación:** Token JWT en header `Authorization: Bearer <token>` (después de login)

### Endpoints Consumidos Actualmente

| Método | Endpoint | Descripción | Componente |
|--------|----------|-------------|------------|
| GET | `/oportunidades` | Listar todas las ofertas con paginación y filtros | FeedPage |
| POST | `/auth/login` | Inicio de sesión JWT | Auth (pending) |
| POST | `/auth/register` | Registro con validación de teléfono | Auth (pending) |
| GET | `/auth/verify-email` | Verificación de email | Auth (pending) |
| POST | `/oportunidades` | Publicar nueva oferta | CrearOfertaPage |
| GET | `/trabajadores/:id` | Obtener perfil trabajador | PerfilTrabajadorPage |
| POST | `/postulaciones` | Enviar postulación | CrearPostulacionPage |
| GET | `/postulaciones/{id}` | Obtener postulación por ID | (pending) |
| PATCH | `/postulaciones/{id}/seleccionar` | Seleccionar postulación | (pending) |
| PATCH | `/postulaciones/{id}/rechazar` | Rechazar postulación | (pending) |

### Endpoints Disponibles pero no Consumidos (Brechas UI)
- `/etiquetas` - Catálogo de especialidades
- `/favoritos/empleador/{id}/trabajador/{id}` - Gestionar favoritos
- `/valoraciones` - Ver/crear valoraciones
- `/reportes` - Panel de reportes
- `/suscripciones` - Plan Basic/Pro management
- `/publicidades` - Anuncios publicitarios
- `/admin/dashboard` - Panel administrativo
- `/comunicados/foro` - Foro vecinal
- `/preferencias` - Preferencias notificación
- `/auth/register` - Registro de usuario
- `/auth/verify-email` - Verificación de email
- `/auth/password` - Cambiar contraseña
- `/auth/password/verify` - Verificar contraseña actual
- `/postulaciones/{id}/seleccionar` - Seleccionar postulación
- `/postulaciones/{id}/rechazar` - Rechazar postulación

### Respuesta API Esperada
```typescript
// Ejemplo oferta
interface Oportunidad {
  id: number;
  titulo: string;
  descripcion: string;
  ubicacion: string;
  modalidad: string;     // "PRESENCIAL", "REMOTO", "HIBRIDO"
  rangoSalarial: string; // "Ej: $10.000 - $20.000"
  estado: string;        // "PENDIENTE", "ACTIVA", "CERRADA"
  autor: { id: number; nombre: string };
  etiquetas: string[];   // ["Electricidad", "Plomería"]
}

// Ejemplo trabajador perfil
interface TrabajadorPerfil {
  id: number;
  nombre: string;
  email: string;
  telefono: string;
  fotografia?: string;
  biografia?: string;
  certificados?: string;
  especialidades: string[];
  horarios: Horario[];
  portafolioItems: PortafolioItem[];
  nivelRecomendacion?: number;
  valoracionesCount?: number;
}
```

## Información Importante

### Configuración de Desarrollo
1. **Backend:** Ejecutar desde IDE o `./mvnw spring-boot:run` (puerto 8080)
2. **Frontend:** `npm install` luego `npm run dev` (puerto 5174 por defecto Vite)
3. **Proxy:** Vite proxy configurado para reenviar `/api*` al backend (ver vite.config.ts si existe)
4. **CORS:** Backend tiene `CorsConfig` con origins configurables - en dev usar `http://localhost:5174`

### Navegación
- Usar `<Link>` de react-router-dom para navegación client-side
- Estado global manejado en App.tsx con useState (screen transition)
- Cada página es responsable de cargar sus datos en useEffect

### Manejo de Errores
- Todas las llamadas API van envueltas en try/catch
- Errores se muestran con `alert('Mensaje de error')` o console.error
- Se recomienda crear un hook `useFetch` con manejo centralizado de errores

### Validaciones
- **Frontend:** validaciones básicas (required, pattern, maxLength) en formularios
- **Backend:** validaciones obligatorias (@NotNull, @Size, @Email en DTOs)
- **Nunca confiar solo en validación frontend** - el backend es la fuente de verdad

### Estado de las Brechas (según documento técnico)
- ✅ Favoritos: API implementada, UI componente creado (FavoriteButton.tsx)
- ✅ Perfil trabajador: API tiene datos, página nueva creada (PerfilTrabajadorPage.tsx)
- ✅ Búsqueda por especialidad: Repository tiene métodos, FeedPage tiene filtro
- ✅ Open Graph: Meta tags añadidos a FeedPage
- ⚠️ Notificaciones push/WA: Service implementado lógica de matching, pero UI de alerta no conectada
- ⚠️ Plan Basic/Pro limits: Servicio Java tiene validación, pero UI aún no muestra contador/warning
- ⚠️ Recuperación contraseña: Documentado que "no existe ni en web ni en móvil" - pendiente implementación
- ⚠️ Estadísticas trabajador: Endpoint admin existe, panel worker pendiente

### Comandos Útiles
```bash
# Instalar dependencias
npm install

# Servir en desarrollo (hot reload)
npm run dev

# Build producción
npm run build

# Lint type-aware
npm run lint  # o npx oxlint

# Preview build producido
npm run preview
```