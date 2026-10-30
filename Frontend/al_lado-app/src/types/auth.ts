import { RolUsuario } from './usuario';

export interface AuthRequestDTO {
  email: string;
  contrasena: string;
}

export interface AuthResponseDTO {
  token: string;
  email: string;
  roles: string[];
  // AL-API-005: el backend ya devuelve la identidad mínima en login/registro
  id?: number;
  nombre?: string;
}

export interface RegisterCredentials {
  nombre: string;
  apellido: string;
  email: string;
  contrasena: string;
  telefono: string;
  rol: RolUsuario;
}