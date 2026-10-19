'use client';

import { useState } from 'react';
import Link from 'next/link';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { foroService } from '@/features/comunidad/services/moderacionService';
import { valoracionesService } from '@/features/comunidad/services/valoracionesService';
import { trabajadorService } from '@/features/usuario/services/trabajadorService';
import { gestionService } from '@/features/admin/services/gestionService';
import { Feedback, LoadError } from '@/features/admin/components/Feedback';
import { Pagination } from '@/features/admin/components/Pagination';
import { PageHeader, Tabs, Empty, Note } from '@/components/ui/shared';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { errorMessage } from '@/lib/errors';
import { formatDate } from '@/lib/utils';

export default function ForoAdminPage() {
  const [tab, setTab] = useState<'FORO' | 'VALORACIONES'>('FORO');
  const [search, setSearch] = useState('');
  const [filters, setFilters] = useState({ filtro: '', page: 0 });
  const [workerSearch, setWorkerSearch] = useState('');
  const [workerId, setWorkerId] = useState('');
  const [reviewPage, setReviewPage] = useState(0);
  const client = useQueryClient();
  const posts = useQuery({
    queryKey: ['admin-foro', filters],
    enabled: tab === 'FORO',
    queryFn: () => gestionService.foro(filters.filtro, filters.page),
  });
  const workers = useQuery({
    queryKey: ['admin-personas', 'trabajadores'],
    enabled: tab === 'VALORACIONES',
    queryFn: () => trabajadorService.obtenerTodos(),
  });
  const reviews = useQuery({
    queryKey: ['admin-moderacion-valoraciones', Number(workerId), reviewPage],
    enabled: tab === 'VALORACIONES' && !!workerId,
    queryFn: () => gestionService.valoraciones(Number(workerId), reviewPage),
  });
  const deletion = useMutation({
    mutationFn: ({ kind, id }: { kind: 'FORO' | 'VALORACION'; id: number }) =>
      kind === 'FORO' ? foroService.eliminar(id) : valoracionesService.eliminar(id),
    onSuccess: (_, action) => {
      if (action.kind === 'FORO') {
        if (posts.data?.content.length === 1 && filters.page > 0) {
          setFilters((previous) => ({ ...previous, page: previous.page - 1 }));
        }
        client.invalidateQueries({ queryKey: ['admin-foro'] });
      } else {
        if (reviews.data?.content.length === 1 && reviewPage > 0) {
          setReviewPage((previous) => previous - 1);
        }
        client.invalidateQueries({ queryKey: ['admin-moderacion-valoraciones'] });
        client.invalidateQueries({ queryKey: ['admin-valoraciones'] });
        client.invalidateQueries({ queryKey: ['admin-persona', 'TRABAJADOR'] });
      }
    },
  });
  const people = (workers.data ?? []).filter((person) =>
    [person.nombre, person.email].some((value) =>
      value.toLowerCase().includes(workerSearch.trim().toLowerCase()),
    ),
  );

  return (
    <div className="space-y-6">
      <PageHeader
        eyebrow="Moderación"
        title="Foro y valoraciones"
        subtitle="Consultá contenido y eliminá el que corresponde."
        action={
          <Link href="/admin/reportes">
            <Button variant="outline">Volver a Reportes</Button>
          </Link>
        }
      />
      <Tabs
        value={tab}
        onChange={(value) => {
          setTab(value);
          deletion.reset();
        }}
        options={[
          { value: 'FORO', label: 'Foro' },
          { value: 'VALORACIONES', label: 'Valoraciones por trabajador' },
        ]}
      />
      {deletion.isError && <Feedback message={errorMessage(deletion.error)} />}
      {tab === 'FORO' ? (
        <>
          <form
            className="flex flex-wrap gap-3"
            onSubmit={(event) => {
              event.preventDefault();
              setFilters({ filtro: search.trim(), page: 0 });
            }}
          >
            <Input
              className="max-w-md"
              aria-label="Buscar publicaciones"
              placeholder="Título, contenido o autor"
              value={search}
              onChange={(event) => setSearch(event.target.value)}
            />
            <Button type="submit">Buscar</Button>
            <Button
              type="button"
              variant="outline"
              onClick={() => {
                setSearch('');
                setFilters({ filtro: '', page: 0 });
              }}
            >
              Limpiar
            </Button>
          </form>
          {posts.isPending ? (
            <p role="status">Cargando publicaciones…</p>
          ) : posts.isError ? (
            <LoadError
              message={errorMessage(posts.error)}
              retry={() => void posts.refetch()}
            />
          ) : (
            <>
              {!posts.data.content.length ? (
                <Empty mensaje="No hay publicaciones para esta búsqueda." />
              ) : (
                <div className="grid gap-4 lg:grid-cols-2">
                  {posts.data.content.map((post) => (
                    <article
                      key={post.id}
                      className="space-y-3 rounded-2xl border border-[#2e2e33] bg-[#1a1a1c] p-5"
                    >
                      <h2 className="font-bold">{post.titulo}</h2>
                      <p className="whitespace-pre-wrap text-sm text-zinc-300">
                        {post.contenido}
                      </p>
                      <p className="text-xs text-zinc-400">
                        {post.autor?.nombre} · {formatDate(post.fechaCreacion)}
                      </p>
                      <Button
                        size="sm"
                        variant="destructive"
                        disabled={deletion.isPending}
                        onClick={() => {
                          if (
                            post.id &&
                            confirm(
                              `Eliminar definitivamente la publicación «${post.titulo}»?`,
                            )
                          ) {
                            deletion.mutate({ kind: 'FORO', id: post.id });
                          }
                        }}
                      >
                        Eliminar publicación
                      </Button>
                    </article>
                  ))}
                </div>
              )}
              <Pagination
                {...posts.data}
                busy={posts.isFetching}
                change={(page) => setFilters({ ...filters, page })}
              />
            </>
          )}
        </>
      ) : (
        <>
          <div className="space-y-3">
            <label className="block max-w-md space-y-1 text-sm">
              <span>Buscar trabajador</span>
              <Input
                placeholder="Nombre o email"
                value={workerSearch}
                onChange={(event) => {
                  setWorkerSearch(event.target.value);
                  setWorkerId('');
                  setReviewPage(0);
                }}
              />
            </label>
            {workers.isPending ? (
              <p role="status">Cargando trabajadores…</p>
            ) : workers.isError ? (
              <LoadError
                message={errorMessage(workers.error)}
                retry={() => void workers.refetch()}
              />
            ) : (
              <label className="block space-y-1 text-sm">
                <span>Trabajador</span>
                <select
                  aria-label="Trabajador"
                  className={
                    'block w-full max-w-xl rounded-xl border ' +
                    'border-[#3a3a3f] bg-[#26262a] p-3'
                  }
                  value={workerId}
                  onChange={(event) => {
                    setWorkerId(event.target.value);
                    setReviewPage(0);
                  }}
                >
                  <option value="">Seleccioná un trabajador</option>
                  {people.map((person) => (
                    <option key={person.id} value={person.id}>
                      {person.nombre} · {person.email}
                    </option>
                  ))}
                </select>
                {!people.length && (
                  <p className="text-zinc-400">No hay trabajadores para esa búsqueda.</p>
                )}
              </label>
            )}
          </div>
          {!workerId ? (
            <Empty mensaje="Seleccioná un trabajador para consultar sus valoraciones." />
          ) : reviews.isPending ? (
            <p role="status">Cargando valoraciones…</p>
          ) : reviews.isError ? (
            <LoadError
              message={errorMessage(reviews.error)}
              retry={() => void reviews.refetch()}
            />
          ) : (
            reviews.data && (
              <>
                {!reviews.data.content.length ? (
                  <Empty mensaje="Este trabajador no tiene valoraciones." />
                ) : (
                  <div className="grid gap-4 lg:grid-cols-2">
                    {reviews.data.content.map((review) => (
                      <article
                        key={review.id}
                        className="space-y-3 rounded-2xl border border-[#2e2e33] bg-[#1a1a1c] p-5"
                      >
                        <p className="text-[#ffc107]">★ {review.puntuacion}/5</p>
                        <p className="whitespace-pre-wrap text-sm">
                          {review.comentario || 'Sin comentario.'}
                        </p>
                        <p className="text-xs text-zinc-400">
                          Autor: {review.autor?.nombre} ·{' '}
                          {formatDate(review.fechaCreacion)}
                        </p>
                        <p className="text-xs text-zinc-400">
                          Trabajador: {review.trabajador?.nombre}
                        </p>
                        <Button
                          size="sm"
                          variant="destructive"
                          disabled={deletion.isPending}
                          onClick={() => {
                            if (
                              review.id &&
                              confirm('Eliminar definitivamente esta valoración?')
                            ) {
                              deletion.mutate({ kind: 'VALORACION', id: review.id });
                            }
                          }}
                        >
                          Eliminar valoración
                        </Button>
                      </article>
                    ))}
                  </div>
                )}
                <Pagination
                  {...reviews.data}
                  busy={reviews.isFetching}
                  change={setReviewPage}
                />
              </>
            )
          )}
        </>
      )}
      <Note tone="danger">
        Eliminar publicaciones o valoraciones es irreversible. Revisá el contenido y su
        autor antes de confirmar.
      </Note>
    </div>
  );
}
