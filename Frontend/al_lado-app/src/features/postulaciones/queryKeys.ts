/**
 * Claves de caché de postulaciones.
 */
export const postulacionesKeys = {
  todo: ['postulaciones'] as const,
  candidatos: (oportunidadId: number) =>
    [...postulacionesKeys.todo, 'candidatos', oportunidadId] as const,
};
