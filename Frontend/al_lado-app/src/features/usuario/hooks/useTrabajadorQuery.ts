'use client';

import { useQuery } from '@tanstack/react-query';
import { isAxiosError } from 'axios';
import { useAuth } from '@/features/auth/hooks/useAuth';

/** Consulta datos del trabajador de la sesión y separa la caché por usuario. */
export function useTrabajadorQuery<T>(
  recurso: string,
  obtener: (id: number) => Promise<T>,
  habilitada = true,
) {
  const { userId, user } = useAuth();
  return useQuery({
    queryKey: ['trabajador', recurso, userId],
    queryFn: () => {
      if (!userId) throw new Error('No se pudo identificar al trabajador.');
      return obtener(userId);
    },
    enabled:
      habilitada &&
      !!userId &&
      !!user?.roles.some((rol) => rol === 'ROLE_TRABAJADOR' || rol === 'TRABAJADOR'),
    retry: false,
  });
}

/** Solo un 404 representa un registro opcional ausente; otros errores se muestran. */
export async function registroOpcional<T>(obtener: () => Promise<T>): Promise<T | null> {
  try {
    return await obtener();
  } catch (error) {
    if (isAxiosError(error) && error.response?.status === 404) return null;
    throw error;
  }
}
