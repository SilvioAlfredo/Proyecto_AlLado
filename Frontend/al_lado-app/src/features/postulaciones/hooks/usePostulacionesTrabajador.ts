'use client';

import { useAuth } from '@/features/auth/hooks/useAuth';
import { useTrabajadorQuery } from '@/features/usuario/hooks/useTrabajadorQuery';
import { postulacionesService } from '../services/postulacionesService';
import { errorMessage } from '@/lib/errors';

export function usePostulacionesTrabajador() {
  const { loading, userId } = useAuth();
  const consulta = useTrabajadorQuery('postulaciones', async (id) => {
    const datos = await postulacionesService.obtenerTodas();
    return datos.filter((post) => post.autor?.id === id);
  });
  return {
    postulaciones: consulta.data ?? [],
    loading: loading || (!!userId && consulta.isPending),
    error: consulta.error
      ? errorMessage(consulta.error)
      : !loading && !userId
        ? 'No se pudo identificar tu usuario.'
        : null,
    refetch: consulta.refetch,
  };
}
