import api from '@/lib/api';
import { OportunidadLaboralDTO } from '@/types/trabajo';

export interface FiltrosOportunidades {
  filtro?: string;
  modalidad?: string;
  etiquetas?: string[];
}

export const oportunidadesService = {
  async obtenerTodas(): Promise<OportunidadLaboralDTO[]> {
    const { data } = await api.get<OportunidadLaboralDTO[]>('/oportunidades');
    return data;
  },

  async obtenerPorId(id: number): Promise<OportunidadLaboralDTO> {
    const { data } = await api.get<OportunidadLaboralDTO>(`/oportunidades/${id}`);
    return data;
  },

  async crear(dto: OportunidadLaboralDTO): Promise<OportunidadLaboralDTO> {
    const { data } = await api.post<OportunidadLaboralDTO>('/oportunidades', dto);
    return data;
  },

  async actualizar(id: number, dto: OportunidadLaboralDTO): Promise<OportunidadLaboralDTO> {
    const { data } = await api.put<OportunidadLaboralDTO>(`/oportunidades/${id}`, dto);
    return data;
  },

  // El backend no tiene /cerrar: se cierra con PUT /oportunidades/{id} + activa=false
  async cerrar(id: number): Promise<OportunidadLaboralDTO> {
    const actual = await this.obtenerPorId(id);
    return this.actualizar(id, { ...actual, activa: false });
  },

  async eliminar(id: number): Promise<void> {
    await api.delete(`/oportunidades/${id}`);
  },

  async listarConFiltros(filtros?: FiltrosOportunidades): Promise<OportunidadLaboralDTO[]> {
    const { data } = await api.post<OportunidadLaboralDTO[]>('/oportunidades/filtrar', filtros || {});
    return data;
  },
};
