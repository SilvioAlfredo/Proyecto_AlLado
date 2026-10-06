import { useQuery } from '@tanstack/react-query';
import { EstadisticasTrabajadorDTO } from '@/types/usuario';
import { trabajadorService } from '@/features/usuario/services/trabajadorService';

export const useStats = (trabajadorId?: number) => {
  return useQuery<EstadisticasTrabajadorDTO, Error>({
    queryKey: ['estadisticas-trabajador', trabajadorId ?? 'default'],
    queryFn: () => trabajadorService.obtenerEstadisticas(trabajadorId ?? 1),
    staleTime: 5 * 60 * 1000,
  });
};
