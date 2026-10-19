'use client';
import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import Link from 'next/link';
import { useEmpleador } from '@/features/empleador/hooks/useEmpleador';
import { publicidadesService } from '@/features/comunidad/services/moderacionService';
import { PageHeader, Tabs, Empty } from '@/components/ui/shared';
import {
  panel,
  QueryStatus,
  Feedback,
  fecha,
} from '@/features/empleador/components/EmployerUI';
import { Button } from '@/components/ui/button';
export default function Page() {
  const { userId } = useEmpleador();
  const query = useQuery({
    queryKey: ['empleador', userId, 'anuncios'],
    enabled: !!userId,
    queryFn: () => publicidadesService.porAnunciante(userId!),
  });
  const [tab, setTab] = useState('TODOS');
  const [deleting, setDeleting] = useState<number | null>(null);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState<unknown>(null);
  const items = (query.data ?? []).filter(
    (a) => tab === 'TODOS' || (tab === 'ACTIVOS' ? a.activa : !a.activa),
  );
  return (
    <div className="space-y-6">
      <PageHeader
        eyebrow="Promoción"
        title="Mis anuncios"
        subtitle="Gestioná las publicaciones que promocionan tu negocio."
        action={
          <Link href="/empleador/anuncios/nuevo">
            <Button>+ Crear anuncio</Button>
          </Link>
        }
      />
      <Tabs
        value={tab}
        onChange={setTab}
        options={[
          { value: 'TODOS', label: 'Todos' },
          { value: 'ACTIVOS', label: 'Activos' },
          { value: 'INACTIVOS', label: 'Inactivos' },
        ]}
      />
      <QueryStatus pending={query.isPending} error={query.error} />
      <Feedback error={error} />
      {!query.isPending && !query.error && !items.length && (
        <Empty mensaje="Sin anuncios en este filtro. Creá uno para promocionar tu negocio." />
      )}
      <div className="space-y-4">
        {items.map((a) => (
          <article key={a.id} className={`${panel} space-y-3`}>
            <div className="flex items-center justify-between gap-3">
              <h2 className="text-xl font-bold">{a.titulo}</h2>
              <span className="text-xs text-[#ffc107]">
                {a.activa ? 'Activo' : 'Inactivo'}
              </span>
            </div>
            <p className="text-sm text-zinc-400">{a.contenido}</p>
            <p className="text-xs text-zinc-500">
              {fecha(a.fechaInicio)} — {fecha(a.fechaFin)}
            </p>
            <div className="flex gap-4">
              <Link
                className="text-sm text-[#ffc107]"
                href={`/empleador/anuncios/${a.id}/editar`}
              >
                Editar anuncio
              </Link>
              <button
                className="text-sm text-red-300"
                onClick={() => {
                  setDeleting(a.id!);
                  setError(null);
                }}
              >
                Eliminar
              </button>
            </div>
            {deleting === a.id && (
              <div className="space-y-3 rounded-xl border border-red-400/30 p-4">
                <p className="text-sm">¿Querés eliminar este anuncio?</p>
                <div className="flex gap-3">
                  <Button
                    variant="outline"
                    disabled={busy}
                    onClick={() => setDeleting(null)}
                  >
                    Cancelar
                  </Button>
                  <Button
                    disabled={busy}
                    onClick={async () => {
                      setBusy(true);
                      try {
                        await publicidadesService.eliminar(a.id!);
                        setDeleting(null);
                        await query.refetch();
                      } catch (e) {
                        setError(e);
                      } finally {
                        setBusy(false);
                      }
                    }}
                  >
                    {busy ? 'Eliminando…' : 'Eliminar anuncio'}
                  </Button>
                </div>
              </div>
            )}
          </article>
        ))}
      </div>
    </div>
  );
}
