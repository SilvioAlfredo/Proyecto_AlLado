'use client';

import { Suspense, useState } from 'react';
import { useSearchParams } from 'next/navigation';
import Link from 'next/link';
import { useQuery } from '@tanstack/react-query';
import { gestionService } from '@/features/admin/services/gestionService';
import { LoadError } from '@/features/admin/components/Feedback';
import { Pagination } from '@/features/admin/components/Pagination';
import { PageHeader, Tabs, Empty, Note } from '@/components/ui/shared';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { errorMessage } from '@/lib/errors';
import { formatDate } from '@/lib/utils';

function ReportesContent() {
  const params = useSearchParams();
  const requestedState = params.get('estado') ?? 'PENDIENTE';
  const initialState = [
    'TODOS',
    'PENDIENTE',
    'EN_REVISION',
    'RESUELTO',
    'DESCARTADO',
  ].includes(requestedState)
    ? requestedState
    : 'PENDIENTE';
  const [search, setSearch] = useState('');
  const [filters, setFilters] = useState({ filtro: '', estado: initialState, page: 0 });
  const query = useQuery({
    queryKey: ['admin-reportes', filters],
    queryFn: () => gestionService.reportes(filters.filtro, filters.estado, filters.page),
  });
  const resumen = {
    pendientes: query.data?.content.filter((item) => item.estado === 'PENDIENTE').length ?? 0,
    revision: query.data?.content.filter((item) => item.estado === 'EN_REVISION').length ?? 0,
    resueltos: query.data?.content.filter((item) => item.estado === 'RESUELTO').length ?? 0,
    descartados: query.data?.content.filter((item) => item.estado === 'DESCARTADO').length ?? 0,
  };

  return (
    <div className="space-y-6">
      <PageHeader
        eyebrow="Moderación"
        title="Reportes"
        subtitle="Revisá los avisos de la comunidad."
        action={
          <Link href="/admin/foro">
            <Button variant="outline">Foro y valoraciones</Button>
          </Link>
        }
      />
      <section className="rounded-2xl border border-[#2e2e33] bg-[#1a1a1c] p-5">
        <p className="text-xs font-semibold uppercase tracking-[0.2em] text-[#ffc107]">
          Qué revisar hoy
        </p>
        <div className="mt-3 grid gap-3 sm:grid-cols-2 xl:grid-cols-4">
          <div>
            <p className="text-2xl font-black text-white">{resumen.pendientes}</p>
            <p className="text-sm text-zinc-400">pendientes</p>
          </div>
          <div>
            <p className="text-2xl font-black text-white">{resumen.revision}</p>
            <p className="text-sm text-zinc-400">en revisión</p>
          </div>
          <div>
            <p className="text-2xl font-black text-white">{resumen.resueltos}</p>
            <p className="text-sm text-zinc-400">resueltos</p>
          </div>
          <div>
            <p className="text-2xl font-black text-white">{resumen.descartados}</p>
            <p className="text-sm text-zinc-400">descartados</p>
          </div>
        </div>
        <p className="mt-3 text-sm text-zinc-300">
          {resumen.pendientes > 0
            ? 'Hay reportes pendientes de resolución. Prioriza los casos más urgentes.'
            : 'No hay reportes pendientes. Revisá el resto de moderación o el foro.'}
        </p>
      </section>
      <Tabs
        value={filters.estado}
        onChange={(estado) => setFilters({ ...filters, estado, page: 0 })}
        options={[
          { value: 'PENDIENTE', label: 'Pendientes' },
          { value: 'EN_REVISION', label: 'En revisión' },
          { value: 'RESUELTO', label: 'Resueltos' },
          { value: 'DESCARTADO', label: 'Descartados' },
          { value: 'TODOS', label: 'Todos' },
        ]}
      />
      <form
        className="flex flex-wrap gap-3"
        onSubmit={(event) => {
          event.preventDefault();
          setFilters({ ...filters, filtro: search.trim(), page: 0 });
        }}
      >
        <Input
          className="max-w-md"
          aria-label="Buscar reportes"
          placeholder="Motivo, descripción o persona"
          value={search}
          onChange={(event) => setSearch(event.target.value)}
        />
        <Button type="submit">Buscar</Button>
        <Button
          type="button"
          variant="outline"
          onClick={() => {
            setSearch('');
            setFilters({ ...filters, filtro: '', page: 0 });
          }}
        >
          Limpiar
        </Button>
      </form>
      {query.isPending ? (
        <p role="status">Cargando reportes…</p>
      ) : query.isError ? (
        <LoadError
          message={errorMessage(query.error)}
          retry={() => void query.refetch()}
        />
      ) : (
        <>
          {!query.data.content.length ? (
            <Empty mensaje="No hay reportes para estos filtros." />
          ) : (
            <div className="grid gap-4 lg:grid-cols-2 xl:grid-cols-3">
              {query.data.content.map((report) => (
                <article
                  key={report.id}
                  className="space-y-3 rounded-2xl border border-[#2e2e33] bg-[#1a1a1c] p-5"
                >
                  <div className="flex justify-between gap-2 text-xs">
                    <span className="text-[#ffc107]">{report.estado}</span>
                    <span className="text-zinc-500">
                      {formatDate(report.fechaCreacion)}
                    </span>
                  </div>
                  <h2 className="font-bold">{report.motivo}</h2>
                  <p className="line-clamp-3 text-sm text-zinc-400">
                    {report.descripcion}
                  </p>
                  <p className="text-xs text-zinc-400">
                    Reportante: {report.reportante?.nombre || 'Sin registrar'}
                  </p>
                  <p className="text-xs text-zinc-400">
                    Reportado: {report.reportado?.nombre || 'No especificado'}
                  </p>
                  <Link href={`/admin/reportes/${report.id}`}>
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
      <div className="flex gap-3">
        <Link className="text-sm text-[#ffc107]" href="/admin/foro">
          Foro y valoraciones →
        </Link>
        <Link className="text-sm text-[#ffc107]" href="/admin/empresas">
          Empresas →
        </Link>
      </div>
      <Note>
        Revisá el detalle antes de resolver o descartar un reporte. Los estados finales no
        pueden reabrirse.
      </Note>
    </div>
  );
}

export default function ReportesPage() {
  return (
    <Suspense fallback={<p role="status">Cargando reportes…</p>}>
      <ReportesContent />
    </Suspense>
  );
}
