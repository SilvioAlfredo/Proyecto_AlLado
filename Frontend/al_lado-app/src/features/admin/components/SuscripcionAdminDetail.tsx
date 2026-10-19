'use client';

import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { suscripcionesService } from '@/features/usuario/services/aportesService';
import { PageHeader, Note } from '@/components/ui/shared';
import { Button } from '@/components/ui/button';
import { Feedback, LoadError } from './Feedback';
import { calendarDate, subscriptionState } from '@/features/admin/lib/aportes';
import { errorMessage } from '@/lib/errors';

/**
 * Presenta el titular y la vigencia de una suscripción y sus acciones administrativas.
 *
 * Cancelar conserva el registro; eliminar lo borra. Ambas acciones piden confirmación
 * y las transferencias se conservan por separado.
 *
 * @param props - Opciones del detalle.
 * @param props.id - Identificador de la suscripción, no del usuario.
 */
export function SuscripcionAdminDetail({ id }: { id: number }) {
  const client = useQueryClient();
  const router = useRouter();
  const valid = Number.isSafeInteger(id) && id > 0;
  const query = useQuery({
    queryKey: ['admin-suscripcion', id],
    enabled: valid,
    retry: false,
    queryFn: () => suscripcionesService.obtenerPorId(id),
  });
  const mutation = useMutation({
    mutationFn: async (action: 'cancelar' | 'eliminar') => {
      if (action === 'cancelar') {
        return suscripcionesService.cancelar(id);
      }
      await suscripcionesService.eliminar(id);
      return null;
    },
    onSuccess: (data) => {
      client.invalidateQueries({ queryKey: ['admin-suscripciones'] });
      if (data) {
        client.setQueryData(['admin-suscripcion', id], data);
      } else {
        client.removeQueries({ queryKey: ['admin-suscripcion', id], exact: true });
        router.push('/admin/suscripciones');
      }
    },
  });

  if (!valid) {
    return <Feedback message="El identificador de suscripción no es válido." />;
  }
  if (query.isPending) {
    return <p role="status">Cargando suscripción…</p>;
  }
  if (query.isError) {
    return (
      <LoadError message={errorMessage(query.error)} retry={() => void query.refetch()} />
    );
  }
  const sub = query.data;

  return (
    <div className="space-y-6">
      <Link href="/admin/suscripciones" className="text-sm text-zinc-400">
        ← Volver a Suscripciones
      </Link>
      <PageHeader
        eyebrow="Aportes"
        title="Detalle de suscripción"
        subtitle={`Plan registrado de ${sub.usuario?.nombre}`}
      />
      {mutation.isError && <Feedback message={errorMessage(mutation.error)} />}
      {mutation.isSuccess && sub && (
        <Feedback success message="Suscripción cancelada. El registro se conserva." />
      )}
      <div className="grid gap-5 lg:grid-cols-[1fr_300px]">
        <section className="space-y-5">
          <div className="space-y-2 rounded-2xl border border-[#2e2e33] bg-[#1a1a1c] p-5">
            <h2 className="text-lg font-bold">{sub.usuario?.nombre}</h2>
            <p className="break-all text-sm text-zinc-400">{sub.usuario?.email}</p>
            <p className="text-[#ffc107]">
              {sub.nivel} · {subscriptionState(sub)}
            </p>
          </div>
          <div className="space-y-3 rounded-2xl border border-[#2e2e33] bg-[#1a1a1c] p-5">
            <h2 className="font-bold">Vigencia</h2>
            <dl className="grid gap-4 sm:grid-cols-2">
              {[
                ['Nivel', sub.nivel],
                ['Estado', subscriptionState(sub)],
                ['Fecha de inicio', calendarDate(sub.fechaInicio)],
                ['Fecha de fin', calendarDate(sub.fechaFin)],
              ].map(([label, value]) => (
                <div key={label}>
                  <dt className="text-xs text-zinc-500">{label}</dt>
                  <dd className="text-sm">{value}</dd>
                </div>
              ))}
            </dl>
          </div>
        </section>
        <aside className="h-fit space-y-4 rounded-2xl border border-[#2e2e33] bg-[#1a1a1c] p-5">
          <p className="text-xs font-bold uppercase text-[#ffc107]">Administrar plan</p>
          <h2 className="font-bold">Suscripción {sub.nivel}</h2>
          <p className="text-sm text-zinc-400">
            Cancelar deja la cuenta sin esta suscripción activa y conserva el registro.
          </p>
          <Button
            className="w-full"
            variant="outline"
            disabled={mutation.isPending || !sub.activa}
            onClick={() => {
              if (confirm(`Cancelar la suscripción de ${sub.usuario?.nombre}?`)) {
                mutation.mutate('cancelar');
              }
            }}
          >
            Cancelar suscripción
          </Button>
          <Button
            className="w-full"
            variant="destructive"
            disabled={mutation.isPending}
            onClick={() => {
              if (
                confirm(
                  `Eliminar definitivamente la suscripción de ${sub.usuario?.nombre}? ` +
                    'Esta acción no se puede deshacer.',
                )
              ) {
                mutation.mutate('eliminar');
              }
            }}
          >
            Eliminar suscripción
          </Button>
          <Note tone="danger">
            Eliminar borra el registro del plan. Las transferencias declaradas se
            conservan por separado.
          </Note>
          <Link className="block text-sm text-[#ffc107]" href="/admin/donaciones">
            Consultar transferencias →
          </Link>
        </aside>
      </div>
    </div>
  );
}
