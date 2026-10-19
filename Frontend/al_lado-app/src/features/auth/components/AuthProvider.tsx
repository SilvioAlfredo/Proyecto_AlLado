'use client';
import { useSyncExternalStore, type ReactNode } from 'react';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import { useRouter } from 'next/navigation';
import api from '@/lib/api';
import type { UsuarioDTO } from '@/types/usuario';
import type { AuthRequestDTO, RegisterCredentials } from '@/types/auth';
import { AuthContext, type UserSession } from '../hooks/useAuth';
import { authService } from '../services/authService';
import { loginDestination } from '../lib/access';
const SESSION_EVENT = 'allado-session';
function subscribe(callback: () => void) {
  window.addEventListener('storage', callback);
  window.addEventListener(SESSION_EVENT, callback);
  return () => {
    window.removeEventListener('storage', callback);
    window.removeEventListener(SESSION_EVENT, callback);
  };
}
function snapshot() {
  return JSON.stringify(
    ['auth_token', 'user_email', 'user_roles'].map((key) => localStorage.getItem(key)),
  );
}
function readSession(raw: string): UserSession | null {
  try {
    const [token, email, roles] = JSON.parse(raw);
    const parsed = JSON.parse(roles || '[]');
    return token &&
      email &&
      Array.isArray(parsed) &&
      parsed.every((role) => typeof role === 'string')
      ? { email, roles: parsed }
      : null;
  } catch {
    return null;
  }
}
export function AuthProvider({ children }: { children: ReactNode }) {
  const raw = useSyncExternalStore(subscribe, snapshot, () => '');
  const user = readSession(raw);
  const router = useRouter();
  const queryClient = useQueryClient();
  const identity = useQuery({
    queryKey: ['session-user', user?.email, user?.roles.join(',')],
    enabled: !!user,
    staleTime: Infinity,
    retry: false,
    queryFn: async () => {
      const path = user?.roles.includes('ROLE_ADMIN')
        ? '/administradores'
        : user?.roles.includes('ROLE_EMPLEADOR')
          ? '/empleadores'
          : '/trabajadores';
      const { data } = await api.get<UsuarioDTO[]>(path);
      return (
        data.find((person) => person.email.toLowerCase() === user?.email.toLowerCase())
          ?.id ?? null
      );
    },
  });
  const persist = (data: { token: string; email: string; roles: string[] }) => {
    queryClient.clear();
    localStorage.setItem('auth_token', data.token);
    localStorage.setItem('user_email', data.email);
    localStorage.setItem('user_roles', JSON.stringify(data.roles));
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
    ['auth_token', 'user_email', 'user_roles'].forEach((key) =>
      localStorage.removeItem(key),
    );
    queryClient.clear();
    window.dispatchEvent(new Event(SESSION_EVENT));
    router.replace('/login');
  };
  return (
    <AuthContext.Provider
      value={{
        user,
        userId: identity.data ?? null,
        loading: raw === '' || (!!user && identity.isPending),
        login,
        register,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}
