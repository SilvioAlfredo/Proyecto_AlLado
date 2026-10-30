'use client';
import { createContext, useContext } from 'react';
import type { AuthRequestDTO, RegisterCredentials } from '@/types/auth';
import type { authService } from '../services/authService';
export interface UserSession { email: string; roles: string[] }
export interface AuthContextValue {
  user: UserSession | null;
  userId: number | null;
  /** Nombre declarado en el registro/login; null si la sesión no lo guardó. */
  nombre: string | null;
  loading: boolean;
  login: (credentials: AuthRequestDTO, options?: { adminOnly?: boolean }) => ReturnType<typeof authService.login>;
  register: (credentials: RegisterCredentials) => ReturnType<typeof authService.register>;
  logout: () => Promise<void>;
}
export const AuthContext = createContext<AuthContextValue | null>(null);
export function useAuth() {
  const value = useContext(AuthContext);
  if (!value) throw new Error('La sesión requiere AuthProvider.');
  return value;
}
