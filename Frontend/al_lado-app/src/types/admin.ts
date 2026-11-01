// types/admin.ts
export interface PaginaDTO<T> {
  content: T[];
  page: number;
  totalPages: number;
  totalElements: number;
}

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