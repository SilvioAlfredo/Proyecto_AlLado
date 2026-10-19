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

export default function Page() {
  const [text, setText] = useState('');
  const [filters, setFilters] = useState({ filtro: '', estado: 'TODAS', page: 0 });
  const query = useQuery({
    queryKey: ['admin-empresas', filters],
    queryFn: () => gestionService.empresas(filters.filtro, filters.estado, filters.page),
  });

  return (
    <div className="space-y-6">
      <PageHeader
        eyebrow="Gestión"
        title="Empresas"
        subtitle="Revisá los perfiles empresariales antes de verificar."
      />
      <Tabs
        value={filters.estado}
        onChange={(estado) => setFilters({ ...filters, estado, page: 0 })}
        options={[
          { value: 'TODAS', label: 'Todas' },
          { value: 'PENDIENTES', label: 'Sin verificar' },
          { value: 'SIN_PERFIL', label: 'Sin perfil' },
          { value: 'VERIFICADAS', label: 'Verificadas' },
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
          aria-label="Buscar empresas"
          placeholder="Empresa, CUIT, titular o email"
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
                  <p className="text-xs text-[#ffc107]">
                    {!item.perfil
                      ? 'Sin perfil'
                      : item.perfil.verificado
                        ? 'Verificada'
                        : 'Sin verificar'}
                  </p>
                  <h2 className="text-xl font-bold">
                    {item.perfil?.nombreEmpresa || item.empleador.nombre}
                  </h2>
                  <p className="text-sm text-zinc-400">
                    CUIT: {item.perfil?.cuit || 'Sin registrar'}
                  </p>
                  <p className="text-sm">
                    {item.empleador?.nombre} · {item.empleador?.email}
                  </p>
                  {item.empleador?.id && (
                    <Link
                      href={
                        item.perfil
                          ? `/admin/empresas/${item.empleador.id}`
                          : `/admin/personas/empleadores/${item.empleador.id}`
                      }
                    >
                      <Button variant="outline">
                        {item.perfil ? 'Revisar empresa' : 'Ver empleador'}
                      </Button>
                    </Link>
                  )}
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
