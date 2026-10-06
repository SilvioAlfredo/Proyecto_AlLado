import { useQuery } from '@tanstack/react-query';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { EstadisticasTrabajadorDTO } from '@/types/usuario';
import { trabajadorService } from '@/features/usuario/services/trabajadorService';

export const useEstadisticasTrabajador = () => {
  const { user, loading } = useAuth();
  const trabajadorId = user?.email ? user.email : undefined;

  return useQuery<EstadisticasTrabajadorDTO, Error>({
    queryKey: ['estadisticas-trabajador'],
    queryFn: () => trabajadorService.obtenerEstadisticasPorEmail(trabajadorId as string),
    enabled: !loading && !!trabajadorId,
    staleTime: 5 * 60 * 1000, // 5 minutos
  });
};