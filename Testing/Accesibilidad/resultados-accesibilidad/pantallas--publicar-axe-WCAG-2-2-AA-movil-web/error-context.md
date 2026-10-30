# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: pantallas.spec.ts >> /publicar >> axe WCAG 2.2 AA
- Location: auditoria/pantallas.spec.ts:31:9

# Error details

```
Error: Infracciones automáticas de accesibilidad

expect(received).toEqual(expected) // deep equality

- Expected  -   1
+ Received  + 440

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
+               "bgColor": "#141416",
+               "contrastRatio": 3.81,
+               "expectedContrastRatio": "4.5:1",
+               "fgColor": "#71717b",
+               "fontSize": "9.0pt (12px)",
+               "fontWeight": "normal",
+               "messageKey": null,
+             },
+             "id": "color-contrast",
+             "impact": "serious",
+             "message": "Element has insufficient color contrast of 3.81 (foreground color: #71717b, background color: #141416, font size: 9.0pt (12px), font weight: normal). Expected contrast ratio of 4.5:1",
+             "relatedNodes": Array [
+               Object {
+                 "html": "<form class=\"space-y-6 rounded-2xl border border-[#2e2e33] bg-[#141416] p-6\">",
+                 "target": Array [
+                   "form",
+                 ],
+               },
+             ],
+           },
+         ],
+         "failureSummary": "Fix any of the following:
+   Element has insufficient color contrast of 3.81 (foreground color: #71717b, background color: #141416, font size: 9.0pt (12px), font weight: normal). Expected contrast ratio of 4.5:1",
+         "html": "<span class=\"text-xs text-zinc-500\">0<!-- -->/500</span>",
+         "impact": "serious",
+         "none": Array [],
+         "target": Array [
+           "div:nth-child(1) > .mb-2.justify-between.items-center > .text-xs.text-zinc-500",
+         ],
+       },
+       Object {
+         "all": Array [],
+         "any": Array [
+           Object {
+             "data": Object {
+               "bgColor": "#141416",
+               "contrastRatio": 3.81,
+               "expectedContrastRatio": "4.5:1",
+               "fgColor": "#71717b",
+               "fontSize": "9.0pt (12px)",
+               "fontWeight": "normal",
+               "messageKey": null,
+             },
+             "id": "color-contrast",
+             "impact": "serious",
+             "message": "Element has insufficient color contrast of 3.81 (foreground color: #71717b, background color: #141416, font size: 9.0pt (12px), font weight: normal). Expected contrast ratio of 4.5:1",
+             "relatedNodes": Array [
+               Object {
+                 "html": "<form class=\"space-y-6 rounded-2xl border border-[#2e2e33] bg-[#141416] p-6\">",
+                 "target": Array [
+                   "form",
+                 ],
+               },
+             ],
+           },
+         ],
+         "failureSummary": "Fix any of the following:
+   Element has insufficient color contrast of 3.81 (foreground color: #71717b, background color: #141416, font size: 9.0pt (12px), font weight: normal). Expected contrast ratio of 4.5:1",
+         "html": "<span class=\"text-xs text-zinc-500\">0/5</span>",
+         "impact": "serious",
+         "none": Array [],
+         "target": Array [
+           "div:nth-child(4) > .mb-2.justify-between.items-center > .text-xs.text-zinc-500",
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
+                 "html": "<aside class=\"h-fit space-y-4 rounded-2xl border border-[#2e2e33] bg-[#1a1a1c] p-6 lg:sticky lg:top-20\">",
+                 "target": Array [
+                   "aside",
+                 ],
+               },
+             ],
+           },
+         ],
+         "failureSummary": "Fix any of the following:
+   Element has insufficient color contrast of 3.59 (foreground color: #71717b, background color: #1a1a1c, font size: 10.5pt (14px), font weight: normal). Expected contrast ratio of 4.5:1",
+         "html": "<dt class=\"text-zinc-500\">Especialidad</dt>",
+         "impact": "serious",
+         "none": Array [],
+         "target": Array [
+           ".justify-between.flex:nth-child(1) > dt",
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
+                 "html": "<aside class=\"h-fit space-y-4 rounded-2xl border border-[#2e2e33] bg-[#1a1a1c] p-6 lg:sticky lg:top-20\">",
+                 "target": Array [
+                   "aside",
+                 ],
+               },
+             ],
+           },
+         ],
+         "failureSummary": "Fix any of the following:
+   Element has insufficient color contrast of 3.59 (foreground color: #71717b, background color: #1a1a1c, font size: 10.5pt (14px), font weight: normal). Expected contrast ratio of 4.5:1",
+         "html": "<dt class=\"text-zinc-500\">Ubicación</dt>",
+         "impact": "serious",
+         "none": Array [],
+         "target": Array [
+           ".justify-between.flex:nth-child(2) > dt",
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
+                 "html": "<aside class=\"h-fit space-y-4 rounded-2xl border border-[#2e2e33] bg-[#1a1a1c] p-6 lg:sticky lg:top-20\">",
+                 "target": Array [
+                   "aside",
+                 ],
+               },
+             ],
+           },
+         ],
+         "failureSummary": "Fix any of the following:
+   Element has insufficient color contrast of 3.59 (foreground color: #71717b, background color: #1a1a1c, font size: 10.5pt (14px), font weight: normal). Expected contrast ratio of 4.5:1",
+         "html": "<dt class=\"text-zinc-500\">Modalidad</dt>",
+         "impact": "serious",
+         "none": Array [],
+         "target": Array [
+           ".justify-between.flex:nth-child(3) > dt",
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
+     "description": "Ensure every form element has a label",
+     "help": "Form elements must have labels",
+     "helpUrl": "https://dequeuniversity.com/rules/axe/4.14/label?application=playwright",
+     "id": "label",
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
+             "data": Object {
+               "messageKey": "noAttr",
+             },
+             "id": "non-empty-placeholder",
+             "impact": "critical",
+             "message": "Element has no placeholder attribute",
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
+   Element has no placeholder attribute
+   Element's default semantics were not overridden with role=\"none\" or role=\"presentation\"",
+         "html": "<input class=\"w-full rounded-full border border-[#3a3a3f] bg-[#26262a] px-4 py-3 text-sm text-white focus:outline-none focus:ring-2 focus:ring-[#ffc107]\" value=\"Villa Urquiza, CABA\">",
+         "impact": "critical",
+         "none": Array [],
+         "target": Array [
+           "input[value=\"Villa Urquiza, CABA\"]",
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
+         "html": "<select class=\"w-full rounded-full border border-[#3a3a3f] bg-[#26262a] px-4 py-3 text-sm text-white focus:outline-none focus:ring-2 focus:ring-[#ffc107]\">",
+         "impact": "critical",
+         "none": Array [],
+         "target": Array [
+           "form > div:nth-child(2) > select",
+         ],
+       },
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
+         "html": "<select class=\"w-full rounded-full border border-[#3a3a3f] bg-[#26262a] px-4 py-3 text-sm text-white\"><option selected=\"\">PRESENCIAL</option><option>REMOTO</option><option>HIBRIDO</option></select>",
+         "impact": "critical",
+         "none": Array [],
+         "target": Array [
+           ".gap-4 > div:nth-child(2) > select",
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
        - link "Notificaciones" [ref=e10] [cursor=pointer]:
          - /url: /empleador/notificaciones
        - link "Mi Panel" [ref=e14] [cursor=pointer]:
          - /url: /empleador/dashboard
        - button "Salir" [ref=e15] [cursor=pointer]
  - main [ref=e16]:
    - generic [ref=e17]:
      - generic [ref=e18]:
        - heading "Nueva solicitud" [level=1] [ref=e19]
        - paragraph [ref=e20]: Contanos qué necesitás y recibí propuestas de profesionales cerca tuyo.
      - generic [ref=e21]:
        - generic [ref=e22]:
          - generic [ref=e23]:
            - generic [ref=e24]:
              - generic [ref=e25]: 📝 ¿Qué necesitás?
              - generic [ref=e26]: 0/500
            - 'textbox "Describí el problema o trabajo que necesitás... Ej.: El panel LED no enciende, necesito revisarlo y repararlo." [ref=e27]'
          - generic [ref=e28]:
            - generic [ref=e29]: 🔧 Especialidad
            - combobox [ref=e30]:
              - option "Electricidad" [selected]
              - option "Plomería"
              - option "Hogar"
              - option "Tecnología"
              - option "Pintura"
              - option "Jardinería"
              - option "Carpintería"
              - option "Limpieza"
              - option "Albañilería"
              - option "Gas"
              - option "Aire Acondicionado"
              - option "Otros"
            - generic [ref=e31]:
              - button "Electricidad" [ref=e32]
              - button "Plomería" [ref=e33]
              - button "Hogar" [ref=e34]
              - button "Tecnología" [ref=e35]
              - button "Pintura" [ref=e36]
              - button "Jardinería" [ref=e37]
              - button "Carpintería" [ref=e38]
              - button "Limpieza" [ref=e39]
              - button "Albañilería" [ref=e40]
              - button "Gas" [ref=e41]
              - button "Aire Acondicionado" [ref=e42]
              - button "Otros" [ref=e43]
          - generic [ref=e44]:
            - generic [ref=e45]:
              - generic [ref=e46]: 📍 Ubicación
              - textbox [ref=e47]: Villa Urquiza, CABA
              - button "➤ Usar mi ubicación actual" [ref=e48]
            - generic [ref=e49]:
              - generic [ref=e50]: Modalidad / Presupuesto
              - combobox [ref=e51]:
                - option "PRESENCIAL" [selected]
                - option "REMOTO"
                - option "HIBRIDO"
              - 'textbox "Ej: $15.000 - $20.000" [ref=e52]'
          - generic [ref=e53]:
            - generic [ref=e54]:
              - generic [ref=e55]: 📎 Fotos (opcional)
              - generic [ref=e56]: 0/5
            - generic [ref=e57]:
              - paragraph [ref=e58]: Agregá fotos del problema (opcional). Esto ayuda a recibir mejores propuestas.
              - button "+ Agregar" [ref=e59] [cursor=pointer]
          - button "✈ Publicar solicitud" [disabled]
        - complementary [ref=e60]:
          - heading "Resumen" [level=3] [ref=e61]
          - generic [ref=e62]:
            - generic [ref=e63]:
              - term [ref=e64]: Especialidad
              - definition [ref=e65]: Electricidad
            - generic [ref=e66]:
              - term [ref=e67]: Ubicación
              - definition [ref=e68]: Villa Urquiza, CABA
            - generic [ref=e69]:
              - term [ref=e70]: Modalidad
              - definition [ref=e71]: PRESENCIAL
          - generic [ref=e72]:
            - text: "Endpoint real:"
            - code [ref=e73]: POST /oportunidades
            - text: con JWT. Requiere
            - code [ref=e74]: ROLE_EMPLEADOR
            - text: .
  - button "Open Next.js Dev Tools" [ref=e80] [cursor=pointer]
  - alert [ref=e84]
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