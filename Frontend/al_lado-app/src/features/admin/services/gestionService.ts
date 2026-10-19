import api from '@/lib/api';
import type {
  ComunicadoDTO,
  PublicacionForoDTO,
  ValoracionDTO,
  PublicidadDTO,
  PerfilEmpresarialDTO,
} from '@/types/comunidad';
import type { OportunidadLaboralDTO, PostulacionDTO } from '@/types/trabajo';
import type { ReporteDTO } from '@/features/comunidad/services/moderacionService';
import type {
  DonacionDTO,
  SuscripcionDTO,
} from '@/features/usuario/services/aportesService';

/**
 * Fila del listado empresarial: incluye al empleador aunque todavía no tenga perfil.
 * El id identifica al empleador; el identificador del perfil está en perfil.id.
 */
export interface EmpresaAdminDTO {
  id: number;
  empleador: import('@/types/usuario').UsuarioResumenDTO;
  perfil?: PerfilEmpresarialDTO;
}

/**
 * Resultado paginado del backend: page utiliza índices desde cero.
 * Los totales corresponden a los filtros aplicados, no solo a la página actual.
 */
export interface Pagina<T> {
  content: T[];
  page: number;
  totalPages: number;
  totalElements: number;
}

/**
 * Consultas paginadas del panel, protegidas por el rol ADMIN en el backend.
 *
 * Solicitan diez registros por página. Propagan errores HTTP para que las vistas
 * distingan una consulta fallida de un resultado vacío y permitan reintentar.
 */
export const gestionService = {
  /**
   * Busca anuncios por título, nombre o correo del anunciante.
   *
   * @param filtro - Texto de búsqueda; vacío para no filtrar por texto.
   * @param estado - TODAS, VIGENTES, PROGRAMADAS, VENCIDAS o INACTIVAS.
   * @param page - Índice de página desde cero.
   */
  async publicidades(filtro: string, estado: string, page: number) {
    return (
      await api.get<Pagina<PublicidadDTO>>('/api/admin/publicidades', {
        params: { filtro, estado, page, size: 10 },
      })
    ).data;
  },
  /**
   * Busca empleadores por empresa, CUIT, nombre o correo del titular.
   *
   * @param filtro - Texto de búsqueda; vacío para no filtrar por texto.
   * @param estado - TODAS, VERIFICADAS, PENDIENTES o SIN_PERFIL.
   * @param page - Índice de página desde cero.
   */
  async empresas(filtro: string, estado: string, page: number) {
    return (
      await api.get<Pagina<EmpresaAdminDTO>>('/api/admin/empresas', {
        params: { filtro, estado, page, size: 10 },
      })
    ).data;
  },
  /**
   * Busca transferencias por persona, correo, mecanismo o asunto.
   *
   * @param filtro - Texto de búsqueda; vacío para no filtrar por texto.
   * @param estado - TODAS, PENDIENTE, VERIFICADO o RECHAZADO.
   * @param page - Índice de página desde cero.
   */
  async donaciones(filtro: string, estado: string, page: number) {
    return (
      await api.get<Pagina<DonacionDTO>>('/api/admin/donaciones', {
        params: { filtro, estado, page, size: 10 },
      })
    ).data;
  },
  /**
   * Busca suscripciones por nombre o correo del titular.
   *
   * La vigencia se calcula en el backend; INACTIVAS incluye canceladas y vencidas.
   *
   * @param filtro - Texto de búsqueda; vacío para no filtrar por texto.
   * @param estado - TODAS, ACTIVAS o INACTIVAS.
   * @param page - Índice de página desde cero.
   */
  async suscripciones(filtro: string, estado: string, page: number) {
    return (
      await api.get<Pagina<SuscripcionDTO>>('/api/admin/suscripciones', {
        params: { filtro, estado, page, size: 10 },
      })
    ).data;
  },
  /**
   * Busca reportes por motivo, descripción o personas involucradas.
   *
   * @param filtro - Texto de búsqueda; vacío para no filtrar por texto.
   * @param estado - TODOS, PENDIENTE, EN_REVISION, RESUELTO o DESCARTADO.
   * @param page - Índice de página desde cero.
   */
  async reportes(filtro: string, estado: string, page: number) {
    return (
      await api.get<Pagina<ReporteDTO>>('/api/admin/reportes', {
        params: { filtro, estado, page, size: 10 },
      })
    ).data;
  },
  /**
   * Busca publicaciones por título, contenido o nombre del autor.
   *
   * @param filtro - Texto de búsqueda; vacío para no filtrar por texto.
   * @param page - Índice de página desde cero.
   */
  async foro(filtro: string, page: number) {
    return (
      await api.get<Pagina<PublicacionForoDTO>>('/api/admin/foro', {
        params: { filtro, page, size: 10 },
      })
    ).data;
  },
  /**
   * Consulta las valoraciones de un trabajador concreto.
   *
   * @param trabajadorId - Identificador del trabajador; debe ser positivo.
   * @param page - Índice de página desde cero.
   */
  async valoraciones(trabajadorId: number, page: number) {
    return (
      await api.get<Pagina<ValoracionDTO>>('/api/admin/valoraciones', {
        params: { trabajadorId, page, size: 10 },
      })
    ).data;
  },
  /**
   * Busca comunicados y permite elegir su alcance y orden.
   *
   * @param filtro - Texto presente en el título o contenido.
   * @param alcance - Alcance exacto; vacío para consultar todos.
   * @param orden - recientes, antiguos o titulo.
   * @param page - Índice de página desde cero.
   */
  async comunicados(filtro: string, alcance: string, orden: string, page: number) {
    return (
      await api.get<Pagina<ComunicadoDTO>>('/api/admin/comunicados', {
        params: { filtro, alcance, orden, page, size: 10 },
      })
    ).data;
  },
  /**
   * Busca ofertas combinando texto, modalidad y etiquetas.
   *
   * Cada etiqueta se envía como un parámetro independiente con el mismo nombre.
   *
   * @param filtro - Texto de búsqueda de oportunidades.
   * @param modalidad - Modalidad; vacío para omitir este filtro.
   * @param etiquetas - Nombres de etiquetas que se enviarán al backend.
   * @param page - Índice de página desde cero.
   */
  async oportunidades(
    filtro: string,
    modalidad: string,
    etiquetas: string[],
    page: number,
  ) {
    const params = new URLSearchParams({ filtro, page: String(page), size: '10' });
    if (modalidad) {
      params.set('modalidad', modalidad);
    }
    etiquetas.forEach((nombre) => params.append('etiquetas', nombre));
    return (
      await api.get<Pagina<OportunidadLaboralDTO>>(`/api/admin/oportunidades?${params}`)
    ).data;
  },
  /**
   * Consulta las postulaciones de una oportunidad existente.
   *
   * @param id - Identificador de la oportunidad, no de una postulación.
   * @param page - Índice de página desde cero.
   */
  async postulaciones(id: number, page: number) {
    return (
      await api.get<Pagina<PostulacionDTO>>(
        `/api/admin/oportunidades/${id}/postulaciones`,
        { params: { page, size: 10 } },
      )
    ).data;
  },
};
