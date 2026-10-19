import api from '@/lib/api';
import { UsuarioResumenDTO } from '@/types/usuario';

export interface SuscripcionDTO {
  id?: number;
  usuario: UsuarioResumenDTO;
  nivel: string;
  fechaInicio: string;
  fechaFin?: string;
  activa?: boolean;
}

export const suscripcionesService = {
  async listar(): Promise<SuscripcionDTO[]> {
    const { data } = await api.get<SuscripcionDTO[]>('/suscripciones');
    return data;
  },
  async porUsuario(usuarioId: number): Promise<SuscripcionDTO> {
    const { data } = await api.get<SuscripcionDTO>(`/suscripciones/usuario/${usuarioId}`);
    return data;
  },
  async obtenerPorId(id: number): Promise<SuscripcionDTO> {
    const { data } = await api.get<SuscripcionDTO>(`/suscripciones/${id}`);
    return data;
  },
  async eliminar(id: number): Promise<void> {
    await api.delete(`/suscripciones/${id}`);
  },
  async crear(dto: Partial<SuscripcionDTO>): Promise<SuscripcionDTO> {
    const { data } = await api.post<SuscripcionDTO>('/suscripciones', dto);
    return data;
  },
  async cancelar(id: number): Promise<SuscripcionDTO> {
    const { data } = await api.put<SuscripcionDTO>(`/suscripciones/cancelar/${id}`);
    return data;
  },
};

export interface DonacionDTO {
  id?: number;
  monto: number;
  moneda?: string;
  mecanismoPago?: string;
  estado?: string;
  fechaDonacion?: string;
  usuario: UsuarioResumenDTO;
  banco?: string;
  tipoTransferencia?: string;
  asuntoTransferencia?: string;
}

export const donacionesService = {
  async listar(): Promise<DonacionDTO[]> {
    const { data } = await api.get<DonacionDTO[]>('/donaciones');
    return data;
  },
  async porUsuario(usuarioId: number, page = 0): Promise<DonacionDTO[]> {
    const { data } = await api.get<DonacionDTO[]>(`/donaciones/usuario/${usuarioId}`, {
      params: { page, size: 10 },
    });
    return data;
  },
  async obtenerPorId(id: number): Promise<DonacionDTO> {
    const { data } = await api.get<DonacionDTO>(`/donaciones/${id}`);
    return data;
  },
  async crear(dto: Partial<DonacionDTO>): Promise<DonacionDTO> {
    const { data } = await api.post<DonacionDTO>('/donaciones', dto);
    return data;
  },
  /**
   * Solicita verificar una transferencia con el asunto y monto observados en el banco.
   *
   * El backend compara ambos datos y rechaza registros ya procesados.
   * Esta operación no realiza un cobro; puede activar PRO si es una suscripción.
   *
   * @param id - Identificador de la donación o transferencia.
   * @param asunto - Asunto observado en el comprobante bancario.
   * @param monto - Importe observado. Preferir la cadena devuelta por bankAmount
   * para conservar los decimales al enviarlos al backend.
   */
  async verificar(
    id: number,
    asunto: string,
    monto: number | string,
  ): Promise<DonacionDTO> {
    const { data } = await api.post<DonacionDTO>(
      `/donaciones/${id}/verificar?asunto=${encodeURIComponent(asunto)}&monto=${monto}`,
    );
    return data;
  },
};
