/**
 * Claves de caché de la comunidad (foro, comunicados, reportes).
 *
 * `todo` agrupa todo para invalidar de una sola vez cuando se crea o se
 * modifica algo.
 */
export const comunidadKeys = {
  todo: ['comunidad'] as const,
  posts: () => [...comunidadKeys.todo, 'posts'] as const,
  comunicados: () => [...comunidadKeys.todo, 'comunicados'] as const,
  reportes: () => [...comunidadKeys.todo, 'reportes'] as const,
};
