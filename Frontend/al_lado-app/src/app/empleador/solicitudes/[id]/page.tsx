'use client';
import { useState } from 'react';
import { useParams } from 'next/navigation';
import Link from 'next/link';
import { useEmpleador } from '@/features/empleador/hooks/useEmpleador';
import { oportunidadesService } from '@/features/oportunidades/services/oportunidadService';
import { postulacionesService } from '@/features/postulaciones/services/postulacionesService';
import { PageHeader, Note } from '@/components/ui/shared';
import {
  panel,
  Feedback,
  QueryStatus,
  fecha,
} from '@/features/empleador/components/EmployerUI';
import { Button } from '@/components/ui/button';
export default function Page() {
  const { id } = useParams<{ id: string }>();
  const { solicitudes, propuestas, refresh } = useEmpleador();
  const [error, setError] = useState<unknown>(null);
  const [busy, setBusy] = useState(false);
  if (solicitudes.isPending || solicitudes.error)
    return <QueryStatus pending={solicitudes.isPending} error={solicitudes.error} />;
  const o = solicitudes.data?.find((o) => o.id === Number(id));
  if (!o) return <p role="alert">No encontramos esta solicitud en tu cuenta.</p>;
  const posts = (propuestas.data ?? []).filter((p) => p.oportunidadLaboral.id === o.id);
  const selected = posts.filter((p) => ['SELECCIONADA', 'COMPLETADA'].includes(p.estado));
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
        eyebrow="Gestión de solicitud"
        title={o.titulo}
        subtitle={`Publicada ${fecha(o.fechaCreacion)} · ${o.ubicacion}`}
        action={
          <Link href={`/empleador/solicitudes/${o.id}/editar`}>
            <Button variant="outline">Editar solicitud</Button>
          </Link>
        }
      />
      <Feedback error={error} />
      <div className="grid gap-5 lg:grid-cols-[1fr_270px]">
        <div className="space-y-5">
          <section className={`${panel} space-y-4`}>
            <span className="text-sm font-bold text-[#ffc107]">
              {o.activa ? 'Activa' : 'Cerrada'}
            </span>
            <h2 className="font-bold">El trabajo</h2>
            <p className="whitespace-pre-wrap text-sm text-zinc-300">{o.contenido}</p>
            <div className="grid grid-cols-2 gap-4 text-sm">
              <p className="text-zinc-400">
                Ubicación
                <br />
                <span className="text-white">{o.ubicacion}</span>
              </p>
              <p className="text-zinc-400">
                Modalidad
                <br />
                <span className="text-white">{o.modalidad}</span>
              </p>
              <p className="text-zinc-400">
                Presupuesto
                <br />
                <span className="text-[#ffc107]">{o.rangoSalarial}</span>
              </p>
              <p className="text-zinc-400">
                Especialidades
                <br />
                <span className="text-white">
                  {o.etiquetas?.map((t) => t.nombre).join(' · ')}
                </span>
              </p>
            </div>
          </section>
          <QueryStatus pending={propuestas.isPending} error={propuestas.error} />
          {selected.map((p) => (
            <section key={p.id} className={`${panel} space-y-3`}>
              <h2 className="font-bold">Profesional seleccionado</h2>
              <Link
                className="text-[#ffc107]"
                href={`/empleador/trabajadores/${p.autor?.id}`}
              >
                {p.autor?.nombre} →
              </Link>
              <p className="text-sm text-zinc-400">{p.contenido}</p>
              <p>${p.remuneracionPretendida.toLocaleString('es-AR')} ARS</p>
              {p.estado === 'SELECCIONADA' ? (
                <Button
                  disabled={busy}
                  onClick={() => void run(() => postulacionesService.completar(p.id!))}
                >
                  Marcar como completado
                </Button>
              ) : (
                <Link href={`/empleador/valoraciones?trabajadorId=${p.autor?.id}`}>
                  <Button>Valorar trabajo</Button>
                </Link>
              )}
            </section>
          ))}
          <Note>
            Podés seleccionar más de una propuesta. Cerrar la solicitud evita nuevas
            postulaciones.
          </Note>
        </div>
        <aside className={`${panel} h-fit space-y-4`}>
          <h2 className="font-bold">Propuestas recibidas</h2>
          <p className="text-4xl font-bold">{posts.length}</p>
          <p className="text-sm text-zinc-400">
            {posts.filter((p) => p.estado === 'PENDIENTE').length} pendientes
          </p>
          <Link href={`/empleador/candidatos?oportunidadId=${o.id}`}>
            <Button className="w-full">Ver postulantes</Button>
          </Link>
          <hr className="border-[#30302f]" />
          <h3 className="font-bold">Estado de la publicación</h3>
          <p className="text-sm text-zinc-400">
            {o.activa
              ? 'Tu solicitud está recibiendo propuestas.'
              : 'Tu solicitud está cerrada.'}
          </p>
          <Button
            disabled={busy}
            variant="outline"
            className="w-full"
            onClick={() =>
              void run(() =>
                oportunidadesService.actualizar(o.id!, { ...o, activa: !o.activa }),
              )
            }
          >
            {o.activa ? 'Cerrar solicitud' : 'Reactivar solicitud'}
          </Button>
        </aside>
      </div>
    </div>
  );
}
