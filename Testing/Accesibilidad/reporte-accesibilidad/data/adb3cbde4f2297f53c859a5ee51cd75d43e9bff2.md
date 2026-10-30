# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: pantallas.spec.ts >> /admin/oportunidades >> axe WCAG 2.2 AA
- Location: auditoria/pantallas.spec.ts:31:9

# Error details

```
Error: Infracciones automáticas de accesibilidad

expect(received).toEqual(expected) // deep equality

- Expected  -   1
+ Received  + 408

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
+           ".min-w-36:nth-child(1) > .mb-1.text-zinc-500.font-semibold",
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
+           ".min-w-36:nth-child(2) > .mb-1.text-zinc-500.font-semibold",
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
+                 "html": "<article class=\"flex flex-wrap items-center justify-between gap-4 rounded-2xl border border-[#2e2e33] bg-[#1a1a1c] p-5\">",
+                 "target": Array [
+                   "article:nth-child(1)",
+                 ],
+               },
+             ],
+           },
+         ],
+         "failureSummary": "Fix any of the following:
+   Element has insufficient color contrast of 3.59 (foreground color: #71717b, background color: #1a1a1c, font size: 9.0pt (12px), font weight: normal). Expected contrast ratio of 4.5:1",
+         "html": "<p class=\"text-xs text-zinc-500\">Hogares SA · Jardinería</p>",
+         "impact": "serious",
+         "none": Array [],
+         "target": Array [
+           "article:nth-child(1) > div > .text-zinc-500",
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
+                 "html": "<article class=\"flex flex-wrap items-center justify-between gap-4 rounded-2xl border border-[#2e2e33] bg-[#1a1a1c] p-5\">",
+                 "target": Array [
+                   "article:nth-child(2)",
+                 ],
+               },
+             ],
+           },
+         ],
+         "failureSummary": "Fix any of the following:
+   Element has insufficient color contrast of 3.59 (foreground color: #71717b, background color: #1a1a1c, font size: 9.0pt (12px), font weight: normal). Expected contrast ratio of 4.5:1",
+         "html": "<p class=\"text-xs text-zinc-500\">Empresa Sur · Pintura</p>",
+         "impact": "serious",
+         "none": Array [],
+         "target": Array [
+           "article:nth-child(2) > div > .text-zinc-500",
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
+                 "html": "<article class=\"flex flex-wrap items-center justify-between gap-4 rounded-2xl border border-[#2e2e33] bg-[#1a1a1c] p-5\">",
+                 "target": Array [
+                   "article:nth-child(3)",
+                 ],
+               },
+             ],
+           },
+         ],
+         "failureSummary": "Fix any of the following:
+   Element has insufficient color contrast of 3.59 (foreground color: #71717b, background color: #1a1a1c, font size: 9.0pt (12px), font weight: normal). Expected contrast ratio of 4.5:1",
+         "html": "<p class=\"text-xs text-zinc-500\">Empresa Sur · Electricidad</p>",
+         "impact": "serious",
+         "none": Array [],
+         "target": Array [
+           "article:nth-child(3) > div > .text-zinc-500",
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
+                 "html": "<article class=\"flex flex-wrap items-center justify-between gap-4 rounded-2xl border border-[#2e2e33] bg-[#1a1a1c] p-5\">",
+                 "target": Array [
+                   "article:nth-child(4)",
+                 ],
+               },
+             ],
+           },
+         ],
+         "failureSummary": "Fix any of the following:
+   Element has insufficient color contrast of 3.59 (foreground color: #71717b, background color: #1a1a1c, font size: 9.0pt (12px), font weight: normal). Expected contrast ratio of 4.5:1",
+         "html": "<p class=\"text-xs text-zinc-500\">Hogares SA · </p>",
+         "impact": "serious",
+         "none": Array [],
+         "target": Array [
+           "article:nth-child(4) > div > .text-zinc-500",
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
+                 "html": "<article class=\"flex flex-wrap items-center justify-between gap-4 rounded-2xl border border-[#2e2e33] bg-[#1a1a1c] p-5\">",
+                 "target": Array [
+                   "article:nth-child(5)",
+                 ],
+               },
+             ],
+           },
+         ],
+         "failureSummary": "Fix any of the following:
+   Element has insufficient color contrast of 3.59 (foreground color: #71717b, background color: #1a1a1c, font size: 9.0pt (12px), font weight: normal). Expected contrast ratio of 4.5:1",
+         "html": "<p class=\"text-xs text-zinc-500\">Hogares SA · Jardinería</p>",
+         "impact": "serious",
+         "none": Array [],
+         "target": Array [
+           "article:nth-child(5) > div > .text-zinc-500",
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
+                 "html": "<article class=\"flex flex-wrap items-center justify-between gap-4 rounded-2xl border border-[#2e2e33] bg-[#1a1a1c] p-5\">",
+                 "target": Array [
+                   "article:nth-child(6)",
+                 ],
+               },
+             ],
+           },
+         ],
+         "failureSummary": "Fix any of the following:
+   Element has insufficient color contrast of 3.59 (foreground color: #71717b, background color: #1a1a1c, font size: 9.0pt (12px), font weight: normal). Expected contrast ratio of 4.5:1",
+         "html": "<p class=\"text-xs text-zinc-500\">Empresa Sur · Pintura</p>",
+         "impact": "serious",
+         "none": Array [],
+         "target": Array [
+           "article:nth-child(6) > div > .text-zinc-500",
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
+                 "html": "<article class=\"flex flex-wrap items-center justify-between gap-4 rounded-2xl border border-[#2e2e33] bg-[#1a1a1c] p-5\">",
+                 "target": Array [
+                   "article:nth-child(7)",
+                 ],
+               },
+             ],
+           },
+         ],
+         "failureSummary": "Fix any of the following:
+   Element has insufficient color contrast of 3.59 (foreground color: #71717b, background color: #1a1a1c, font size: 9.0pt (12px), font weight: normal). Expected contrast ratio of 4.5:1",
+         "html": "<p class=\"text-xs text-zinc-500\">Empresa Sur · Electricidad</p>",
+         "impact": "serious",
+         "none": Array [],
+         "target": Array [
+           "article:nth-child(7) > div > .text-zinc-500",
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
+                 "html": "<article class=\"flex flex-wrap items-center justify-between gap-4 rounded-2xl border border-[#2e2e33] bg-[#1a1a1c] p-5\">",
+                 "target": Array [
+                   "article:nth-child(8)",
+                 ],
+               },
+             ],
+           },
+         ],
+         "failureSummary": "Fix any of the following:
+   Element has insufficient color contrast of 3.59 (foreground color: #71717b, background color: #1a1a1c, font size: 9.0pt (12px), font weight: normal). Expected contrast ratio of 4.5:1",
+         "html": "<p class=\"text-xs text-zinc-500\">Demo Empleador · </p>",
+         "impact": "serious",
+         "none": Array [],
+         "target": Array [
+           "article:nth-child(8) > div > .text-zinc-500",
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
+                 "html": "<article class=\"flex flex-wrap items-center justify-between gap-4 rounded-2xl border border-[#2e2e33] bg-[#1a1a1c] p-5\">",
+                 "target": Array [
+                   "article:nth-child(9)",
+                 ],
+               },
+             ],
+           },
+         ],
+         "failureSummary": "Fix any of the following:
+   Element has insufficient color contrast of 3.59 (foreground color: #71717b, background color: #1a1a1c, font size: 9.0pt (12px), font weight: normal). Expected contrast ratio of 4.5:1",
+         "html": "<p class=\"text-xs text-zinc-500\">Demo Empleador · Plomería</p>",
+         "impact": "serious",
+         "none": Array [],
+         "target": Array [
+           "article:nth-child(9) > div > .text-zinc-500",
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
        - generic [ref=e46]:
          - paragraph [ref=e47]: Gestión
          - heading "Oportunidades activas" [level=1] [ref=e48]
          - paragraph [ref=e49]: Ofertas visibles para la comunidad.
        - generic [ref=e50]:
          - generic [ref=e51]:
            - generic [ref=e52]:
              - text: Buscar oportunidad
              - textbox "Buscar oportunidad" [ref=e53]:
                - /placeholder: Título, descripción o ubicación
            - generic [ref=e54]:
              - text: Modalidad
              - combobox "Modalidad" [ref=e55]:
                - option "Todas" [selected]
                - option "Presencial"
                - option "Remoto"
                - option "Híbrido"
            - button "Aplicar filtros" [ref=e56] [cursor=pointer]
            - button "Limpiar" [ref=e57] [cursor=pointer]
          - group "Especialidades · coincide con alguna de las seleccionadas" [ref=e58]:
            - generic [ref=e60]:
              - generic [ref=e61]:
                - checkbox "Plomería" [ref=e62]
                - text: Plomería
              - generic [ref=e63]:
                - checkbox "Electricidad" [ref=e64]
                - text: Electricidad
              - generic [ref=e65]:
                - checkbox "Pintura" [ref=e66]
                - text: Pintura
              - generic [ref=e67]:
                - checkbox "Jardinería" [ref=e68]
                - text: Jardinería
              - generic [ref=e69]:
                - checkbox "Limpieza" [ref=e70]
                - text: Limpieza
              - generic [ref=e71]:
                - checkbox "Carpintería" [ref=e72]
                - text: Carpintería
              - generic [ref=e73]:
                - checkbox "Albañilería" [ref=e74]
                - text: Albañilería
        - generic [ref=e75]:
          - article [ref=e76]:
            - generic [ref=e77]:
              - paragraph [ref=e78]: Activa · PRESENCIAL
              - heading "Corte de pasto" [level=2] [ref=e79]
              - paragraph [ref=e80]: Sur · 30000-50000
              - paragraph [ref=e81]: Hogares SA · Jardinería
            - link [ref=e82] [cursor=pointer]:
              - /url: /admin/oportunidades/15
              - button "Ver detalle" [ref=e83]
          - article [ref=e84]:
            - generic [ref=e85]:
              - paragraph [ref=e86]: Activa · PRESENCIAL
              - heading "Pintura departamento" [level=2] [ref=e87]
              - paragraph [ref=e88]: Norte · 150000-200000
              - paragraph [ref=e89]: Empresa Sur · Pintura
            - link [ref=e90] [cursor=pointer]:
              - /url: /admin/oportunidades/14
              - button "Ver detalle" [ref=e91]
          - article [ref=e92]:
            - generic [ref=e93]:
              - paragraph [ref=e94]: Activa · PRESENCIAL
              - heading "Reparación eléctrica" [level=2] [ref=e95]
              - paragraph [ref=e96]: Centro · 80000-120000
              - paragraph [ref=e97]: Empresa Sur · Electricidad
            - link [ref=e98] [cursor=pointer]:
              - /url: /admin/oportunidades/13
              - button "Ver detalle" [ref=e99]
          - article [ref=e100]:
            - generic [ref=e101]:
              - paragraph [ref=e102]: Activa · REMOTO
              - heading "Soporte remoto PC" [level=2] [ref=e103]
              - paragraph [ref=e104]: Remoto · 20000-30000
              - paragraph [ref=e105]: Hogares SA ·
            - link [ref=e106] [cursor=pointer]:
              - /url: /admin/oportunidades/8
              - button "Ver detalle" [ref=e107]
          - article [ref=e108]:
            - generic [ref=e109]:
              - paragraph [ref=e110]: Activa · PRESENCIAL
              - heading "Corte de pasto" [level=2] [ref=e111]
              - paragraph [ref=e112]: Sur · 30000-50000
              - paragraph [ref=e113]: Hogares SA · Jardinería
            - link [ref=e114] [cursor=pointer]:
              - /url: /admin/oportunidades/7
              - button "Ver detalle" [ref=e115]
          - article [ref=e116]:
            - generic [ref=e117]:
              - paragraph [ref=e118]: Activa · PRESENCIAL
              - heading "Pintura departamento" [level=2] [ref=e119]
              - paragraph [ref=e120]: Norte · 150000-200000
              - paragraph [ref=e121]: Empresa Sur · Pintura
            - link [ref=e122] [cursor=pointer]:
              - /url: /admin/oportunidades/6
              - button "Ver detalle" [ref=e123]
          - article [ref=e124]:
            - generic [ref=e125]:
              - paragraph [ref=e126]: Activa · PRESENCIAL
              - heading "Reparación eléctrica" [level=2] [ref=e127]
              - paragraph [ref=e128]: Centro · 80000-120000
              - paragraph [ref=e129]: Empresa Sur · Electricidad
            - link [ref=e130] [cursor=pointer]:
              - /url: /admin/oportunidades/5
              - button "Ver detalle" [ref=e131]
          - article [ref=e132]:
            - generic [ref=e133]:
              - paragraph [ref=e134]: Activa · PRESENCIAL
              - heading "Tramite de jubilación" [level=2] [ref=e135]
              - paragraph [ref=e136]: San Martín 147 · 50000/60000
              - paragraph [ref=e137]: Demo Empleador ·
            - link [ref=e138] [cursor=pointer]:
              - /url: /admin/oportunidades/3
              - button "Ver detalle" [ref=e139]
          - article [ref=e140]:
            - generic [ref=e141]:
              - paragraph [ref=e142]: Activa · PRESENCIAL
              - heading "Arreglo de cañería" [level=2] [ref=e143]
              - paragraph [ref=e144]: Belgrano · 50000-80000
              - paragraph [ref=e145]: Demo Empleador · Plomería
            - link [ref=e146] [cursor=pointer]:
              - /url: /admin/oportunidades/1
              - button "Ver detalle" [ref=e147]
        - navigation "Paginación" [ref=e148]:
          - generic [ref=e149]: 9 registros · Página 1 de 1
          - generic [ref=e150]:
            - button "Anterior" [disabled]
            - button "Siguiente" [disabled]
        - generic [ref=e151]: ⓘ Las ofertas cerradas permanecen en la ficha del empleador.
  - button "Open Next.js Dev Tools" [ref=e157] [cursor=pointer]
  - alert [ref=e161]
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