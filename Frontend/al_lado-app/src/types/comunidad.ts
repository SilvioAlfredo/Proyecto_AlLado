import { UsuarioResumenDTO } from './usuario';

export interface PerfilEmpresarialDTO {
  id?: number;
  nombreEmpresa: string;
  cuit?: string;
  descripcion?: string;
  sitioWeb?: string;
  urlLogo?: string;
  verificado?: boolean;
  empleador?: UsuarioResumenDTO;
  supervisadoPor?: UsuarioResumenDTO;
}

export interface ComunicadoDTO {
  id?: number;
  titulo: string;
  contenido: string;
  alcance?: string;
  fechaEmision?: string;
  emisor: UsuarioResumenDTO;
}

export interface ValoracionDTO {
  id?: number;
  puntuacion: number;
  comentario?: string;
  fechaCreacion?: string;
  autor: UsuarioResumenDTO;
  trabajador: UsuarioResumenDTO;
}

export interface PublicacionForoDTO {
  id?: number;
  titulo: string;
  contenido: string;
  categoria?: string;
  fechaCreacion?: string;
  autor?: UsuarioResumenDTO;
}