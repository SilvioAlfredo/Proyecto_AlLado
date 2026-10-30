/**
 * Claves de caché de notificaciones.
 *
 * `todo` agrupa la lista y el contador para que una lectura invalide ambos de
 * una sola vez. El contador vive en la Navbar como consulta aparte: mientras no
 * se invalidaba, el badge seguía mostrando el número de la primera carga.
 */
export const notificacionesKeys = {
  todo: ['notificaciones'] as const,
  lista: (usuarioId: number) =>
    [...notificacionesKeys.todo, 'lista', usuarioId] as const,
  contador: (usuarioId: number) =>
    [...notificacionesKeys.todo, 'contador', usuarioId] as const,
};
