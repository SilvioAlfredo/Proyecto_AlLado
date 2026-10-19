'use client';

import { useState } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { oportunidadesService } from '@/features/oportunidades/services/oportunidadService';
import { etiquetasService } from '@/features/oportunidades/services/etiquetasService';
import { postulacionesService } from '@/features/postulaciones/services/postulacionesService';
import { gestionService } from '@/features/admin/services/gestionService';
import type { OportunidadLaboralDTO } from '@/types/trabajo';
import { Feedback, LoadError } from './Feedback';
import { Pagination } from './Pagination';
import { PageHeader, Empty, Note } from '@/components/ui/shared';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { errorMessage } from '@/lib/errors';
import { formatDate } from '@/lib/utils';

function Editor({
  offer,
  close,
  saved,
}: {
  offer: OportunidadLaboralDTO;
  close: () => void;
  saved: (offer: OportunidadLaboralDTO) => void;
}) {
  const [form, setForm] = useState(offer);
  const tags = useQuery({
    queryKey: ['etiquetas'],
    queryFn: () => etiquetasService.listar(),
  });
  const mutation = useMutation({
    mutationFn: () =>
      oportunidadesService.actualizar(offer.id!, {
        ...form,
        titulo: form.titulo.trim(),
        contenido: form.contenido.trim(),
      }),
    onSuccess: saved,
  });

  return (
    <form
      className="space-y-4 rounded-2xl border border-[#2e2e33] bg-[#1a1a1c] p-5"
      onSubmit={(event) => {
        event.preventDefault();
        mutation.mutate();
      }}
    >
      <h2 className="font-bold">Editar oferta</h2>
      {mutation.isError && <Feedback message={errorMessage(mutation.error)} />}
      <label className="block space-y-2 text-sm">
        <span>Título</span>
        <Input
          required
          maxLength={150}
          value={form.titulo}
          onChange={(event) => setForm({ ...form, titulo: event.target.value })}
        />
      </label>
      <label className="block space-y-2 text-sm">
        <span>Descripción</span>
        <textarea
          required
          rows={5}
          className="w-full rounded-xl border border-[#3a3a3f] bg-[#26262a] p-3"
          value={form.contenido}
          onChange={(event) => setForm({ ...form, contenido: event.target.value })}
        />
      </label>
      <div className="grid gap-3 sm:grid-cols-2">
        <label className="space-y-2 text-sm">
          <span>Ubicación</span>
          <Input
            maxLength={100}
            value={form.ubicacion ?? ''}
            onChange={(event) => setForm({ ...form, ubicacion: event.target.value })}
          />
        </label>
        <label className="space-y-2 text-sm">
          <span>Rango salarial</span>
          <Input
            maxLength={100}
            value={form.rangoSalarial ?? ''}
            onChange={(event) => setForm({ ...form, rangoSalarial: event.target.value })}
          />
        </label>
      </div>
      <label className="block space-y-2 text-sm">
        <span>Modalidad</span>
        <select
          required
          className="h-11 rounded-xl border border-[#3a3a3f] bg-[#26262a] px-3"
          value={form.modalidad ?? ''}
          onChange={(event) => setForm({ ...form, modalidad: event.target.value })}
        >
          <option value="" disabled>
            Elegí una modalidad
          </option>
          <option value="PRESENCIAL">Presencial</option>
          <option value="REMOTO">Remoto</option>
          <option value="HIBRIDO">Híbrido</option>
        </select>
      </label>
      <fieldset>
        <legend className="mb-2 text-sm">Etiquetas</legend>
        {tags.isError ? (
          <Feedback
            message={
              'No se pudo cargar el catálogo. ' + 'Se conservarán las etiquetas actuales.'
            }
          />
        ) : (
          <div className="flex flex-wrap gap-3">
            {tags.data?.map((tag) => (
              <label
                key={tag.id ?? tag.nombre}
                className="flex items-center gap-2 text-sm"
              >
                <input
                  type="checkbox"
                  checked={!!form.etiquetas?.some((value) => value.nombre === tag.nombre)}
                  onChange={(event) =>
                    setForm({
                      ...form,
                      etiquetas: event.target.checked
                        ? [...(form.etiquetas ?? []), tag]
                        : (form.etiquetas ?? []).filter(
                            (value) => value.nombre !== tag.nombre,
                          ),
                    })
                  }
                />
                {tag.nombre}
              </label>
            ))}
          </div>
        )}
      </fieldset>
      <Note>El empleador titular y el historial de postulaciones se conservan.</Note>
      <div className="flex gap-3">
        <Button type="submit" disabled={mutation.isPending}>
          {mutation.isPending ? 'Guardando…' : 'Guardar cambios'}
        </Button>
        <Button
          type="button"
          variant="outline"
          disabled={mutation.isPending}
          onClick={close}
        >
          Cancelar
        </Button>
      </div>
    </form>
  );
}

/**
 * Presenta una oferta, su edición y sus postulaciones paginadas.
 *
 * Las acciones disponibles dependen del estado de cada postulación; el backend
 * valida las transiciones y puede rechazar la eliminación de ofertas relacionadas.
 *
 * @param props - Opciones del detalle.
 * @param props.id - Identificador de la oportunidad laboral.
 */
export function OportunidadAdminDetail({ id }: { id: number }) {
  const valid = Number.isSafeInteger(id) && id > 0;
  const [editing, setEditing] = useState(false);
  const [page, setPage] = useState(0);
  const [notice, setNotice] = useState('');
  const client = useQueryClient();
  const router = useRouter();
  const query = useQuery({
    queryKey: ['admin-oportunidad', id],
    enabled: valid,
    retry: false,
    queryFn: () => oportunidadesService.obtenerPorId(id),
  });
  const applications = useQuery({
    queryKey: ['admin-oportunidad-postulaciones', id, page],
    enabled: valid && !!query.data,
    queryFn: () => gestionService.postulaciones(id, page),
  });
  const refresh = () => {
    client.invalidateQueries({ queryKey: ['admin-oportunidad', id] });
    client.invalidateQueries({ queryKey: ['admin-oportunidad-postulaciones', id] });
    client.invalidateQueries({ queryKey: ['admin-oportunidades'] });
    client.invalidateQueries({ queryKey: ['admin-persona', 'EMPLEADOR'] });
    client.invalidateQueries({ queryKey: ['admin-personas', 'empleadores'] });
  };
  const status = useMutation({
    mutationFn: async () => {
      if (!query.data) {
        throw new Error('La oportunidad no está disponible.');
      }
      return oportunidadesService.actualizar(id, {
        ...query.data,
        activa: !query.data.activa,
      });
    },
    onSuccess: (data) => {
      setNotice(data.activa ? 'Oferta reactivada.' : 'Oferta desactivada.');
      refresh();
    },
  });
  const deletion = useMutation({
    mutationFn: () => oportunidadesService.eliminar(id),
    onSuccess: () => {
      refresh();
      router.push('/admin/oportunidades');
    },
  });
  const applicationAction = useMutation({
    mutationFn: ({
      applicationId,
      action,
    }: {
      applicationId: number;
      action: 'seleccionar' | 'rechazar' | 'completar';
    }) => postulacionesService[action](applicationId),
    onSuccess: () => {
      setNotice('Estado de la postulación actualizado.');
      refresh();
    },
  });

  if (!valid) {
    return <Feedback message="El identificador de la oportunidad no es válido." />;
  }
  if (query.isPending) {
    return <p role="status">Cargando oportunidad…</p>;
  }
  if (query.isError) {
    return (
      <LoadError message={errorMessage(query.error)} retry={() => void query.refetch()} />
    );
  }
  const offer = query.data;

  return (
    <div className="space-y-6">
      <Link href="/admin/oportunidades" className="text-sm text-zinc-400">
        ← Volver a Oportunidades
      </Link>
      <PageHeader
        eyebrow="Gestión"
        title={offer.titulo}
        subtitle={`Oferta de ${offer.autor?.nombre ?? 'empleador'}`}
        action={
          <div className="flex gap-2">
            <Button
              variant="outline"
              disabled={status.isPending || editing}
              onClick={() => {
                if (
                  confirm(`${offer.activa ? 'Desactivar' : 'Reactivar'} esta oferta?`)
                ) {
                  status.mutate();
                }
              }}
            >
              {offer.activa ? 'Desactivar' : 'Reactivar'}
            </Button>
            <Button onClick={() => setEditing(!editing)}>
              {editing ? 'Cerrar edición' : 'Editar oferta'}
            </Button>
          </div>
        }
      />
      {notice && <Feedback success message={notice} />}
      {status.isError && <Feedback message={errorMessage(status.error)} />}
      {applicationAction.isError && (
        <Feedback message={errorMessage(applicationAction.error)} />
      )}
      {deletion.isError && <Feedback message={errorMessage(deletion.error)} />}
      {editing && (
        <Editor
          offer={offer}
          close={() => setEditing(false)}
          saved={() => {
            setEditing(false);
            setNotice('Oferta actualizada correctamente.');
            refresh();
          }}
        />
      )}
      <div className="grid gap-5 xl:grid-cols-[1fr_380px]">
        <section className="space-y-5">
          <div className="space-y-3 rounded-2xl border border-[#2e2e33] bg-[#1a1a1c] p-5">
            <p className="text-xs text-[#ffc107]">
              {offer.activa ? 'Oferta activa' : 'Oferta cerrada'} ·{' '}
              {formatDate(offer.fechaCreacion)}
            </p>
            <div className="grid gap-3 sm:grid-cols-2">
              <p className="text-sm text-zinc-300">
                Ubicación: {offer.ubicacion || 'Sin registrar'}
              </p>
              <p className="text-sm text-zinc-300">
                Modalidad: {offer.modalidad || 'Sin registrar'}
              </p>
              <p className="text-sm text-zinc-300">
                Rango salarial: {offer.rangoSalarial || 'Sin registrar'}
              </p>
              {offer.autor && (
                <Link
                  className="text-sm text-[#ffc107]"
                  href={`/admin/personas/empleadores/${offer.autor.id}`}
                >
                  Ver ficha del empleador →
                </Link>
              )}
            </div>
          </div>
          <div className="space-y-3 rounded-2xl border border-[#2e2e33] bg-[#1a1a1c] p-5">
            <h2 className="font-bold">Descripción</h2>
            <p className="whitespace-pre-wrap text-sm text-zinc-300">{offer.contenido}</p>
            <div className="flex flex-wrap gap-2">
              {offer.etiquetas?.map((tag) => (
                <span
                  key={tag.id ?? tag.nombre}
                  className="rounded-full bg-[#ffc107]/10 px-3 py-1 text-xs text-[#ffc107]"
                >
                  {tag.nombre}
                </span>
              ))}
            </div>
          </div>
          <Button
            variant="destructive"
            disabled={deletion.isPending || editing}
            onClick={() => {
              if (
                confirm(
                  `Eliminar definitivamente «${offer.titulo}»? ` +
                    'No se puede eliminar si tiene postulaciones.',
                )
              ) {
                deletion.mutate();
              }
            }}
          >
            Eliminar oferta
          </Button>
          <Note tone="danger">
            Desactivar conserva el historial e impide nuevas postulaciones. La eliminación
            es definitiva y requiere que no haya postulaciones asociadas.
          </Note>
        </section>
        <aside className="space-y-4">
          <h2 className="font-bold">
            Postulaciones ({applications.data?.totalElements ?? '…'})
          </h2>
          {applications.isPending ? (
            <p role="status">Cargando postulaciones…</p>
          ) : applications.isError ? (
            <LoadError
              message={errorMessage(applications.error)}
              retry={() => void applications.refetch()}
            />
          ) : (
            <>
              {!applications.data.content.length ? (
                <Empty mensaje="No hay postulaciones." />
              ) : (
                applications.data.content.map((application) => (
                  <article
                    key={application.id}
                    className="space-y-3 rounded-2xl border border-[#2e2e33] bg-[#1a1a1c] p-4"
                  >
                    <p className="text-xs text-[#ffc107]">
                      {application.estado} · {formatDate(application.fechaCreacion)}
                    </p>
                    {application.autor && (
                      <Link
                        className="font-bold hover:text-[#ffc107]"
                        href={`/admin/personas/trabajadores/${application.autor.id}`}
                      >
                        {application.autor.nombre}
                      </Link>
                    )}
                    <h3 className="font-semibold">{application.titulo}</h3>
                    <p className="whitespace-pre-wrap text-sm text-zinc-400">
                      {application.contenido}
                    </p>
                    <p className="text-sm">
                      Remuneración pretendida:{' '}
                      {application.remuneracionPretendida == null
                        ? 'Sin especificar'
                        : `$ ${application.remuneracionPretendida.toLocaleString('es-AR')}`}
                    </p>
                    <div className="flex flex-wrap gap-2">
                      {(['seleccionar', 'completar', 'rechazar'] as const)
                        .filter((action) =>
                          action === 'seleccionar'
                            ? application.estado === 'PENDIENTE'
                            : action === 'completar'
                              ? application.estado === 'SELECCIONADA'
                              : ['PENDIENTE', 'SELECCIONADA'].includes(
                                  application.estado,
                                ),
                        )
                        .map((action) => (
                          <Button
                            key={action}
                            size="sm"
                            variant={action === 'rechazar' ? 'outline' : 'default'}
                            disabled={applicationAction.isPending}
                            onClick={() => {
                              const actionLabel = {
                                seleccionar: 'Seleccionar',
                                completar: 'Completar',
                                rechazar: 'Rechazar',
                              }[action];

                              if (
                                application.id &&
                                confirm(`¿${actionLabel} esta postulación?`)
                              ) {
                                applicationAction.mutate({
                                  applicationId: application.id,
                                  action,
                                });
                              }
                            }}
                          >
                            {
                              {
                                seleccionar: 'Seleccionar',
                                completar: 'Completar',
                                rechazar: 'Rechazar',
                              }[action]
                            }
                          </Button>
                        ))}
                    </div>
                  </article>
                ))
              )}
              <Pagination
                {...applications.data}
                busy={applications.isFetching}
                change={setPage}
              />
            </>
          )}
        </aside>
      </div>
    </div>
  );
}
