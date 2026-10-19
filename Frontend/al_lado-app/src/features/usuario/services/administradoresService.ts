import api from '@/lib/api';
import { UsuarioDTO } from '@/types/usuario';

export interface AdministradorDTO extends UsuarioDTO {
  nivelAcceso?: string;
}

// Endpoints base /administradores
export const administradoresService = {
  async listar(): Promise<AdministradorDTO[]> {
    const { data } = await api.get<AdministradorDTO[]>('/administradores');
    return data;
  },

  async obtenerPorId(id: number): Promise<AdministradorDTO> {
    const { data } = await api.get<AdministradorDTO>(`/administradores/${id}`);
    return data;
  },

  async crear(dto: Partial<AdministradorDTO>): Promise<AdministradorDTO> {
    const { data } = await api.post<AdministradorDTO>('/administradores', dto);
    return data;
  },

  async actualizar(id: number, dto: Partial<AdministradorDTO>): Promise<AdministradorDTO> {
    const { data: actual } = await api.get<AdministradorDTO>(`/administradores/${id}`);
    const { data } = await api.put<AdministradorDTO>(`/administradores/${id}`, { ...actual, ...dto });
    return data;
  },

  async eliminar(id: number): Promise<void> {
    await api.delete(`/administradores/${id}`);
  },
};
