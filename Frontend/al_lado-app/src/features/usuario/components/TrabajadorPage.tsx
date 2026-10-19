'use client';

import type { ReactNode } from 'react';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { errorMessage } from '@/lib/errors';

export const campoTrabajador =
  'w-full rounded-xl border border-allado-border bg-allado-card2 p-3 text-sm text-white placeholder:text-zinc-500 focus:outline-none focus:ring-2 focus:ring-allado-yellow disabled:opacity-50';
export const panelTrabajador =
  'rounded-2xl border border-allado-border bg-allado-card p-6 shadow-sm';

export function TrabajadorPage({
  titulo,
  children,
}: {
  titulo: string;
  children: ReactNode;
}) {
  return (
    <div className="mx-auto max-w-5xl space-y-6 text-white">
      <h1 className="text-2xl font-bold">{titulo}</h1>
      {children}
    </div>
  );
}

export function EstadoTrabajador({
  consulta,
}: {
  consulta: { isPending: boolean; error: unknown; refetch: () => unknown };
}) {
  const { loading, userId, user } = useAuth();
  if (loading) return <p role="status">Cargando sesión…</p>;
  if (!user?.roles.some((rol) => rol === 'ROLE_TRABAJADOR' || rol === 'TRABAJADOR')) {
    return <p role="alert">Inicia sesión como trabajador para ver esta información.</p>;
  }
  if (!userId)
    return (
      <p role="alert">No se pudo identificar tu usuario. Vuelve a iniciar sesión.</p>
    );
  if (consulta.error)
    return (
      <div role="alert" className="text-red-300">
        <p>{errorMessage(consulta.error)}</p>
        <button
          type="button"
          className="underline"
          onClick={() => void consulta.refetch()}
        >
          Reintentar
        </button>
      </div>
    );
  if (consulta.isPending) return <p role="status">Cargando datos…</p>;
  return null;
}
