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
    // Backend: POST /auth/register {nombre, email, password, telefono, rol} → AuthResponseDTO con token
    const { data } = await api.post<AuthResponseDTO>('/auth/register', {
      nombre: `${credentials.nombre} ${credentials.apellido}`.trim(),
      email: credentials.email,
      password: credentials.contrasena,
      telefono: credentials.telefono,
      rol: credentials.rol,
    });
    return data;
  },

  async forgotPassword(email: string): Promise<void> {
    // Backend: POST /auth/password/forgot {email}
    await api.post('/auth/password/forgot', { email });
  },

  async resetPassword(token: string, newPassword: string): Promise<void> {
    // Backend: POST /auth/password/reset {token, newPassword}
    await api.post('/auth/password/reset', { token, newPassword });
  },

  async logout(): Promise<void> {
    // Backend: POST /auth/logout (revoca el jti). Best-effort: si falla se limpia igual la sesión local
    try {
      await api.post('/auth/logout');
    } catch {
      /* sesión local se limpia igual */
    }
  },

  async verifyEmail(token: string): Promise<void> {
    // Backend: GET /auth/verify-email?token=
    await api.get(`/auth/verify-email?token=${encodeURIComponent(token)}`);
  },

  async changePassword(email: string, currentPassword: string, newPassword: string): Promise<void> {
    // Backend: POST /auth/password {email, currentPassword, newPassword} (autenticado)
    await api.post('/auth/password', { email, currentPassword, newPassword });
  },
};