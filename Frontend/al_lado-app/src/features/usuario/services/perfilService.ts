import api from '@/lib/api';
import { PortafolioItemDTO, HorarioDisponibilidadDTO, PreferenciaNotificacionDTO } from '@/types/usuario';

export const portafolioService = {
  async listar(trabajadorId: number): Promise<PortafolioItemDTO[]> {
    const { data } = await api.get<PortafolioItemDTO[]>(`/portafolio-items/trabajador/${trabajadorId}`);
    return data;
  },
  async crear(trabajadorId: number, dto: Partial<PortafolioItemDTO>): Promise<PortafolioItemDTO> {
    const { data } = await api.post<PortafolioItemDTO>(`/portafolio-items/trabajador/${trabajadorId}`, dto);
    return data;
  },
  async eliminar(id: number): Promise<void> {
    await api.delete(`/portafolio-items/${id}`);
  },
};

export const horariosService = {
  async listar(trabajadorId: number): Promise<HorarioDisponibilidadDTO[]> {
    const { data } = await api.get<HorarioDisponibilidadDTO[]>(`/horarios/trabajador/${trabajadorId}`);
    return data;
  },
  async crear(trabajadorId: number, dto: Partial<HorarioDisponibilidadDTO>): Promise<HorarioDisponibilidadDTO> {
    const { data } = await api.post<HorarioDisponibilidadDTO>(`/horarios/trabajador/${trabajadorId}`, dto);
    return data;
  },
  async eliminar(id: number): Promise<void> {
    await api.delete(`/horarios/${id}`);
  },
};

export const preferenciasService = {
  async obtener(trabajadorId: number): Promise<PreferenciaNotificacionDTO> {
    const { data } = await api.get<PreferenciaNotificacionDTO>(`/preferencias/trabajador/${trabajadorId}`);
    return data;
  },
  async guardar(trabajadorId: number, dto: Partial<PreferenciaNotificacionDTO>): Promise<PreferenciaNotificacionDTO> {
    const { data } = await api.put<PreferenciaNotificacionDTO>(`/preferencias/trabajador/${trabajadorId}`, dto);
    return data;
  },
};
