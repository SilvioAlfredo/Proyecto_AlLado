import api from '@/lib/api';
import { OportunidadLaboralDTO } from '@/types/trabajo';
import { PaginaDTO } from '@/types/admin';

export interface FiltrosOportunidades {
  filtro?: string;
  modalidad?: string;
  etiquetas?: string[];
  soloDestacadas?: boolean;
  ubicacion?: string;
  sector?: string;
}

// AL-SEC-006: el contacto del empleador no viene en la oferta pública
export interface ContactoEmpleadorDTO {
  nombre: string;
  email: string;
  telefono?: string | null;
}

export const oportunidadesService = {
  async obtenerTodas(): Promise<OportunidadLaboralDTO[]> {
    const { data } = await api.get<OportunidadLaboralDTO[]>('/oportunidades');
    return data;
  },

  async obtenerPorId(id: number): Promise<OportunidadLaboralDTO> {
    const { data } = await api.get<OportunidadLaboralDTO>(`/oportunidades/${id}`);
    return data;
  },

  // AL-SEC-006: proyección por audiencia; solo el titular, ADMIN o quien se
  // postuló puede ver el contacto del empleador de la oferta
  async contacto(id: number): Promise<ContactoEmpleadorDTO> {
    const { data } = await api.get<ContactoEmpleadorDTO>(`/oportunidades/${id}/contacto`);
    return data;
  },

  async crear(dto: OportunidadLaboralDTO): Promise<OportunidadLaboralDTO> {
    const { data } = await api.post<OportunidadLaboralDTO>('/oportunidades', dto);
    return data;
  },

  async actualizar(id: number, dto: OportunidadLaboralDTO): Promise<OportunidadLaboralDTO> {
    const { data } = await api.put<OportunidadLaboralDTO>(`/oportunidades/${id}`, dto);
    return data;
  },

  // Cierra (desactiva) una oferta mediante PATCH /oportunidades/{id}/cerrar
  async cerrar(id: number): Promise<OportunidadLaboralDTO> {
    const { data } = await api.patch<OportunidadLaboralDTO>(`/oportunidades/${id}/cerrar`);
    return data;
  },

  async eliminar(id: number): Promise<void> {
    await api.delete(`/oportunidades/${id}`);
  },

  async listarConFiltros(
    filtros?: FiltrosOportunidades,
    page = 0,
    size = 10
  ): Promise<PaginaDTO<OportunidadLaboralDTO>> {
    const params = new URLSearchParams({ page: String(page), size: String(size) });
    const { data } = await api.post<PaginaDTO<OportunidadLaboralDTO>>(
      `/oportunidades/filtrar?${params.toString()}`,
      filtros || {}
    );
    return data;
  },

  async listarPublicas(
  filtros?: FiltrosOportunidades,
  page = 0,
  size = 10
): Promise<PaginaDTO<OportunidadLaboralDTO>> {
  const params = new URLSearchParams({
    page: String(page),
    size: String(size),
  });

  const { data } = await api.post<PaginaDTO<OportunidadLaboralDTO>>(
    `/oportunidades/publicas/filtrar?${params.toString()}`,
    filtros || {}
  );

  return data;
},

  async propuestasPro(trabajadorId: number): Promise<OportunidadLaboralDTO[]> {
    const { data } = await api.get<OportunidadLaboralDTO[]>(
      `/oportunidades/propuestas-pro?trabajadorId=${trabajadorId}`
    );
    return data;
  },
};
