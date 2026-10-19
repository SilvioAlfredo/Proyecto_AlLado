'use client';
import { useState } from 'react';
import Link from 'next/link';
import { useEmpleador } from '@/features/empleador/hooks/useEmpleador';
import { favoritosService } from '@/features/comunidad/services/favoritosService';
import { PageHeader, Avatar, Empty } from '@/components/ui/shared';
import { Button } from '@/components/ui/button';
import { Feedback, QueryStatus, panel } from '@/features/empleador/components/EmployerUI';
export default function Page() {
  const { userId, favoritos } = useEmpleador();
  const [busy, setBusy] = useState<number | null>(null);
  const [error, setError] = useState<unknown>(null);
  return (
    <div className="space-y-6">
      <PageHeader
        eyebrow="Favoritos"
        title="Tu gente de confianza."
        subtitle="Volvé a encontrar a los profesionales que guardaste."
        action={
          <Link href="/empleador/trabajadores">
            <Button>Explorar trabajadores</Button>
          </Link>
        }
      />
      <Feedback error={error} />
      <QueryStatus pending={favoritos.isPending} error={favoritos.error} />
      {favoritos.data && (
        <p className="text-sm text-zinc-400">
          {favoritos.data.length} profesionales guardados
        </p>
      )}
      {favoritos.data?.length === 0 && (
        <Empty mensaje="Guardá profesionales desde la búsqueda o su perfil para encontrarlos acá." />
      )}
      <div className="grid gap-4 lg:grid-cols-2">
        {favoritos.data?.map((f) => (
          <article key={f.id ?? f.trabajador?.id} className={`${panel} space-y-4`}>
            <div className="flex items-center gap-3">
              <Avatar nombre={f.trabajador?.nombre ?? 'Trabajador'} />
              <h2 className="font-bold">{f.trabajador?.nombre}</h2>
            </div>
            <div className="flex justify-between gap-4">
              <Link
                href={`/empleador/trabajadores/${f.trabajador?.id}`}
                className="text-sm text-[#ffc107]"
              >
                Ver perfil →
              </Link>
              <button
                disabled={busy !== null}
                className="text-sm text-zinc-400 disabled:opacity-50"
                onClick={async () => {
                  if (!f.trabajador) return;
                  setBusy(f.trabajador.id);
                  setError(null);
                  try {
                    await favoritosService.quitar(userId!, f.trabajador.id);
                    await favoritos.refetch();
                  } catch (e) {
                    setError(e);
                  } finally {
                    setBusy(null);
                  }
                }}
              >
                {busy === f.trabajador?.id ? 'Quitando…' : 'Quitar de favoritos'}
              </button>
            </div>
          </article>
        ))}
      </div>
    </div>
  );
}
