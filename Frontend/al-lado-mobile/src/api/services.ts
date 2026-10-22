// Servicios espejo del frontend web (al_lado-app) contra el backend Spring Boot.
import { api } from './client';
import type {
  AuthResponse,
  Comunicado,
  DashboardMetrics,
  Donacion,
  Empleador,
  Etiqueta,
  ForoPost,
  Horario,
  Notificacion,
  Oportunidad,
  PortafolioItem,
  Postulacion,
  Publicidad,
  Reporte,
  Rol,
  Suscripcion,
  Trabajador,
  Valoracion,
} from '../types';

export const authService = {
  login: (email: string, password: string): Promise<AuthResponse> =>
      api.post<AuthResponse>('/auth/login', { email, password }),
  register: (input: {
    nombre: string;
    email: string;
    password: string;
    telefono: string;
    rol: Rol;
  }): Promise<AuthResponse> => api.post<AuthResponse>('/auth/register', input),
  logout: (): Promise<unknown> => api.post('/auth/logout').catch(() => null),
  forgot: (email: string): Promise<unknown> =>
      api.post('/auth/password/forgot', { email }),
  reset: (token: string, newPassword: string): Promise<unknown> =>
      api.post('/auth/password/reset', { token, newPassword }),
  changePassword: (
      email: string,
      currentPassword: string,
      newPassword: string,
  ): Promise<unknown> =>
      api.post('/auth/password', { email, currentPassword, newPassword }),
};

export const oportunidadService = {
  listar: (): Promise<Oportunidad[]> => api.get<Oportunidad[]>('/oportunidades'),
  detalle: (id: string | number): Promise<Oportunidad> =>
      api.get<Oportunidad>(`/oportunidades/${id}`),
  filtrar: (filtros: Record<string, unknown> = {}): Promise<Oportunidad[]> =>
      api.post<Oportunidad[]>('/oportunidades/filtrar', filtros),
  crear: (dto: Record<string, unknown>): Promise<Oportunidad> =>
      api.post<Oportunidad>('/oportunidades', dto),
  actualizar: (id: string | number, dto: Record<string, unknown>): Promise<Oportunidad> =>
      api.put<Oportunidad>(`/oportunidades/${id}`, dto),
  cerrar: (id: string | number): Promise<Oportunidad> =>
      api.put<Oportunidad>(`/oportunidades/${id}`, { activa: false }),
  eliminar: (id: string | number): Promise<unknown> =>
      api.del(`/oportunidades/${id}`),
  propuestasPro: (trabajadorId: string | number): Promise<Oportunidad[]> =>
      api.get<Oportunidad[]>(`/oportunidades/propuestas-pro?trabajadorId=${trabajadorId}`),
};

export const postulacionService = {
  listar: (): Promise<Postulacion[]> => api.get<Postulacion[]>('/postulaciones'),
  detalle: (id: string | number): Promise<Postulacion> =>
      api.get<Postulacion>(`/postulaciones/${id}`),
  crear: (dto: Record<string, unknown>): Promise<Postulacion> =>
      api.post<Postulacion>('/postulaciones', dto),
  seleccionar: (id: string | number): Promise<Postulacion> =>
      api.patch<Postulacion>(`/postulaciones/${id}/seleccionar`),
  rechazar: (id: string | number): Promise<Postulacion> =>
      api.patch<Postulacion>(`/postulaciones/${id}/rechazar`),
  completar: (id: string | number): Promise<Postulacion> =>
      api.patch<Postulacion>(`/postulaciones/${id}/completar`),
  retirar: (id: string | number): Promise<Postulacion> =>
      api.patch<Postulacion>(`/postulaciones/${id}/retirar`),
};

export const trabajadorService = {
  listar: (): Promise<Trabajador[]> => api.get<Trabajador[]>('/trabajadores'),
  detalle: (id: string | number): Promise<Trabajador> =>
      api.get<Trabajador>(`/trabajadores/${id}`),
  actualizar: (id: string | number, dto: { nombre?: string; email?: string; telefono?: string; [k: string]: unknown }): Promise<Trabajador> =>
      api.put<Trabajador>(`/trabajadores/${id}`, dto),
  estadisticasPorEmail: (email: string): Promise<unknown> =>
      api.get(`/trabajadores/por-email/estadisticas?email=${encodeURIComponent(email)}`),
};

export const empleadorService = {
  listar: (): Promise<Empleador[]> => api.get<Empleador[]>('/empleadores'),
  detalle: (id: string | number): Promise<Empleador> =>
      api.get<Empleador>(`/empleadores/${id}`),
  actualizar: (id: string | number, dto: { nombre?: string; email?: string; telefono?: string; [k: string]: unknown }): Promise<Empleador> =>
      api.put<Empleador>(`/empleadores/${id}`, dto),
};

export const perfilService = {
  portafolio: (id: string | number): Promise<PortafolioItem[]> =>
      api.get<PortafolioItem[]>(`/portafolio-items/trabajador/${id}`),
  agregarPortafolio: (
      id: string | number,
      dto: Record<string, unknown>,
  ): Promise<PortafolioItem> =>
      api.post<PortafolioItem>(`/portafolio-items/trabajador/${id}`, dto),
  eliminarPortafolio: (id: string | number): Promise<unknown> =>
      api.del(`/portafolio-items/${id}`),
  horarios: (id: string | number): Promise<Horario[]> =>
      api.get<Horario[]>(`/horarios/trabajador/${id}`),
  agregarHorario: (id: string | number, dto: Record<string, unknown>): Promise<Horario> =>
      api.post<Horario>(`/horarios/trabajador/${id}`, dto),
  eliminarHorario: (id: string | number): Promise<unknown> =>
      api.del(`/horarios/${id}`),
  preferencias: (id: string | number): Promise<Record<string, unknown>> =>
      api.get<Record<string, unknown>>(`/preferencias/trabajador/${id}`),
  guardarPreferencias: (
      id: string | number,
      dto: Record<string, unknown>,
  ): Promise<unknown> => api.put(`/preferencias/trabajador/${id}`, dto),
};

export const comunidadService = {
  foro: (): Promise<ForoPost[]> => api.get<ForoPost[]>('/foro'),
  publicar: (dto: { titulo: string; contenido: string; autorId?: number; etiquetas?: Array<{ id: number; nombre: string } | string> }): Promise<ForoPost> =>
      api.post<ForoPost>('/foro', dto),
  eliminarPost: (id: string | number): Promise<unknown> => api.del(`/foro/${id}`),
  reportar: (dto: {
    motivo: string;
    descripcion: string;
    reportante: { id: number };
    reportado?: { id: number };
  }): Promise<unknown> => api.post('/reportes', dto),
  valoracionesDe: (id: string | number): Promise<Valoracion[]> =>
      api.get<Valoracion[]>(`/valoraciones/trabajador/${id}`),
  promedio: (id: string | number): Promise<number> =>
      api.get<number>(`/valoraciones/trabajador/${id}/promedio`),
  valorar: (dto: Record<string, unknown>): Promise<Valoracion> =>
      api.post<Valoracion>('/valoraciones', dto),
  favoritosDe: (empleadorId: string | number): Promise<Record<string, unknown>[]> =>
      api.get<Record<string, unknown>[]>(`/favoritos/empleador/${empleadorId}`),
  guardarFavorito: (
      empleadorId: string | number,
      trabajadorId: string | number,
  ): Promise<unknown> =>
      api.post(`/favoritos/empleador/${empleadorId}/trabajador/${trabajadorId}`),
  quitarFavorito: (
      empleadorId: string | number,
      trabajadorId: string | number,
  ): Promise<unknown> =>
      api.del(`/favoritos/empleador/${empleadorId}/trabajador/${trabajadorId}`),
  comunicados: (): Promise<Comunicado[]> => api.get<Comunicado[]>('/comunicados'),
};

export const etiquetasService = {
  listar: (): Promise<Etiqueta[]> => api.get<Etiqueta[]>('/etiquetas'),
};

export const empresaService = {
  perfil: (empleadorId: string | number): Promise<Record<string, unknown>> =>
      api.get<Record<string, unknown>>(`/perfiles-empresariales/empleador/${empleadorId}`),
  guardar: (
      empleadorId: string | number,
      dto: {
        nombreEmpresa: string;
        cuit?: string;
        descripcion?: string;
        sitioWeb?: string;
        [k: string]: unknown;
      },
  ): Promise<unknown> =>
      api.put(`/perfiles-empresariales/empleador/${empleadorId}`, dto),
};

export const publicidadService = {
  listar: (): Promise<Publicidad[]> => api.get<Publicidad[]>('/publicidades'),
  crear: (dto: {
    titulo: string;
    contenido?: string;
    imagenUrl?: string;
    fechaInicio?: string;
    fechaFin?: string;
    anunciante?: string | { id: number };
    [k: string]: unknown;
  }): Promise<Publicidad> => api.post<Publicidad>('/publicidades', dto),
  actualizar: (id: string | number, dto: Record<string, unknown>): Promise<Publicidad> =>
      api.put<Publicidad>(`/publicidades/${id}`, dto),
  eliminar: (id: string | number): Promise<unknown> =>
      api.del(`/publicidades/${id}`),
};

export const suscripcionService = {
  porUsuario: (usuarioId: string | number): Promise<Suscripcion> =>
      api.get<Suscripcion>(`/suscripciones/usuario/${usuarioId}`),
  crear: (dto: Record<string, unknown>): Promise<Suscripcion> =>
      api.post<Suscripcion>('/suscripciones', dto),
  cancelar: (id: string | number): Promise<unknown> =>
      api.put(`/suscripciones/cancelar/${id}`),
  // AL-MOB-002: Aporte tipado con monto, asunto y usuario
  donar: (dto: {
    monto: number;
    asunto: string;
    usuario: { id: number };
    [k: string]: unknown;
  }): Promise<Donacion> =>
      api.post<Donacion>('/donaciones', dto),
  donacionesDe: (usuarioId: string | number): Promise<Donacion[]> =>
      api.get<Donacion[]>(`/donaciones/usuario/${usuarioId}`),
};

export const notificacionService = {
  listar: (usuarioId: string | number): Promise<Notificacion[]> =>
      api.get<Notificacion[]>(`/notificaciones/usuario/${usuarioId}`),
  noLeidas: (usuarioId: string | number): Promise<number> =>
      api
          .get<{ noLeidas: number }>(`/notificaciones/usuario/${usuarioId}/no-leidas`)
          .then((r) => r.noLeidas ?? 0),
  marcarLeida: (id: string | number): Promise<Notificacion> =>
      api.patch<Notificacion>(`/notificaciones/${id}/leer`),
  marcarTodasLeidas: (usuarioId: string | number): Promise<unknown> =>
      api.patch(`/notificaciones/usuario/${usuarioId}/leer-todas`),
};

export const adminService = {
  metrics: (): Promise<DashboardMetrics> =>
      api.get<DashboardMetrics>('/api/admin/dashboard/metrics'),
  usuarios: (): Promise<unknown> => api.get('/usuarios'),
  reportes: (): Promise<Reporte[]> => api.get<Reporte[]>('/reportes'),
  resolverReporte: (
      id: string | number,
      moderadorId: string | number | null | undefined,
      estado: string,
  ): Promise<unknown> => {
    const params = new URLSearchParams();
    if (moderadorId != null && String(moderadorId).trim() !== '' && String(moderadorId) !== 'null') {
      params.append('moderadorId', String(moderadorId));
    }
    if (estado) {
      params.append('estado', estado);
    }
    const query = params.toString();
    return api.put(`/reportes/${id}/resolver${query ? `?${query}` : ''}`);
  },
  donaciones: (): Promise<Donacion[]> => api.get<Donacion[]>('/donaciones'),
  verificarDonacion: (
      id: string | number,
      asunto: string,
      monto: number,
  ): Promise<unknown> =>
      api.post(
          `/donaciones/${id}/verificar?asunto=${encodeURIComponent(asunto)}&monto=${monto}`,
      ),
  crearEtiqueta: (nombre: string): Promise<Etiqueta> =>
      api.post<Etiqueta>('/etiquetas', { nombre }),
  eliminarEtiqueta: (id: string | number): Promise<unknown> =>
      api.del(`/etiquetas/${id}`),
  crearComunicado: (dto: { titulo: string; contenido: string; emisor?: string | { id: number }; [k: string]: unknown }): Promise<Comunicado> =>
      api.post<Comunicado>('/comunicados', dto),
  eliminarComunicado: (id: string | number): Promise<unknown> =>
      api.del(`/comunicados/${id}`),
};

export async function resolveIdByEmail(
    email: string | null,
    role?: Rol,
): Promise<number | null> {
  if (!email) return null;
  const lists: Array<() => Promise<Array<{ id: number; email?: string }>>> =
      role === 'EMPLEADOR'
          ? [() => empleadorService.listar()]
          : role === 'TRABAJADOR'
              ? [() => trabajadorService.listar()]
              : [() => trabajadorService.listar(), () => empleadorService.listar()];
  for (const fn of lists) {
    try {
      const arr = await fn();
      const found = (Array.isArray(arr) ? arr : []).find(
          (u) => u.email?.toLowerCase() === email.toLowerCase(),
      );
      if (found) return found.id;
    } catch {
      // sigue
    }
  }
  return null;
}