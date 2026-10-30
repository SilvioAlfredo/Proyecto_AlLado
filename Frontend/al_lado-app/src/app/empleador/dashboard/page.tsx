'use client';
import Link from 'next/link';
import { Zap, Wrench, PaintRoller, Leaf } from 'lucide-react';
import { useEmpleador } from '@/features/empleador/hooks/useEmpleador';
import { panel, QueryStatus } from '@/features/empleador/components/EmployerUI';
import { PageHeader, StatCard, Empty } from '@/components/ui/shared';
import { Button } from '@/components/ui/button';
export default function EmpleadorDashboardPage() {
  const { perfil, solicitudes, propuestas, favoritos } = useEmpleador();
  const items = solicitudes.data ?? [];
  const posts = propuestas.data ?? [];
  const activas = items.filter((o) => o.activa);
  const curso = posts.filter((p) => p.estado === 'SELECCIONADA');
  const completas = posts.filter((p) => p.estado === 'COMPLETADA');
  const pendientes = posts.filter((p) => p.estado === 'PENDIENTE');
  const pendientesAccion = [
    ...(pendientes.length > 0
      ? [`Hay ${pendientes.length} propuesta${pendientes.length > 1 ? 's' : ''} por revisar.`]
      : []),
    ...(activas.length > 0
      ? [`Tienes ${activas.length} solicitud${activas.length > 1 ? 'es' : ''} activa${activas.length > 1 ? 's' : ''}.`]
      : []),
    ...(curso.length > 0
      ? [`Trabajos en curso: ${curso.length}.`]
      : []),
  ];
  return (
    <div className="space-y-6">
      <PageHeader
        eyebrow="Tu espacio · Empleador"
        title={`Hola${perfil.data?.nombre ? `, ${perfil.data.nombre.split(' ')[0]}` : ''} 👋`}
        subtitle="¿Qué necesitás resolver hoy?"
        action={
          <Link href="/empleador/trabajadores">
            <Button variant="outline">Buscar trabajadores</Button>
          </Link>
        }
      />
      <QueryStatus
        pending={solicitudes.isPending || propuestas.isPending}
        error={solicitudes.error || propuestas.error || favoritos.error}
      />
      <section className={`${panel} border-l-4 border-l-[#ffc107]`}>
        <p className="text-xs font-semibold uppercase tracking-[0.2em] text-[#ffc107]">
          Qué revisar hoy
        </p>
        <div className="mt-3 space-y-2 text-sm text-zinc-300">
          {pendientesAccion.length > 0 ? (
            pendientesAccion.map((mensaje) => <p key={mensaje}>{mensaje}</p>)
          ) : (
            <p>No tenés tareas pendientes por revisar. Podés publicar una nueva solicitud.</p>
          )}
        </div>
      </section>
      <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
        {[
          [
            'Solicitudes activas',
            activas.length,
            `${items.length} publicaciones en total`,
          ],
          [
            'Propuestas recibidas',
            posts.length,
            `${posts.filter((p) => p.estado === 'PENDIENTE').length} pendientes`,
          ],
          ['Trabajos en curso', curso.length, `${completas.length} completados`],
          [
            'Profesionales guardados',
            favoritos.data?.length ?? 0,
            'Tu gente de confianza',
          ],
        ].map(([label, value, hint]) => (
          <StatCard
            key={label}
            label={String(label)}
            value={String(value)}
            hint={String(hint)}
          />
        ))}
      </div>
      <div className="grid gap-5 xl:grid-cols-[1.4fr_1fr]">
        <section className={panel}>
          <span className="rounded-lg bg-[#ffc107]/10 px-3 py-2 text-xs font-bold text-[#ffc107]">
            UNA NECESIDAD, MUCHAS SOLUCIONES
          </span>
          <h2 className="my-5 text-2xl font-bold">El profesional indicado, a un paso.</h2>
          <p className="mb-4 text-sm text-zinc-400">
            Contá qué necesitás y recibí propuestas para tu trabajo.
          </p>
          <Link href="/empleador/solicitudes/nueva">
            <Button>+ Publicar una solicitud</Button>
          </Link>
        </section>
        <section className={panel}>
          <h2 className="mb-4 text-xl font-bold">Explorá especialidades</h2>
          <div className="grid grid-cols-2 gap-3 sm:grid-cols-4">
            {[
              [Zap, 'Electricidad'],
              [Wrench, 'Plomería'],
              [PaintRoller, 'Pintura'],
              [Leaf, 'Jardinería'],
            ].map(([Icon, label]) => {
              const I = Icon as typeof Zap;
              return (
                <Link
                  key={String(label)}
                  href={`/empleador/trabajadores?especialidad=${encodeURIComponent(String(label))}`}
                  className="flex flex-col items-center gap-4 rounded-xl border border-[#3a3a3f] bg-white/5 px-2 py-5 text-xs hover:border-[#ffc107]"
                >
                  <I size={26} className="text-[#ffc107]" />
                  {String(label)}
                </Link>
              );
            })}
          </div>
        </section>
        <section className={panel}>
          <h2 className="mb-4 text-xl font-bold">Mis solicitudes activas</h2>
          {!solicitudes.isPending && !activas.length && (
            <Empty mensaje="Publicá tu primera solicitud para recibir propuestas." />
          )}
          {activas.slice(0, 3).map((o) => (
            <article key={o.id} className="space-y-2 border-b border-[#30302f] py-4">
              <h3 className="font-bold">{o.titulo}</h3>
              <p className="line-clamp-2 text-sm text-zinc-400">{o.contenido}</p>
              <Link
                className="inline-block text-sm font-bold text-[#ffc107]"
                href={`/empleador/solicitudes/${o.id}`}
              >
                Gestionar solicitud →
              </Link>
            </article>
          ))}
          <Link
            href="/empleador/solicitudes"
            className="mt-4 inline-block text-sm text-[#ffc107]"
          >
            Ver todas las solicitudes →
          </Link>
        </section>
        <section className={panel}>
          <h2 className="mb-4 text-xl font-bold">Tus trabajos</h2>
          {curso.length === 0 && !propuestas.isPending && (
            <p className="text-sm text-zinc-400">
              Cuando selecciones una propuesta, encontrarás el trabajo acá.
            </p>
          )}
          {curso.slice(0, 2).map((p) => (
            <article key={p.id} className="mb-4 space-y-2">
              <p className="font-bold">{p.autor?.nombre}</p>
              <p className="text-sm text-zinc-400">{p.oportunidadLaboral.titulo}</p>
              <Link
                href={`/empleador/solicitudes/${p.oportunidadLaboral.id}`}
                className="text-sm text-[#ffc107]"
              >
                Ver trabajo →
              </Link>
            </article>
          ))}
          {completas.length > 0 && (
            <Link
              href="/empleador/contratos"
              className="mt-5 block border-t border-[#30302f] pt-4 text-sm text-[#ffc107]"
            >
              Un trabajo terminado, una opinión útil. Valorar →
            </Link>
          )}
        </section>
      </div>
    </div>
  );
}
