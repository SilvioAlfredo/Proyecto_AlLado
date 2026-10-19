'use client';

import { useEffect, type ReactNode } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '@/features/auth/hooks/useAuth';

/**
 * Protege el montaje de las vistas del panel según la sesión y el rol.
 *
 * Sin sesión, redirige al acceso Admin. Otros roles vuelven a su propio panel.
 * Esta protección de interfaz complementa los permisos que valida el backend.
 */
export function AdminAccess({ children }: { children: ReactNode }) {
  const { user, loading } = useAuth();
  const router = useRouter();
  const allowed = user?.roles.includes('ROLE_ADMIN');
  useEffect(() => {
    if (loading || allowed) {
      return;
    }
    router.replace(
      !user
        ? '/acceso-admin'
        : user.roles.includes('ROLE_EMPLEADOR')
          ? '/empleador/dashboard'
          : '/trabajador/dashboard',
    );
  }, [user, loading, allowed, router]);
  if (loading || !allowed) {
    return (
      <p role="status" className="p-6 text-zinc-400">
        Verificando acceso al panel…
      </p>
    );
  }
  return children;
}
