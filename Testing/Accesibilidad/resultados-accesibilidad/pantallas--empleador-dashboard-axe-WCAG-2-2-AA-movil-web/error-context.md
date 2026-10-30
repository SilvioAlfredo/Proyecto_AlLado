# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: pantallas.spec.ts >> /empleador/dashboard >> axe WCAG 2.2 AA
- Location: auditoria/pantallas.spec.ts:31:9

# Error details

```
Error: Infracciones automáticas de accesibilidad

expect(received).toEqual(expected) // deep equality

- Expected  -   1
+ Received  + 303

- Array []
+ Array [
+   Object {
+     "description": "Ensure the contrast between foreground and background colors meets WCAG 2 AA minimum contrast ratio thresholds",
+     "help": "Elements must meet minimum color contrast ratio thresholds",
+     "helpUrl": "https://dequeuniversity.com/rules/axe/4.14/color-contrast?application=playwright",
+     "id": "color-contrast",
+     "impact": "serious",
+     "nodes": Array [
+       Object {
+         "all": Array [],
+         "any": Array [
+           Object {
+             "data": Object {
+               "bgColor": "#1a1a1c",
+               "contrastRatio": 3.59,
+               "expectedContrastRatio": "4.5:1",
+               "fgColor": "#71717b",
+               "fontSize": "8.3pt (11px)",
+               "fontWeight": "bold",
+               "messageKey": null,
+             },
+             "id": "color-contrast",
+             "impact": "serious",
+             "message": "Element has insufficient color contrast of 3.59 (foreground color: #71717b, background color: #1a1a1c, font size: 8.3pt (11px), font weight: bold). Expected contrast ratio of 4.5:1",
+             "relatedNodes": Array [
+               Object {
+                 "html": "<div class=\"rounded-2xl border p-4 border-[#2e2e33] bg-[#1a1a1c]\"><p class=\"text-[11px] font-bold uppercase tracking-wide text-zinc-500\">Solicitudes activas</p><p class=\"mt-1 text-2xl font-extrabold text-white\">2</p><p class=\"mt-1 text-xs text-zinc-500\">2 publicaciones en total</p></div>",
+                 "target": Array [
+                   ".p-4.border-\\[\\#2e2e33\\].bg-\\[\\#1a1a1c\\]:nth-child(1)",
+                 ],
+               },
+             ],
+           },
+         ],
+         "failureSummary": "Fix any of the following:
+   Element has insufficient color contrast of 3.59 (foreground color: #71717b, background color: #1a1a1c, font size: 8.3pt (11px), font weight: bold). Expected contrast ratio of 4.5:1",
+         "html": "<p class=\"text-[11px] font-bold uppercase tracking-wide text-zinc-500\">Solicitudes activas</p>",
+         "impact": "serious",
+         "none": Array [],
+         "target": Array [
+           ".p-4.border-\\[\\#2e2e33\\].bg-\\[\\#1a1a1c\\]:nth-child(1) > .tracking-wide.text-\\[11px\\].uppercase",
+         ],
+       },
+       Object {
+         "all": Array [],
+         "any": Array [
+           Object {
+             "data": Object {
+               "bgColor": "#1a1a1c",
+               "contrastRatio": 3.59,
+               "expectedContrastRatio": "4.5:1",
+               "fgColor": "#71717b",
+               "fontSize": "9.0pt (12px)",
+               "fontWeight": "normal",
+               "messageKey": null,
+             },
+             "id": "color-contrast",
+             "impact": "serious",
+             "message": "Element has insufficient color contrast of 3.59 (foreground color: #71717b, background color: #1a1a1c, font size: 9.0pt (12px), font weight: normal). Expected contrast ratio of 4.5:1",
+             "relatedNodes": Array [
+               Object {
+                 "html": "<div class=\"rounded-2xl border p-4 border-[#2e2e33] bg-[#1a1a1c]\"><p class=\"text-[11px] font-bold uppercase tracking-wide text-zinc-500\">Solicitudes activas</p><p class=\"mt-1 text-2xl font-extrabold text-white\">2</p><p class=\"mt-1 text-xs text-zinc-500\">2 publicaciones en total</p></div>",
+                 "target": Array [
+                   ".p-4.border-\\[\\#2e2e33\\].bg-\\[\\#1a1a1c\\]:nth-child(1)",
+                 ],
+               },
+             ],
+           },
+         ],
+         "failureSummary": "Fix any of the following:
+   Element has insufficient color contrast of 3.59 (foreground color: #71717b, background color: #1a1a1c, font size: 9.0pt (12px), font weight: normal). Expected contrast ratio of 4.5:1",
+         "html": "<p class=\"mt-1 text-xs text-zinc-500\">2 publicaciones en total</p>",
+         "impact": "serious",
+         "none": Array [],
+         "target": Array [
+           ".p-4.border-\\[\\#2e2e33\\].bg-\\[\\#1a1a1c\\]:nth-child(1) > .mt-1.text-zinc-500.text-xs",
+         ],
+       },
+       Object {
+         "all": Array [],
+         "any": Array [
+           Object {
+             "data": Object {
+               "bgColor": "#1a1a1c",
+               "contrastRatio": 3.59,
+               "expectedContrastRatio": "4.5:1",
+               "fgColor": "#71717b",
+               "fontSize": "8.3pt (11px)",
+               "fontWeight": "bold",
+               "messageKey": null,
+             },
+             "id": "color-contrast",
+             "impact": "serious",
+             "message": "Element has insufficient color contrast of 3.59 (foreground color: #71717b, background color: #1a1a1c, font size: 8.3pt (11px), font weight: bold). Expected contrast ratio of 4.5:1",
+             "relatedNodes": Array [
+               Object {
+                 "html": "<div class=\"rounded-2xl border p-4 border-[#2e2e33] bg-[#1a1a1c]\"><p class=\"text-[11px] font-bold uppercase tracking-wide text-zinc-500\">Propuestas recibidas</p><p class=\"mt-1 text-2xl font-extrabold text-white\">2</p><p class=\"mt-1 text-xs text-zinc-500\">1 pendientes</p></div>",
+                 "target": Array [
+                   ".p-4.border-\\[\\#2e2e33\\].bg-\\[\\#1a1a1c\\]:nth-child(2)",
+                 ],
+               },
+             ],
+           },
+         ],
+         "failureSummary": "Fix any of the following:
+   Element has insufficient color contrast of 3.59 (foreground color: #71717b, background color: #1a1a1c, font size: 8.3pt (11px), font weight: bold). Expected contrast ratio of 4.5:1",
+         "html": "<p class=\"text-[11px] font-bold uppercase tracking-wide text-zinc-500\">Propuestas recibidas</p>",
+         "impact": "serious",
+         "none": Array [],
+         "target": Array [
+           ".p-4.border-\\[\\#2e2e33\\].bg-\\[\\#1a1a1c\\]:nth-child(2) > .tracking-wide.text-\\[11px\\].uppercase",
+         ],
+       },
+       Object {
+         "all": Array [],
+         "any": Array [
+           Object {
+             "data": Object {
+               "bgColor": "#1a1a1c",
+               "contrastRatio": 3.59,
+               "expectedContrastRatio": "4.5:1",
+               "fgColor": "#71717b",
+               "fontSize": "9.0pt (12px)",
+               "fontWeight": "normal",
+               "messageKey": null,
+             },
+             "id": "color-contrast",
+             "impact": "serious",
+             "message": "Element has insufficient color contrast of 3.59 (foreground color: #71717b, background color: #1a1a1c, font size: 9.0pt (12px), font weight: normal). Expected contrast ratio of 4.5:1",
+             "relatedNodes": Array [
+               Object {
+                 "html": "<div class=\"rounded-2xl border p-4 border-[#2e2e33] bg-[#1a1a1c]\"><p class=\"text-[11px] font-bold uppercase tracking-wide text-zinc-500\">Propuestas recibidas</p><p class=\"mt-1 text-2xl font-extrabold text-white\">2</p><p class=\"mt-1 text-xs text-zinc-500\">1 pendientes</p></div>",
+                 "target": Array [
+                   ".p-4.border-\\[\\#2e2e33\\].bg-\\[\\#1a1a1c\\]:nth-child(2)",
+                 ],
+               },
+             ],
+           },
+         ],
+         "failureSummary": "Fix any of the following:
+   Element has insufficient color contrast of 3.59 (foreground color: #71717b, background color: #1a1a1c, font size: 9.0pt (12px), font weight: normal). Expected contrast ratio of 4.5:1",
+         "html": "<p class=\"mt-1 text-xs text-zinc-500\">1 pendientes</p>",
+         "impact": "serious",
+         "none": Array [],
+         "target": Array [
+           ".p-4.border-\\[\\#2e2e33\\].bg-\\[\\#1a1a1c\\]:nth-child(2) > .mt-1.text-zinc-500.text-xs",
+         ],
+       },
+       Object {
+         "all": Array [],
+         "any": Array [
+           Object {
+             "data": Object {
+               "bgColor": "#1a1a1c",
+               "contrastRatio": 3.59,
+               "expectedContrastRatio": "4.5:1",
+               "fgColor": "#71717b",
+               "fontSize": "8.3pt (11px)",
+               "fontWeight": "bold",
+               "messageKey": null,
+             },
+             "id": "color-contrast",
+             "impact": "serious",
+             "message": "Element has insufficient color contrast of 3.59 (foreground color: #71717b, background color: #1a1a1c, font size: 8.3pt (11px), font weight: bold). Expected contrast ratio of 4.5:1",
+             "relatedNodes": Array [
+               Object {
+                 "html": "<div class=\"rounded-2xl border p-4 border-[#2e2e33] bg-[#1a1a1c]\"><p class=\"text-[11px] font-bold uppercase tracking-wide text-zinc-500\">Trabajos en curso</p><p class=\"mt-1 text-2xl font-extrabold text-white\">0</p><p class=\"mt-1 text-xs text-zinc-500\">1 completados</p></div>",
+                 "target": Array [
+                   ".p-4.border-\\[\\#2e2e33\\].bg-\\[\\#1a1a1c\\]:nth-child(3)",
+                 ],
+               },
+             ],
+           },
+         ],
+         "failureSummary": "Fix any of the following:
+   Element has insufficient color contrast of 3.59 (foreground color: #71717b, background color: #1a1a1c, font size: 8.3pt (11px), font weight: bold). Expected contrast ratio of 4.5:1",
+         "html": "<p class=\"text-[11px] font-bold uppercase tracking-wide text-zinc-500\">Trabajos en curso</p>",
+         "impact": "serious",
+         "none": Array [],
+         "target": Array [
+           ".p-4.border-\\[\\#2e2e33\\].bg-\\[\\#1a1a1c\\]:nth-child(3) > .tracking-wide.text-\\[11px\\].uppercase",
+         ],
+       },
+       Object {
+         "all": Array [],
+         "any": Array [
+           Object {
+             "data": Object {
+               "bgColor": "#1a1a1c",
+               "contrastRatio": 3.59,
+               "expectedContrastRatio": "4.5:1",
+               "fgColor": "#71717b",
+               "fontSize": "9.0pt (12px)",
+               "fontWeight": "normal",
+               "messageKey": null,
+             },
+             "id": "color-contrast",
+             "impact": "serious",
+             "message": "Element has insufficient color contrast of 3.59 (foreground color: #71717b, background color: #1a1a1c, font size: 9.0pt (12px), font weight: normal). Expected contrast ratio of 4.5:1",
+             "relatedNodes": Array [
+               Object {
+                 "html": "<div class=\"rounded-2xl border p-4 border-[#2e2e33] bg-[#1a1a1c]\"><p class=\"text-[11px] font-bold uppercase tracking-wide text-zinc-500\">Trabajos en curso</p><p class=\"mt-1 text-2xl font-extrabold text-white\">0</p><p class=\"mt-1 text-xs text-zinc-500\">1 completados</p></div>",
+                 "target": Array [
+                   ".p-4.border-\\[\\#2e2e33\\].bg-\\[\\#1a1a1c\\]:nth-child(3)",
+                 ],
+               },
+             ],
+           },
+         ],
+         "failureSummary": "Fix any of the following:
+   Element has insufficient color contrast of 3.59 (foreground color: #71717b, background color: #1a1a1c, font size: 9.0pt (12px), font weight: normal). Expected contrast ratio of 4.5:1",
+         "html": "<p class=\"mt-1 text-xs text-zinc-500\">1 completados</p>",
+         "impact": "serious",
+         "none": Array [],
+         "target": Array [
+           ".p-4.border-\\[\\#2e2e33\\].bg-\\[\\#1a1a1c\\]:nth-child(3) > .mt-1.text-zinc-500.text-xs",
+         ],
+       },
+       Object {
+         "all": Array [],
+         "any": Array [
+           Object {
+             "data": Object {
+               "bgColor": "#1a1a1c",
+               "contrastRatio": 3.59,
+               "expectedContrastRatio": "4.5:1",
+               "fgColor": "#71717b",
+               "fontSize": "8.3pt (11px)",
+               "fontWeight": "bold",
+               "messageKey": null,
+             },
+             "id": "color-contrast",
+             "impact": "serious",
+             "message": "Element has insufficient color contrast of 3.59 (foreground color: #71717b, background color: #1a1a1c, font size: 8.3pt (11px), font weight: bold). Expected contrast ratio of 4.5:1",
+             "relatedNodes": Array [
+               Object {
+                 "html": "<div class=\"rounded-2xl border p-4 border-[#2e2e33] bg-[#1a1a1c]\"><p class=\"text-[11px] font-bold uppercase tracking-wide text-zinc-500\">Profesionales guardados</p><p class=\"mt-1 text-2xl font-extrabold text-white\">1</p><p class=\"mt-1 text-xs text-zinc-500\">Tu gente de confianza</p></div>",
+                 "target": Array [
+                   ".p-4.border-\\[\\#2e2e33\\].bg-\\[\\#1a1a1c\\]:nth-child(4)",
+                 ],
+               },
+             ],
+           },
+         ],
+         "failureSummary": "Fix any of the following:
+   Element has insufficient color contrast of 3.59 (foreground color: #71717b, background color: #1a1a1c, font size: 8.3pt (11px), font weight: bold). Expected contrast ratio of 4.5:1",
+         "html": "<p class=\"text-[11px] font-bold uppercase tracking-wide text-zinc-500\">Profesionales guardados</p>",
+         "impact": "serious",
+         "none": Array [],
+         "target": Array [
+           ".p-4.border-\\[\\#2e2e33\\].bg-\\[\\#1a1a1c\\]:nth-child(4) > .tracking-wide.text-\\[11px\\].uppercase",
+         ],
+       },
+       Object {
+         "all": Array [],
+         "any": Array [
+           Object {
+             "data": Object {
+               "bgColor": "#1a1a1c",
+               "contrastRatio": 3.59,
+               "expectedContrastRatio": "4.5:1",
+               "fgColor": "#71717b",
+               "fontSize": "9.0pt (12px)",
+               "fontWeight": "normal",
+               "messageKey": null,
+             },
+             "id": "color-contrast",
+             "impact": "serious",
+             "message": "Element has insufficient color contrast of 3.59 (foreground color: #71717b, background color: #1a1a1c, font size: 9.0pt (12px), font weight: normal). Expected contrast ratio of 4.5:1",
+             "relatedNodes": Array [
+               Object {
+                 "html": "<div class=\"rounded-2xl border p-4 border-[#2e2e33] bg-[#1a1a1c]\"><p class=\"text-[11px] font-bold uppercase tracking-wide text-zinc-500\">Profesionales guardados</p><p class=\"mt-1 text-2xl font-extrabold text-white\">1</p><p class=\"mt-1 text-xs text-zinc-500\">Tu gente de confianza</p></div>",
+                 "target": Array [
+                   ".p-4.border-\\[\\#2e2e33\\].bg-\\[\\#1a1a1c\\]:nth-child(4)",
+                 ],
+               },
+             ],
+           },
+         ],
+         "failureSummary": "Fix any of the following:
+   Element has insufficient color contrast of 3.59 (foreground color: #71717b, background color: #1a1a1c, font size: 9.0pt (12px), font weight: normal). Expected contrast ratio of 4.5:1",
+         "html": "<p class=\"mt-1 text-xs text-zinc-500\">Tu gente de confianza</p>",
+         "impact": "serious",
+         "none": Array [],
+         "target": Array [
+           ".p-4.border-\\[\\#2e2e33\\].bg-\\[\\#1a1a1c\\]:nth-child(4) > .mt-1.text-zinc-500.text-xs",
+         ],
+       },
+     ],
+     "tags": Array [
+       "cat.color",
+       "wcag2aa",
+       "wcag143",
+       "TTv5",
+       "TT13.c",
+       "EN-301-549",
+       "EN-9.1.4.3",
+       "ACT",
+       "RGAAv4",
+       "RGAA-3.2.1",
+     ],
+   },
+ ]
```

# Page snapshot

```yaml
- generic [active] [ref=e1]:
  - banner [ref=e2]:
    - generic [ref=e3]:
      - link "◉ AlLado SERVICIOS LOCALES" [ref=e4] [cursor=pointer]:
        - /url: /
        - generic [ref=e5]: ◉
        - generic [ref=e6]:
          - generic [ref=e7]: AlLado
          - generic [ref=e8]: SERVICIOS LOCALES
      - navigation "Navegación principal" [ref=e9]:
        - link "Notificaciones" [ref=e10] [cursor=pointer]:
          - /url: /empleador/notificaciones
        - link "Mi Panel" [ref=e14] [cursor=pointer]:
          - /url: /empleador/dashboard
        - button "Salir" [ref=e15] [cursor=pointer]
  - main [ref=e16]:
    - generic [ref=e18]:
      - button "Espacio empleador" [ref=e20]
      - generic [ref=e22]:
        - generic [ref=e23]:
          - generic [ref=e24]:
            - paragraph [ref=e25]: Tu espacio · Empleador
            - heading "Hola, Demo 👋" [level=1] [ref=e26]
            - paragraph [ref=e27]: ¿Qué necesitás resolver hoy?
          - link [ref=e29] [cursor=pointer]:
            - /url: /empleador/trabajadores
            - button "Buscar trabajadores" [ref=e30]
        - generic [ref=e31]:
          - generic [ref=e32]:
            - paragraph [ref=e33]: Solicitudes activas
            - paragraph [ref=e34]: "2"
            - paragraph [ref=e35]: 2 publicaciones en total
          - generic [ref=e36]:
            - paragraph [ref=e37]: Propuestas recibidas
            - paragraph [ref=e38]: "2"
            - paragraph [ref=e39]: 1 pendientes
          - generic [ref=e40]:
            - paragraph [ref=e41]: Trabajos en curso
            - paragraph [ref=e42]: "0"
            - paragraph [ref=e43]: 1 completados
          - generic [ref=e44]:
            - paragraph [ref=e45]: Profesionales guardados
            - paragraph [ref=e46]: "1"
            - paragraph [ref=e47]: Tu gente de confianza
        - generic [ref=e48]:
          - generic [ref=e49]:
            - text: UNA NECESIDAD, MUCHAS SOLUCIONES
            - heading "El profesional indicado, a un paso." [level=2] [ref=e50]
            - paragraph [ref=e51]: Contá qué necesitás y recibí propuestas para tu trabajo.
            - link [ref=e52] [cursor=pointer]:
              - /url: /empleador/solicitudes/nueva
              - button "+ Publicar una solicitud" [ref=e53]
          - generic [ref=e54]:
            - heading "Explorá especialidades" [level=2] [ref=e55]
            - generic [ref=e56]:
              - link "Electricidad" [ref=e57] [cursor=pointer]:
                - /url: /empleador/trabajadores?especialidad=Electricidad
              - link "Plomería" [ref=e60] [cursor=pointer]:
                - /url: /empleador/trabajadores?especialidad=Plomer%C3%ADa
              - link "Pintura" [ref=e63] [cursor=pointer]:
                - /url: /empleador/trabajadores?especialidad=Pintura
              - link "Jardinería" [ref=e68] [cursor=pointer]:
                - /url: /empleador/trabajadores?especialidad=Jardiner%C3%ADa
          - generic [ref=e72]:
            - heading "Mis solicitudes activas" [level=2] [ref=e73]
            - article [ref=e74]:
              - heading "Arreglo de cañería" [level=3] [ref=e75]
              - paragraph [ref=e76]: Se necesita plomero matriculado en Belgrano
              - link "Gestionar solicitud →" [ref=e77] [cursor=pointer]:
                - /url: /empleador/solicitudes/1
            - article [ref=e78]:
              - heading "Tramite de jubilación" [level=3] [ref=e79]
              - paragraph [ref=e80]: Necesito una persona que pueda hacer tramites de jubilación.
              - link "Gestionar solicitud →" [ref=e81] [cursor=pointer]:
                - /url: /empleador/solicitudes/3
            - link "Ver todas las solicitudes →" [ref=e82] [cursor=pointer]:
              - /url: /empleador/solicitudes
          - generic [ref=e83]:
            - heading "Tus trabajos" [level=2] [ref=e84]
            - paragraph [ref=e85]: Cuando selecciones una propuesta, encontrarás el trabajo acá.
            - link "Un trabajo terminado, una opinión útil. Valorar →" [ref=e86] [cursor=pointer]:
              - /url: /empleador/contratos
  - button "Open Next.js Dev Tools" [ref=e92] [cursor=pointer]
  - alert [ref=e96]
```

# Test source

```ts
  1  | import { test, expect } from '@playwright/test';
  2  | import AxeBuilder from '@axe-core/playwright';
  3  | import fs from 'node:fs';
  4  | import path from 'node:path';
  5  | 
  6  | const root = process.cwd();
  7  | const app = path.resolve(root, '../../Frontend/al_lado-app/src/app');
  8  | const roles = ['trabajador', 'empleador', 'admin'];
  9  | function files(dir: string): string[] {
  10 |   return fs.readdirSync(dir, { withFileTypes: true }).flatMap(entry => {
  11 |     const p = path.join(dir, entry.name);
  12 |     return entry.isDirectory() ? files(p) : entry.name === 'page.tsx' ? [p] : [];
  13 |   });
  14 | }
  15 | if (!fs.existsSync(app)) throw new Error('Ejecutar desde Testing/Accesibilidad; no se encontró src/app del frontend.');
  16 | const catalog = files(app).map(file => {
  17 |   const route = '/' + path.relative(app, path.dirname(file)).split(path.sep)
  18 |     .filter(part => part && !part.startsWith('(')).join('/');
  19 |   const role = roles.find(r => route.startsWith('/' + r + '/'))
  20 |     || (route === '/publicar' ? 'empleador' : 'publico');
  21 |   const excluded = route.includes('[') ? 'Requiere ID real y datos de prueba.'
  22 |     : ['/reset-password', '/verify-email'].includes(route) ? 'Requiere token válido y estado específico.' : '';
  23 |   return { route, role, excluded, file: path.relative(path.resolve(root, '../..'), file) };
  24 | }).sort((a,b) => a.route.localeCompare(b.route));
  25 | 
  26 | for (const item of catalog) {
  27 |   const state = path.join(root, '.auth', item.role + '.json');
  28 |   const authenticated = item.role !== 'publico' && fs.existsSync(state);
  29 |   test.describe(item.route, () => {
  30 |     test.use({ storageState: authenticated ? state : undefined });
  31 |     test('axe WCAG 2.2 AA', async ({ page }, testInfo) => {
  32 |       await testInfo.attach('alcance.json', {
  33 |         body: JSON.stringify({ ...item, project: testInfo.project.name, outputDir: testInfo.outputDir }), contentType: 'application/json',
  34 |       });
  35 |       test.skip(Boolean(item.excluded), item.excluded);
  36 |       test.skip(item.role !== 'publico' && !authenticated,
  37 |         `Falta sesión ${item.role}: node auditar.mjs sesion ${item.role}`);
  38 |       const errors: string[] = [];
  39 |       page.on('response', response => {
  40 |         const url = new URL(response.url());
  41 |         // HTTP errors are execution diagnostics, not axe accessibility violations.
  42 |         if (['fetch','xhr'].includes(response.request().resourceType())
  43 |             && ['localhost', '127.0.0.1'].includes(url.hostname) && response.status() >= 400) {
  44 |           errors.push(`${response.status()} ${url.pathname}`);
  45 |         }
  46 |       });
  47 |       const response = await page.goto(item.route, { waitUntil: 'load' });
  48 |       expect(response?.ok(), 'La página debe cargar sin error HTTP').toBeTruthy();
  49 |       await expect.poll(() => new URL(page.url()).pathname.replace(/\/$/, '') || '/',
  50 |         { message: 'La ruta no debe redirigir al login ni a otra pantalla' })
  51 |         .toBe(item.route.replace(/\/$/, '') || '/');
  52 |       await expect(page.locator('h1:visible, h2:visible, form:visible').first()).toBeVisible();
  53 |       await expect.poll(async () => page.getByText(/^Cargando(?:\s.*|\.{3}|…)?$/i).count(),
  54 |         { message: 'Esperar a que desaparezca el estado de carga' }).toBe(0);
  55 |       await page.evaluate(() => document.fonts.ready.then(() => undefined));
  56 |       if (item.role !== 'publico') {
  57 |         const actualRoles = await page.evaluate(() => JSON.parse(localStorage.getItem('user_roles') || '[]'));
  58 |         expect(actualRoles).toContain('ROLE_' + item.role.toUpperCase());
  59 |       }
  60 |       const results = await new AxeBuilder({ page }).withTags([
  61 |         'wcag2a', 'wcag2aa', 'wcag21a', 'wcag21aa', 'wcag22aa',
  62 |       ]).analyze();
  63 |       await testInfo.attach('resultado-axe.json', {
  64 |         body: JSON.stringify(results, null, 2), contentType: 'application/json',
  65 |       });
  66 |       await testInfo.attach('pantalla-auditada.png', {
  67 |         body: await page.screenshot({ fullPage: true }), contentType: 'image/png',
  68 |       });
  69 |       await testInfo.attach('diagnostico-http.json', {
  70 |         body: JSON.stringify(errors, null, 2), contentType: 'application/json',
  71 |       });
  72 |       // Store a JSON next to the runner's evidence for aggregation and sharing.
  73 |       await fs.promises.writeFile(testInfo.outputPath('resultado-axe.json'), JSON.stringify(results, null, 2));
  74 |       await expect.soft(page, 'No debe cambiar de ruta durante la auditoría')
  75 |         .toHaveURL(new RegExp(item.route === '/' ? '/$' : item.route.replace(/[.*+?^${}()|[\]\\]/g, '\\$&') + '/?$'));
  76 |       expect.soft(errors, 'Errores de API: revisar antes de acreditar la pantalla completa').toEqual([]);
> 77 |       expect(results.violations, 'Infracciones automáticas de accesibilidad').toEqual([]);
     |                                                                               ^ Error: Infracciones automáticas de accesibilidad
  78 |       // incomplete is deliberately kept for human review, never asserted as a confirmed failure.
  79 |     });
  80 |   });
  81 | }
  82 | 
```