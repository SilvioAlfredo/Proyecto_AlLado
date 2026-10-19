'use client';

import { useState } from 'react';
import Link from 'next/link';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { gestionService } from '@/features/admin/services/gestionService';
import { comunicadosService } from '@/features/comunidad/services/moderacionService';
import { Feedback, LoadError } from '@/features/admin/components/Feedback';
import { Pagination } from '@/features/admin/components/Pagination';
import { PageHeader, Empty, Note } from '@/components/ui/shared';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { errorMessage } from '@/lib/errors';
import { formatDate } from '@/lib/utils';

export default function ComunicadosPage() {
  const [text, setText] = useState('');
  const [scope, setScope] = useState('');
  const [filters, setFilters] = useState({
    filtro: '',
    alcance: '',
    orden: 'recientes',
    page: 0,
  });
  const client = useQueryClient();
  const query = useQuery({
    queryKey: ['admin-comunicados', filters],
    queryFn: () =>
      gestionService.comunicados(
        filters.filtro,
        filters.alcance,
        filters.orden,
        filters.page,
      ),
  });
  const deletion = useMutation({
    mutationFn: (id: number) => comunicadosService.eliminar(id),
    onSuccess: () => {
      if (query.data?.content.length === 1 && filters.page > 0) {
        setFilters((previous) => ({ ...previous, page: previous.page - 1 }));
      }
      client.invalidateQueries({ queryKey: ['admin-comunicados'] });
    },
  });

  return (
    <div className="space-y-6">
      <PageHeader
        eyebrow="Gestión"
        title="Comunicados"
        subtitle="Mensajes publicados desde AlLado."
        action={
          <Link href="/admin/comunicados/nuevo">
            <Button>Crear comunicado</Button>
          </Link>
        }
      />
      <form
        className="flex flex-wrap items-end gap-3"
        onSubmit={(event) => {
          event.preventDefault();
          setFilters({ ...filters, filtro: text.trim(), alcance: scope.trim(), page: 0 });
        }}
      >
        <label className="space-y-1 text-sm">
          <span>Buscar comunicado</span>
          <Input
            value={text}
            onChange={(event) => setText(event.target.value)}
            placeholder="Título o contenido"
          />
        </label>
        <label className="space-y-1 text-sm">
          <span>Alcance</span>
          <Input
            value={scope}
            maxLength={50}
            onChange={(event) => setScope(event.target.value)}
            placeholder="Todos los alcances"
          />
        </label>
        <label className="space-y-1 text-sm">
          <span>Orden</span>
          <select
            className="block h-11 rounded-xl border border-[#3a3a3f] bg-[#26262a] px-3"
            value={filters.orden}
            onChange={(event) =>
              setFilters({ ...filters, orden: event.target.value, page: 0 })
            }
          >
            <option value="recientes">Más recientes</option>
            <option value="antiguos">Más antiguos</option>
            <option value="titulo">Título A–Z</option>
          </select>
        </label>
        <Button type="submit">Buscar</Button>
        <Button
          type="button"
          variant="outline"
          onClick={() => {
            setText('');
            setScope('');
            setFilters({ filtro: '', alcance: '', orden: 'recientes', page: 0 });
          }}
        >
          Limpiar
        </Button>
      </form>
      {deletion.isError && <Feedback message={errorMessage(deletion.error)} />}
      {query.isPending ? (
        <p role="status">Cargando comunicados…</p>
      ) : query.isError ? (
        <LoadError
          message={errorMessage(query.error)}
          retry={() => void query.refetch()}
        />
      ) : (
        <>
          {!query.data.content.length ? (
            <Empty mensaje="No hay comunicados para esta búsqueda." />
          ) : (
            <div className="space-y-3">
              {query.data.content.map((item) => (
                <article
                  key={item.id}
                  className="space-y-3 rounded-2xl border border-[#2e2e33] bg-[#1a1a1c] p-5"
                >
                  <div className="flex flex-wrap justify-between gap-3">
                    <div>
                      <p className="text-xs text-[#ffc107]">
                        {item.alcance || 'Comunidad'}
                      </p>
                      <h2 className="font-bold">{item.titulo}</h2>
                    </div>
                    <p className="text-xs text-zinc-400">
                      {item.emisor?.nombre} · {formatDate(item.fechaEmision)}
                    </p>
                  </div>
                  <p className="whitespace-pre-wrap text-sm text-zinc-300">
                    {item.contenido}
                  </p>
                  <Button
                    size="sm"
                    variant="outline"
                    disabled={deletion.isPending}
                    onClick={() => {
                      if (
                        item.id &&
                        confirm(
                          `Eliminar definitivamente el comunicado «${item.titulo}»?`,
                        )
                      ) {
                        deletion.mutate(item.id);
                      }
                    }}
                  >
                    Eliminar comunicado
                  </Button>
                </article>
              ))}
            </div>
          )}
          <Pagination
            {...query.data}
            busy={query.isFetching}
            change={(page) => setFilters({ ...filters, page })}
          />
        </>
      )}
      <Note>Los comunicados se publican al crearlos. Su eliminación es definitiva.</Note>
    </div>
  );
}
