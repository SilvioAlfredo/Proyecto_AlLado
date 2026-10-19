'use client';
import { useState } from 'react';
import { useParams } from 'next/navigation';
import { useQuery } from '@tanstack/react-query';
import Link from 'next/link';
import { trabajadorService } from '@/features/usuario/services/trabajadorService';
import { valoracionesService } from '@/features/comunidad/services/valoracionesService';
import {
  portafolioService,
  horariosService,
} from '@/features/usuario/services/perfilService';
import { favoritosService } from '@/features/comunidad/services/favoritosService';
import { useEmpleador } from '@/features/empleador/hooks/useEmpleador';
import { PageHeader, Avatar, Note } from '@/components/ui/shared';
import { Button } from '@/components/ui/button';
import { QueryStatus, Feedback, panel } from '@/features/empleador/components/EmployerUI';
const safeUrl = (value?: string) =>
  value && /^https?:\/\//i.test(value) ? value : undefined;
export default function Page() {
  const { id: raw } = useParams<{ id: string }>();
  const id = Number(raw);
  const { userId, favoritos } = useEmpleador();
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState<unknown>(null);
  const query = useQuery({
    queryKey: ['trabajador', id],
    queryFn: () => trabajadorService.obtenerPorId(id),
  });
  const vals = useQuery({
    queryKey: ['trabajador', id, 'valoraciones'],
    queryFn: () => valoracionesService.porTrabajador(id),
  });
  const port = useQuery({
    queryKey: ['trabajador', id, 'portafolio'],
    queryFn: () => portafolioService.listar(id),
  });
  const horarios = useQuery({
    queryKey: ['trabajador', id, 'horarios'],
    queryFn: () => horariosService.listar(id),
  });
  if (query.isPending || query.error)
    return <QueryStatus pending={query.isPending} error={query.error} />;
  const t = query.data!;
  const saved = favoritos.data?.some((f) => f.trabajador?.id === id);
  const average = vals.data?.length
    ? vals.data.reduce((n, v) => n + v.puntuacion, 0) / vals.data.length
    : null;
  return (
    <div className="space-y-6">
      <PageHeader
        eyebrow="Perfil del trabajador"
        title={t.nombre}
        action={
          <Link className="text-sm text-[#ffc107]" href="/empleador/trabajadores">
            Volver a la búsqueda →
          </Link>
        }
      />
      <Feedback error={error || favoritos.error} />
      <div className="grid gap-5 lg:grid-cols-[1fr_280px]">
        <div className="space-y-5">
          <section className={`${panel} flex flex-wrap items-center gap-4`}>
            <Avatar nombre={t.nombre} size="lg" />
            <div className="flex-1">
              <p className="font-bold">
                {t.especialidades?.map((e) => e.nombre).join(' · ') ||
                  'Profesional de AlLado'}
              </p>
              <p className="mt-2 text-sm text-zinc-400">{t.sector}</p>
              <p className="mt-2 text-sm text-[#ffc107]">
                {average !== null
                  ? `★ ${average.toFixed(1)} (${vals.data?.length} opiniones)`
                  : 'Sin valoraciones todavía'}
              </p>
            </div>
            <Button
              disabled={busy || !favoritos.data}
              onClick={async () => {
                setBusy(true);
                setError(null);
                try {
                  if (saved) await favoritosService.quitar(userId!, id);
                  else await favoritosService.agregar(userId!, id);
                  await favoritos.refetch();
                } catch (e) {
                  setError(e);
                } finally {
                  setBusy(false);
                }
              }}
            >
              {busy
                ? 'Guardando…'
                : saved
                  ? 'Quitar de favoritos'
                  : 'Guardar en favoritos'}
            </Button>
          </section>
          <section className={`${panel} space-y-3`}>
            <h2 className="font-bold">Sobre {t.nombre.split(' ')[0]}</h2>
            <p className="whitespace-pre-wrap text-sm text-zinc-400">
              {t.biografia || 'Sin biografía publicada.'}
            </p>
            <h2 className="pt-3 font-bold">Certificados</h2>
            <p className="whitespace-pre-wrap text-sm text-zinc-400">
              {t.certificados || 'Sin certificados publicados.'}
            </p>
          </section>
          <section className={`${panel} space-y-4`}>
            <h2 className="font-bold">Portafolio</h2>
            <QueryStatus pending={port.isPending} error={port.error} />
            {port.data?.length === 0 && (
              <p className="text-sm text-zinc-400">Sin trabajos publicados.</p>
            )}
            {port.data?.map((p) => (
              <article key={p.id} className="space-y-2 border-t border-[#30302f] pt-3">
                <h3 className="font-bold">{p.titulo}</h3>
                <p className="text-sm text-zinc-400">{p.descripcion}</p>
                {safeUrl(p.urlArchivo) && (
                  <a
                    href={safeUrl(p.urlArchivo)}
                    target="_blank"
                    rel="noreferrer"
                    className="text-sm text-[#ffc107]"
                  >
                    Ver trabajo →
                  </a>
                )}
              </article>
            ))}
          </section>
          <section className={`${panel} space-y-4`}>
            <h2 className="font-bold">Opiniones ({vals.data?.length ?? 0})</h2>
            <QueryStatus pending={vals.isPending} error={vals.error} />
            {vals.data?.length === 0 && (
              <p className="text-sm text-zinc-400">Todavía no recibió opiniones.</p>
            )}
            {vals.data?.map((v) => (
              <article key={v.id} className="border-t border-[#30302f] pt-3">
                <p className="text-sm font-bold">
                  {v.autor.nombre}{' '}
                  <span className="text-[#ffc107]">★ {v.puntuacion}/5</span>
                </p>
                <p className="mt-2 text-sm text-zinc-400">{v.comentario}</p>
              </article>
            ))}
          </section>
        </div>
        <aside className="space-y-5">
          <section className={`${panel} space-y-4`}>
            <h2 className="font-bold">Contactalo directamente</h2>
            <p className="break-all text-sm text-zinc-400">{t.email}</p>
            <a
              href={`mailto:${t.email}`}
              className="block rounded-xl bg-[#ffc107] p-3 text-center text-sm font-bold text-black"
            >
              Escribir por email
            </a>
            {t.telefono && (
              <>
                <p className="text-sm text-zinc-400">{t.telefono}</p>
                <a
                  href={`tel:${t.telefono.replace(/[^\d+]/g, '')}`}
                  className="block rounded-xl border border-[#30302f] p-3 text-center text-sm"
                >
                  Llamar por teléfono
                </a>
              </>
            )}
          </section>
          <section className={`${panel} space-y-3`}>
            <h2 className="font-bold">Horarios semanales</h2>
            <QueryStatus pending={horarios.isPending} error={horarios.error} />
            {horarios.data?.length === 0 && (
              <p className="text-sm text-zinc-400">
                Consultá su disponibilidad por contacto directo.
              </p>
            )}
            {horarios.data?.map((h) => (
              <p key={h.id} className="flex justify-between gap-3 text-xs text-zinc-400">
                <span>{h.diaSemana}</span>
                <span>
                  {h.horaInicio.slice(0, 5)}–{h.horaFin.slice(0, 5)}
                </span>
              </p>
            ))}
          </section>
          <Note>Elegí a quién contactar por experiencia, formación y opiniones.</Note>
        </aside>
      </div>
    </div>
  );
}
