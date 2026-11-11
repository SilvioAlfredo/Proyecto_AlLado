import React from 'react';
import { createContext, useContext, useEffect, useMemo, useState } from 'react';
import type { ReactNode, Dispatch, SetStateAction } from 'react';
import * as SecureStore from 'expo-secure-store';
import { Platform } from 'react-native';
import { authService, resolveIdByEmail } from '../api/services';
import { setToken } from '../api/client';
import type { AuthResponse, JwtRol, Rol } from '../types';
import type { Href } from 'expo-router';

const EMAIL_KEY = 'user_email';
const ROLES_KEY = 'user_roles';
const UID_KEY = 'user_id';

function storageGet(k: string): Promise<string | null> {
  if (Platform.OS === 'web') return Promise.resolve(localStorage.getItem(k));
  return SecureStore.getItemAsync(k);
}

function storageSet(k: string, v: string | null): Promise<void> {
  if (Platform.OS === 'web') {
    if (v == null) localStorage.removeItem(k);
    else localStorage.setItem(k, v);
    return Promise.resolve();
  }
  if (v == null) return SecureStore.deleteItemAsync(k).then(() => undefined);
  return SecureStore.setItemAsync(k, v);
}

export function roleHome(roles: JwtRol[] = []): Href {
  if (roles.includes('ROLE_ADMIN')) return '/admin/dashboard';
  if (roles.includes('ROLE_EMPLEADOR')) return '/empleador';
  if (roles.includes('ROLE_TRABAJADOR')) return '/trabajador/oportunidades';
  return '/(auth)/login';
}

interface RegisterInput {
  nombre: string;
  email: string;
  password: string;
  telefono: string;
  rol: Rol;
}

interface AuthContextValue {
  token: string | null;
  email: string | null;
  roles: JwtRol[];
  userId: number | null;
  loading: boolean;
  isAdmin: boolean;
  isEmpleador: boolean;
  isTrabajador: boolean;
  login: (email: string, password: string) => Promise<AuthResponse & { userId: number | null }>;
  register: (input: RegisterInput) => Promise<AuthResponse>;
  logout: () => Promise<void>;
  setUserId: Dispatch<SetStateAction<number | null>>;
}

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: ReactNode }): React.JSX.Element {
  const [token, setTokenState] = useState<string | null>(null);
  const [email, setEmail] = useState<string | null>(null);
  const [roles, setRoles] = useState<JwtRol[]>([]);
  const [userId, setUserId] = useState<number | null>(null);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    (async () => {
      const [t, e, r, u] = await Promise.all([
        storageGet('auth_token'),
        storageGet(EMAIL_KEY),
        storageGet(ROLES_KEY),
        storageGet(UID_KEY),
      ]);
      let parsed: JwtRol[] = [];
      try {
        parsed = r ? (JSON.parse(r) as JwtRol[]) : [];
      } catch {
        parsed = [];
      }
      setTokenState(t);
      setEmail(e);
      setRoles(parsed);
      setUserId(u ? Number(u) : null);
      setLoading(false);
      // Backfill: sesiones guardadas sin userId (login anterior) lo resuelven
      // en segundo plano para evitar resolveIdByEmail en cada pantalla.
      if (t && e && !u && parsed.length > 0) {
        const simple = parsed[0].replace('ROLE_', '') as Rol;
        resolveIdByEmail(e, simple).then((uid) => {
          if (uid != null) {
            storageSet(UID_KEY, String(uid)).catch(() => undefined);
            setUserId(uid);
          }
        });
      }
    })();
  }, []);

  const persist = async (
    t: string | null,
    e: string | null,
    r: JwtRol[],
    uid: number | null,
  ): Promise<void> => {
    await setToken(t);
    await storageSet(EMAIL_KEY, e ?? null);
    await storageSet(ROLES_KEY, r ? JSON.stringify(r) : null);
    await storageSet(UID_KEY, uid != null ? String(uid) : null);
    setTokenState(t);
    setEmail(e ?? null);
    setRoles(r ?? []);
    setUserId(uid ?? null);
  };

  const login = async (
    emailInput: string,
    password: string,
  ): Promise<AuthResponse & { userId: number | null }> => {
    const res = await authService.login(emailInput, password);
    const simple: Rol[] = res.roles.map((x) => x.replace('ROLE_', '') as Rol);
    const uid = await resolveIdByEmail(res.email, simple[0]);
    await persist(res.token, res.email, res.roles, uid);
    return { ...res, userId: uid };
  };

  const register = async (input: RegisterInput): Promise<AuthResponse> => {
    const res = await authService.register(input);
    const uid = await resolveIdByEmail(res.email, input.rol);
    await persist(res.token, res.email, res.roles, uid);
    return res;
  };

  const logout = async (): Promise<void> => {
    try {
      await authService.logout();
    } catch {
      // best-effort como en la web
    }
    await persist(null, null, [], null);
  };

  const value = useMemo<AuthContextValue>(
    () => ({
      token,
      email,
      roles,
      userId,
      loading,
      isAdmin: roles.includes('ROLE_ADMIN'),
      isEmpleador: roles.includes('ROLE_EMPLEADOR'),
      isTrabajador: roles.includes('ROLE_TRABAJADOR'),
      login,
      register,
      logout,
      setUserId,
    }),
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [token, email, roles, userId, loading],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth debe usarse dentro de AuthProvider');
  return ctx;
}
