'use client';

import { useState } from 'react';
import Link from 'next/link';
import { useQuery } from '@tanstack/react-query';
import { gestionService } from '@/features/admin/services/gestionService';
import { LoadError } from '@/features/admin/components/Feedback';
import { Pagination } from '@/features/admin/components/Pagination';
import { PageHeader, Tabs, Empty, Note } from '@/components/ui/shared';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { calendarDate, subscriptionState } from '@/features/admin/lib/aportes';
import { errorMessage } from '@/lib/errors';

export default function SuscripcionesPage() {
  const [text, setText] = useState('');
  const [filters, setFilters] = useState({ filtro: '', estado: 'TODAS', page: 0 });
  const query = useQuery({
    queryKey: ['admin-suscripciones', filters],
    queryFn: () =>
      gestionService.suscripciones(filters.filtro, filters.estado, filters.page),
  });

  return (
    <div className="space-y-6">
      <PageHeader
        eyebrow="Aportes"
        title="Suscripciones"
        subtitle="Plan y vigencia de cada usuario."
        action={
          <Link href="/admin/donaciones">
            <Button variant="outline">Ver transferencias</Button>
          </Link>
        }
      />
      <Tabs
        value={filters.estado}
        onChange={(estado) => setFilters({ ...filters, estado, page: 0 })}
        options={[
          { value: 'TODAS', label: 'Todas' },
          { value: 'ACTIVAS', label: 'Activas' },
          { value: 'INACTIVAS', label: 'Inactivas o vencidas' },
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
          aria-label="Buscar suscripciones"
          placeholder="Nombre o email"
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
        <p role="status">Cargando suscripciones…</p>
      ) : query.isError ? (
        <LoadError
          message={errorMessage(query.error)}
          retry={() => void query.refetch()}
        />
      ) : (
        <>
          {!query.data.content.length ? (
            <Empty mensaje="No hay suscripciones para estos filtros." />
          ) : (
            <div className="space-y-3">
              {query.data.content.map((sub) => (
                <article
                  key={sub.id}
                  className={
                    'flex flex-wrap items-center justify-between gap-4 ' +
                    'rounded-2xl border border-[#2e2e33] bg-[#1a1a1c] p-5'
                  }
                >
                  <div>
                    <p className="text-xs text-[#ffc107]">{subscriptionState(sub)}</p>
                    <h2 className="font-bold">
                      {sub.usuario?.nombre} · {sub.nivel}
                    </h2>
                    <p className="break-all text-sm text-zinc-400">
                      {sub.usuario?.email}
                    </p>
                    <p className="text-xs text-zinc-500">
                      Inicio: {calendarDate(sub.fechaInicio)} · Fin:{' '}
                      {calendarDate(sub.fechaFin)}
                    </p>
                  </div>
                  <Link href={`/admin/suscripciones/${sub.id}`}>
                    <Button variant="outline" size="sm">
                      Ver detalle
                    </Button>
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
      <Note>
        Las suscripciones vencidas se incluyen entre las inactivas. La verificación de una
        transferencia de suscripción activa el plan PRO.
      </Note>
    </div>
  );
}
