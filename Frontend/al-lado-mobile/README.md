# AlLado Mobile (Expo SDK 57 + Expo Router + TypeScript)

App móvil oscura (#0b0b0c + amarillo #ffc107) según Figma `diseños-figma/mobile`
y paridad funcional con `al_lado-app` (Next.js) contra el backend Spring Boot.

App móvil oscura (#0b0b0c + amarillo #ffc107) según Figma `diseños-figma/mobile`
y paridad funcional con `al_lado-app` (Next.js) contra el backend Spring Boot.

## Configurar API

Prioridad: `EXPO_PUBLIC_API_URL` > `app.json > extra.apiUrl` > `http://localhost:8080`.

```bash
# Emulador / web local
EXPO_PUBLIC_API_URL=http://localhost:8080 npx expo start

# Expo Go en dispositivo físico (misma WiFi, IP de tu PC)
EXPO_PUBLIC_API_URL=http://192.168.1.10:8080 npx expo start
```

O editá `app.json > extra.apiUrl`. No hardcodear IPs en código.

## Comandos

```bash
npm install
npx expo start --clear   # dev (usá --tunnel si el teléfono no ve tu LAN)
npx tsc --noEmit         # typecheck (TS estricto)
npx expo-doctor          # diagnóstico
```

Auth: `POST /auth/login|register` → JWT en SecureStore
(`auth_token`), roles `ROLE_*`, `user_id` resuelto por email.
401 de backend = sesión inválida (re-login).

## Rutas

- `/(auth)/login|register|forgot-password`
- `/trabajador/oportunidades[/filtros|/[id][/postular]]`, `/postulaciones[/[id]]`, `/comunidad[/nueva|/reportar]`, `/perfil[/editar|/disponibilidad|/portafolio|/valoraciones|/suscripcion|/preferencias]`
- `/empleador` (inicio), `/solicitudes[/nueva|/[id][/postulantes]]`, `/trabajadores[/[id]]`, `/contratos`, `/contratos-valorar`, `/cuenta[/favoritos|/empresa|/anuncios|/anuncio-editar|/plan]`
- `/admin/dashboard|personas|moderacion|aportes|gestion` (solo ROLE_ADMIN)
