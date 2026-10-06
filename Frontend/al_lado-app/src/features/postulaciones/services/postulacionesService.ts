import api from '@/lib/api';
import { PostulacionDTO } from '@/types/trabajo';

export const postulacionesService = {
  async obtenerTodas(): Promise<PostulacionDTO[]> {
    const { data } = await api.get<PostulacionDTO[]>('/postulaciones');
    return data;
  },

  async obtenerPorId(id: number): Promise<PostulacionDTO> {
    const { data } = await api.get<PostulacionDTO>(`/postulaciones/${id}`);
    return data;
  },

  async crear(dto: Partial<PostulacionDTO>): Promise<PostulacionDTO> {
    const { data } = await api.post<PostulacionDTO>('/postulaciones', dto);
    return data;
  },

  // Backend usa @PatchMapping (no PUT): PATCH /postulaciones/{id}/seleccionar|rechazar|completar
  async seleccionar(id: number): Promise<PostulacionDTO> {
    const { data } = await api.patch<PostulacionDTO>(`/postulaciones/${id}/seleccionar`);
    return data;
  },

  async rechazar(id: number): Promise<PostulacionDTO> {
    const { data } = await api.patch<PostulacionDTO>(`/postulaciones/${id}/rechazar`);
    return data;
  },

  async completar(id: number): Promise<PostulacionDTO> {
    const { data } = await api.patch<PostulacionDTO>(`/postulaciones/${id}/completar`);
    return data;
  },
};