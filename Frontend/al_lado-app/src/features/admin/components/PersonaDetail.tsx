'use client';

import { useState } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import api from '@/lib/api';
import { errorMessage } from '@/lib/errors';
import { trabajadorService } from '@/features/usuario/services/trabajadorService';
import { empleadorService } from '@/features/usuario/services/empleadorService';
import { usuariosService } from '@/features/usuario/services/usuariosService';
import { etiquetasService } from '@/features/oportunidades/services/etiquetasService';
import type { TrabajadorDTO, EmpleadorDTO } from '@/types/usuario';
import type { ValoracionDTO } from '@/types/comunidad';
import { PageHeader, Avatar, Empty, Note } from '@/components/ui/shared';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Feedback, LoadError } from './Feedback';

export type Role = 'TRABAJADOR' | 'EMPLEADOR';

type Person = TrabajadorDTO | EmpleadorDTO;

const card = 'space-y-3 rounded-2xl border border-[#2e2e33] bg-[#1a1a1c] p-5';

function safeUrl(value?: string): string | undefined {
  try {
    const url = new URL(value ?? '');
    return ['https:', 'http:'].includes(url.protocol) ? url.href : undefined;
  } catch {
    return undefined;
  }
}

function PersonEditor({
  person,
  role,
  close,
  saved,
}: {
  person: Person;
  role: Role;
  close: () => void;
  saved: () => void;
}) {
  const [form, setForm] = useState(person);
  const worker = form as TrabajadorDTO;
  const tags = useQuery({
    queryKey: ['etiquetas'],
    queryFn: () => etiquetasService.listar(),
    enabled: role === 'TRABAJADOR',
  });
  const mutation = useMutation({
    mutationFn: async () => {
      // Los endpoints específicos actualizan la entidad gestionada y conservan sus relaciones.
      const payload = {
        ...form,
        nombre: form.nombre.trim(),
        email: form.email.trim(),
        telefono: form.telefono?.trim(),
        password: undefined,
      };
      await api.put(
        `/${role === 'TRABAJADOR' ? 'trabajadores' : 'empleadores'}/${person.id}`,
        payload,
      );
    },
    onSuccess: saved,
  });
  const update = (key: string, value: string) =>
    setForm((previous) => ({ ...previous, [key]: value }));

  return (
    <form
      className={card}
      onSubmit={(event) => {
        event.preventDefault();
        mutation.mutate();
      }}
    >
      <h2 className="font-bold">
        Editar {role === 'TRABAJADOR' ? 'trabajador' : 'empleador'}
      </h2>
      {mutation.isError && <Feedback message={errorMessage(mutation.error)} />}
      <div className="grid gap-4 sm:grid-cols-2">
        {(['nombre', 'email', 'telefono'] as const).map((key) => (
          <label className="space-y-2 text-sm" key={key}>
            <span>{{ nombre: 'Nombre', email: 'Email', telefono: 'Teléfono' }[key]}</span>
            <Input
              required
              type={key === 'email' ? 'email' : key === 'telefono' ? 'tel' : 'text'}
              maxLength={key === 'nombre' ? 100 : key === 'email' ? 150 : 20}
              value={form[key] ?? ''}
              onChange={(event) => update(key, event.target.value)}
            />
          </label>
        ))}
      </div>
      {role === 'TRABAJADOR' && (
        <div className="space-y-4">
          {(['biografia', 'certificados', 'sector', 'fotografia'] as const).map((key) => (
            <label className="block space-y-2 text-sm" key={key}>
              <span>
                {
                  {
                    biografia: 'Biografía',
                    certificados: 'Certificados',
                    sector: 'Zona de cobertura',
                    fotografia: 'URL de fotografía',
                  }[key]
                }
              </span>
              {key === 'biografia' || key === 'certificados' ? (
                <textarea
                  className="w-full rounded-xl border border-[#3a3a3f] bg-[#26262a] p-3"
                  rows={3}
                  value={worker[key] ?? ''}
                  onChange={(event) => update(key, event.target.value)}
                />
              ) : (
                <Input
                  type={key === 'fotografia' ? 'url' : 'text'}
                  value={worker[key] ?? ''}
                  onChange={(event) => update(key, event.target.value)}
                />
              )}
            </label>
          ))}
          <fieldset>
            <legend className="mb-2 text-sm">Especialidades</legend>
            {tags.isError ? (
              <Feedback
                message={
                  'No se pudo cargar el catálogo. ' +
                  'Las especialidades actuales se conservarán.'
                }
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
                      checked={
                        !!worker.especialidades?.some(
                          (item) => item.nombre === tag.nombre,
                        )
                      }
                      onChange={(event) =>
                        setForm((previous) => ({
                          ...previous,
                          especialidades: event.target.checked
                            ? [...((previous as TrabajadorDTO).especialidades ?? []), tag]
                            : ((previous as TrabajadorDTO).especialidades ?? []).filter(
                                (item) => item.nombre !== tag.nombre,
                              ),
                        }))
                      }
                    />
                    {tag.nombre}
                  </label>
                ))}
              </div>
            )}
          </fieldset>
        </div>
      )}
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
 * Presenta la ficha de un trabajador o empleador y sus acciones de administración.
 *
 * Utiliza los endpoints del rol para editar conservando sus relaciones.
 * Las acciones de suspensión y eliminación requieren confirmación.
 *
 * @param props - Opciones de la ficha.
 * @param props.id - Identificador del usuario.
 * @param props.role - Rol que determina el servicio y los datos de la ficha.
 */
export function PersonaDetail({ id, role }: { id: number; role: Role }) {
  const client = useQueryClient();
  const router = useRouter();
  const [editing, setEditing] = useState(false);
  const [notice, setNotice] = useState('');
  const [page, setPage] = useState(0);
  const [showReviews, setShowReviews] = useState(false);
  const valid = Number.isSafeInteger(id) && id > 0;
  const query = useQuery<Person>({
    queryKey: ['admin-persona', role, id],
    enabled: valid,
    retry: false,
    queryFn: () =>
      role === 'TRABAJADOR'
        ? trabajadorService.obtenerPorId(id)
        : empleadorService.obtenerPorId(id),
  });
  const reviews = useQuery({
    queryKey: ['admin-valoraciones', id, page],
    enabled: valid && role === 'TRABAJADOR' && showReviews,
    queryFn: async () =>
      (
        await api.get<ValoracionDTO[]>(`/valoraciones/trabajador/${id}`, {
          params: { page, size: 10 },
        })
      ).data,
  });
  const action = useMutation({
    mutationFn: async (operation: 'DELETE' | 'STATUS') => {
      if (operation === 'DELETE') {
        await api.delete(
          `/${role === 'TRABAJADOR' ? 'trabajadores' : 'empleadores'}/${id}`,
        );
      } else if (query.data?.habilitado === false) {
        await usuariosService.habilitar(id);
      } else {
        await usuariosService.deshabilitar(id);
      }
      return operation;
    },
    onSuccess: (operation) => {
      client.invalidateQueries({ queryKey: ['admin-personas'] });
      client.invalidateQueries({ queryKey: ['admin-persona', role, id] });
      if (operation === 'DELETE') {
        router.push(`/admin/personas?rol=${role}`);
      } else {
        setNotice('Estado de la cuenta actualizado.');
      }
    },
  });

  if (!valid) {
    return <Feedback message="El identificador de la persona no es válido." />;
  }
  if (query.isPending) {
    return <p role="status">Cargando ficha…</p>;
  }
  if (query.isError) {
    return (
      <LoadError message={errorMessage(query.error)} retry={() => void query.refetch()} />
    );
  }
  const person = query.data;

  if (!person) {
    return <Empty mensaje="No se encontró esta persona." />;
  }
  const worker = role === 'TRABAJADOR' ? (person as TrabajadorDTO) : null;
  const employer = role === 'EMPLEADOR' ? (person as EmpleadorDTO) : null;

  return (
    <div className="space-y-6">
      <Link
        href={`/admin/personas?rol=${role}`}
        className="text-sm text-zinc-400 hover:text-white"
      >
        ← Volver a Personas
      </Link>
      <PageHeader
        title={`Ficha del ${worker ? 'trabajador' : 'empleador'}`}
        subtitle={
          worker ? 'Perfil y datos registrados.' : 'Cuenta, empresa y oportunidades.'
        }
        action={
          <Button
            onClick={() => {
              setEditing(!editing);
              setNotice('');
            }}
          >
            {editing ? 'Cerrar edición' : `Editar ${worker ? 'trabajador' : 'empleador'}`}
          </Button>
        }
      />
      {notice && <Feedback success message={notice} />}
      {action.isError && <Feedback message={errorMessage(action.error)} />}
      <section className={`${card} flex flex-wrap items-center justify-between gap-4`}>
        <div className="flex items-center gap-4">
          <Avatar nombre={person.nombre} size="lg" />
          <div>
            <p className="text-xs text-[#ffc107]">
              {worker ? 'Trabajador' : 'Empleador'} ·{' '}
              {person.habilitado === false ? 'Suspendido' : 'Activo'}
            </p>
            <h2 className="text-xl font-bold">{person.nombre}</h2>
            <p className="break-all text-sm text-zinc-400">{person.email}</p>
          </div>
        </div>
        <p className="text-sm text-zinc-300">
          Teléfono: {person.telefono || 'Sin teléfono'}
        </p>
      </section>
      {editing && (
        <PersonEditor
          person={person}
          role={role}
          close={() => setEditing(false)}
          saved={() => {
            setEditing(false);
            setNotice('Los datos se guardaron correctamente.');
            client.invalidateQueries({ queryKey: ['admin-persona', role, id] });
            client.invalidateQueries({ queryKey: ['admin-personas'] });
          }}
        />
      )}
      {worker && (
        <div className="grid gap-5 lg:grid-cols-2">
          <section className={card}>
            <h2 className="font-bold">Biografía</h2>
            <p className="whitespace-pre-wrap text-sm text-zinc-400">
              {worker.biografia || 'Sin biografía registrada.'}
            </p>
            <h3 className="font-bold">Especialidades</h3>
            <div className="flex flex-wrap gap-2">
              {worker.especialidades?.length ? (
                worker.especialidades.map((tag) => (
                  <span
                    key={tag.id ?? tag.nombre}
                    className="rounded-full bg-[#ffc107]/10 px-3 py-1 text-xs text-[#ffc107]"
                  >
                    {tag.nombre}
                  </span>
                ))
              ) : (
                <p className="text-sm text-zinc-400">Sin especialidades.</p>
              )}
            </div>
            <h3 className="font-bold">Certificados</h3>
            <p className="whitespace-pre-wrap text-sm text-zinc-400">
              {worker.certificados || 'Sin certificados registrados.'}
            </p>
            <h3 className="font-bold">Zona de cobertura</h3>
            <p className="text-sm text-zinc-400">
              {worker.sector || 'Sin zona registrada.'}
            </p>
            <h3 className="font-bold">Horarios semanales</h3>
            {worker.horarios?.length ? (
              worker.horarios.map((hour, index) => (
                <p className="text-sm text-zinc-400" key={hour.id ?? index}>
                  {hour.diaSemana}: {hour.horaInicio}–{hour.horaFin}
                </p>
              ))
            ) : (
              <p className="text-sm text-zinc-400">Sin horarios registrados.</p>
            )}
          </section>
          <section className={card}>
            <h2 className="font-bold">Portafolio</h2>
            {worker.portafolioItems?.length ? (
              worker.portafolioItems.map((item, index) => (
                <article
                  key={item.id ?? index}
                  className="space-y-2 rounded-xl border border-[#3a3a3f] p-4"
                >
                  <h3 className="font-semibold">{item.titulo}</h3>
                  <p className="text-sm text-zinc-400">{item.descripcion}</p>
                  {safeUrl(item.urlArchivo) && (
                    <a
                      className="text-sm text-[#ffc107]"
                      href={safeUrl(item.urlArchivo)}
                      target="_blank"
                      rel="noopener noreferrer"
                    >
                      Abrir archivo ↗
                    </a>
                  )}
                </article>
              ))
            ) : (
              <Empty mensaje="Sin trabajos en el portafolio." />
            )}
            <Button variant="outline" onClick={() => setShowReviews(!showReviews)}>
              {showReviews ? 'Ocultar valoraciones' : 'Ver valoraciones'}
            </Button>
            {showReviews && (
              <div className="space-y-3">
                {reviews.isPending ? (
                  <p role="status">Cargando valoraciones…</p>
                ) : reviews.isError ? (
                  <LoadError
                    message={errorMessage(reviews.error)}
                    retry={() => void reviews.refetch()}
                  />
                ) : (
                  <>
                    {reviews.data?.length ? (
                      reviews.data.map((review, index) => (
                        <article
                          key={review.id ?? index}
                          className="border-t border-[#3a3a3f] pt-3"
                        >
                          <p className="text-[#ffc107]">★ {review.puntuacion}/5</p>
                          <p className="text-sm">
                            {review.comentario || 'Sin comentario.'}
                          </p>
                          <p className="text-xs text-zinc-400">{review.autor?.nombre}</p>
                        </article>
                      ))
                    ) : (
                      <p className="text-sm text-zinc-400">
                        No hay valoraciones en esta página.
                      </p>
                    )}
                    <div className="flex items-center gap-2">
                      <Button
                        size="sm"
                        variant="outline"
                        disabled={page === 0 || reviews.isFetching}
                        onClick={() => setPage(page - 1)}
                      >
                        Anterior
                      </Button>
                      <span className="text-xs">Página {page + 1}</span>
                      <Button
                        size="sm"
                        variant="outline"
                        disabled={(reviews.data?.length ?? 0) < 10 || reviews.isFetching}
                        onClick={() => setPage(page + 1)}
                      >
                        Siguiente
                      </Button>
                    </div>
                  </>
                )}
              </div>
            )}
          </section>
        </div>
      )}
      {employer && (
        <div className="grid gap-5 lg:grid-cols-2">
          <section className={card}>
            <h2 className="font-bold">Perfil empresarial</h2>
            {employer.perfilEmpresarial ? (
              <>
                <p className="text-xs text-[#ffc107]">
                  {employer.perfilEmpresarial.verificado ? 'Verificado' : 'No verificado'}
                </p>
                <h3 className="text-lg font-semibold">
                  {employer.perfilEmpresarial.nombreEmpresa}
                </h3>
                <p className="text-sm text-zinc-400">
                  CUIT: {employer.perfilEmpresarial.cuit || 'Sin registrar'}
                </p>
                <p className="whitespace-pre-wrap text-sm text-zinc-400">
                  {employer.perfilEmpresarial.descripcion || 'Sin descripción.'}
                </p>
                {safeUrl(employer.perfilEmpresarial.sitioWeb) && (
                  <a
                    className="block text-sm text-[#ffc107]"
                    href={safeUrl(employer.perfilEmpresarial.sitioWeb)}
                    target="_blank"
                    rel="noopener noreferrer"
                  >
                    Sitio de la empresa ↗
                  </a>
                )}
                <Link
                  className="inline-block text-sm text-[#ffc107]"
                  href="/admin/empresas"
                >
                  Ir a revisión de empresas →
                </Link>
              </>
            ) : (
              <Empty mensaje="Este empleador no tiene un perfil empresarial." />
            )}
          </section>
          <section className={card}>
            <h2 className="font-bold">
              Oportunidades ({employer.oportunidades?.length ?? 0})
            </h2>
            {employer.oportunidades?.length ? (
              employer.oportunidades.map((offer) => (
                <article
                  className="space-y-2 rounded-xl border border-[#3a3a3f] p-4"
                  key={offer.id}
                >
                  <p className="text-xs text-[#ffc107]">
                    {offer.activa ? 'Activa' : 'Cerrada'} · {offer.modalidad}
                  </p>
                  <h3 className="font-semibold">{offer.titulo}</h3>
                  <p className="text-sm text-zinc-400">{offer.contenido}</p>
                  <p className="text-xs text-zinc-500">
                    {offer.ubicacion} · {offer.rangoSalarial}
                  </p>
                  <Link
                    className="text-sm text-[#ffc107]"
                    href={`/admin/oportunidades/${offer.id}`}
                  >
                    Ver detalle →
                  </Link>
                </article>
              ))
            ) : (
              <Empty mensaje="Sin oportunidades publicadas." />
            )}
          </section>
        </div>
      )}
      <section className="flex flex-wrap items-center gap-3">
        <Button
          variant="outline"
          disabled={action.isPending}
          onClick={() => {
            if (
              confirm(
                (person.habilitado === false ? 'Rehabilitar' : 'Suspender') +
                  ` la cuenta de ${person.nombre}?`,
              )
            ) {
              action.mutate('STATUS');
            }
          }}
        >
          {person.habilitado === false ? 'Rehabilitar cuenta' : 'Suspender cuenta'}
        </Button>
        <Button
          variant="destructive"
          disabled={action.isPending}
          onClick={() => {
            if (
              confirm(
                `Eliminar definitivamente a ${person.nombre}? ` +
                  'Esta acción no se puede deshacer y puede ser rechazada ' +
                  'si hay registros asociados.',
              )
            ) {
              action.mutate('DELETE');
            }
          }}
        >
          {action.isPending
            ? 'Procesando…'
            : `Eliminar ${worker ? 'trabajador' : 'empleador'}`}
        </Button>
      </section>
      <Note tone="danger">
        La suspensión impide iniciar sesión y conserva el historial. La eliminación es
        definitiva y está sujeta a los registros asociados.
      </Note>
    </div>
  );
}
