import api from '@/lib/api';
import { TrabajadorDTO, EmpleadorDTO, UsuarioResumenDTO } from '@/types/usuario';

// Resuelve {id, nombre, email} del usuario en sesión buscando por email.
// El backend exige autor/usuario/emisor/reportante/anunciante @NotNull en los POST,
// pero el JWT solo guarda email/roles: hay que resolver el id contra cada tabla
// (trabajadores, empleadores y administradores, según el rol en sesión).
export async function obtenerRefPorEmail(email: string): Promise<UsuarioResumenDTO | null> {
  const mail = email.toLowerCase();
  try {
    const { data } = await api.get<TrabajadorDTO[]>('/trabajadores');
    const t = data.find((u) => u.email?.toLowerCase() === mail);
    if (t?.id) return { id: t.id, nombre: t.nombre, email: t.email };
  } catch {
    /* sigue a empleadores */
  }
  try {
    const { data } = await api.get<EmpleadorDTO[]>('/empleadores');
    const e = data.find((u) => u.email?.toLowerCase() === mail);
    if (e?.id) return { id: e.id, nombre: e.nombre, email: e.email };
  } catch {
    /* sigue a administradores */
  }
  try {
    const { data } = await api.get<UsuarioResumenDTO[]>('/administradores');
    const a = data.find((u) => u.email?.toLowerCase() === mail);
    if (a?.id) return { id: a.id, nombre: a.nombre, email: a.email };
  } catch {
    return null;
  }
  return null;
}

export function emailSesion(): string | null {
  if (typeof window === 'undefined') return null;
  return localStorage.getItem('user_email');
}
