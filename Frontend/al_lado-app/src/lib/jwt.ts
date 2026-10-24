/**
 * Lectura del payload de un JWT (AL-API-005).
 *
 * <p>No verifica la firma: eso lo hace el backend. Solo se usa para recuperar el
 * identificador del usuario ({@code uid}) de una sesión ya guardada, sin tener que
 * listar usuarios para averiguarlo.</p>
 */

interface JwtPayload {
  uid?: number | string;
  email?: string;
  roles?: string[];
  [key: string]: unknown;
}

/**
 * Decodifica el payload de un JWT.
 *
 * @param token token completo (header.payload.firma)
 * @returns el payload, o `null` si el token no es un JWT legible
 */
export function decodeJwtPayload(token: string | null | undefined): JwtPayload | null {
  if (!token) return null;
  try {
    const partes = token.split('.');
    if (partes.length < 2) return null;
    const base64 = partes[1].replace(/-/g, '+').replace(/_/g, '/');
    const relleno = base64.length % 4 === 0 ? '' : '='.repeat(4 - (base64.length % 4));
    const json =
      typeof atob === 'function'
        ? atob(base64 + relleno)
        : Buffer.from(base64 + relleno, 'base64').toString('utf-8');
    return JSON.parse(json) as JwtPayload;
  } catch {
    return null;
  }
}

/**
 * Identificador del usuario dueño de un token, si el token lo declara.
 *
 * @param token token completo
 * @returns el identificador, o `null`
 */
export function userIdFromToken(token: string | null | undefined): number | null {
  const payload = decodeJwtPayload(token);
  const uid = payload?.uid;
  if (typeof uid === 'number' && Number.isFinite(uid)) return uid;
  if (typeof uid === 'string' && uid.trim() !== '') {
    const numerico = Number(uid);
    return Number.isFinite(numerico) ? numerico : null;
  }
  return null;
}
