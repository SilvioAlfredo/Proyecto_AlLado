import { RolUsuario } from './usuario';

export interface AuthRequestDTO {
  email: string;
  contrasena: string;
}

export interface AuthResponseDTO {
  token: string;
  email: string;
  roles: string[];
}

export interface RegisterCredentials {
  nombre: string;
  apellido: string;
  email: string;
  contrasena: string;
  rol: RolUsuario;
}