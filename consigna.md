Clasificación de casos de prueba y estrategia de automatización

Antes de automatizar, el equipo debe decidir qué automatiza, por qué y en qué capa. Para eso se actualiza la Planilla de Test Cases del Sprint 3 agregando las siguientes columnas:

    Tipo de testing (Columna Etiquetas): funcional, negativa, regresión, smoke, integración, accesibilidad o rendimiento básico.
    Capa de la pirámide: unitaria/integración (backend), API o UI/E2E.
    ¿Automatizable?: Sí / No, con justificación según el checklist de decisión: ejecución repetitiva, resultado determinista y costo de automatizar menor que el de ejecutar manualmente N veces. Las pruebas exploratorias, de usabilidad o de un solo uso no se automatizan.
    Herramienta y ID del test automatizado (ruta y nombre del test en el repositorio).

La estrategia del equipo debe respetar la forma de la pirámide: muchas pruebas rápidas en la base y pocas pruebas E2E reservadas a flujos críticos de negocio. Invertirla (la “copa de helado”) genera suites lentas y frágiles.

Capa
	

Qué se prueba en el proyecto
	

Herramienta
	

Mínimo requerido

Unitaria / Integración backend
	

Modelos, serializers, vistas y permisos de DRF, contra una base PostgreSQL de prueba.
	

JUnit / Mokito
	

tests que cubran todos los recursos expuestos

API (caja negra)
	

Contrato HTTP de los endpoints: estado, cuerpo, cabeceras, tiempos y efectos laterales.
	

Postman + Newman
	

1 colección CRUD de al menos 2 recursos, con pruebas negativas

UI / E2E
	

Flujos críticos de negocio en la SPA , en navegador real.
	

Playwright Test en TypeScript) con POM / Selenium / Cypress
	

3 flujos críticos CRUD

Accesibilidad
	

Cumplimiento de WCAG 2.2 nivel AA en las pantallas principales.
	

axe-core integrado en Playwright + Lighthouse + revisión manual
	

Todas las pantallas principales
Gestión ágil: Milestone, Issues, Kanban y trazabilidad

    Cargar como Issues cada requerimiento de esta sección (usando su código) y cada bug detectado, con las etiquetas automatización, accesibilidad o bug, según corresponda.
    Mantener las columnas del tablero Kanban definidas en el Sprint 3. Un test automatizado pasa a Done solo cuando está integrado a la rama principal mediante un Pull Request revisado por otro integrante y la ejecución está en verde.
    Extender la Matriz de Trazabilidad de Requisitos con las columnas: Historia de Usuario → Caso de prueba → Test automatizado (archivo::nombre) → Resultado de la última ejecución → Bug asociado.
    La participación individual se evidencia en el historial de commits y Pull Requests: cada integrante debe ser autor de al menos un test automatizado o de un hallazgo de accesibilidad reportado.

Wiki del repositorio: actualización de Testing.md

La página Testing.md, creada en el Sprint 3, se amplía con:

•  Estrategia de automatización: la pirámide del proyecto, con la cantidad de tests por capa y la justificación de qué se automatizó y qué no.

•  Cómo ejecutar las pruebas: requisitos previos, variables de entorno y comandos para backend, Newman y Playwright, de modo que cualquier persona pueda reproducirlas.

•  Resultados: enlaces a los reportes (artifacts del pipeline o Drive).

•  Accesibilidad: enlace al Informe de Accesibilidad (H2 de Accesibilidad en pagina de Testing) y a los Issues con la etiqueta accesibilidad.
Entregables:

Trabajarán sobre los mismos documentos del Sprint 3 (Plan de Pruebas,  Planilla de Test Case, Wiki, Issues.)

Actualización de documentación con lo requerido en este Sprint 4. 

N.°
	

Entregable

1
	

Planilla de Test Cases actualizada con la clasificación por tipo de testing, capa de la pirámide, decisión de automatización justificada, herramienta e ID del test automatizado.

2
	

Suite de tests del backend que cumpla AUT-BE-01 a AUT-BE-06, con el reporte de cobertura .

3
	

Colección Postman  con su(s) entorno(s) y los reportes HTML y de Newman (AUT-API-01 a AUT-API-05).

4
	

Suite E2E con Playwright y Page Object Model que cubra al menos 3 flujos críticos, con evidencias de ejecución: reporte, y screenshot, video o trace de al menos un fallo detectado (AUT-E2E-01 a AUT-E2E-07).

5
	

Informe de Accesibilidad: resultados de axe-core y Lighthouse, checklist manual completo y un mínimo de 3 bugs de accesibilidad reportados como Issues, o la justificación documentada si no se encontraron (ACC-01 a ACC-05).

6
	

Milestone Sprint 4 en el Kanban, Matriz de Trazabilidad extendida y página Testing.md actualizada en la Wiki.
