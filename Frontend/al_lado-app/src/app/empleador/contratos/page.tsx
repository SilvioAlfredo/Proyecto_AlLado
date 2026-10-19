'use client';
import { useState } from 'react';
import Link from 'next/link';
import { useEmpleador } from '@/features/empleador/hooks/useEmpleador';
import { postulacionesService } from '@/features/postulaciones/services/postulacionesService';
import { panel, QueryStatus, Feedback } from '@/features/empleador/components/EmployerUI';
import { PageHeader, Tabs, Empty, Avatar } from '@/components/ui/shared';
import { Button } from '@/components/ui/button';
export default function ContratosPage() {
  const { propuestas, refresh } = useEmpleador();
  const [tab, setTab] = useState('TODAS');
  const [busy, setBusy] = useState<number | null>(null);
  const [error, setError] = useState<unknown>(null);
  const items = (propuestas.data ?? []).filter((p) =>
    ['SELECCIONADA', 'COMPLETADA'].includes(p.estado),
  );
  const filtered = items.filter((p) => tab === 'TODAS' || p.estado === tab);
  return (
    <div className="space-y-6">
      <PageHeader
        eyebrow="Contratos y trabajos"
        title="Tus trabajos"
        subtitle="Seguí los trabajos en curso y compartí tu experiencia al terminarlos."
      />
      <Tabs
        value={tab}
        onChange={setTab}
        options={[
          { value: 'TODAS', label: `Todos · ${items.length}` },
          { value: 'SELECCIONADA', label: 'En curso' },
          { value: 'COMPLETADA', label: 'Completados' },
        ]}
      />
      <QueryStatus pending={propuestas.isPending} error={propuestas.error} />
      <Feedback error={error} />
      {!propuestas.isPending && !propuestas.error && !filtered.length && (
        <Empty mensaje="Sin trabajos en este filtro." />
      )}
      <div className="grid gap-4 lg:grid-cols-2">
        {filtered.map((p) => (
          <article key={p.id} className={`${panel} space-y-4`}>
            <span className="text-xs font-bold text-[#ffc107]">
              {p.estado === 'SELECCIONADA' ? 'En curso' : 'Completado'}
            </span>
            <h2 className="text-xl font-bold">{p.oportunidadLaboral.titulo}</h2>
            <div className="flex items-center gap-3">
              <Avatar nombre={p.autor?.nombre ?? 'Trabajador'} />
              <div>
                <p className="font-bold">{p.autor?.nombre}</p>
                {p.autor?.id && (
                  <Link
                    className="text-sm text-[#ffc107]"
                    href={`/empleador/trabajadores/${p.autor.id}`}
                  >
                    Ver perfil →
                  </Link>
                )}
              </div>
            </div>
            <p className="text-sm text-zinc-400">{p.contenido}</p>
            <p className="font-bold">
              ${p.remuneracionPretendida.toLocaleString('es-AR')} ARS
            </p>
            <Link
              className="block text-sm text-zinc-400"
              href={`/empleador/solicitudes/${p.oportunidadLaboral.id}`}
            >
              Ver solicitud →
            </Link>
            {p.estado === 'SELECCIONADA' ? (
              <Button
                disabled={busy !== null}
                onClick={async () => {
                  setBusy(p.id!);
                  setError(null);
                  try {
                    await postulacionesService.completar(p.id!);
                    await refresh();
                  } catch (e) {
                    setError(e);
                  } finally {
                    setBusy(null);
                  }
                }}
              >
                {busy === p.id ? 'Guardando…' : 'Marcar como completado'}
              </Button>
            ) : (
              <Link href={`/empleador/valoraciones?trabajadorId=${p.autor?.id}`}>
                <Button>Valorar trabajo</Button>
              </Link>
            )}
          </article>
        ))}
      </div>
    </div>
  );
}
