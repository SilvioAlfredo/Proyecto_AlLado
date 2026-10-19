'use client';

import { useState } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { publicidadesService } from '@/features/comunidad/services/moderacionService';
import { empleadorService } from '@/features/usuario/services/empleadorService';
import type { PublicidadDTO } from '@/types/comunidad';
import { PageHeader, Note } from '@/components/ui/shared';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Feedback, LoadError } from './Feedback';
import { errorMessage } from '@/lib/errors';
import { safeWebUrl } from '../lib/publicidad';

const card = 'space-y-4 rounded-2xl border border-[#2e2e33] bg-[#1a1a1c] p-5';

/**
 * Edita los datos del anuncio y muestra una vista previa sin guardarlos aún.
 *
 * Valida fechas y URLs antes de enviar; conserva el anunciante al editar.
 * La eliminación requiere confirmación y los permisos se verifican en el backend.
 */
function PublicidadEditor({ initial }: { initial: PublicidadDTO }) {
  const [form, setForm] = useState(initial);
  const [validation, setValidation] = useState('');
  const cache = useQueryClient();
  const router = useRouter();
  const nuevo = !initial.id;
  const owners = useQuery({
    queryKey: ['admin-anunciantes'],
    queryFn: () => empleadorService.obtenerTodos(),
    enabled: nuevo,
  });
  const save = useMutation({
    mutationFn: () =>
      nuevo
        ? publicidadesService.crear(form)
        : publicidadesService.actualizar(initial.id!, form),
    onSuccess: (data) => {
      cache.setQueryData(['admin-publicidad', data.id], data);
      void cache.invalidateQueries({ queryKey: ['admin-publicidades'] });
      void cache.invalidateQueries({ queryKey: ['publicidades'] });
      if (nuevo) {
        router.replace(`/admin/publicidad/${data.id}`);
      }
    },
  });
  const remove = useMutation({
    mutationFn: () => publicidadesService.eliminar(initial.id!),
    onSuccess: () => {
      cache.removeQueries({ queryKey: ['admin-publicidad', initial.id] });
      void cache.invalidateQueries({ queryKey: ['admin-publicidades'] });
      void cache.invalidateQueries({ queryKey: ['publicidades'] });
      router.push('/admin/publicidad');
    },
  });
  const busy = save.isPending || remove.isPending;
  const update = (field: keyof PublicidadDTO, value: string | boolean) => {
    setValidation('');
    save.reset();
    setForm((previous) => ({ ...previous, [field]: value }));
  };

  return (
    <div className="grid gap-6 lg:grid-cols-[2fr_1fr]">
      <form
        className={card}
        onSubmit={(event) => {
          event.preventDefault();
          setValidation('');
          if (!form.titulo.trim() || !form.anunciante?.id) {
            setValidation('Completá título y anunciante.');
            return;
          }
          if (!form.fechaInicio || !form.fechaFin || form.fechaFin < form.fechaInicio) {
            setValidation(
              'La fecha de fin debe ser igual o posterior a la fecha de inicio.',
            );
            return;
          }
          if (
            (form.urlImagen && !safeWebUrl(form.urlImagen)) ||
            (form.urlDestino && !safeWebUrl(form.urlDestino))
          ) {
            setValidation('Las URLs deben comenzar con http:// o https://.');
            return;
          }
          save.mutate();
        }}
      >
        <label className="block">
          Título
          <Input
            required
            maxLength={120}
            value={form.titulo}
            onChange={(event) => update('titulo', event.target.value)}
          />
        </label>
        {nuevo ? (
          <label className="block">
            Anunciante
            <select
              required
              aria-label="Anunciante"
              className="mt-2 w-full rounded-xl border border-zinc-700 bg-[#1a1a1c] p-3"
              value={form.anunciante?.id || ''}
              onChange={(event) => {
                const owner = owners.data?.find(
                  (item) => item.id === Number(event.target.value),
                );
                setForm({
                  ...form,
                  anunciante: owner
                    ? { id: owner.id, nombre: owner.nombre, email: owner.email }
                    : undefined,
                });
              }}
            >
              <option value="">Seleccionar empleador</option>
              {owners.data?.map((owner) => (
                <option key={owner.id} value={owner.id}>
                  {owner.nombre} · {owner.email}
                </option>
              ))}
            </select>
          </label>
        ) : (
          <div>
            <p>Anunciante</p>
            <p className="text-zinc-400">
              {form.anunciante?.nombre} · {form.anunciante?.email}
            </p>
          </div>
        )}
        {nuevo && owners.isPending && <p role="status">Cargando anunciantes…</p>}
        {nuevo && owners.isError && (
          <LoadError
            message={errorMessage(owners.error)}
            retry={() => void owners.refetch()}
          />
        )}
        <label className="block">
          Contenido
          <textarea
            className="mt-2 min-h-32 w-full rounded-xl border border-zinc-700 bg-[#1a1a1c] p-3"
            value={form.contenido || ''}
            onChange={(event) => update('contenido', event.target.value)}
          />
        </label>
        <label className="block">
          URL de imagen
          <Input
            type="url"
            maxLength={255}
            value={form.urlImagen || ''}
            onChange={(event) => update('urlImagen', event.target.value)}
          />
        </label>
        <label className="block">
          URL de destino
          <Input
            type="url"
            maxLength={255}
            value={form.urlDestino || ''}
            onChange={(event) => update('urlDestino', event.target.value)}
          />
        </label>
        <div className="grid gap-3 sm:grid-cols-2">
          <label>
            Fecha de inicio
            <Input
              required
              type="date"
              value={form.fechaInicio || ''}
              onChange={(event) => update('fechaInicio', event.target.value)}
            />
          </label>
          <label>
            Fecha de fin
            <Input
              required
              type="date"
              min={form.fechaInicio}
              value={form.fechaFin || ''}
              onChange={(event) => update('fechaFin', event.target.value)}
            />
          </label>
        </div>
        {!nuevo && (
          <label className="flex items-center gap-2">
            <input
              type="checkbox"
              checked={form.activa ?? true}
              onChange={(event) => update('activa', event.target.checked)}
            />
            Publicidad activa
          </label>
        )}
        <Note>El anunciante debe ser un empleador con suscripción PRO vigente.</Note>
        {validation && <Feedback message={validation} />}
        {save.isError && <Feedback message={errorMessage(save.error)} />}
        {remove.isError && <Feedback message={errorMessage(remove.error)} />}
        {save.isSuccess && <Feedback success message="Publicidad guardada." />}
        <div className="flex flex-wrap gap-3">
          <Button disabled={busy || (nuevo && !owners.data)} type="submit">
            {save.isPending
              ? 'Guardando…'
              : nuevo
                ? 'Crear publicidad'
                : 'Guardar cambios'}
          </Button>
          {!nuevo && (
            <Button
              type="button"
              variant="outline"
              disabled={busy}
              onClick={() => {
                if (confirm(`¿Eliminar definitivamente «${initial.titulo}»?`)) {
                  remove.mutate();
                }
              }}
            >
              Eliminar publicidad
            </Button>
          )}
        </div>
      </form>
      <aside className={card}>
        <p className="text-xs text-[#ffc107]">VISTA PREVIA</p>
        <h2 className="font-bold">Así se verá el anuncio</h2>
        {safeWebUrl(form.urlImagen) && (
          // Las imágenes publicitarias admiten dominios externos configurados por el anunciante.
          // eslint-disable-next-line @next/next/no-img-element
          <img
            src={safeWebUrl(form.urlImagen)}
            alt={form.titulo || 'Imagen del anuncio'}
            className="aspect-video w-full rounded-xl object-cover"
          />
        )}
        <h3 className="text-xl font-bold">{form.titulo || 'Título del anuncio'}</h3>
        <p className="whitespace-pre-wrap text-sm text-zinc-400">
          {form.contenido || 'Contenido del anuncio'}
        </p>
        {safeWebUrl(form.urlDestino) && (
          <a
            href={safeWebUrl(form.urlDestino)}
            target="_blank"
            rel="noopener noreferrer"
            className="inline-block rounded-xl border border-zinc-700 px-4 py-2"
          >
            Conocer más
          </a>
        )}
      </aside>
    </div>
  );
}

/**
 * Carga un anuncio para editarlo o presenta el formulario de creación.
 *
 * El anunciante es un empleador y el backend exige su suscripción PRO vigente.
 *
 * @param props - Opciones del formulario.
 * @param props.id - Identificador de publicidad. Si se omite, crea un anuncio nuevo.
 */
export function PublicidadAdminDetail({ id }: { id?: number }) {
  const valid = id == null || (Number.isSafeInteger(id) && id > 0);
  const query = useQuery({
    queryKey: ['admin-publicidad', id],
    queryFn: () => publicidadesService.obtener(id!),
    enabled: valid && id != null,
  });

  if (!valid) {
    return <Feedback message="Publicidad inválida." />;
  }
  if (id != null && query.isPending) {
    return <p role="status">Cargando publicidad…</p>;
  }
  if (id != null && query.isError) {
    return (
      <LoadError message={errorMessage(query.error)} retry={() => void query.refetch()} />
    );
  }
  return (
    <div className="space-y-6">
      <Link href="/admin/publicidad" className="text-[#ffc107]">
        ← Volver a Publicidad
      </Link>
      <PageHeader
        eyebrow="Gestión"
        title={id == null ? 'Nueva publicidad' : 'Editar publicidad'}
        subtitle={query.data?.titulo}
      />
      <PublicidadEditor
        key={id || 'nueva'}
        initial={
          query.data || {
            titulo: '',
            contenido: '',
            fechaInicio: '',
            fechaFin: '',
            activa: true,
          }
        }
      />
    </div>
  );
}
