import api from '@/lib/api';
import { DashboardMetricsDTO } from '@/types/admin';

export const adminService = {
  /**
   * Consulta las métricas acumuladas del sistema para una cuenta ADMIN.
   *
   * Los montos declarados y la estimación de planes no acreditan pagos recibidos.
   * Los usuarios activos representan cuentas habilitadas, no sesiones conectadas.
   *
   * @returns Métricas del backend, sin reemplazar los errores por valores en cero.
   */
  async getMetrics(): Promise<DashboardMetricsDTO> {
    const { data } = await api.get<DashboardMetricsDTO>('/api/admin/dashboard/metrics');
    return data;
  },
};
