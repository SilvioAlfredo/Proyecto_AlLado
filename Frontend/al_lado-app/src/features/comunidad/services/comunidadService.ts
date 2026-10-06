import api from '@/lib/api';
import { PublicacionForoDTO, ComunicadoDTO } from '@/types/comunidad';

export const comunidadService = {
  async getPostsForo(): Promise<PublicacionForoDTO[]> {
    const { data } = await api.get<PublicacionForoDTO[]>('/foro');
    return data;
  },

  async crearPostForo(dto: Partial<PublicacionForoDTO>): Promise<PublicacionForoDTO> {
    const { data } = await api.post<PublicacionForoDTO>('/foro', dto);
    return data;
  },

  async getComunicados(): Promise<ComunicadoDTO[]> {
    const { data } = await api.get<ComunicadoDTO[]>('/comunicados');
    return data;
  },

  // Backend: GET /reportes (ADMIN) · GET /reportes/estado/{estado} · PUT /reportes/{id}/resolver
  async obtenerReportes(): Promise<any[]> {
    const { data } = await api.get<any[]>('/reportes');
    return data;
  },
};