# Documentación TypeDoc del frontend web

TypeDoc genera documentación HTML a partir de los comentarios `/** ... */` y los tipos de TypeScript. Se mantiene el contenido de los comentarios que se habían incorporado al Admin y a los servicios compartidos.

## Generar y consultar

Desde `Frontend/al_lado-app`:

```bash
npm ci
npm run docs
```

Abrí `docs/typedoc/index.html` en el navegador. No hace falta iniciar el frontend ni el backend.

Para regenerarla al editar los comentarios:

```bash
npm run docs:watch
```

El comando termina con error si encuentra errores de TypeScript o advertencias de documentación.

## Alcance

`typedoc.json` selecciona los archivos documentados del Admin, autenticación, aportes, perfil empresarial y funciones compartidas, junto con los DTO de `src/types`. Las páginas puramente visuales no se agregan automáticamente. Si se documenta otro archivo, hay que incorporarlo a `entryPoints`.

Se explican servicios paginados, filtros, permisos, confirmaciones, cambios de estado y reglas de fechas e importes. Los comentarios permanecen junto al código y también pueden consultarse desde el editor.

## Criterio utilizado

- Explicar el propósito y las reglas que no resultan evidentes al leer la firma.
- Usar `@param` para precisar identificadores, formatos y valores admitidos.
- Usar `@returns` para explicar resultados, valores alternativos y límites.
- Usar `@throws` en funciones que rechazan explícitamente una entrada inválida.
- Mantener los tipos en TypeScript, sin duplicarlos en los comentarios.
- Documentar cambios relevantes de comportamiento junto con su implementación.

Los servicios de gestión explican filtros, páginas desde cero y propagación de errores. Las funciones de aportes documentan fechas inclusivas, formato de calendario y precisión decimal. Los componentes explican permisos, confirmaciones y diferencias entre cancelar, eliminar y verificar. Los servicios compartidos utilizados por el Admin aclaran los identificadores y la atribución al administrador autenticado.

## Mercurial

Se versionan los comentarios, `TYPEDOC.md`, `typedoc.json`, `package.json` y `package-lock.json`. También se versiona la salida `docs/typedoc/`, para consultar la documentación sin instalar dependencias. `.hgignore` conserva ignorados los reportes internos de `docs`, pero permite los archivos de TypeDoc. Después de modificar comentarios, ejecutá `npm run docs` y agregá los archivos generados con `hg addremove docs/typedoc`.

Esta documentación complementa los documentos Markdown de instalación y funcionamiento. No modifica el comportamiento ni el diseño de las pantallas.

Referencia: [TypeDoc](https://typedoc.org/).
