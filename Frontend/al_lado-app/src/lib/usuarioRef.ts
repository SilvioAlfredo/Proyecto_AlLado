import { UsuarioResumenDTO } from '@/types/usuario';

// AL-API-005: la referencia {id, nombre, email} del usuario en sesión sale de lo
// guardado en el navegador (login/registro o rehidratación con GET /auth/me),
// nunca de listar el directorio de usuarios y comparando emails.
export function obtenerRefSesion(): UsuarioResumenDTO | null {
  if (typeof window === 'undefined') return null;
  const email = localStorage.getItem('user_email');
  if (!email) return null;
  const idGuardado = localStorage.getItem('user_id');
  // Sesión anterior a AL-API-005 sin id: queda en 0 hasta rehidratarlo
  const id = idGuardado && Number.isFinite(Number(idGuardado)) ? Number(idGuardado) : 0;
  return { id, nombre: localStorage.getItem('user_nombre') ?? '', email };
}

export function emailSesion(): string | null {
  if (typeof window === 'undefined') return null;
  return localStorage.getItem('user_email');
}
