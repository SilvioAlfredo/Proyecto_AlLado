import { UsuarioResumenDTO } from './usuario';

export interface EtiquetaDTO {
  id?: number;
  nombre: string;
}

export interface OportunidadLaboralResumenDTO {
  id: number;
  titulo: string;
  contenido: string;
  ubicacion: string;
  modalidad: string;
  rangoSalarial: string;
  activa: boolean;
  fechaCreacion: string;
}

export interface PostulacionResumenDTO {
  id: number;
  titulo: string;
  estado: string;
  remuneracionPretendida: number;
  fechaCreacion: string;
  autor: UsuarioResumenDTO;
}

export interface OportunidadLaboralDTO {
  id?: number;
  titulo: string;
  contenido: string;
  fechaCreacion?: string;
  autor?: UsuarioResumenDTO;
  ubicacion: string;
  modalidad: string;
  rangoSalarial: string;
  activa: boolean;
  destacada?: boolean;
  etiquetas?: EtiquetaDTO[];
  postulaciones?: PostulacionResumenDTO[];
}

export interface PostulacionDTO {
  id?: number;
  titulo: string;
  contenido: string;
  fechaCreacion?: string;
  autor?: UsuarioResumenDTO;
  // Backend: PENDIENTE | SELECCIONADA | RECHAZADA | COMPLETADA | RETIRADA
  estado: 'PENDIENTE' | 'SELECCIONADA' | 'RECHAZADA' | 'COMPLETADA' | 'RETIRADA' | string;
  remuneracionPretendida: number;
  oportunidadLaboral: OportunidadLaboralResumenDTO;
}

/**
 * Registro del historial de contratos (HU-08).
 * Se construye a partir de una postulación que ha avanzado a estados de ejecución.
 */
export interface ContratoDTO {
  id: number;
  estado: string;
  remuneracionPretendida?: number;
  fechaCreacion?: string;
  trabajador?: UsuarioResumenDTO;
  empleador?: UsuarioResumenDTO;
  oportunidad?: OportunidadLaboralResumenDTO;
}