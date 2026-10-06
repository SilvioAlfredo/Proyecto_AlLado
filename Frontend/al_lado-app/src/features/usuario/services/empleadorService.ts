import api from '@/lib/api';
import { EmpleadorDTO } from '@/types/usuario';

export const empleadorService = {
  async obtenerPerfil(): Promise<EmpleadorDTO> {
    const { data } = await api.get<EmpleadorDTO>('/empleadores/perfil');
    return data;
  },

  async actualizarPerfil(dto: Partial<EmpleadorDTO>): Promise<EmpleadorDTO> {
    const { data } = await api.put<EmpleadorDTO>('/empleadores/perfil', dto);
    return data;
  },

  async obtenerTodos(): Promise<EmpleadorDTO[]> {
    const { data } = await api.get<EmpleadorDTO[]>('/empleadores');
    return data;
  },

  async obtenerPorId(id: number): Promise<EmpleadorDTO> {
    const { data } = await api.get<EmpleadorDTO>(`/empleadores/${id}`);
    return data;
  },
};
