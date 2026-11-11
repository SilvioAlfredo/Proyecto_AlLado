# Evidencia final de accesibilidad — axe-core

**Proyecto:** Al Lado
**Sprint:** 4
**Fecha:** 11/11/2026
**Herramientas:** Playwright y axe-core
**Referencia:** WCAG 2.2 AA
**Entornos:** Escritorio y móvil web

## Resultados

| Estado | Cantidad |
|---|---:|
| PASS | 78 |
| FAIL | 2 |
| SKIPPED | 68 |
| Total | 148 |

- Casos con infracciones automáticas confirmadas: 0.
- Casos con comprobaciones inconclusas: 78.
- Los resultados aprobados no equivalen a conformidad integral WCAG 2.2 AA.

## Bloqueos

La ruta `/empleador/trabajadores` falló en escritorio y móvil web por un error HTTP 500 del backend, identificado previamente en `/trabajadores/filtrar`.

Ambas pruebas se conservan como fallidas por bloqueo técnico. No se consideran aprobadas ni se clasifican automáticamente como defectos de accesibilidad.

## Alcance y limitaciones

Se evaluaron las rutas públicas y las pantallas de Empleador y Trabajador incluidas en la suite.

El módulo de Administración queda pendiente para una etapa posterior.

Los 68 casos omitidos no se consideran evaluados satisfactoriamente. Las comprobaciones inconclusas requieren interpretación o revisión manual.

## Evidencias

- `reporte-accesibilidad/resumen.json`: resultados estructurados.
- `reporte-accesibilidad/index.html`: reporte HTML.
- `resultados-accesibilidad/`: resultados individuales de axe-core y trazas de errores.

## Conclusión

La ejecución automatizada finalizó con 78 casos aprobados, sin infracciones automáticas confirmadas, dos casos fallidos por bloqueo técnico y 68 casos omitidos.

Se conserva la evidencia para su integración en el Informe de Accesibilidad del Sprint 4.
