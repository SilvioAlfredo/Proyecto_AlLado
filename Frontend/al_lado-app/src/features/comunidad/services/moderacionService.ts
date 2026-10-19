import api from '@/lib/api';
import { PublicidadDTO, ComunicadoDTO, PublicacionForoDTO } from '@/types/comunidad';
import { UsuarioResumenDTO } from '@/types/usuario';

export interface ReporteDTO {
  id?: number;
  motivo?: string;
  descripcion?: string;
  estado?: string;
  fechaCreacion?: string;
  reportante?: UsuarioResumenDTO;
  reportado?: UsuarioResumenDTO;
  moderador?: UsuarioResumenDTO;
}

export const reportesService = {
  async listar(): Promise<ReporteDTO[]> {
    const { data } = await api.get<ReporteDTO[]>('/reportes');
    return data;
  },
  async porEstado(estado: string): Promise<ReporteDTO[]> {
    const { data } = await api.get<ReporteDTO[]>(`/reportes/estado/${estado}`);
    return data;
  },
  async obtener(id: number): Promise<ReporteDTO> {
    const { data } = await api.get<ReporteDTO>(`/reportes/${id}`);
    return data;
  },
  async crear(dto: Partial<ReporteDTO>): Promise<ReporteDTO> {
    const { data } = await api.post<ReporteDTO>('/reportes', dto);
    return data;
  },
  // Backend: PUT /reportes/{id}/resolver?moderadorId=&estado= (ADMIN).
  // moderadorId es opcional: si se omite el backend usa el admin autenticado.
  async resolver(id: number, estado = 'RESUELTO', moderadorId?: number): Promise<ReporteDTO> {
    const params = new URLSearchParams({ estado });
    if (moderadorId != null) params.set('moderadorId', String(moderadorId));
    const { data } = await api.put<ReporteDTO>(`/reportes/${id}/resolver?${params.toString()}`);
    return data;
  },
};

export const foroService = {
  async listar(): Promise<PublicacionForoDTO[]> {
    const { data } = await api.get<PublicacionForoDTO[]>('/foro');
    return data;
  },
  async obtener(id: number): Promise<PublicacionForoDTO> {
    const { data } = await api.get<PublicacionForoDTO>(`/foro/${id}`);
    return data;
  },
  async porAutor(autorId: number): Promise<PublicacionForoDTO[]> {
    const { data } = await api.get<PublicacionForoDTO[]>(`/foro/autor/${autorId}`);
    return data;
  },
  async crear(dto: Partial<PublicacionForoDTO>): Promise<PublicacionForoDTO> {
    const { data } = await api.post<PublicacionForoDTO>('/foro', dto);
    return data;
  },
  async eliminar(id: number): Promise<void> {
    await api.delete(`/foro/${id}`);
  },
};

export const publicidadesService = {
  async obtener(id: number): Promise<PublicidadDTO> {
    return (await api.get<PublicidadDTO>(`/publicidades/${id}`)).data;
  },
  async listar(): Promise<PublicidadDTO[]> {
    const { data } = await api.get<PublicidadDTO[]>('/publicidades');
    return data;
  },
  async vigentes(): Promise<PublicidadDTO[]> {
    const { data } = await api.get<PublicidadDTO[]>('/publicidades/vigentes');
    return data;
  },
  async porAnunciante(anuncianteId: number): Promise<PublicidadDTO[]> {
    const { data } = await api.get<PublicidadDTO[]>(`/publicidades/anunciante/${anuncianteId}`);
    return data;
  },
  async crear(dto: Partial<PublicidadDTO>): Promise<PublicidadDTO> {
    const { data } = await api.post<PublicidadDTO>('/publicidades', dto);
    return data;
  },
  async actualizar(id: number, dto: Partial<PublicidadDTO>): Promise<PublicidadDTO> {
    const { data } = await api.put<PublicidadDTO>(`/publicidades/${id}`, dto);
    return data;
  },
  async eliminar(id: number): Promise<void> {
    await api.delete(`/publicidades/${id}`);
  },
};

export const comunicadosService = {
  async listar(): Promise<ComunicadoDTO[]> {
    const { data } = await api.get<ComunicadoDTO[]>('/comunicados');
    return data;
  },
  async porAlcance(alcance: string): Promise<ComunicadoDTO[]> {
    const { data } = await api.get<ComunicadoDTO[]>(`/comunicados/alcance/${encodeURIComponent(alcance)}`);
    return data;
  },
  async crear(dto: Partial<ComunicadoDTO>): Promise<ComunicadoDTO> {
    const { data } = await api.post<ComunicadoDTO>('/comunicados', dto);
    return data;
  },
  async eliminar(id: number): Promise<void> {
    await api.delete(`/comunicados/${id}`);
  },
};
