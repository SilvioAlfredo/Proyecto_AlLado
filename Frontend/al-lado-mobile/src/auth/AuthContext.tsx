import React from 'react';
import { createContext, useContext, useEffect, useMemo, useState } from 'react';
import type { ReactNode, Dispatch, SetStateAction } from 'react';
import * as SecureStore from 'expo-secure-store';
import { Platform } from 'react-native';
import { authService } from '../api/services';
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

/** Decodifica base64url a UTF-8 (payload del JWT). */
function decodificarBase64Url(texto: string): string {
  const normalizado = texto.replace(/-/g, '+').replace(/_/g, '/');
  const completo = normalizado + '='.repeat((4 - (normalizado.length % 4)) % 4);
  if (typeof atob === 'function') {
    // atob devuelve binario: se reinterpreta como UTF-8.
    return decodeURIComponent(
      atob(completo)
        .split('')
        .map((c) => `%${c.charCodeAt(0).toString(16).padStart(2, '0')}`)
        .join(''),
    );
  }
  // Fallback para runtimes sin atob (p. ej. Node en los tests).
  return Buffer.from(completo, 'base64').toString('utf8');
}

/**
 * Lee el claim `uid` del token de sesión (AL-API-005).
 * Devuelve null si el token es inválido o no declara uid.
 */
export function userIdFromToken(token: string | null): number | null {
  if (!token) return null;
  const partes = token.split('.');
  if (partes.length < 2) return null;
  try {
    const payload = JSON.parse(decodificarBase64Url(partes[1])) as { uid?: unknown };
    if (payload?.uid == null) return null;
    const uid = Number(payload.uid);
    return Number.isFinite(uid) ? uid : null;
  } catch {
    return null;
  }
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
      // Backfill (AL-API-005): sesiones guardadas sin user_id resuelven el
      // identificador desde el claim `uid` del token; si el token no lo trae,
      // con GET /auth/me. Nunca listando el directorio de usuarios.
      if (t && !u) {
        const uid = userIdFromToken(t);
        if (uid != null) {
          storageSet(UID_KEY, String(uid)).catch(() => undefined);
          setUserId(uid);
        } else {
          authService
            .me()
            .then(async (identidad) => {
              if (identidad?.id == null) return;
              await storageSet(UID_KEY, String(identidad.id)).catch(() => undefined);
              setUserId(identidad.id);
            })
            .catch(() => undefined);
        }
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

  /** Identidad mínima: el id llega con el login/registro o del claim `uid`;
   *  si tampoco está, se rehidrata con GET /auth/me (AL-API-005). */
  const resolverIdDeSesion = async (res: AuthResponse): Promise<number | null> => {
    if (res.id != null) return res.id;
    const uid = userIdFromToken(res.token);
    if (uid != null) return uid;
    try {
      const identidad = await authService.me();
      return identidad?.id ?? null;
    } catch {
      return null;
    }
  };

  const login = async (
    emailInput: string,
    password: string,
  ): Promise<AuthResponse & { userId: number | null }> => {
    const res = await authService.login(emailInput, password);
    const uid = await resolverIdDeSesion(res);
    await persist(res.token, res.email, res.roles, uid);
    return { ...res, userId: uid };
  };

  const register = async (input: RegisterInput): Promise<AuthResponse> => {
    const res = await authService.register(input);
    const uid = await resolverIdDeSesion(res);
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
