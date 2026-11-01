import api from '@/lib/api';
import { ContratoDTO } from '@/types/trabajo';

/**
 * Servicio de historial de contratos (HU-08).
 *
 * Consume los endpoints del backend implementados en ContratoController:
 * - GET /contratos/historial/empleador   (solo EMPLEADOR/ADMIN)
 * - GET /contratos/historial/trabajador/{id} (TRABAJADOR/EMPLEADOR/ADMIN)
 * - GET /contratos/{id}                  (autenticado)
 *
 * No modifica estilos ni componentes existentes; solo añade la capa de datos.
 */
export const contratosService = {
  /**
   * Historial de contratos del empleador autenticado.
   * Devuelve todas las postulaciones de sus ofertas, ordenadas por fecha descendente.
   */
  async historialEmpleador(): Promise<ContratoDTO[]> {
    const { data } = await api.get<ContratoDTO[]>('/contratos/historial/empleador');
    return data;
  },

  /**
   * Historial de contratos de un trabajador: sus postulaciones con estados de
   * ejecución (SELECCIONADA, COMPLETADA, RETIRADA, RECHAZADA).
   */
  async historialTrabajador(trabajadorId: number): Promise<ContratoDTO[]> {
    const { data } = await api.get<ContratoDTO[]>(`/contratos/historial/trabajador/${trabajadorId}`);
    return data;
  },

  /**
   * Detalle de un contrato/postulación por su ID.
   */
  async obtenerPorId(id: number): Promise<ContratoDTO> {
    const { data } = await api.get<ContratoDTO>(`/contratos/${id}`);
    return data;
  },
};
