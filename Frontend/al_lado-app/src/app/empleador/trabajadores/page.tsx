'use client';
import { Suspense, useEffect, useState } from 'react';
import { useSearchParams } from 'next/navigation';
import { useQuery } from '@tanstack/react-query';
import Link from 'next/link';
import { Bookmark } from 'lucide-react';
import { trabajadorService } from '@/features/usuario/services/trabajadorService';
import { etiquetasService } from '@/features/oportunidades/services/etiquetasService';
import { favoritosService } from '@/features/comunidad/services/favoritosService';
import { useEmpleador } from '@/features/empleador/hooks/useEmpleador';
import { PageHeader, Avatar, Empty } from '@/components/ui/shared';
import { Button } from '@/components/ui/button';
import {
  Field,
  Feedback,
  QueryStatus,
  panel,
  control,
} from '@/features/empleador/components/EmployerUI';
function Content() {
  const params = useSearchParams();
  const { userId, favoritos } = useEmpleador();
  const [q, setQ] = useState('');
  const [especialidad, setEspecialidad] = useState(params.get('especialidad') ?? '');
  const [sector, setSector] = useState('');
  const [sectorFiltrado, setSectorFiltrado] = useState('');
  const [busy, setBusy] = useState<number | null>(null);
  const [error, setError] = useState<unknown>(null);
  const tags = useQuery({ queryKey: ['etiquetas'], queryFn: etiquetasService.listar });
  useEffect(() => {
    const timer = window.setTimeout(() => setSectorFiltrado(sector.trim()), 300);
    return () => window.clearTimeout(timer);
  }, [sector]);
  const query = useQuery({
    queryKey: ['trabajadores-directorio', especialidad, sectorFiltrado],
    queryFn: () => trabajadorService.filtrar(especialidad || undefined, sectorFiltrado || undefined),
    placeholderData: (previousData) => previousData,
  });
  const normalize = (v: string) =>
    v
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .toLowerCase();
  const items = (query.data ?? []).filter(
    (t) =>
      (!q ||
        normalize(
          [
            t.nombre,
            t.biografia,
            t.sector,
            ...(t.especialidades ?? []).map((e) => e.nombre),
          ].join(' '),
        ).includes(normalize(q))) &&
      (!especialidad || t.especialidades?.some((e) => e.nombre === especialidad)) &&
      (!sector || normalize(t.sector ?? '').includes(normalize(sector))),
  );
  return (
    <div className="space-y-6">
      <PageHeader
        eyebrow="Buscar trabajadores"
        title="Encontrá profesionales."
        subtitle="Explorá perfiles y contactá a quienes pueden ayudarte."
        action={
          <Link href="/empleador/favoritos">
            <Button variant="outline">Ver favoritos</Button>
          </Link>
        }
      />
      <section className={`${panel} grid gap-4 sm:grid-cols-3`}>
        <Field label="Nombre o especialidad">
          <input
            className={control}
            value={q}
            onChange={(e) => setQ(e.target.value)}
            placeholder="¿A quién estás buscando?"
          />
        </Field>
        <Field label="Especialidad">
          <select
            className={control}
            value={especialidad}
            onChange={(e) => setEspecialidad(e.target.value)}
          >
            <option value="">Todas las especialidades</option>
            {tags.data?.map((e) => (
              <option key={e.id ?? e.nombre}>{e.nombre}</option>
            ))}
          </select>
        </Field>
        <Field label="Sector o barrio">
          <input
            className={control}
            value={sector}
            onChange={(e) => setSector(e.target.value)}
            placeholder="Todos los barrios"
          />
        </Field>
      </section>
      <Feedback error={error || favoritos.error || tags.error} />
      <QueryStatus pending={query.isPending} error={query.error} />
      {!query.isPending && !query.error && (
        <p className="text-sm text-zinc-400">{items.length} perfiles para explorar</p>
      )}
      {!query.isPending && !query.error && !items.length && (
        <Empty mensaje="No encontramos profesionales con esos filtros." />
      )}
      <div className="grid gap-4 lg:grid-cols-2">
        {items.map((t) => {
          const saved = favoritos.data?.some((f) => f.trabajador?.id === t.id);
          return (
            <article key={t.id} className={`${panel} space-y-4`}>
              <div className="flex items-start justify-between gap-3">
                <div className="flex gap-3">
                  <Avatar nombre={t.nombre} />
                  <div>
                    <h2 className="font-bold">{t.nombre}</h2>
                    <p className="mt-1 text-xs text-zinc-400">
                      {t.especialidades?.map((e) => e.nombre).join(' · ') ||
                        'Sin especialidades registradas'}
                    </p>
                    <p className="mt-1 text-xs text-zinc-500">{t.sector}</p>
                  </div>
                </div>
                <button
                  aria-label={saved ? 'Quitar de favoritos' : 'Guardar en favoritos'}
                  aria-pressed={!!saved}
                  disabled={busy !== null || !favoritos.data}
                  onClick={async () => {
                    setBusy(t.id);
                    setError(null);
                    try {
                      if (saved) await favoritosService.quitar(userId!, t.id);
                      else await favoritosService.agregar(userId!, t.id);
                      await favoritos.refetch();
                    } catch (e) {
                      setError(e);
                    } finally {
                      setBusy(null);
                    }
                  }}
                  className="p-2 text-[#ffc107] disabled:opacity-50"
                >
                  <Bookmark size={20} className={saved ? 'fill-[#ffc107]' : ''} />
                </button>
              </div>
              <p className="line-clamp-2 text-sm text-zinc-400">
                {t.biografia || 'Conocé más sobre este profesional en su perfil.'}
              </p>
              <Link
                className="inline-block text-sm font-bold text-[#ffc107]"
                href={`/empleador/trabajadores/${t.id}`}
              >
                Ver perfil →
              </Link>
            </article>
          );
        })}
      </div>
    </div>
  );
}
export default function Page() {
  return (
    <Suspense fallback={<p>Cargando…</p>}>
      <Content />
    </Suspense>
  );
}
