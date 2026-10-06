import api from '@/lib/api';
import { AuthRequestDTO, AuthResponseDTO, RegisterCredentials } from '@/types/auth';

export const authService = {
  async login(credentials: AuthRequestDTO): Promise<AuthResponseDTO> {
    const { data } = await api.post<AuthResponseDTO>('/auth/login', {
      email: credentials.email,
      password: credentials.contrasena, 
    });
    return data;
  },

  async register(credentials: RegisterCredentials): Promise<AuthResponseDTO> {
    const endpoint = credentials.rol === 'TRABAJADOR' ? '/trabajadores' : '/empleadores';
    await api.post(endpoint, {
      nombre: `${credentials.nombre} ${credentials.apellido}`.trim(),
      email: credentials.email,
      password: credentials.contrasena,
    });
    // El registro no devuelve token: iniciamos sesión con las mismas credenciales
    return this.login({ email: credentials.email, contrasena: credentials.contrasena });
  },
};