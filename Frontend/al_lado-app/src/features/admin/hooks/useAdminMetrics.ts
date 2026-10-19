'use client';

import { useQuery } from '@tanstack/react-query';
import { adminService } from '../services/adminService';

/**
 * Gestiona la consulta y caché de métricas del panel con React Query.
 *
 * Se utiliza dentro del panel protegido; conserva estados de carga, error
 * y reintento en lugar de ocultar fallos detrás de métricas en cero.
 */
export function useAdminMetrics() {
  return useQuery({
    queryKey: ['admin-metrics'],
    queryFn: () => adminService.getMetrics(),
  });
}
