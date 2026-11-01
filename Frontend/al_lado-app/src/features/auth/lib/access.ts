/**
 * Normaliza roles del backend para que la app acepte tanto
 * `ROLE_TRABAJADOR` como `TRABAJADOR`.
 */
export function normalizeRoles(roles: string[] = []) {
  return roles.map((rol) => {
    const value = String(rol ?? '').trim();
    if (!value) return value;
    return value.toUpperCase().startsWith('ROLE_') ? value.toUpperCase() : `ROLE_${value.toUpperCase()}`;
  });
}

/**
 * Resuelve el panel de destino y valida el acceso exclusivo de administración.
 *
 * El proveedor de autenticación ejecuta esta comprobación antes de guardar la sesión.
 *
 * @param roles - Roles devueltos por el backend, con o sin prefijo ROLE_.
 * @param adminOnly - Si es true, solo admite cuentas con ROLE_ADMIN.
 * @returns Ruta de destino; ADMIN tiene prioridad cuando hay varios roles.
 * @throws Si se solicita acceso exclusivo y la cuenta no tiene rol ADMIN.
 */
export function loginDestination(roles: string[] = [], adminOnly = false) {
  const normalized = normalizeRoles(roles);

  if (normalized.includes('ROLE_ADMIN')) return '/admin/dashboard';
  if (adminOnly) throw new Error('Esta cuenta no tiene acceso de administración.');
  if (normalized.includes('ROLE_EMPLEADOR')) return '/empleador/dashboard';
  if (normalized.includes('ROLE_TRABAJADOR')) return '/trabajador/dashboard';
  return '/';
}
