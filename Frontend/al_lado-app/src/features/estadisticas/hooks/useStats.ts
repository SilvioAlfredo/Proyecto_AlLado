'use client';
import { useQuery } from '@tanstack/react-query';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { trabajadorService } from '@/features/usuario/services/trabajadorService';

export const useStats = (trabajadorId?: number) => {
  const { userId } = useAuth();
  const id = trabajadorId ?? userId;
  return useQuery({
    queryKey: ['trabajador', 'estadisticas', id],
    queryFn: () => {
      if (!id) throw new Error('No se pudo identificar tu usuario.');
      return trabajadorService.obtenerEstadisticas(id);
    },
    enabled: !!id,
    staleTime: 5 * 60 * 1000,
  });
};
