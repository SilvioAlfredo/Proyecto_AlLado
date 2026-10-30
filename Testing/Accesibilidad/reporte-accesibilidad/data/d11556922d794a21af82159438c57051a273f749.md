# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: pantallas.spec.ts >> /admin/dashboard >> axe WCAG 2.2 AA
- Location: auditoria/pantallas.spec.ts:31:9

# Error details

```
Error: Infracciones automáticas de accesibilidad

expect(received).toEqual(expected) // deep equality

- Expected  -   1
+ Received  + 478

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
+           ".min-w-36:nth-child(1) > .mb-1.font-semibold.text-zinc-500",
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
+           ".min-w-36:nth-child(2) > .mb-1.font-semibold.text-zinc-500",
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
+                 "html": "<div class=\"rounded-2xl border p-4 border-[#2e2e33] bg-[#1a1a1c]\"><p class=\"text-[11px] font-bold uppercase tracking-wide text-zinc-500\">Postulaciones</p><p class=\"mt-1 text-2xl font-extrabold text-white\">10</p><p class=\"mt-1 text-xs text-zinc-500\">Total de postulaciones registradas</p></div>",
+                 "target": Array [
+                   ".xl\\:grid-cols-4 > .p-4.bg-\\[\\#1a1a1c\\].border-\\[\\#2e2e33\\]:nth-child(2)",
+                 ],
+               },
+             ],
+           },
+         ],
+         "failureSummary": "Fix any of the following:
+   Element has insufficient color contrast of 3.59 (foreground color: #71717b, background color: #1a1a1c, font size: 8.3pt (11px), font weight: bold). Expected contrast ratio of 4.5:1",
+         "html": "<p class=\"text-[11px] font-bold uppercase tracking-wide text-zinc-500\">Postulaciones</p>",
+         "impact": "serious",
+         "none": Array [],
+         "target": Array [
+           ".xl\\:grid-cols-4 > .p-4.bg-\\[\\#1a1a1c\\].border-\\[\\#2e2e33\\]:nth-child(2) > .tracking-wide.text-\\[11px\\].uppercase",
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
+                 "html": "<div class=\"rounded-2xl border p-4 border-[#2e2e33] bg-[#1a1a1c]\"><p class=\"text-[11px] font-bold uppercase tracking-wide text-zinc-500\">Postulaciones</p><p class=\"mt-1 text-2xl font-extrabold text-white\">10</p><p class=\"mt-1 text-xs text-zinc-500\">Total de postulaciones registradas</p></div>",
+                 "target": Array [
+                   ".xl\\:grid-cols-4 > .p-4.bg-\\[\\#1a1a1c\\].border-\\[\\#2e2e33\\]:nth-child(2)",
+                 ],
+               },
+             ],
+           },
+         ],
+         "failureSummary": "Fix any of the following:
+   Element has insufficient color contrast of 3.59 (foreground color: #71717b, background color: #1a1a1c, font size: 9.0pt (12px), font weight: normal). Expected contrast ratio of 4.5:1",
+         "html": "<p class=\"mt-1 text-xs text-zinc-500\">Total de postulaciones registradas</p>",
+         "impact": "serious",
+         "none": Array [],
+         "target": Array [
+           ".p-4.bg-\\[\\#1a1a1c\\].border-\\[\\#2e2e33\\]:nth-child(2) > .mt-1.text-zinc-500.text-xs",
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
+                 "html": "<div class=\"rounded-2xl border p-4 border-[#2e2e33] bg-[#1a1a1c]\"><p class=\"text-[11px] font-bold uppercase tracking-wide text-zinc-500\">Completadas</p><p class=\"mt-1 text-2xl font-extrabold text-white\">1</p><p class=\"mt-1 text-xs text-zinc-500\">10.0% del total</p></div>",
+                 "target": Array [
+                   ".p-4.bg-\\[\\#1a1a1c\\].border-\\[\\#2e2e33\\]:nth-child(3)",
+                 ],
+               },
+             ],
+           },
+         ],
+         "failureSummary": "Fix any of the following:
+   Element has insufficient color contrast of 3.59 (foreground color: #71717b, background color: #1a1a1c, font size: 8.3pt (11px), font weight: bold). Expected contrast ratio of 4.5:1",
+         "html": "<p class=\"text-[11px] font-bold uppercase tracking-wide text-zinc-500\">Completadas</p>",
+         "impact": "serious",
+         "none": Array [],
+         "target": Array [
+           ".p-4.bg-\\[\\#1a1a1c\\].border-\\[\\#2e2e33\\]:nth-child(3) > .tracking-wide.text-\\[11px\\].uppercase",
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
+                 "html": "<div class=\"rounded-2xl border p-4 border-[#2e2e33] bg-[#1a1a1c]\"><p class=\"text-[11px] font-bold uppercase tracking-wide text-zinc-500\">Completadas</p><p class=\"mt-1 text-2xl font-extrabold text-white\">1</p><p class=\"mt-1 text-xs text-zinc-500\">10.0% del total</p></div>",
+                 "target": Array [
+                   ".p-4.bg-\\[\\#1a1a1c\\].border-\\[\\#2e2e33\\]:nth-child(3)",
+                 ],
+               },
+             ],
+           },
+         ],
+         "failureSummary": "Fix any of the following:
+   Element has insufficient color contrast of 3.59 (foreground color: #71717b, background color: #1a1a1c, font size: 9.0pt (12px), font weight: normal). Expected contrast ratio of 4.5:1",
+         "html": "<p class=\"mt-1 text-xs text-zinc-500\">10.0% del total</p>",
+         "impact": "serious",
+         "none": Array [],
+         "target": Array [
+           ".p-4.bg-\\[\\#1a1a1c\\].border-\\[\\#2e2e33\\]:nth-child(3) > .mt-1.text-zinc-500.text-xs",
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
+                 "html": "<div class=\"rounded-2xl border p-4 border-[#2e2e33] bg-[#1a1a1c]\">",
+                 "target": Array [
+                   ".p-4.bg-\\[\\#1a1a1c\\].border-\\[\\#2e2e33\\]:nth-child(4)",
+                 ],
+               },
+             ],
+           },
+         ],
+         "failureSummary": "Fix any of the following:
+   Element has insufficient color contrast of 3.59 (foreground color: #71717b, background color: #1a1a1c, font size: 8.3pt (11px), font weight: bold). Expected contrast ratio of 4.5:1",
+         "html": "<p class=\"text-[11px] font-bold uppercase tracking-wide text-zinc-500\">Remuneración pretendida promedio</p>",
+         "impact": "serious",
+         "none": Array [],
+         "target": Array [
+           ".p-4.bg-\\[\\#1a1a1c\\].border-\\[\\#2e2e33\\]:nth-child(4) > .tracking-wide.text-\\[11px\\].uppercase",
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
+                 "html": "<div class=\"rounded-2xl border p-4 border-[#2e2e33] bg-[#1a1a1c]\">",
+                 "target": Array [
+                   ".p-4.bg-\\[\\#1a1a1c\\].border-\\[\\#2e2e33\\]:nth-child(4)",
+                 ],
+               },
+             ],
+           },
+         ],
+         "failureSummary": "Fix any of the following:
+   Element has insufficient color contrast of 3.59 (foreground color: #71717b, background color: #1a1a1c, font size: 9.0pt (12px), font weight: normal). Expected contrast ratio of 4.5:1",
+         "html": "<p class=\"mt-1 text-xs text-zinc-500\">Promedio de montos positivos declarados; no representa cobros</p>",
+         "impact": "serious",
+         "none": Array [],
+         "target": Array [
+           ".p-4.bg-\\[\\#1a1a1c\\].border-\\[\\#2e2e33\\]:nth-child(4) > .mt-1.text-zinc-500.text-xs",
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
+                 "html": "<div class=\"rounded-2xl border p-4 border-[#2e2e33] bg-[#1a1a1c]\">",
+                 "target": Array [
+                   ".gap-4.sm\\:grid-cols-2.grid:nth-child(3) > .p-4.bg-\\[\\#1a1a1c\\].border-\\[\\#2e2e33\\]:nth-child(1)",
+                 ],
+               },
+             ],
+           },
+         ],
+         "failureSummary": "Fix any of the following:
+   Element has insufficient color contrast of 3.59 (foreground color: #71717b, background color: #1a1a1c, font size: 8.3pt (11px), font weight: bold). Expected contrast ratio of 4.5:1",
+         "html": "<p class=\"text-[11px] font-bold uppercase tracking-wide text-zinc-500\">Usuarios habilitados</p>",
+         "impact": "serious",
+         "none": Array [],
+         "target": Array [
+           ".gap-4.sm\\:grid-cols-2.grid:nth-child(3) > .p-4.bg-\\[\\#1a1a1c\\].border-\\[\\#2e2e33\\]:nth-child(1) > .tracking-wide.text-\\[11px\\].uppercase",
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
+                 "html": "<div class=\"rounded-2xl border p-4 border-[#2e2e33] bg-[#1a1a1c]\">",
+                 "target": Array [
+                   ".gap-4.sm\\:grid-cols-2.grid:nth-child(3) > .p-4.bg-\\[\\#1a1a1c\\].border-\\[\\#2e2e33\\]:nth-child(1)",
+                 ],
+               },
+             ],
+           },
+         ],
+         "failureSummary": "Fix any of the following:
+   Element has insufficient color contrast of 3.59 (foreground color: #71717b, background color: #1a1a1c, font size: 9.0pt (12px), font weight: normal). Expected contrast ratio of 4.5:1",
+         "html": "<p class=\"mt-1 text-xs text-zinc-500\">Cuentas habilitadas, no sesiones conectadas</p>",
+         "impact": "serious",
+         "none": Array [],
+         "target": Array [
+           ".p-4.bg-\\[\\#1a1a1c\\].border-\\[\\#2e2e33\\]:nth-child(1) > .mt-1.text-zinc-500.text-xs",
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
+                 "html": "<div class=\"rounded-2xl border p-4 border-[#2e2e33] bg-[#1a1a1c]\"><p class=\"text-[11px] font-bold uppercase tracking-wide text-zinc-500\">Usuarios deshabilitados</p><p class=\"mt-1 text-2xl font-extrabold text-white\">0</p></div>",
+                 "target": Array [
+                   ".gap-4.sm\\:grid-cols-2.grid:nth-child(3) > .p-4.bg-\\[\\#1a1a1c\\].border-\\[\\#2e2e33\\]:nth-child(2)",
+                 ],
+               },
+             ],
+           },
+         ],
+         "failureSummary": "Fix any of the following:
+   Element has insufficient color contrast of 3.59 (foreground color: #71717b, background color: #1a1a1c, font size: 8.3pt (11px), font weight: bold). Expected contrast ratio of 4.5:1",
+         "html": "<p class=\"text-[11px] font-bold uppercase tracking-wide text-zinc-500\">Usuarios deshabilitados</p>",
+         "impact": "serious",
+         "none": Array [],
+         "target": Array [
+           ".gap-4.sm\\:grid-cols-2.grid:nth-child(3) > .p-4.bg-\\[\\#1a1a1c\\].border-\\[\\#2e2e33\\]:nth-child(2) > .tracking-wide.text-\\[11px\\].uppercase",
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
+                 "html": "<div class=\"rounded-2xl border p-4 border-[#2e2e33] bg-[#1a1a1c]\"><p class=\"text-[11px] font-bold uppercase tracking-wide text-zinc-500\">Aportes y planes (estimación)</p><p class=\"mt-1 text-2xl font-extrabold text-white\">$&nbsp;100.000</p></div>",
+                 "target": Array [
+                   "section:nth-child(5) > .gap-4.sm\\:grid-cols-2.grid > .p-4.bg-\\[\\#1a1a1c\\].border-\\[\\#2e2e33\\]:nth-child(1)",
+                 ],
+               },
+             ],
+           },
+         ],
+         "failureSummary": "Fix any of the following:
+   Element has insufficient color contrast of 3.59 (foreground color: #71717b, background color: #1a1a1c, font size: 8.3pt (11px), font weight: bold). Expected contrast ratio of 4.5:1",
+         "html": "<p class=\"text-[11px] font-bold uppercase tracking-wide text-zinc-500\">Aportes y planes (estimación)</p>",
+         "impact": "serious",
+         "none": Array [],
+         "target": Array [
+           "section:nth-child(5) > .gap-4.sm\\:grid-cols-2.grid > .p-4.bg-\\[\\#1a1a1c\\].border-\\[\\#2e2e33\\]:nth-child(1) > .tracking-wide.text-\\[11px\\].uppercase",
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
+                 "html": "<div class=\"rounded-2xl border p-4 border-[#2e2e33] bg-[#1a1a1c]\"><p class=\"text-[11px] font-bold uppercase tracking-wide text-zinc-500\">Remuneración en postulaciones completadas</p><p class=\"mt-1 text-2xl font-extrabold text-white\">$&nbsp;60.000</p></div>",
+                 "target": Array [
+                   "section:nth-child(5) > .gap-4.sm\\:grid-cols-2.grid > .p-4.bg-\\[\\#1a1a1c\\].border-\\[\\#2e2e33\\]:nth-child(2)",
+                 ],
+               },
+             ],
+           },
+         ],
+         "failureSummary": "Fix any of the following:
+   Element has insufficient color contrast of 3.59 (foreground color: #71717b, background color: #1a1a1c, font size: 8.3pt (11px), font weight: bold). Expected contrast ratio of 4.5:1",
+         "html": "<p class=\"text-[11px] font-bold uppercase tracking-wide text-zinc-500\">Remuneración en postulaciones completadas</p>",
+         "impact": "serious",
+         "none": Array [],
+         "target": Array [
+           "section:nth-child(5) > .gap-4.sm\\:grid-cols-2.grid > .p-4.bg-\\[\\#1a1a1c\\].border-\\[\\#2e2e33\\]:nth-child(2) > .tracking-wide.text-\\[11px\\].uppercase",
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
        - generic [ref=e45]:
          - generic [ref=e46]:
            - paragraph [ref=e47]: Inicio
            - heading "La comunidad, en números" [level=1] [ref=e48]
            - paragraph [ref=e49]: Resumen acumulado de AlLado.
          - link [ref=e51] [cursor=pointer]:
            - /url: /admin/personas
            - button "Explorar personas" [ref=e52]
        - generic [ref=e53]:
          - generic [ref=e54]:
            - paragraph [ref=e55]: Trabajadores
            - paragraph [ref=e56]: "7"
            - paragraph [ref=e57]: Personas con perfil de trabajador
          - generic [ref=e58]:
            - paragraph [ref=e59]: Postulaciones
            - paragraph [ref=e60]: "10"
            - paragraph [ref=e61]: Total de postulaciones registradas
          - generic [ref=e62]:
            - paragraph [ref=e63]: Completadas
            - paragraph [ref=e64]: "1"
            - paragraph [ref=e65]: 10.0% del total
          - generic [ref=e66]:
            - paragraph [ref=e67]: Remuneración pretendida promedio
            - paragraph [ref=e68]: $ 55.000
            - paragraph [ref=e69]: Promedio de montos positivos declarados; no representa cobros
        - generic [ref=e70]:
          - generic [ref=e71]:
            - paragraph [ref=e72]: Usuarios habilitados
            - paragraph [ref=e73]: "15"
            - paragraph [ref=e74]: Cuentas habilitadas, no sesiones conectadas
          - generic [ref=e75]:
            - paragraph [ref=e76]: Usuarios deshabilitados
            - paragraph [ref=e77]: "0"
        - generic [ref=e78]:
          - generic [ref=e79]:
            - generic [ref=e80]:
              - heading "Las 5 etiquetas más usadas" [level=2] [ref=e81]
              - link "Administrar etiquetas →" [ref=e82] [cursor=pointer]:
                - /url: /admin/etiquetas
            - paragraph [ref=e83]: Cantidad de oportunidades por etiqueta
            - generic [ref=e84]:
              - generic [ref=e85]:
                - generic [ref=e86]: Electricidad
                - generic [ref=e87]: "2"
              - meter "Electricidad" [ref=e88]
            - generic [ref=e90]:
              - generic [ref=e91]:
                - generic [ref=e92]: Jardinería
                - generic [ref=e93]: "2"
              - meter "Jardinería" [ref=e94]
            - generic [ref=e96]:
              - generic [ref=e97]:
                - generic [ref=e98]: Pintura
                - generic [ref=e99]: "2"
              - meter "Pintura" [ref=e100]
            - generic [ref=e102]:
              - generic [ref=e103]:
                - generic [ref=e104]: Plomería
                - generic [ref=e105]: "1"
              - meter "Plomería" [ref=e106]
            - generic [ref=e108]: ⓘ Una oportunidad puede tener varias etiquetas. Los conteos no son porcentajes excluyentes.
          - generic [ref=e109]:
            - heading "Postulaciones por estado" [level=2] [ref=e110]
            - generic [ref=e111]:
              - generic [ref=e112]: PENDIENTE
              - strong [ref=e113]: "3"
            - generic [ref=e114]:
              - generic [ref=e115]: SELECCIONADA
              - strong [ref=e116]: "2"
            - generic [ref=e117]:
              - generic [ref=e118]: RECHAZADA
              - strong [ref=e119]: "2"
            - generic [ref=e120]:
              - generic [ref=e121]: COMPLETADA
              - strong [ref=e122]: "1"
            - generic [ref=e123]:
              - generic [ref=e124]: RETIRADA
              - strong [ref=e125]: "2"
        - generic [ref=e126]:
          - heading "Montos declarados" [level=2] [ref=e127]
          - generic [ref=e128]:
            - generic [ref=e129]:
              - paragraph [ref=e130]: Aportes y planes (estimación)
              - paragraph [ref=e131]: $ 100.000
            - generic [ref=e132]:
              - paragraph [ref=e133]: Remuneración en postulaciones completadas
              - paragraph [ref=e134]: $ 60.000
          - generic [ref=e135]: ⓘ La estimación suma aportes declarados y planes PRO activos a un valor de referencia. Las remuneraciones son montos pretendidos. Estos indicadores no acreditan pagos recibidos.
        - generic [ref=e136]:
          - heading "Publicaciones y reportes recientes" [level=2] [ref=e137]
          - paragraph [ref=e138]: Ordenados por fecha de creación o emisión.
          - link [ref=e139] [cursor=pointer]:
            - /url: /admin/comunicados
            - paragraph [ref=e140]: Comunicado · 09/10/2026
            - paragraph [ref=e141]: Mantenimiento
          - link [ref=e142] [cursor=pointer]:
            - /url: /admin/reportes/2
            - paragraph [ref=e143]: Reporte · RESUELTO · 09/10/2026
            - paragraph [ref=e144]: Spam
          - link [ref=e145] [cursor=pointer]:
            - /url: /admin/comunicados
            - paragraph [ref=e146]: Comunicado · 09/10/2026
            - paragraph [ref=e147]: Mantenimiento
          - link [ref=e148] [cursor=pointer]:
            - /url: /admin/reportes/1
            - paragraph [ref=e149]: Reporte · RESUELTO · 09/10/2026
            - paragraph [ref=e150]: Spam
        - generic [ref=e151]:
          - heading "Accesos de gestión" [level=2] [ref=e152]
          - generic [ref=e153]:
            - link [ref=e154] [cursor=pointer]:
              - /url: /admin/personas
              - heading "Personas →" [level=3] [ref=e155]
              - paragraph [ref=e156]: Trabajadores, empleadores y administración
            - link [ref=e157] [cursor=pointer]:
              - /url: /admin/comunicados
              - heading "Comunicados →" [level=3] [ref=e158]
              - paragraph [ref=e159]: Publicaciones institucionales
            - link [ref=e160] [cursor=pointer]:
              - /url: /admin/oportunidades
              - heading "Oportunidades →" [level=3] [ref=e161]
              - paragraph [ref=e162]: Ofertas y postulaciones
            - link [ref=e163] [cursor=pointer]:
              - /url: /admin/reportes
              - heading "Moderación →" [level=3] [ref=e164]
              - paragraph [ref=e165]: Reportes de la comunidad
            - link [ref=e166] [cursor=pointer]:
              - /url: /admin/foro
              - heading "Foro y valoraciones →" [level=3] [ref=e167]
              - paragraph [ref=e168]: Contenido de la comunidad
            - link [ref=e169] [cursor=pointer]:
              - /url: /admin/empresas
              - heading "Empresas →" [level=3] [ref=e170]
              - paragraph [ref=e171]: Revisión de perfiles empresariales
            - link [ref=e172] [cursor=pointer]:
              - /url: /admin/donaciones
              - heading "Transferencias →" [level=3] [ref=e173]
              - paragraph [ref=e174]: Aportes declarados y verificación
            - link [ref=e175] [cursor=pointer]:
              - /url: /admin/suscripciones
              - heading "Suscripciones →" [level=3] [ref=e176]
              - paragraph [ref=e177]: Planes y vigencia
            - link [ref=e178] [cursor=pointer]:
              - /url: /admin/publicidad
              - heading "Publicidad →" [level=3] [ref=e179]
              - paragraph [ref=e180]: Anuncios y sus fechas
            - link [ref=e181] [cursor=pointer]:
              - /url: /admin/etiquetas
              - heading "Etiquetas →" [level=3] [ref=e182]
              - paragraph [ref=e183]: Catálogo de especialidades
  - button "Open Next.js Dev Tools" [ref=e189] [cursor=pointer]
  - alert [ref=e193]
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