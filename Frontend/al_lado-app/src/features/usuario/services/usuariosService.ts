import api from '@/lib/api';
import { UsuarioDTO } from '@/types/usuario';

// Endpoints base /usuarios (listar y borrar exigen ADMIN)
export interface UsuarioAdminDTO extends UsuarioDTO {
  suscripcionActiva?: boolean;
}

export const usuariosService = {
  async listar(): Promise<UsuarioAdminDTO[]> {
    const { data } = await api.get<UsuarioAdminDTO[]>('/usuarios');
    return data;
  },

  async obtenerPorId(id: number): Promise<UsuarioAdminDTO> {
    const { data } = await api.get<UsuarioAdminDTO>(`/usuarios/${id}`);
    return data;
  },

  async actualizar(id: number, dto: Partial<UsuarioAdminDTO>): Promise<UsuarioAdminDTO> {
    // PUT exige DTO completo (nombre/email/telefono @NotBlank): se fusiona con el actual
    const { data: actual } = await api.get<UsuarioAdminDTO>(`/usuarios/${id}`);
    const { data } = await api.put<UsuarioAdminDTO>(`/usuarios/${id}`, { ...actual, ...dto });
    return data;
  },

  async eliminar(id: number): Promise<void> {
    await api.delete(`/usuarios/${id}`);
  },

  async deshabilitar(id: number): Promise<UsuarioAdminDTO> {
    const { data } = await api.put<UsuarioAdminDTO>(`/usuarios/${id}/deshabilitar`);
    return data;
  },

  async habilitar(id: number): Promise<UsuarioAdminDTO> {
    const { data } = await api.put<UsuarioAdminDTO>(`/usuarios/${id}/habilitar`);
    return data;
  },
};
