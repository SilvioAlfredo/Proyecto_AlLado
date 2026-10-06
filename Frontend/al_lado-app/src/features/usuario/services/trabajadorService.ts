import api from '@/lib/api';
import { TrabajadorDTO } from '@/types/usuario';
import { EstadisticasTrabajadorDTO } from '@/types/usuario';

export const trabajadorService = {
  async obtenerPerfil(): Promise<TrabajadorDTO> {
    const { data } = await api.get<TrabajadorDTO>('/trabajadores/perfil');
    return data;
  },

  async actualizarPerfil(dto: Partial<TrabajadorDTO>): Promise<TrabajadorDTO> {
    const { data } = await api.put<TrabajadorDTO>('/trabajadores/perfil', dto);
    return data;
  },

  async obtenerTodos(): Promise<TrabajadorDTO[]> {
    const { data } = await api.get<TrabajadorDTO[]>('/trabajadores');
    return data;
  },

  async obtenerPorId(id: number): Promise<TrabajadorDTO> {
    const { data } = await api.get<TrabajadorDTO>(`/trabajadores/${id}`);
    return data;
  },

  async obtenerEstadisticasPorEmail(email: string): Promise<EstadisticasTrabajadorDTO> {
    const { data } = await api.get<EstadisticasTrabajadorDTO>(`/trabajadores/por-email/estadisticas?email=${email}`);
    return data;
  },

  async obtenerEstadisticas(id: number): Promise<EstadisticasTrabajadorDTO> {
    const { data } = await api.get<EstadisticasTrabajadorDTO>(`/trabajadores/${id}/estadisticas`);
    return data;
  },
};
