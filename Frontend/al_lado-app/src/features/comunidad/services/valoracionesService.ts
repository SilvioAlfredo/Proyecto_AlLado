import api from '@/lib/api';
import { ValoracionDTO } from '@/types/comunidad';

export const valoracionesService = {
  async porTrabajador(trabajadorId: number): Promise<ValoracionDTO[]> {
    const { data } = await api.get<ValoracionDTO[]>(`/valoraciones/trabajador/${trabajadorId}`);
    return data;
  },
  async promedio(trabajadorId: number): Promise<number> {
    const { data } = await api.get<number>(`/valoraciones/trabajador/${trabajadorId}/promedio`);
    return data;
  },
  async porAutor(autorId: number): Promise<ValoracionDTO[]> {
    const { data } = await api.get<ValoracionDTO[]>(`/valoraciones/autor/${autorId}`);
    return data;
  },
  async crear(dto: Partial<ValoracionDTO>): Promise<ValoracionDTO> {
    const { data } = await api.post<ValoracionDTO>('/valoraciones', dto);
    return data;
  },
  async eliminar(id: number): Promise<void> {
    await api.delete(`/valoraciones/${id}`);
  },
};
