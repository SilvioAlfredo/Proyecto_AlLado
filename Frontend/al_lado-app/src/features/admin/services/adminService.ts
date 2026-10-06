import api from '@/lib/api';
import { DashboardMetricsDTO } from '@/types/admin';

export const adminService = {
  async getMetrics(): Promise<DashboardMetricsDTO> {
    const { data } = await api.get<DashboardMetricsDTO>('/admin/dashboard/metrics');
    return data;
  },
};