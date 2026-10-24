// Tipos espejo de los DTOs del backend Spring Boot + frontend web.
export type Rol = 'TRABAJADOR' | 'EMPLEADOR' | 'ADMIN';
export type JwtRol = 'ROLE_TRABAJADOR' | 'ROLE_EMPLEADOR' | 'ROLE_ADMIN';

export interface AuthResponse {
  token: string;
  id: number;
  nombre?: string;
  email: string;
  roles: JwtRol[];
}

/** Identidad mínima de la sesión (AL-API-005): GET /auth/me. */
export interface Identidad {
  id: number;
  nombre?: string;
  email: string;
  roles: JwtRol[];
}

export interface IdRef {
  id: number;
}

export interface UsuarioRef extends IdRef {
  nombre?: string;
  email?: string;
}

export interface Oportunidad {
  id: number;
  titulo: string;
  contenido: string;
  ubicacion?: string;
  modalidad?: 'PRESENCIAL' | 'REMOTO' | 'HIBRIDO';
  rangoSalarial?: string;
  activa: boolean;
  destacada?: boolean;
  fechaCreacion?: string;
  etiquetas?: Array<{ id?: number; nombre: string } | string>;
  autor?: UsuarioRef;
}

export type EstadoPostulacion = 'PENDIENTE' | 'SELECCIONADA' | 'RECHAZADA' | 'COMPLETADA' | 'RETIRADA';

export interface Postulacion {
  id: number;
  titulo: string;
  contenido: string;
  estado: EstadoPostulacion;
  remuneracionPretendida?: number;
  oportunidadLaboral?: IdRef;
  autor?: UsuarioRef;
  trabajador?: UsuarioRef;
}

export interface Trabajador {
  id: number;
  nombre: string;
  email: string;
  telefono?: string;
  biografia?: string;
  especialidades?: string[];
}

export interface Empleador {
  id: number;
  nombre: string;
  email: string;
  telefono?: string;
}

export interface Etiqueta {
  id?: number;
  nombre: string;
}

export interface Horario {
  id: number;
  diaSemana: string;
  horaInicio: string;
  horaFin: string;
}

export interface PortafolioItem {
  id: number;
  titulo: string;
  descripcion?: string;
  urlArchivo?: string;
}

export interface Valoracion {
  id: number;
  puntuacion: number;
  comentario?: string;
  autor?: UsuarioRef;
}

export interface ForoPost {
  id: number;
  titulo: string;
  contenido: string;
  autor?: UsuarioRef;
}

export interface Donacion {
  id: number;
  monto: number;
  estado: string;
  asunto?: string;
  usuario?: UsuarioRef;
}

export interface Suscripcion {
  id: number;
  nivel: string;
  activa: boolean;
  fechaInicio?: string;
  fechaFin?: string;
}

export interface Comunicado {
  id: number;
  titulo: string;
  contenido?: string;
}

export interface Publicidad {
  id: number;
  titulo: string;
  contenido?: string;
  urlImagen?: string;
  urlDestino?: string;
  anunciante?: UsuarioRef;
}

export interface Reporte {
  id: number;
  motivo?: string;
  titulo?: string;
  descripcion?: string;
  contenido?: string;
  estado?: string;
}

export interface DashboardMetrics {
  totalIngresos?: string;
  metricContratos?: number;
  metricTrabajadores?: number;
  metricAvg?: string | number;
  dineroComunidad?: string;
  trabajosCompletados?: number;
  tendencias?: Array<{ periodo?: string; valor?: number }>;
  usuariosActivos?: number;
  usuariosInactivos?: number;
  postulacionesPorEstado?: Record<string, number>;
}

export type TipoNotificacion = 'POSTULACION_RECIBIDA' | 'POSTULACION_ESTADO' | 'SISTEMA' | string;

export interface Notificacion {
  id?: number;
  destinatario?: UsuarioRef;
  titulo: string;
  mensaje: string;
  tipo?: TipoNotificacion;
  referenciaId?: number;
  leida?: boolean;
  fechaCreacion?: string;
}
