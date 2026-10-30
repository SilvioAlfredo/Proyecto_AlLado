# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: pantallas.spec.ts >> /empleador/notificaciones >> axe WCAG 2.2 AA
- Location: auditoria/pantallas.spec.ts:31:9

# Error details

```
Error: Infracciones automáticas de accesibilidad

expect(received).toEqual(expected) // deep equality

- Expected  -  1
+ Received  + 58

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
+         "html": "<div class=\"rounded-2xl border border-dashed border-[#3a3a3f] p-10 text-center text-sm text-zinc-500\">No tenés notificaciones todavía.</div>",
+         "impact": "serious",
+         "none": Array [],
+         "target": Array [
+           ".rounded-2xl",
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
        - generic [ref=e25]:
          - paragraph [ref=e26]: Avisos
          - heading "Notificaciones" [level=1] [ref=e27]
        - generic [ref=e28]: No tenés notificaciones todavía.
  - button "Open Next.js Dev Tools" [ref=e34] [cursor=pointer]
  - alert [ref=e38]
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