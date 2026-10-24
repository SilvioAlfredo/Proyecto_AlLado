'use client';
import { useSyncExternalStore, type ReactNode } from 'react';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import { useRouter } from 'next/navigation';
import api from '@/lib/api';
import { userIdFromToken } from '@/lib/jwt';
import type { AuthRequestDTO, AuthResponseDTO, RegisterCredentials } from '@/types/auth';
import { AuthContext, type UserSession } from '../hooks/useAuth';
import { authService } from '../services/authService';
import { loginDestination } from '../lib/access';
const SESSION_EVENT = 'allado-session';
// Claves de la sesión completa en el navegador (AL-API-005)
const SESSION_KEYS = ['auth_token', 'user_email', 'user_roles', 'user_id', 'user_nombre'];
// El snapshot incluye user_id para exponer el identificador en el primer render
const SNAPSHOT_KEYS = ['auth_token', 'user_email', 'user_roles', 'user_id'];
function subscribe(callback: () => void) {
  window.addEventListener('storage', callback);
  window.addEventListener(SESSION_EVENT, callback);
  return () => {
    window.removeEventListener('storage', callback);
    window.removeEventListener(SESSION_EVENT, callback);
  };
}
function snapshot() {
  return JSON.stringify(SNAPSHOT_KEYS.map((key) => localStorage.getItem(key)));
}
function readSession(raw: string): { user: UserSession | null; userId: number | null } {
  try {
    const [token, email, roles, userIdGuardado] = JSON.parse(raw);
    const parsed = JSON.parse(roles || '[]');
    const user =
      token && email && Array.isArray(parsed) && parsed.every((role) => typeof role === 'string')
        ? { email, roles: parsed }
        : null;
    // AL-API-005: el id se lee de lo guardado o del claim uid del token, en el
    // navegador; no hace falta esperar a la API para conocerlo
    const guardado =
      userIdGuardado && Number.isFinite(Number(userIdGuardado)) ? Number(userIdGuardado) : null;
    return { user, userId: user ? guardado ?? userIdFromToken(token) : null };
  } catch {
    return { user: null, userId: null };
  }
}
export function AuthProvider({ children }: { children: ReactNode }) {
  const raw = useSyncExternalStore(subscribe, snapshot, () => '');
  const { user, userId: userIdSincronico } = readSession(raw);
  const router = useRouter();
  const queryClient = useQueryClient();
  const identity = useQuery({
    queryKey: ['session-user', user?.email, user?.roles.join(',')],
    enabled: !!user,
    staleTime: Infinity,
    retry: false,
    queryFn: async () => {
      // AL-API-005: el identificador sale de lo guardado en el navegador o del
      // claim uid del token; solo si no está se pide GET /auth/me. Nunca se
      // lista el directorio de usuarios.
      const guardado = localStorage.getItem('user_id');
      if (guardado && Number.isFinite(Number(guardado))) return Number(guardado);
      const uid = userIdFromToken(localStorage.getItem('auth_token'));
      if (uid !== null) {
        localStorage.setItem('user_id', String(uid));
        return uid;
      }
      const { data } = await api.get<{ id: number }>('/auth/me');
      localStorage.setItem('user_id', String(data.id));
      return data.id;
    },
  });
  const persist = (data: AuthResponseDTO) => {
    queryClient.clear();
    localStorage.setItem('auth_token', data.token);
    localStorage.setItem('user_email', data.email);
    localStorage.setItem('user_roles', JSON.stringify(data.roles));
    // Identidad mínima que ya devuelve el backend en login/registro
    const id = data.id ?? userIdFromToken(data.token);
    if (id !== null && id !== undefined) localStorage.setItem('user_id', String(id));
    if (data.nombre) localStorage.setItem('user_nombre', data.nombre);
    window.dispatchEvent(new Event(SESSION_EVENT));
  };
  const login = async (
    credentials: AuthRequestDTO,
    options?: { adminOnly?: boolean },
  ) => {
    const data = await authService.login(credentials);
    loginDestination(data.roles, options?.adminOnly);
    persist(data);
    return data;
  };
  const register = async (credentials: RegisterCredentials) => {
    const data = await authService.register(credentials);
    if (data.token) persist(data);
    return data;
  };
  const logout = async () => {
    await authService.logout();
    SESSION_KEYS.forEach((key) => localStorage.removeItem(key));
    queryClient.clear();
    window.dispatchEvent(new Event(SESSION_EVENT));
    router.replace('/login');
  };
  return (
    <AuthContext.Provider
      value={{
        user,
        userId: userIdSincronico ?? identity.data ?? null,
        loading: raw === '' || (!!user && identity.isPending && userIdSincronico === null),
        login,
        register,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}
