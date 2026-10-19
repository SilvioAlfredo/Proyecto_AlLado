'use client';

import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { etiquetasService } from '@/features/oportunidades/services/etiquetasService';
import { PageHeader, Empty, Note } from '@/components/ui/shared';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Feedback, LoadError } from '@/features/admin/components/Feedback';
import { errorMessage } from '@/lib/errors';

export default function Page() {
  const [search, setSearch] = useState('');
  const [nombre, setNombre] = useState('');
  const cache = useQueryClient();
  const query = useQuery({
    queryKey: ['etiquetas'],
    queryFn: () => etiquetasService.listar(),
  });
  const create = useMutation({
    mutationFn: () => etiquetasService.crear(nombre.trim()),
    onSuccess: () => {
      setNombre('');
      void cache.invalidateQueries({ queryKey: ['etiquetas'] });
    },
  });
  const remove = useMutation({
    mutationFn: (id: number) => etiquetasService.eliminar(id),
    onSuccess: () => {
      void cache.invalidateQueries({ queryKey: ['etiquetas'] });
    },
  });
  const items =
    query.data
      ?.filter((item) =>
        item.nombre.toLocaleLowerCase().includes(search.trim().toLocaleLowerCase()),
      )
      .sort((a, b) => a.nombre.localeCompare(b.nombre, 'es')) || [];
  const busy = create.isPending || remove.isPending;

  return (
    <div className="space-y-6">
      <PageHeader
        eyebrow="Gestión"
        title="Etiquetas"
        subtitle="Organizá el catálogo de especialidades."
      />
      <div className="grid gap-6 lg:grid-cols-[2fr_1fr]">
        <section className="space-y-4">
          <Input
            aria-label="Buscar etiqueta"
            placeholder="Buscar etiqueta"
            value={search}
            onChange={(event) => setSearch(event.target.value)}
          />
          {query.isPending ? (
            <p role="status">Cargando etiquetas…</p>
          ) : query.isError ? (
            <LoadError
              message={errorMessage(query.error)}
              retry={() => void query.refetch()}
            />
          ) : (
            <>
              <h2 className="font-bold">Etiquetas disponibles ({items.length})</h2>
              {!items.length ? (
                <Empty mensaje="No hay etiquetas para esta búsqueda." />
              ) : (
                <div className="grid gap-3 sm:grid-cols-2">
                  {items.map((item) => (
                    <article
                      key={item.id}
                      className={
                        'flex items-center justify-between gap-3 rounded-xl ' +
                        'border border-zinc-700 bg-[#1a1a1c] p-4'
                      }
                    >
                      <span className="font-semibold">{item.nombre}</span>
                      <Button
                        size="sm"
                        variant="outline"
                        disabled={busy || !item.id}
                        onClick={() => {
                          if (
                            confirm(
                              `¿Eliminar definitivamente la etiqueta «${item.nombre}»?`,
                            )
                          ) {
                            remove.mutate(item.id!);
                          }
                        }}
                      >
                        Eliminar
                      </Button>
                    </article>
                  ))}
                </div>
              )}
            </>
          )}
          {remove.isError && <Feedback message={errorMessage(remove.error)} />}
          {remove.isSuccess && <Feedback success message="Etiqueta eliminada." />}
        </section>
        <form
          className="h-fit space-y-4 rounded-2xl border border-zinc-700 bg-[#1a1a1c] p-5"
          onSubmit={(event) => {
            event.preventDefault();
            if (nombre.trim()) {
              create.mutate();
            }
          }}
        >
          <h2 className="font-bold">Agregar etiqueta</h2>
          <p className="text-sm text-zinc-400">
            Las etiquetas clasifican oportunidades y especialidades de trabajadores.
          </p>
          <label className="block">
            Nombre
            <Input
              required
              maxLength={50}
              value={nombre}
              onChange={(event) => {
                create.reset();
                setNombre(event.target.value);
              }}
            />
          </label>
          <Button type="submit" disabled={busy || !nombre.trim()}>
            {create.isPending ? 'Creando…' : 'Crear etiqueta'}
          </Button>
          {create.isError && <Feedback message={errorMessage(create.error)} />}
          {create.isSuccess && (
            <Feedback
              success
              message="Etiqueta disponible. Si ya existía, se conservó el registro."
            />
          )}
          <Note>
            Eliminar es irreversible. Una etiqueta asociada a trabajadores u oportunidades
            no puede eliminarse.
          </Note>
        </form>
      </div>
    </div>
  );
}
