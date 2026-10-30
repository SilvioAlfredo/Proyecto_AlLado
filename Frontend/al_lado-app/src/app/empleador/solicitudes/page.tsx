'use client';
import { useState } from 'react';
import Link from 'next/link';
import { useEmpleador } from '@/features/empleador/hooks/useEmpleador';
import { oportunidadesService } from '@/features/oportunidades/services/oportunidadService';
import {
  panel,
  QueryStatus,
  Feedback,
  fecha,
} from '@/features/empleador/components/EmployerUI';
import { PageHeader, Tabs, Empty } from '@/components/ui/shared';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import type { OportunidadLaboralDTO } from '@/types/trabajo';
export default function MisSolicitudesPage() {
  const { solicitudes, refresh } = useEmpleador();
  const [tab, setTab] = useState('TODAS');
  const [busy, setBusy] = useState<number | null>(null);
  const [error, setError] = useState<unknown>(null);
  const items = solicitudes.data ?? [];
  const activas = items.filter((o) => o.activa).length;
  const cerradas = items.length - activas;
  const propuestasRecibidas = items.reduce((n, o) => n + (o.postulaciones?.length ?? 0), 0);
  const accionPrioritaria =
    activas > 0
      ? `Tenés ${activas} solicitud${activas === 1 ? '' : 'es'} activa${activas === 1 ? '' : 's'} lista${activas === 1 ? '' : 's'} para revisar.`
      : 'Todavía no tenés solicitudes activas. Publicá una para empezar a recibir propuestas.';
  const filtered = items.filter(
    (o) => tab === 'TODAS' || (tab === 'ACTIVAS' ? o.activa : !o.activa),
  );
  async function toggle(o: OportunidadLaboralDTO) {
    setBusy(o.id!);
    setError(null);
    try {
      await oportunidadesService.actualizar(o.id!, { ...o, activa: !o.activa });
      await refresh();
    } catch (e) {
      setError(e);
    } finally {
      setBusy(null);
    }
  }
  return (
    <div className="space-y-6">
      <PageHeader
        eyebrow="Tus publicaciones"
        title="Mis solicitudes"
        subtitle="Administrá tus publicaciones y las propuestas que recibiste."
        action={
          <Link href="/empleador/solicitudes/nueva">
            <Button>+ Nueva solicitud</Button>
          </Link>
        }
      />
      <section className={`${panel} border-l-4 border-l-[#ffc107]`}>
        <p className="text-xs font-semibold uppercase tracking-[0.2em] text-[#ffc107]">
          Qué revisar hoy
        </p>
        <div className="mt-3 flex flex-wrap items-end justify-between gap-3">
          <div>
            <p className="text-2xl font-black text-white">{items.length}</p>
            <p className="text-sm text-zinc-400">solicitudes publicadas</p>
          </div>
          <span className="rounded-full bg-[#ffc107]/10 px-2.5 py-1 text-xs font-bold text-[#ffc107]">
            {activas} activas / {cerradas} cerradas
          </span>
        </div>
        <p className="mt-3 text-sm text-zinc-300">{accionPrioritaria}</p>
      </section>
      <Tabs
        value={tab}
        onChange={setTab}
        options={[
          { value: 'TODAS', label: `Todas · ${items.length}` },
          {
            value: 'ACTIVAS',
            label: `Activas · ${activas}`,
          },
          {
            value: 'CERRADAS',
            label: `Cerradas · ${cerradas}`,
          },
        ]}
      />
      <QueryStatus pending={solicitudes.isPending} error={solicitudes.error} />
      <Feedback error={error} />
      <div className="grid gap-5 xl:grid-cols-[1fr_250px]">
        <section className="space-y-4">
          {!solicitudes.isPending && !solicitudes.error && !filtered.length && (
            <Empty mensaje="Sin solicitudes en este filtro." />
          )}
          {filtered.map((o) => (
            <article key={o.id} className={panel}>
              <div className="flex justify-between gap-3">
                <h2 className="font-bold">{o.titulo}</h2>
                <Badge variant={o.activa ? 'warning' : 'default'}>
                  {o.activa ? 'Activa' : 'Cerrada'}
                </Badge>
              </div>
              <p className="mt-2 text-xs text-zinc-500">
                {o.etiquetas?.map((e) => e.nombre).join(' · ')} · {fecha(o.fechaCreacion)}{' '}
                · {o.ubicacion}
              </p>
              <p className="my-3 text-sm text-zinc-400">{o.contenido}</p>
              <p className="mb-4 text-xs text-zinc-400">
                {o.postulaciones?.length ?? 0} postulantes
              </p>
              <div className="flex flex-wrap items-center gap-3">
                <Link href={`/empleador/candidatos?oportunidadId=${o.id}`}>
                  <Button size="sm">Ver postulantes</Button>
                </Link>
                <Link
                  className="text-sm text-zinc-300"
                  href={`/empleador/solicitudes/${o.id}`}
                >
                  Gestionar
                </Link>
                <Link
                  className="text-sm text-zinc-300"
                  href={`/empleador/solicitudes/${o.id}/editar`}
                >
                  Editar
                </Link>
                <button
                  disabled={busy !== null}
                  onClick={() => void toggle(o)}
                  className="text-sm text-[#ffc107] disabled:opacity-50"
                >
                  {busy === o.id
                    ? 'Guardando…'
                    : o.activa
                      ? 'Cerrar solicitud'
                      : 'Reactivar solicitud'}
                </button>
              </div>
            </article>
          ))}
        </section>
        <aside className={`${panel} h-fit space-y-4`}>
          <h2 className="font-bold">Tu actividad</h2>
          <p className="text-sm text-zinc-400">{items.length} solicitudes publicadas</p>
          <p className="text-sm text-zinc-400">{propuestasRecibidas} propuestas recibidas</p>
          <hr className="border-[#30302f]" />
          <h3 className="font-bold">Vos administrás</h3>
          <p className="text-sm text-zinc-400">
            Editá tus solicitudes, revisá perfiles y elegí las propuestas que mejor se
            adapten a tu trabajo.
          </p>
        </aside>
      </div>
    </div>
  );
}
