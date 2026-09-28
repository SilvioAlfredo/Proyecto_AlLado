import { api } from "../../../services/api";

export interface PostulacionDTO {
  id?: number;
  titulo: string;
  contenido: string;
  fechaCreacion?: string;
  autor: { id: number; nombre?: string; email?: string };
  estado?: string;
  remuneracionPretendida?: number;
  oportunidadLaboral: { id: number; titulo?: string };
}

export const postulacionService = {
  obtenerTodas: async () => (await api.get<PostulacionDTO[]>("/postulaciones")).data,
  crear: async (data: PostulacionDTO) => (await api.post<PostulacionDTO>("/postulaciones", data)).data,
  seleccionar: async (id: number) => (await api.put<PostulacionDTO>(`/postulaciones/seleccionar/${id}`)).data,
  rechazar: async (id: number) => (await api.put<PostulacionDTO>(`/postulaciones/rechazar/${id}`)).data,
};
