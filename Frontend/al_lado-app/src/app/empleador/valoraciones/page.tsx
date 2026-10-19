'use client';
import { Suspense, useState } from 'react';
import { useSearchParams } from 'next/navigation';
import { useQuery } from '@tanstack/react-query';
import Link from 'next/link';
import { Star } from 'lucide-react';
import { useEmpleador } from '@/features/empleador/hooks/useEmpleador';
import { valoracionesService } from '@/features/comunidad/services/valoracionesService';
import { PageHeader, Empty } from '@/components/ui/shared';
import { Button } from '@/components/ui/button';
import {
  panel,
  control,
  Field,
  Feedback,
  QueryStatus,
} from '@/features/empleador/components/EmployerUI';
function Content() {
  const params = useSearchParams();
  const { userId, perfil, propuestas } = useEmpleador();
  const [worker, setWorker] = useState(params.get('trabajadorId') ?? '');
  const [score, setScore] = useState(5);
  const [comment, setComment] = useState('');
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState<unknown>(null);
  const [ok, setOk] = useState('');
  const query = useQuery({
    queryKey: ['empleador', userId, 'valoraciones'],
    enabled: !!userId,
    queryFn: () => valoracionesService.porAutor(userId!),
  });
  const workers = Array.from(
    new Map(
      (propuestas.data ?? [])
        .filter((p) => p.estado === 'COMPLETADA' && p.autor)
        .map((p) => [p.autor!.id, p.autor!]),
    ).values(),
  );
  const selected = workers.find((w) => w.id === Number(worker));
  return (
    <div className="space-y-6">
      <PageHeader
        eyebrow="Valorar trabajador"
        title="¿Cómo fue tu trabajo?"
        subtitle="Tu experiencia ayuda a otras personas a elegir."
        action={
          <Link className="text-sm text-[#ffc107]" href="/empleador/contratos">
            Volver a mis trabajos →
          </Link>
        }
      />
      <QueryStatus pending={propuestas.isPending} error={propuestas.error} />
      <div className="grid gap-5 lg:grid-cols-[1fr_280px]">
        <form
          className={`${panel} space-y-5`}
          onSubmit={async (e) => {
            e.preventDefault();
            setBusy(true);
            setError(null);
            setOk('');
            try {
              if (!perfil.data || !selected)
                throw new Error('Elegí un trabajador de tus trabajos completados.');
              await valoracionesService.crear({
                puntuacion: score,
                comentario: comment.trim() || undefined,
                autor: {
                  id: perfil.data.id,
                  nombre: perfil.data.nombre,
                  email: perfil.data.email,
                },
                trabajador: selected,
              });
              setComment('');
              setOk('Gracias. Tu valoración fue publicada.');
              await query.refetch();
            } catch (err) {
              setError(err);
            } finally {
              setBusy(false);
            }
          }}
        >
          <Feedback error={error} success={ok} />
          <Field label="Trabajador">
            <select
              required
              className={control}
              value={worker}
              onChange={(e) => setWorker(e.target.value)}
            >
              <option value="">Elegí un trabajador</option>
              {workers.map((w) => (
                <option key={w.id} value={w.id}>
                  {w.nombre}
                </option>
              ))}
            </select>
          </Field>
          <fieldset>
            <legend className="mb-3 text-sm font-medium">
              Elegí una puntuación del 1 al 5
            </legend>
            <div className="flex gap-2">
              {[1, 2, 3, 4, 5].map((n) => (
                <button
                  type="button"
                  key={n}
                  aria-label={`${n} ${n === 1 ? 'estrella' : 'estrellas'}`}
                  aria-pressed={score === n}
                  onClick={() => setScore(n)}
                  className="rounded-lg p-2 focus-visible:outline-2 focus-visible:outline-[#ffc107]"
                >
                  <Star
                    className={
                      n <= score ? 'fill-[#ffc107] text-[#ffc107]' : 'text-zinc-500'
                    }
                    size={30}
                  />
                </button>
              ))}
            </div>
          </fieldset>
          <Field label="Comentario (opcional)">
            <textarea
              className={control}
              rows={5}
              maxLength={1000}
              value={comment}
              onChange={(e) => setComment(e.target.value)}
              placeholder="¿Cómo fue la comunicación y el resultado del trabajo?"
            />
          </Field>
          <Button type="submit" disabled={busy || !selected}>
            {busy ? 'Publicando…' : 'Publicar valoración'}
          </Button>
          {!propuestas.isPending && !workers.length && (
            <p className="text-sm text-zinc-400">
              Podés valorar a los profesionales después de completar un trabajo.
            </p>
          )}
        </form>
        <aside className={`${panel} h-fit space-y-3`}>
          <h2 className="font-bold">Vas a valorar a</h2>
          <p>{selected?.nombre || 'Elegí un profesional'}</p>
          <p className="text-sm text-zinc-400">
            Compartí una opinión respetuosa basada en tu experiencia.
          </p>
        </aside>
      </div>
      <h2 className="text-xl font-bold">Mis valoraciones</h2>
      <QueryStatus pending={query.isPending} error={query.error} />
      {query.data?.length === 0 && (
        <Empty mensaje="Todavía no publicaste valoraciones." />
      )}
      <div className="grid gap-4 md:grid-cols-2">
        {query.data?.map((v) => (
          <article key={v.id} className={panel}>
            <p className="font-bold">{v.trabajador.nombre}</p>
            <p className="my-2 text-[#ffc107]">
              {'★'.repeat(v.puntuacion)} <span className="text-xs">{v.puntuacion}/5</span>
            </p>
            <p className="text-sm text-zinc-400">{v.comentario}</p>
          </article>
        ))}
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
