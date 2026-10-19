import Constants from 'expo-constants';
import * as SecureStore from 'expo-secure-store';
import { Platform } from 'react-native';

// URL personalizable: EXPO_PUBLIC_API_URL > app.json extra.apiUrl > localhost.
// Para Expo Go en dispositivo físico usar la IP LAN, ej: http://192.168.1.10:8080
function resolveBaseUrl(): string {
  const fromEnv = process.env.EXPO_PUBLIC_API_URL;
  if (fromEnv) return fromEnv.replace(/\/$/, '');
  const fromExtra = (Constants?.expoConfig?.extra as { apiUrl?: string } | undefined)?.apiUrl;
  if (fromExtra) return String(fromExtra).replace(/\/$/, '');
  return 'http://localhost:8080';
}

export const API_URL: string = resolveBaseUrl();

const TOKEN_KEY = 'auth_token';

async function getToken(): Promise<string | null> {
  try {
    if (Platform.OS === 'web') {
      return localStorage.getItem(TOKEN_KEY);
    }
    return await SecureStore.getItemAsync(TOKEN_KEY);
  } catch {
    return null;
  }
}

export async function setToken(token: string | null): Promise<void> {
  if (Platform.OS === 'web') {
    if (token) localStorage.setItem(TOKEN_KEY, token);
    else localStorage.removeItem(TOKEN_KEY);
    return;
  }
  if (token) await SecureStore.setItemAsync(TOKEN_KEY, token);
  else await SecureStore.deleteItemAsync(TOKEN_KEY);
}

export class ApiError extends Error {
  status: number;
  data: unknown;
  constructor(message: string, status: number, data: unknown) {
    super(message);
    this.status = status;
    this.data = data;
  }
}

type HttpOptions = Omit<RequestInit, 'headers' | 'body'> & {
  headers?: Record<string, string>;
  body?: string;
};

export async function apiFetch<T = unknown>(path: string, options: HttpOptions = {}): Promise<T> {
  const token = await getToken();
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...(options.headers ?? {}),
  };
  if (token) headers.Authorization = `Bearer ${token}`;

  const res = await fetch(`${API_URL}${path}`, { ...options, headers });
  if (res.status === 204) return null as T;
  const text = await res.text();
  const data = text ? (JSON.parse(text) as T & { message?: string }) : null;
  if (!res.ok) {
    const message =
      (data as { message?: string } | null)?.message ?? `Error ${res.status}`;
    throw new ApiError(message, res.status, data);
  }
  return data as T;
}

export const api = {  get: <T = unknown>(p: string): Promise<T> => apiFetch<T>(p),
  post: <T = unknown>(p: string, body?: unknown): Promise<T> =>
    apiFetch<T>(p, { method: 'POST', body: JSON.stringify(body ?? {}) }),
  put: <T = unknown>(p: string, body?: unknown): Promise<T> =>
    apiFetch<T>(p, { method: 'PUT', body: JSON.stringify(body ?? {}) }),
  patch: <T = unknown>(p: string, body?: unknown): Promise<T> =>
    apiFetch<T>(p, { method: 'PATCH', body: JSON.stringify(body ?? {}) }),
  del: <T = unknown>(p: string): Promise<T> => apiFetch<T>(p, { method: 'DELETE' }),
};

/** Mensaje legible desde catch (e: unknown). */
export function errMsg(e: unknown): string {
  if (e instanceof ApiError) return e.message;
  if (e instanceof Error) return e.message;
  return String(e);
}

/** Normaliza un param de expo-router (string | string[] | undefined) a string. */
export function param(p: string | string[] | undefined): string {
  return Array.isArray(p) ? (p[0] ?? '') : (p ?? '');
}
