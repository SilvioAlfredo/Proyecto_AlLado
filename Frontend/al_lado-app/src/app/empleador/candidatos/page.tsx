'use client';
import { Suspense, useState } from 'react';
import { useSearchParams } from 'next/navigation';
import Link from 'next/link';
import { useEmpleador } from '@/features/empleador/hooks/useEmpleador';
import { postulacionesService } from '@/features/postulaciones/services/postulacionesService';
import { CandidatosList } from '@/features/postulaciones/components/CandidatosList';
import { PageHeader, Tabs } from '@/components/ui/shared';
import { Feedback, QueryStatus } from '@/features/empleador/components/EmployerUI';
function Content() {
  const params = useSearchParams();
  const id = params.get('oportunidadId');
  const { solicitudes, propuestas, refresh } = useEmpleador();
  const [tab, setTab] = useState('TODOS');
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState<unknown>(null);
  const solicitud = solicitudes.data?.find((o) => o.id === Number(id));
  const items = (propuestas.data ?? []).filter(
    (p) => !id || p.oportunidadLaboral.id === Number(id),
  );
  const filtered = items.filter((p) => tab === 'TODOS' || p.estado === tab);
  async function run(action: () => Promise<unknown>) {
    setBusy(true);
    setError(null);
    try {
      await action();
      await refresh();
    } catch (e) {
      setError(e);
    } finally {
      setBusy(false);
    }
  }
  return (
    <div className="space-y-6">
      <PageHeader
        eyebrow="Propuestas recibidas"
        title="Postulantes"
        subtitle={
          id
            ? (solicitud?.titulo ?? 'Solicitud no encontrada en tu cuenta')
            : 'Todas las propuestas para tus solicitudes'
        }
        action={
          solicitud && (
            <Link
              className="text-sm text-[#ffc107]"
              href={`/empleador/solicitudes/${id}`}
            >
              Ver solicitud →
            </Link>
          )
        }
      />
      <Tabs
        value={tab}
        onChange={setTab}
        options={[
          ['TODOS', 'Todos'],
          ['PENDIENTE', 'Pendientes'],
          ['SELECCIONADA', 'Seleccionados'],
          ['RECHAZADA', 'Rechazados'],
          ['COMPLETADA', 'Completados'],
        ].map(([value, label]) => ({
          value,
          label: `${label} · ${value === 'TODOS' ? items.length : items.filter((p) => p.estado === value).length}`,
        }))}
      />
      <Feedback error={error || solicitudes.error || propuestas.error} />
      {propuestas.isPending && !solicitudes.error ? (
        <QueryStatus pending />
      ) : (
        <CandidatosList
          postulaciones={filtered}
          busy={busy}
          onSeleccionar={(id) => void run(() => postulacionesService.seleccionar(id))}
          onRechazar={(id) => void run(() => postulacionesService.rechazar(id))}
        />
      )}
    </div>
  );
}
export default function Page() {
  return (
    <Suspense fallback={<p>Cargando postulantes…</p>}>
      <Content />
    </Suspense>
  );
}
