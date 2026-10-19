'use client';

import { useState } from 'react';
import Link from 'next/link';
import { useQuery } from '@tanstack/react-query';
import { gestionService } from '@/features/admin/services/gestionService';
import { Pagination } from '@/features/admin/components/Pagination';
import { LoadError } from '@/features/admin/components/Feedback';
import { PageHeader, Empty, Tabs } from '@/components/ui/shared';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { errorMessage } from '@/lib/errors';
import { publicidadState } from '@/features/admin/lib/publicidad';
import { calendarDate } from '@/features/admin/lib/aportes';

export default function Page() {
  const [text, setText] = useState('');
  const [filters, setFilters] = useState({ filtro: '', estado: 'TODAS', page: 0 });
  const query = useQuery({
    queryKey: ['admin-publicidades', filters],
    queryFn: () =>
      gestionService.publicidades(filters.filtro, filters.estado, filters.page),
  });

  return (
    <div className="space-y-6">
      <PageHeader
        eyebrow="Gestión"
        title="Publicidad"
        subtitle="Administrá los anuncios y su vigencia."
        action={
          <Link href="/admin/publicidad/nueva">
            <Button>Nueva publicidad</Button>
          </Link>
        }
      />
      <Tabs
        value={filters.estado}
        onChange={(estado) => setFilters({ ...filters, estado, page: 0 })}
        options={[
          { value: 'TODAS', label: 'Todas' },
          { value: 'VIGENTES', label: 'Vigentes' },
          { value: 'PROGRAMADAS', label: 'Programadas' },
          { value: 'VENCIDAS', label: 'Vencidas' },
          { value: 'INACTIVAS', label: 'Inactivas' },
        ]}
      />
      <form
        className="flex flex-wrap gap-3"
        onSubmit={(event) => {
          event.preventDefault();
          setFilters({ ...filters, filtro: text.trim(), page: 0 });
        }}
      >
        <Input
          className="max-w-md"
          aria-label="Buscar publicidad"
          placeholder="Título, anunciante o email"
          value={text}
          onChange={(event) => setText(event.target.value)}
        />
        <Button type="submit">Buscar</Button>
        <Button
          type="button"
          variant="outline"
          onClick={() => {
            setText('');
            setFilters({ ...filters, filtro: '', page: 0 });
          }}
        >
          Limpiar
        </Button>
      </form>
      {query.isPending ? (
        <p role="status">Cargando…</p>
      ) : query.isError ? (
        <LoadError
          message={errorMessage(query.error)}
          retry={() => void query.refetch()}
        />
      ) : (
        <>
          {!query.data.content.length ? (
            <Empty mensaje="No hay registros para estos filtros." />
          ) : (
            <div className="grid gap-4 lg:grid-cols-2">
              {query.data.content.map((item) => (
                <article
                  key={item.id}
                  className="space-y-3 rounded-2xl border border-[#2e2e33] bg-[#1a1a1c] p-5"
                >
                  <p className="text-xs text-[#ffc107]">{publicidadState(item)}</p>
                  <h2 className="text-xl font-bold">{item.titulo}</h2>
                  <p className="text-sm text-zinc-400">{item.contenido}</p>
                  <p className="text-sm text-zinc-400">
                    {item.anunciante?.nombre} · {item.anunciante?.email}
                  </p>
                  <p className="text-sm">
                    {calendarDate(item.fechaInicio)} — {calendarDate(item.fechaFin)}
                  </p>
                  <Link href={`/admin/publicidad/${item.id}`}>
                    <Button variant="outline">Editar publicidad</Button>
                  </Link>
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
    </div>
  );
}
