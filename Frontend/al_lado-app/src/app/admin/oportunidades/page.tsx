'use client';

import { useState } from 'react';
import Link from 'next/link';
import { useQuery } from '@tanstack/react-query';
import { gestionService } from '@/features/admin/services/gestionService';
import { etiquetasService } from '@/features/oportunidades/services/etiquetasService';
import { LoadError } from '@/features/admin/components/Feedback';
import { Pagination } from '@/features/admin/components/Pagination';
import { PageHeader, Empty, Note } from '@/components/ui/shared';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { errorMessage } from '@/lib/errors';

export default function AdminOportunidadesPage() {
  const [draft, setDraft] = useState({
    filtro: '',
    modalidad: '',
    etiquetas: [] as string[],
  });
  const [filters, setFilters] = useState({ ...draft, page: 0 });
  const tags = useQuery({
    queryKey: ['etiquetas'],
    queryFn: () => etiquetasService.listar(),
  });
  const query = useQuery({
    queryKey: ['admin-oportunidades', filters],
    queryFn: () =>
      gestionService.oportunidades(
        filters.filtro,
        filters.modalidad,
        filters.etiquetas,
        filters.page,
      ),
  });

  return (
    <div className="space-y-6">
      <PageHeader
        eyebrow="Gestión"
        title="Oportunidades activas"
        subtitle="Ofertas visibles para la comunidad."
      />
      <form
        className="space-y-3 rounded-2xl border border-[#2e2e33] bg-[#1a1a1c] p-4"
        onSubmit={(event) => {
          event.preventDefault();
          setFilters({ ...draft, page: 0 });
        }}
      >
        <div className="flex flex-wrap items-end gap-3">
          <label className="space-y-1 text-sm">
            <span>Buscar oportunidad</span>
            <Input
              placeholder="Título, descripción o ubicación"
              value={draft.filtro}
              onChange={(event) => setDraft({ ...draft, filtro: event.target.value })}
            />
          </label>
          <label className="space-y-1 text-sm">
            <span>Modalidad</span>
            <select
              className="block h-11 rounded-xl border border-[#3a3a3f] bg-[#26262a] px-3"
              value={draft.modalidad}
              onChange={(event) => setDraft({ ...draft, modalidad: event.target.value })}
            >
              <option value="">Todas</option>
              <option value="PRESENCIAL">Presencial</option>
              <option value="REMOTO">Remoto</option>
              <option value="HIBRIDO">Híbrido</option>
            </select>
          </label>
          <Button type="submit">Aplicar filtros</Button>
          <Button
            type="button"
            variant="outline"
            onClick={() => {
              const cleared = { filtro: '', modalidad: '', etiquetas: [] as string[] };
              setDraft(cleared);
              setFilters({ ...cleared, page: 0 });
            }}
          >
            Limpiar
          </Button>
        </div>
        <fieldset>
          <legend className="mb-2 text-sm text-zinc-400">
            Especialidades · coincide con alguna de las seleccionadas
          </legend>
          {tags.isError ? (
            <LoadError
              message={errorMessage(tags.error)}
              retry={() => void tags.refetch()}
            />
          ) : (
            <div className="flex flex-wrap gap-3">
              {tags.data?.map((tag) => (
                <label
                  className="flex items-center gap-2 text-sm"
                  key={tag.id ?? tag.nombre}
                >
                  <input
                    type="checkbox"
                    checked={draft.etiquetas.includes(tag.nombre)}
                    onChange={(event) =>
                      setDraft({
                        ...draft,
                        etiquetas: event.target.checked
                          ? [...draft.etiquetas, tag.nombre]
                          : draft.etiquetas.filter((value) => value !== tag.nombre),
                      })
                    }
                  />
                  {tag.nombre}
                </label>
              ))}
            </div>
          )}
        </fieldset>
      </form>
      {query.isPending ? (
        <p role="status">Cargando oportunidades…</p>
      ) : query.isError ? (
        <LoadError
          message={errorMessage(query.error)}
          retry={() => void query.refetch()}
        />
      ) : (
        <>
          {!query.data.content.length ? (
            <Empty mensaje="No hay oportunidades activas para estos filtros." />
          ) : (
            <div className="space-y-3">
              {query.data.content.map((offer) => (
                <article
                  key={offer.id}
                  className={
                    'flex flex-wrap items-center justify-between gap-4 ' +
                    'rounded-2xl border border-[#2e2e33] bg-[#1a1a1c] p-5'
                  }
                >
                  <div>
                    <p className="text-xs text-[#ffc107]">Activa · {offer.modalidad}</p>
                    <h2 className="text-lg font-bold">{offer.titulo}</h2>
                    <p className="text-sm text-zinc-400">
                      {offer.ubicacion} · {offer.rangoSalarial}
                    </p>
                    <p className="text-xs text-zinc-500">
                      {offer.autor?.nombre} ·{' '}
                      {offer.etiquetas?.map((tag) => tag.nombre).join(' · ')}
                    </p>
                  </div>
                  <Link href={`/admin/oportunidades/${offer.id}`}>
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
      <Note>Las ofertas cerradas permanecen en la ficha del empleador.</Note>
    </div>
  );
}
