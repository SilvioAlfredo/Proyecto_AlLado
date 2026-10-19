import api from '@/lib/api';
import { UsuarioResumenDTO } from '@/types/usuario';

export interface FavoritoDTO {
  id?: number;
  empleador?: UsuarioResumenDTO;
  trabajador?: UsuarioResumenDTO;
  fechaGuardado?: string;
}

export const favoritosService = {
  async porEmpleador(empleadorId: number): Promise<FavoritoDTO[]> {
    const { data } = await api.get<FavoritoDTO[]>(`/favoritos/empleador/${empleadorId}`);
    return data;
  },
  async agregar(empleadorId: number, trabajadorId: number): Promise<FavoritoDTO> {
    const { data } = await api.post<FavoritoDTO>(
      `/favoritos/empleador/${empleadorId}/trabajador/${trabajadorId}`
    );
    return data;
  },
  async quitar(empleadorId: number, trabajadorId: number): Promise<void> {
    await api.delete(`/favoritos/empleador/${empleadorId}/trabajador/${trabajadorId}`);
  },
};
