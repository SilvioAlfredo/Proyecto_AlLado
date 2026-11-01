/**
 * Claves de caché de oportunidades.
 *
 * `todo` agrupa la lista para que crear/actualizar/cerrar/eliminar invalide
 * todas las consultas de lista de una sola vez. `lista` incluye los filtros y
 * la página en la clave, de modo que cada combinación de filtros tiene su propia
 * entrada en caché (sin colisiones entre búsquedas distintas).
 */
export const oportunidadesKeys = {
  todo: ['oportunidades'] as const,
  lista: (filtros: OportunidadesFiltros, page: number) =>
    [...oportunidadesKeys.todo, 'lista', JSON.stringify(filtros), page] as const,
  detalle: (id: number) => [...oportunidadesKeys.todo, 'detalle', id] as const,
};

export interface OportunidadesFiltros {
  filtro?: string;
  modalidad?: string;
  etiquetas?: string[];
  soloDestacadas?: boolean;
  ubicacion?: string;
  sector?: string;
}
