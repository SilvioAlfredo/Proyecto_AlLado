# Auditoría de accesibilidad de AlLado

Copiar `auditar.mjs`, `playwright.auditoria.config.ts` y la carpeta `auditoria` dentro de `Testing/Accesibilidad`. Conserva el test y la configuración originales. Requiere las dependencias ya instaladas: `@playwright/test`, `@axe-core/playwright` y Chromium.

## Ejecutar todas las rutas

Con frontend, backend y PostgreSQL encendidos, desde `Testing/Accesibilidad`:

```bash
node auditar.mjs
node auditar.mjs reporte
```

El primer comando descubre los `page.tsx` del frontend y recorre rutas estáticas en escritorio (1280x720) y móvil web (390x844). Guarda resultados completos de axe, capturas y resumen JSON/CSV en `reporte-accesibilidad`, con evidencia adicional en `resultados-accesibilidad`. Continúa con otras pantallas aunque una falle.

Si el frontend usa otro puerto:

```bash
BASE_URL=http://localhost:3001 node auditar.mjs
```

Cada corrida reemplaza las carpetas de reportes de esta suite: copiar las evidencias previas si se necesitan conservar para comparar.

## Habilitar pantallas protegidas una sola vez

```bash
node auditar.mjs sesion trabajador
node auditar.mjs sesion empleador
node auditar.mjs sesion admin
```

Cada comando abre el login. Iniciar sesión en el navegador con una cuenta de prueba del rol correspondiente y presionar Enter en la terminal cuando se vea el panel. La sesión se guarda localmente en `.auth`, excluida de Git. No incluir `.auth` al compartir evidencias; no es necesaria para evaluar el informe.

Después basta con `node auditar.mjs`. Sin sesión, las rutas protegidas quedan SKIPPED y NO AUDITADAS. Si la sesión vence o el servidor la revoca, volver a guardarla; una redirección o un rol incorrecto hace fallar el caso y no cuenta como una auditoría válida de esa pantalla.

## Interpretar el reporte

- `violations`: infracciones automáticas confirmadas por axe.
- `incomplete`: requieren revisión humana, incluso con test verde.
- `passes`: reglas aprobadas, no porcentaje de conformidad.
- `inapplicable`: reglas no aplicables al estado auditado.
- `NO AUDITADA`: falta sesión, ruta con ID/token, error de carga o de ejecución.
- Un error HTTP de API se registra como problema de ejecución, no como defecto de accesibilidad. Revisar capturas y carga real de datos antes de acreditar cobertura.

El script no envía formularios, crea usuarios ni modifica registros. Revisa la vista inicial de cada ruta. Las pantallas con `[id]`, recuperación/verificación por token, modales, menús desplegados y formularios con errores necesitan escenarios adicionales con datos reales; aparecen como pendientes o requieren tests específicos. Las regiones cargadas de manera diferida también requieren comprobar que se hayan mostrado en la captura.

Esto automatiza axe-core, no Lighthouse, teclado, lector de pantalla ni Android. La emulación móvil es web y no sustituye una prueba de la aplicación Android. No se certifica cumplimiento WCAG con un test verde.

Para correr solo escritorio:

```bash
node auditar.mjs --project=escritorio
```

Para listar pantallas sin ejecutarlas:

```bash
node auditar.mjs --list
```

Antes de la primera ejecución se verificó que Playwright puede cargar y enumerar la suite. No se ejecutaron auditorías de la aplicación durante la preparación del script.
