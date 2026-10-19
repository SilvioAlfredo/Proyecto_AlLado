import api from '@/lib/api';
import { EtiquetaDTO } from '@/types/trabajo';

export const etiquetasService = {
  async listar(): Promise<EtiquetaDTO[]> {
    const { data } = await api.get<EtiquetaDTO[]>('/etiquetas');
    return data;
  },
  async obtener(id: number): Promise<EtiquetaDTO> {
    const { data } = await api.get<EtiquetaDTO>(`/etiquetas/${id}`);
    return data;
  },
  async crear(nombre: string): Promise<EtiquetaDTO> {
    const { data } = await api.post<EtiquetaDTO>('/etiquetas', { nombre });
    return data;
  },
  async eliminar(id: number): Promise<void> {
    await api.delete(`/etiquetas/${id}`);
  },
};
