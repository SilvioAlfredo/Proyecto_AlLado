'use client';

import { useQuery } from '@tanstack/react-query';
import { PostulacionDTO } from '@/types/trabajo';
import { postulacionesService } from '../services/postulacionesService';
import { postulacionesKeys } from '../queryKeys';
import { errorMessage } from '@/lib/errors';

/**
 * Candidatos de una oportunidad con React Query.
 *
 * La clave incluye el `oportunidadId`, así cada oportunidad tiene su propia
 * entrada en caché. Se filtra en el cliente porque el endpoint devuelve todas
 * las postulaciones; si el backend expusiera un filtro por oportunidad, se
 * movería al `queryFn`.
 */
export function useCandidatosOportunidad(oportunidadId?: number | null) {
  const consulta = useQuery<PostulacionDTO[], Error>({
    queryKey: postulacionesKeys.candidatos(oportunidadId ?? 0),
    enabled: oportunidadId != null,
    queryFn: async () => {
      const data = await postulacionesService.obtenerTodas();
      return data.filter((p) => p.oportunidadLaboral?.id === oportunidadId);
    },
    staleTime: 30_000,
  });

  return {
    candidatos: consulta.data ?? [],
    loading: consulta.isPending,
    error: consulta.error
      ? errorMessage(consulta.error, 'Error al cargar candidatos')
      : null,
    refetch: () => consulta.refetch(),
  };
}
