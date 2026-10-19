// types/admin.ts
export interface TendenciaDTO {
  nombre: string;
  cantidad: number;
  porcentaje: number;
  colorClass: string;
}

export interface DashboardMetricsDTO {
  totalIngresos: string;
  metricContratos: number;
  metricTrabajadores: number;
  metricAvg: string;
  dineroComunidad: string;
  trabajosCompletados: number;
  tendencias: TendenciaDTO[];
  usuariosActivos?: number;
  usuariosInactivos?: number;
  postulacionesPorEstado?: Record<string, number>;
}