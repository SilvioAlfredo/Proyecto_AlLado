import { api } from "../../../services/api";

export interface OportunidadLaboralDTO {
  id?: number;
  titulo: string;
  contenido: string;
  fechaCreacion?: string;
  autor: { id: number; nombre?: string; email?: string };
  ubicacion?: string;
  modalidad?: string;
  rangoSalarial?: string;
  activa: boolean;
  etiquetas?: unknown[];
  postulaciones?: unknown[];
}

export const oportunidadService = {
  obtenerTodas: async () => (await api.get<OportunidadLaboralDTO[]>("/oportunidades")).data,
  obtenerPorId: async (id: number) => (await api.get<OportunidadLaboralDTO>(`/oportunidades/${id}`)).data,
  crear: async (data: OportunidadLaboralDTO) => (await api.post<OportunidadLaboralDTO>("/oportunidades", data)).data,
  cerrar: async (id: number) => (await api.put<OportunidadLaboralDTO>(`/oportunidades/cerrar/${id}`)).data,
};
