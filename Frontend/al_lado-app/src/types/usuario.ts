import {
  PostulacionResumenDTO,
  OportunidadLaboralResumenDTO,
  EtiquetaDTO,
} from './trabajo';
import { PerfilEmpresarialDTO } from './comunidad';

export type RolUsuario = 'TRABAJADOR' | 'EMPLEADOR' | 'ADMIN';

export interface UsuarioResumenDTO {
  id: number;
  nombre: string;
  email: string;
}

export interface UsuarioDTO {
  id: number;
  nombre: string;
  email: string;
  telefono?: string;
  password?: string;
  habilitado?: boolean;
}

export interface PortafolioItemDTO {
  id?: number;
  titulo: string;
  descripcion?: string;
  urlArchivo?: string;
}

export interface HorarioDisponibilidadDTO {
  id?: number;
  diaSemana: string;
  horaInicio: string;
  horaFin: string;
}

export interface PreferenciaNotificacionDTO {
  id?: number;
  notificarEmail: boolean;
  notificarPush: boolean;
  alertasNuevasOfertas?: boolean;
  alertasNuevasPostulaciones?: boolean;
}

export interface EstadisticasTrabajadorDTO {
  id: number;
  nombre: string;
  email: string;

  // Métricas financieras
  ingresosTotales: number;
  ingresosUltimos3Meses: number;
  valoracionesCount: number;
  promedioValoraciones: number; // 1-5 estrellas

  // Métricas de actividad
  ofertasPublicadas: number;
  postulacionesEnviadas: number;
  postulacionesSeleccionadas: number;
  postulacionesCompletadas: number;

  // Métricas de perfil
  nivelRecomendacion: number; // 1-5
  perfilCompleto: boolean;

  // Metadatos
  ultimoLogin: string | null;
  suscripcionActiva: boolean;
}

export interface TrabajadorDTO extends UsuarioDTO {
  sector?: string;
  fotografia?: string;
  biografia?: string;
  certificados?: string;
  postulaciones?: PostulacionResumenDTO[];
  especialidades?: EtiquetaDTO[];
  portafolioItems?: PortafolioItemDTO[];
  horarios?: HorarioDisponibilidadDTO[];
  preferenciaNotificacion?: PreferenciaNotificacionDTO;
}

export interface EmpleadorDTO extends UsuarioDTO {
  perfilEmpresarial?: PerfilEmpresarialDTO;
  oportunidades?: OportunidadLaboralResumenDTO[];
}