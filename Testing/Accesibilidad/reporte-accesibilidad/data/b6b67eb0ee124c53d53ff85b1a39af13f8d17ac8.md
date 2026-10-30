# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: pantallas.spec.ts >> /admin/publicaciones >> axe WCAG 2.2 AA
- Location: auditoria/pantallas.spec.ts:31:9

# Error details

```
Error: Infracciones automáticas de accesibilidad

expect(received).toEqual(expected) // deep equality

- Expected  -   1
+ Received  + 403

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
+               "bgColor": "#111111",
+               "contrastRatio": 3.91,
+               "expectedContrastRatio": "4.5:1",
+               "fgColor": "#71717b",
+               "fontSize": "9.0pt (12px)",
+               "fontWeight": "normal",
+               "messageKey": null,
+             },
+             "id": "color-contrast",
+             "impact": "serious",
+             "message": "Element has insufficient color contrast of 3.91 (foreground color: #71717b, background color: #111111, font size: 9.0pt (12px), font weight: normal). Expected contrast ratio of 4.5:1",
+             "relatedNodes": Array [
+               Object {
+                 "html": "<aside class=\"w-full shrink-0 border-b border-[#2e2e33] bg-[#111] p-4 md:sticky md:top-16 md:h-[calc(100vh-4rem)] md:w-56 md:overflow-y-auto md:border-r md:border-b-0\">",
+                 "target": Array [
+                   "aside",
+                 ],
+               },
+             ],
+           },
+         ],
+         "failureSummary": "Fix any of the following:
+   Element has insufficient color contrast of 3.91 (foreground color: #71717b, background color: #111111, font size: 9.0pt (12px), font weight: normal). Expected contrast ratio of 4.5:1",
+         "html": "<p class=\"mb-1 text-xs font-semibold text-zinc-500\">Inicio</p>",
+         "impact": "serious",
+         "none": Array [],
+         "target": Array [
+           ".min-w-36:nth-child(1) > .mb-1",
+         ],
+       },
+       Object {
+         "all": Array [],
+         "any": Array [
+           Object {
+             "data": Object {
+               "bgColor": "#111111",
+               "contrastRatio": 3.91,
+               "expectedContrastRatio": "4.5:1",
+               "fgColor": "#71717b",
+               "fontSize": "9.0pt (12px)",
+               "fontWeight": "normal",
+               "messageKey": null,
+             },
+             "id": "color-contrast",
+             "impact": "serious",
+             "message": "Element has insufficient color contrast of 3.91 (foreground color: #71717b, background color: #111111, font size: 9.0pt (12px), font weight: normal). Expected contrast ratio of 4.5:1",
+             "relatedNodes": Array [
+               Object {
+                 "html": "<aside class=\"w-full shrink-0 border-b border-[#2e2e33] bg-[#111] p-4 md:sticky md:top-16 md:h-[calc(100vh-4rem)] md:w-56 md:overflow-y-auto md:border-r md:border-b-0\">",
+                 "target": Array [
+                   "aside",
+                 ],
+               },
+             ],
+           },
+         ],
+         "failureSummary": "Fix any of the following:
+   Element has insufficient color contrast of 3.91 (foreground color: #71717b, background color: #111111, font size: 9.0pt (12px), font weight: normal). Expected contrast ratio of 4.5:1",
+         "html": "<p class=\"mb-1 text-xs font-semibold text-zinc-500\">Personas</p>",
+         "impact": "serious",
+         "none": Array [],
+         "target": Array [
+           ".min-w-36:nth-child(2) > .mb-1",
+         ],
+       },
+       Object {
+         "all": Array [],
+         "any": Array [
+           Object {
+             "data": Object {
+               "bgColor": "#0b0b0c",
+               "contrastRatio": 4.07,
+               "expectedContrastRatio": "4.5:1",
+               "fgColor": "#71717b",
+               "fontSize": "10.5pt (14px)",
+               "fontWeight": "normal",
+               "messageKey": null,
+             },
+             "id": "color-contrast",
+             "impact": "serious",
+             "message": "Element has insufficient color contrast of 4.07 (foreground color: #71717b, background color: #0b0b0c, font size: 10.5pt (14px), font weight: normal). Expected contrast ratio of 4.5:1",
+             "relatedNodes": Array [
+               Object {
+                 "html": "<body class=\"min-h-screen flex flex-col bg-[#0b0b0c] text-zinc-100\">",
+                 "target": Array [
+                   "body",
+                 ],
+               },
+             ],
+           },
+         ],
+         "failureSummary": "Fix any of the following:
+   Element has insufficient color contrast of 4.07 (foreground color: #71717b, background color: #0b0b0c, font size: 10.5pt (14px), font weight: normal). Expected contrast ratio of 4.5:1",
+         "html": "<p class=\"text-sm text-zinc-500\">Gestión de reportes · GET /reportes · PUT /reportes/{id}/resolver (ADMIN)</p>",
+         "impact": "serious",
+         "none": Array [],
+         "target": Array [
+           ".space-y-6 > div:nth-child(1) > p",
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
+               "fontSize": "10.5pt (14px)",
+               "fontWeight": "normal",
+               "messageKey": null,
+             },
+             "id": "color-contrast",
+             "impact": "serious",
+             "message": "Element has insufficient color contrast of 3.59 (foreground color: #71717b, background color: #1a1a1c, font size: 10.5pt (14px), font weight: normal). Expected contrast ratio of 4.5:1",
+             "relatedNodes": Array [
+               Object {
+                 "html": "<div class=\"bg-[#1a1a1c] rounded-2xl border border-[#2e2e33] overflow-hidden\">",
+                 "target": Array [
+                   ".bg-\\[\\#1a1a1c\\]",
+                 ],
+               },
+             ],
+           },
+         ],
+         "failureSummary": "Fix any of the following:
+   Element has insufficient color contrast of 3.59 (foreground color: #71717b, background color: #1a1a1c, font size: 10.5pt (14px), font weight: normal). Expected contrast ratio of 4.5:1",
+         "html": "<th class=\"text-left p-4 text-sm font-medium text-zinc-500\">ID</th>",
+         "impact": "serious",
+         "none": Array [],
+         "target": Array [
+           "th:nth-child(1)",
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
+               "fontSize": "10.5pt (14px)",
+               "fontWeight": "normal",
+               "messageKey": null,
+             },
+             "id": "color-contrast",
+             "impact": "serious",
+             "message": "Element has insufficient color contrast of 3.59 (foreground color: #71717b, background color: #1a1a1c, font size: 10.5pt (14px), font weight: normal). Expected contrast ratio of 4.5:1",
+             "relatedNodes": Array [
+               Object {
+                 "html": "<div class=\"bg-[#1a1a1c] rounded-2xl border border-[#2e2e33] overflow-hidden\">",
+                 "target": Array [
+                   ".bg-\\[\\#1a1a1c\\]",
+                 ],
+               },
+             ],
+           },
+         ],
+         "failureSummary": "Fix any of the following:
+   Element has insufficient color contrast of 3.59 (foreground color: #71717b, background color: #1a1a1c, font size: 10.5pt (14px), font weight: normal). Expected contrast ratio of 4.5:1",
+         "html": "<th class=\"text-left p-4 text-sm font-medium text-zinc-500\">Tipo</th>",
+         "impact": "serious",
+         "none": Array [],
+         "target": Array [
+           "th:nth-child(2)",
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
+               "fontSize": "10.5pt (14px)",
+               "fontWeight": "normal",
+               "messageKey": null,
+             },
+             "id": "color-contrast",
+             "impact": "serious",
+             "message": "Element has insufficient color contrast of 3.59 (foreground color: #71717b, background color: #1a1a1c, font size: 10.5pt (14px), font weight: normal). Expected contrast ratio of 4.5:1",
+             "relatedNodes": Array [
+               Object {
+                 "html": "<div class=\"bg-[#1a1a1c] rounded-2xl border border-[#2e2e33] overflow-hidden\">",
+                 "target": Array [
+                   ".bg-\\[\\#1a1a1c\\]",
+                 ],
+               },
+             ],
+           },
+         ],
+         "failureSummary": "Fix any of the following:
+   Element has insufficient color contrast of 3.59 (foreground color: #71717b, background color: #1a1a1c, font size: 10.5pt (14px), font weight: normal). Expected contrast ratio of 4.5:1",
+         "html": "<th class=\"text-left p-4 text-sm font-medium text-zinc-500\">Contenido</th>",
+         "impact": "serious",
+         "none": Array [],
+         "target": Array [
+           "th:nth-child(3)",
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
+               "fontSize": "10.5pt (14px)",
+               "fontWeight": "normal",
+               "messageKey": null,
+             },
+             "id": "color-contrast",
+             "impact": "serious",
+             "message": "Element has insufficient color contrast of 3.59 (foreground color: #71717b, background color: #1a1a1c, font size: 10.5pt (14px), font weight: normal). Expected contrast ratio of 4.5:1",
+             "relatedNodes": Array [
+               Object {
+                 "html": "<div class=\"bg-[#1a1a1c] rounded-2xl border border-[#2e2e33] overflow-hidden\">",
+                 "target": Array [
+                   ".bg-\\[\\#1a1a1c\\]",
+                 ],
+               },
+             ],
+           },
+         ],
+         "failureSummary": "Fix any of the following:
+   Element has insufficient color contrast of 3.59 (foreground color: #71717b, background color: #1a1a1c, font size: 10.5pt (14px), font weight: normal). Expected contrast ratio of 4.5:1",
+         "html": "<th class=\"text-left p-4 text-sm font-medium text-zinc-500\">Motivo</th>",
+         "impact": "serious",
+         "none": Array [],
+         "target": Array [
+           "th:nth-child(4)",
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
+   Object {
+     "description": "Ensure elements that have scrollable content are accessible by keyboard in Safari",
+     "help": "Scrollable region must have keyboard access",
+     "helpUrl": "https://dequeuniversity.com/rules/axe/4.14/scrollable-region-focusable?application=playwright",
+     "id": "scrollable-region-focusable",
+     "impact": "serious",
+     "nodes": Array [
+       Object {
+         "all": Array [],
+         "any": Array [
+           Object {
+             "data": null,
+             "id": "focusable-content",
+             "impact": "serious",
+             "message": "Element should have focusable content",
+             "relatedNodes": Array [],
+           },
+           Object {
+             "data": null,
+             "id": "focusable-element",
+             "impact": "serious",
+             "message": "Element should be focusable",
+             "relatedNodes": Array [],
+           },
+         ],
+         "failureSummary": "Fix any of the following:
+   Element should have focusable content
+   Element should be focusable",
+         "html": "<div class=\"overflow-x-auto\">",
+         "impact": "serious",
+         "none": Array [],
+         "target": Array [
+           ".bg-\\[\\#1a1a1c\\] > .overflow-x-auto",
+         ],
+       },
+     ],
+     "tags": Array [
+       "cat.keyboard",
+       "wcag2a",
+       "wcag211",
+       "wcag213",
+       "TTv5",
+       "TT4.a",
+       "EN-301-549",
+       "EN-9.2.1.1",
+       "EN-9.2.1.3",
+       "RGAAv4",
+       "RGAA-7.3.2",
+     ],
+   },
+   Object {
+     "description": "Ensure select element has an accessible name",
+     "help": "Select element must have an accessible name",
+     "helpUrl": "https://dequeuniversity.com/rules/axe/4.14/select-name?application=playwright",
+     "id": "select-name",
+     "impact": "critical",
+     "nodes": Array [
+       Object {
+         "all": Array [],
+         "any": Array [
+           Object {
+             "data": null,
+             "id": "implicit-label",
+             "impact": "critical",
+             "message": "Element does not have an implicit (wrapped) <label>",
+             "relatedNodes": Array [],
+           },
+           Object {
+             "data": null,
+             "id": "explicit-label",
+             "impact": "critical",
+             "message": "Element does not have an explicit <label>",
+             "relatedNodes": Array [],
+           },
+           Object {
+             "data": null,
+             "id": "aria-label",
+             "impact": "critical",
+             "message": "aria-label attribute does not exist or is empty",
+             "relatedNodes": Array [],
+           },
+           Object {
+             "data": null,
+             "id": "aria-labelledby",
+             "impact": "critical",
+             "message": "aria-labelledby attribute does not exist, references elements that do not exist or references elements that are empty",
+             "relatedNodes": Array [],
+           },
+           Object {
+             "data": Object {
+               "messageKey": "noAttr",
+             },
+             "id": "non-empty-title",
+             "impact": "critical",
+             "message": "Element has no title attribute",
+             "relatedNodes": Array [],
+           },
+           Object {
+             "data": null,
+             "id": "presentational-role",
+             "impact": "critical",
+             "message": "Element's default semantics were not overridden with role=\"none\" or role=\"presentation\"",
+             "relatedNodes": Array [],
+           },
+         ],
+         "failureSummary": "Fix any of the following:
+   Element does not have an implicit (wrapped) <label>
+   Element does not have an explicit <label>
+   aria-label attribute does not exist or is empty
+   aria-labelledby attribute does not exist, references elements that do not exist or references elements that are empty
+   Element has no title attribute
+   Element's default semantics were not overridden with role=\"none\" or role=\"presentation\"",
+         "html": "<select class=\"h-11 w-full rounded-full border border-[#3a3a3f] bg-[#26262a] px-4 text-sm text-white\">",
+         "impact": "critical",
+         "none": Array [],
+         "target": Array [
+           "select",
+         ],
+       },
+     ],
+     "tags": Array [
+       "cat.forms",
+       "wcag2a",
+       "wcag412",
+       "section508",
+       "section508.22.n",
+       "TTv5",
+       "TT5.c",
+       "EN-301-549",
+       "EN-9.4.1.2",
+       "ACT",
+       "RGAAv4",
+       "RGAA-11.1.1",
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
        - link "Reportes de la comunidad" [ref=e10] [cursor=pointer]:
          - /url: /admin/reportes
        - link "Mi Panel" [ref=e14] [cursor=pointer]:
          - /url: /admin/dashboard
        - button "Salir" [ref=e15] [cursor=pointer]
  - main [ref=e16]:
    - generic [ref=e17]:
      - complementary [ref=e18]:
        - paragraph [ref=e19]: Panel administrativo
        - navigation "Administración" [ref=e20]:
          - generic [ref=e21]:
            - paragraph [ref=e22]: Inicio
            - link "Resumen" [ref=e23] [cursor=pointer]:
              - /url: /admin/dashboard
          - generic [ref=e24]:
            - paragraph [ref=e25]: Personas
            - link "Comunidad" [ref=e26] [cursor=pointer]:
              - /url: /admin/personas
            - link "Usuarios" [ref=e27] [cursor=pointer]:
              - /url: /admin/usuarios
          - generic [ref=e28]:
            - paragraph [ref=e29]: Moderación
            - link "Reportes" [ref=e30] [cursor=pointer]:
              - /url: /admin/reportes
            - link "Foro y valoraciones" [ref=e31] [cursor=pointer]:
              - /url: /admin/foro
            - link "Empresas" [ref=e32] [cursor=pointer]:
              - /url: /admin/empresas
          - generic [ref=e33]:
            - paragraph [ref=e34]: Aportes
            - link "Transferencias" [ref=e35] [cursor=pointer]:
              - /url: /admin/donaciones
            - link "Suscripciones" [ref=e36] [cursor=pointer]:
              - /url: /admin/suscripciones
          - generic [ref=e37]:
            - paragraph [ref=e38]: Gestión
            - link "Comunicados" [ref=e39] [cursor=pointer]:
              - /url: /admin/comunicados
            - link "Oportunidades" [ref=e40] [cursor=pointer]:
              - /url: /admin/oportunidades
            - link "Publicidad" [ref=e41] [cursor=pointer]:
              - /url: /admin/publicidad
            - link "Etiquetas" [ref=e42] [cursor=pointer]:
              - /url: /admin/etiquetas
      - generic [ref=e44]:
        - generic [ref=e45]:
          - heading "Panel de Moderación" [level=1] [ref=e46]
          - paragraph [ref=e47]: "Gestión de reportes · GET /reportes · PUT /reportes/{id}/resolver (ADMIN)"
        - combobox [ref=e49]:
          - option "Todos" [selected]
          - option "Pendientes"
          - option "En revisión"
          - option "Resueltos"
          - option "Descartados"
        - table [ref=e52]:
          - rowgroup [ref=e53]:
            - row [ref=e54]:
              - columnheader "ID" [ref=e55]
              - columnheader "Tipo" [ref=e56]
              - columnheader "Contenido" [ref=e57]
              - columnheader "Motivo" [ref=e58]
              - columnheader "Autor" [ref=e59]
              - columnheader "Fecha" [ref=e60]
              - columnheader "Estatus" [ref=e61]
          - rowgroup [ref=e62]:
            - row [ref=e63]:
              - cell "1" [ref=e64]
              - cell "reporte" [ref=e65]
              - cell "Publicación dudosa" [ref=e66]
              - cell "Spam" [ref=e67]
              - cell "Anónimo" [ref=e68]
              - cell "09/10/2026" [ref=e69]
              - cell "RESUELTO" [ref=e70]
            - row [ref=e72]:
              - cell "2" [ref=e73]
              - cell "reporte" [ref=e74]
              - cell "Publicación dudosa" [ref=e75]
              - cell "Spam" [ref=e76]
              - cell "Anónimo" [ref=e77]
              - cell "09/10/2026" [ref=e78]
              - cell "RESUELTO" [ref=e79]
  - button "Open Next.js Dev Tools" [ref=e86] [cursor=pointer]
  - alert [ref=e90]
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