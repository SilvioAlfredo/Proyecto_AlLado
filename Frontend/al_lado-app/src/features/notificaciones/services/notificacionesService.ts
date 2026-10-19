import api from '@/lib/api';

export interface NotificacionDTO {
  id?: number;
  destinatario?: { id: number; nombre: string; email: string };
  titulo: string;
  mensaje: string;
  tipo?: 'POSTULACION_RECIBIDA' | 'POSTULACION_ESTADO' | 'SISTEMA' | string;
  referenciaId?: number;
  leida?: boolean;
  fechaCreacion?: string;
}

// Backend: GET /notificaciones/usuario/{id} · GET .../no-leidas
// PATCH /notificaciones/{id}/leer · PATCH /notificaciones/usuario/{id}/leer-todas
export const notificacionesService = {
  async listar(usuarioId: number): Promise<NotificacionDTO[]> {
    const { data } = await api.get<NotificacionDTO[]>(`/notificaciones/usuario/${usuarioId}`);
    return data;
  },

  async noLeidas(usuarioId: number): Promise<number> {
    const { data } = await api.get<{ noLeidas: number }>(`/notificaciones/usuario/${usuarioId}/no-leidas`);
    return data.noLeidas ?? 0;
  },

  async marcarLeida(id: number): Promise<NotificacionDTO> {
    const { data } = await api.patch<NotificacionDTO>(`/notificaciones/${id}/leer`);
    return data;
  },

  async marcarTodasLeidas(usuarioId: number): Promise<void> {
    await api.patch(`/notificaciones/usuario/${usuarioId}/leer-todas`);
  },
};
