# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: pantallas.spec.ts >> /admin/usuarios >> axe WCAG 2.2 AA
- Location: auditoria/pantallas.spec.ts:31:9

# Error details

```
Error: Infracciones automáticas de accesibilidad

expect(received).toEqual(expected) // deep equality

- Expected  -   1
+ Received  + 198

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
+         "html": "<th class=\"text-left p-4 text-sm font-medium text-zinc-500\">Nombre</th>",
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
+         "html": "<th class=\"text-left p-4 text-sm font-medium text-zinc-500\">Email</th>",
+         "impact": "serious",
+         "none": Array [],
+         "target": Array [
+           "th:nth-child(3)",
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
        - heading "Gestión de Usuarios" [level=1] [ref=e45]
        - textbox "Buscar por nombre o email..." [ref=e47]
        - table [ref=e50]:
          - rowgroup [ref=e51]:
            - row [ref=e52]:
              - columnheader "ID" [ref=e53]
              - columnheader "Nombre" [ref=e54]
              - columnheader "Email" [ref=e55]
              - columnheader "Rol" [ref=e56]
              - columnheader "Teléfono" [ref=e57]
              - columnheader "Estado" [ref=e58]
              - columnheader "Acciones" [ref=e59]
          - rowgroup [ref=e60]:
            - row [ref=e61]:
              - cell "3" [ref=e62]
              - cell "Demo Trabajador" [ref=e63]
              - cell "trabajador@demo.com" [ref=e64]
              - cell "TRABAJADOR" [ref=e65]
              - cell "+549110000003" [ref=e67]
              - cell "ACTIVO" [ref=e68]
              - cell [ref=e70]:
                - generic [ref=e71]:
                  - link [ref=e72] [cursor=pointer]:
                    - /url: /admin/usuarios/3
                    - button "Ver" [ref=e73]
                  - button "Bloquear" [ref=e74] [cursor=pointer]
            - row [ref=e75]:
              - cell "7" [ref=e76]
              - cell "Ana Gómez" [ref=e77]
              - cell "ana@lado.com" [ref=e78]
              - cell "TRABAJADOR" [ref=e79]
              - cell "+549110000011" [ref=e81]
              - cell "ACTIVO" [ref=e82]
              - cell [ref=e84]:
                - generic [ref=e85]:
                  - link [ref=e86] [cursor=pointer]:
                    - /url: /admin/usuarios/7
                    - button "Ver" [ref=e87]
                  - button "Bloquear" [ref=e88] [cursor=pointer]
            - row [ref=e89]:
              - cell "8" [ref=e90]
              - cell "Bruno Díaz" [ref=e91]
              - cell "bruno@lado.com" [ref=e92]
              - cell "TRABAJADOR" [ref=e93]
              - cell "+549110000012" [ref=e95]
              - cell "ACTIVO" [ref=e96]
              - cell [ref=e98]:
                - generic [ref=e99]:
                  - link [ref=e100] [cursor=pointer]:
                    - /url: /admin/usuarios/8
                    - button "Ver" [ref=e101]
                  - button "Bloquear" [ref=e102] [cursor=pointer]
            - row [ref=e103]:
              - cell "9" [ref=e104]
              - cell "Carla Ruiz" [ref=e105]
              - cell "carla@lado.com" [ref=e106]
              - cell "TRABAJADOR" [ref=e107]
              - cell "+549110000013" [ref=e109]
              - cell "ACTIVO" [ref=e110]
              - cell [ref=e112]:
                - generic [ref=e113]:
                  - link [ref=e114] [cursor=pointer]:
                    - /url: /admin/usuarios/9
                    - button "Ver" [ref=e115]
                  - button "Bloquear" [ref=e116] [cursor=pointer]
            - row [ref=e117]:
              - cell "10" [ref=e118]
              - cell "Diego Paz" [ref=e119]
              - cell "diego@lado.com" [ref=e120]
              - cell "TRABAJADOR" [ref=e121]
              - cell "+549110000014" [ref=e123]
              - cell "ACTIVO" [ref=e124]
              - cell [ref=e126]:
                - generic [ref=e127]:
                  - link [ref=e128] [cursor=pointer]:
                    - /url: /admin/usuarios/10
                    - button "Ver" [ref=e129]
                  - button "Bloquear" [ref=e130] [cursor=pointer]
            - row [ref=e131]:
              - cell "13" [ref=e132]
              - cell "Tra Uno" [ref=e133]
              - cell "tra@lado.com" [ref=e134]
              - cell "TRABAJADOR" [ref=e135]
              - cell "+549110000003" [ref=e137]
              - cell "ACTIVO" [ref=e138]
              - cell [ref=e140]:
                - generic [ref=e141]:
                  - link [ref=e142] [cursor=pointer]:
                    - /url: /admin/usuarios/13
                    - button "Ver" [ref=e143]
                  - button "Bloquear" [ref=e144] [cursor=pointer]
            - row [ref=e145]:
              - cell "14" [ref=e146]
              - cell "Tra Dos" [ref=e147]
              - cell "tra2@lado.com" [ref=e148]
              - cell "TRABAJADOR" [ref=e149]
              - cell "+549110000005" [ref=e151]
              - cell "ACTIVO" [ref=e152]
              - cell [ref=e154]:
                - generic [ref=e155]:
                  - link [ref=e156] [cursor=pointer]:
                    - /url: /admin/usuarios/14
                    - button "Ver" [ref=e157]
                  - button "Bloquear" [ref=e158] [cursor=pointer]
            - row [ref=e159]:
              - cell "2" [ref=e160]
              - cell "Demo Empleador" [ref=e161]
              - cell "empleador@demo.com" [ref=e162]
              - cell "EMPLEADOR" [ref=e163]
              - cell "+549110000002" [ref=e165]
              - cell "ACTIVO" [ref=e166]
              - cell [ref=e168]:
                - generic [ref=e169]:
                  - link [ref=e170] [cursor=pointer]:
                    - /url: /admin/usuarios/2
                    - button "Ver" [ref=e171]
                  - button "Bloquear" [ref=e172] [cursor=pointer]
            - row [ref=e173]:
              - cell "11" [ref=e174]
              - cell "Empresa Sur" [ref=e175]
              - cell "sure@lado.com" [ref=e176]
              - cell "EMPLEADOR" [ref=e177]
              - cell "+549110000021" [ref=e179]
              - cell "ACTIVO" [ref=e180]
              - cell [ref=e182]:
                - generic [ref=e183]:
                  - link [ref=e184] [cursor=pointer]:
                    - /url: /admin/usuarios/11
                    - button "Ver" [ref=e185]
                  - button "Bloquear" [ref=e186] [cursor=pointer]
            - row [ref=e187]:
              - cell "12" [ref=e188]
              - cell "Hogares SA" [ref=e189]
              - cell "hogares@lado.com" [ref=e190]
              - cell "EMPLEADOR" [ref=e191]
              - cell "+549110000022" [ref=e193]
              - cell "ACTIVO" [ref=e194]
              - cell [ref=e196]:
                - generic [ref=e197]:
                  - link [ref=e198] [cursor=pointer]:
                    - /url: /admin/usuarios/12
                    - button "Ver" [ref=e199]
                  - button "Bloquear" [ref=e200] [cursor=pointer]
            - row [ref=e201]:
              - cell "15" [ref=e202]
              - cell "Emp Uno" [ref=e203]
              - cell "emp@lado.com" [ref=e204]
              - cell "EMPLEADOR" [ref=e205]
              - cell "+549110000002" [ref=e207]
              - cell "ACTIVO" [ref=e208]
              - cell [ref=e210]:
                - generic [ref=e211]:
                  - link [ref=e212] [cursor=pointer]:
                    - /url: /admin/usuarios/15
                    - button "Ver" [ref=e213]
                  - button "Bloquear" [ref=e214] [cursor=pointer]
            - row [ref=e215]:
              - cell "16" [ref=e216]
              - cell "Emp Dos" [ref=e217]
              - cell "emp2@lado.com" [ref=e218]
              - cell "EMPLEADOR" [ref=e219]
              - cell "+549110000004" [ref=e221]
              - cell "ACTIVO" [ref=e222]
              - cell [ref=e224]:
                - generic [ref=e225]:
                  - link [ref=e226] [cursor=pointer]:
                    - /url: /admin/usuarios/16
                    - button "Ver" [ref=e227]
                  - button "Bloquear" [ref=e228] [cursor=pointer]
  - button "Open Next.js Dev Tools" [ref=e234] [cursor=pointer]
  - alert [ref=e238]
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