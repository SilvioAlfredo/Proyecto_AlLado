'use client';

import { Suspense, useState } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import Link from 'next/link';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { trabajadorService } from '@/features/usuario/services/trabajadorService';
import { empleadorService } from '@/features/usuario/services/empleadorService';
import { administradoresService } from '@/features/usuario/services/administradoresService';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { PageHeader, Tabs, Avatar, Empty, Note } from '@/components/ui/shared';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { Feedback, LoadError } from '@/features/admin/components/Feedback';
import { errorMessage } from '@/lib/errors';

type Tab = 'TRABAJADOR' | 'EMPLEADOR' | 'ADMIN';

function PersonasContent() {
  const params = useSearchParams();
  const router = useRouter();
  const selected = params.get('rol');
  const tab: Tab =
    selected === 'EMPLEADOR' || selected === 'ADMIN' ? selected : 'TRABAJADOR';
  const setTab = (role: Tab) =>
    router.replace(`/admin/personas?rol=${role}`, { scroll: false });
  const [search, setSearch] = useState('');
  const { userId } = useAuth();
  const client = useQueryClient();
  const deletion = useMutation({
    mutationFn: (id: number) => administradoresService.eliminar(id),
    onSuccess: () =>
      client.invalidateQueries({ queryKey: ['admin-personas', 'administradores'] }),
  });
  const workers = useQuery({
    queryKey: ['admin-personas', 'trabajadores'],
    queryFn: () => trabajadorService.obtenerTodos(),
  });
  const employers = useQuery({
    queryKey: ['admin-personas', 'empleadores'],
    queryFn: () => empleadorService.obtenerTodos(),
  });
  const admins = useQuery({
    queryKey: ['admin-personas', 'administradores'],
    queryFn: () => administradoresService.listar(),
  });
  const query = tab === 'TRABAJADOR' ? workers : tab === 'EMPLEADOR' ? employers : admins;
  const term = search.trim().toLocaleLowerCase();
  const people = (query.data ?? []).filter((person) =>
    [person.nombre, person.email, person.telefono ?? ''].some((value) =>
      value.toLocaleLowerCase().includes(term),
    ),
  );

  return (
    <div className="space-y-6">
      <PageHeader
        eyebrow="Personas"
        title="Consultá los perfiles de la comunidad"
        action={
          <Link href="/admin/personas/administradores/nuevo">
            <Button>Agregar administrador</Button>
          </Link>
        }
      />
      <section className="rounded-xl border border-[#2e2e33] bg-[#1a1a1c] p-4">
        <p className="text-xs font-semibold uppercase tracking-[0.2em] text-[#ffc107]">
          Estado del listado
        </p>
        <div className="mt-3 flex flex-wrap items-end justify-between gap-3">
          <div>
            <p className="text-2xl font-black text-white">{query.data?.length ?? 0}</p>
            <p className="text-sm text-zinc-400">
              {tab === 'TRABAJADOR'
                ? 'trabajadores'
                : tab === 'EMPLEADOR'
                  ? 'empleadores'
                  : 'administradores'}
            </p>
          </div>
          <span className="rounded-full bg-[#ffc107]/10 px-2.5 py-1 text-xs font-bold text-[#ffc107]">
            {term ? `Filtro: “${term}”` : 'Sin filtro activo'}
          </span>
        </div>
      </section>
      <div className="flex flex-col justify-between gap-3 xl:flex-row">
        <Tabs
          value={tab}
          onChange={setTab}
          options={[
            { value: 'TRABAJADOR', label: 'Trabajadores' },
            { value: 'EMPLEADOR', label: 'Empleadores' },
            { value: 'ADMIN', label: 'Administradores' },
          ]}
        />
        <Input
          className="max-w-sm"
          aria-label="Buscar personas"
          placeholder="Nombre, email o teléfono"
          value={search}
          onChange={(event) => setSearch(event.target.value)}
        />
      </div>
      {deletion.isError && <Feedback message={errorMessage(deletion.error)} />}
      {query.isPending ? (
        <p role="status">Cargando personas…</p>
      ) : query.isError ? (
        <LoadError
          message={errorMessage(query.error)}
          retry={() => void query.refetch()}
        />
      ) : (
        <>
          <p className="text-sm text-zinc-400">
            {people.length} {people.length === 1 ? 'persona' : 'personas'}
          </p>
          {people.length === 0 ? (
            <div className="space-y-3">
              <Empty
                mensaje={
                  term
                    ? `No hay personas que coincidan con “${term}”. Probá otra búsqueda o cambiá de filtro.`
                    : 'Todavía no hay personas en esta categoría.'
                }
              />
              {term && (
                <Button variant="outline" onClick={() => setSearch('')}>
                  Limpiar búsqueda
                </Button>
              )}
            </div>
          ) : (
            <div className="grid gap-4 lg:grid-cols-2">
              {people.map((person) => (
                <article
                  key={person.id}
                  className={
                    'flex flex-wrap items-center justify-between gap-4 ' +
                    'rounded-2xl border border-[#2e2e33] bg-[#1a1a1c] p-5'
                  }
                >
                  <div className="flex min-w-0 items-center gap-3">
                    <Avatar nombre={person.nombre} />
                    <div className="min-w-0">
                      <p className="text-xs text-[#ffc107]">
                        {tab === 'TRABAJADOR'
                          ? 'Trabajador'
                          : tab === 'EMPLEADOR'
                            ? 'Empleador'
                            : 'Administrador'}
                      </p>
                      <h2 className="font-bold">
                        {person.nombre}
                        {person.id === userId ? ' (vos)' : ''}
                      </h2>
                      <p className="break-all text-sm text-zinc-400">{person.email}</p>
                      <p className="text-xs text-zinc-500">
                        {person.telefono || 'Sin teléfono'}
                        {person.habilitado === false ? ' · Cuenta suspendida' : ''}
                      </p>
                    </div>
                  </div>
                  {tab !== 'ADMIN' ? (
                    <Link
                      href={
                        '/admin/personas/' +
                        (tab === 'TRABAJADOR' ? 'trabajadores' : 'empleadores') +
                        `/${person.id}`
                      }
                    >
                      <Button size="sm" variant="outline">
                        Ver ficha
                      </Button>
                    </Link>
                  ) : (
                    <div className="flex items-center gap-2">
                      <span className="text-xs text-zinc-400">
                        {'nivelAcceso' in person
                          ? String(person.nivelAcceso ?? 'Administrador')
                          : 'Administrador'}
                      </span>
                      <Button
                        size="sm"
                        variant="outline"
                        disabled={
                          userId == null || person.id === userId || deletion.isPending
                        }
                        onClick={() => {
                          if (
                            confirm(
                              `Eliminar definitivamente al administrador ${person.nombre}?`,
                            )
                          ) {
                            deletion.mutate(person.id);
                          }
                        }}
                      >
                        Eliminar
                      </Button>
                    </div>
                  )}
                </article>
              ))}
            </div>
          )}
        </>
      )}
      <Note>
        Los roles se asignan al registrar la cuenta. Las acciones administrativas
        requieren una sesión de administrador.
      </Note>
    </div>
  );
}

export default function PersonasPage() {
  return (
    <Suspense fallback={<p role="status">Cargando personas…</p>}>
      <PersonasContent />
    </Suspense>
  );
}
