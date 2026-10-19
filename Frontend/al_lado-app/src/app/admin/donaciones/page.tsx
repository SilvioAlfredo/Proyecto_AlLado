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
import { errorMessage } from '@/lib/errors';
import { formatDate } from '@/lib/utils';
import { money } from '@/features/admin/lib/aportes';

export default function DonacionesPage() {
  const [text, setText] = useState('');
  const [filters, setFilters] = useState({ filtro: '', estado: 'TODAS', page: 0 });
  const query = useQuery({
    queryKey: ['admin-donaciones', filters],
    queryFn: () =>
      gestionService.donaciones(filters.filtro, filters.estado, filters.page),
  });

  return (
    <div className="space-y-6">
      <PageHeader
        eyebrow="Aportes"
        title="Transferencias y donaciones"
        subtitle="Registros declarados por los usuarios."
        action={
          <Link href="/admin/suscripciones">
            <Button variant="outline">Ver suscripciones</Button>
          </Link>
        }
      />
      <Tabs
        value={filters.estado}
        onChange={(estado) => setFilters({ ...filters, estado, page: 0 })}
        options={[
          { value: 'TODAS', label: 'Todas' },
          { value: 'PENDIENTE', label: 'Pendientes' },
          { value: 'VERIFICADO', label: 'Verificadas' },
          { value: 'RECHAZADO', label: 'Rechazadas' },
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
          aria-label="Buscar transferencias"
          placeholder="Nombre, email, mecanismo o asunto"
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
        <p role="status">Cargando transferencias…</p>
      ) : query.isError ? (
        <LoadError
          message={errorMessage(query.error)}
          retry={() => void query.refetch()}
        />
      ) : (
        <>
          {!query.data.content.length ? (
            <Empty mensaje="No hay transferencias para estos filtros." />
          ) : (
            <div className="grid gap-4 lg:grid-cols-2 xl:grid-cols-3">
              {query.data.content.map((item) => (
                <article
                  key={item.id}
                  className="space-y-3 rounded-2xl border border-[#2e2e33] bg-[#1a1a1c] p-5"
                >
                  <p className="text-xs text-[#ffc107]">
                    {item.estado} · {item.moneda || 'ARS'}
                  </p>
                  <h2 className="text-2xl font-bold">{money(item.monto, item.moneda)}</h2>
                  <p className="font-semibold">{item.usuario?.nombre}</p>
                  <p className="break-all text-xs text-zinc-400">{item.usuario?.email}</p>
                  <p className="text-sm text-zinc-400">
                    {item.mecanismoPago || 'Sin mecanismo registrado'}
                  </p>
                  <p className="text-xs text-zinc-400">
                    {formatDate(item.fechaDonacion)}
                  </p>
                  <Link href={`/admin/donaciones/${item.id}`}>
                    <Button size="sm" variant="outline">
                      {item.estado === 'PENDIENTE'
                        ? 'Verificar transferencia'
                        : 'Ver detalle'}
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
        La verificación no realiza un cobro. Contrastá el registro con la transferencia
        recibida.
      </Note>
    </div>
  );
}
