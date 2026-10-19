'use client';

import { useMutation } from '@tanstack/react-query';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { useTrabajadorQuery } from '@/features/usuario/hooks/useTrabajadorQuery';
import { notificacionesService } from '@/features/notificaciones/services/notificacionesService';
import {
  TrabajadorPage,
  EstadoTrabajador,
  panelTrabajador,
} from '@/features/usuario/components/TrabajadorPage';
import { Button } from '@/components/ui/button';
import { errorMessage } from '@/lib/errors';
import { formatDate } from '@/lib/utils';

export default function TrabajadorNotificacionesPage() {
  const { userId } = useAuth();
  const consulta = useTrabajadorQuery('notificaciones', (id) =>
    notificacionesService.listar(id),
  );
  const leer = useMutation({
    mutationFn: async (id: number | null): Promise<void> => {
      if (id) {
        await notificacionesService.marcarLeida(id);
        return;
      }
      if (!userId) throw new Error('No se pudo identificar tu usuario.');
      await notificacionesService.marcarTodasLeidas(userId);
    },
    onSuccess: async () => {
      await consulta.refetch();
    },
  });
  return (
    <TrabajadorPage titulo="Notificaciones">
      <EstadoTrabajador consulta={consulta} />
      {leer.error && (
        <p role="alert" className="text-red-300">
          {errorMessage(leer.error)}
        </p>
      )}
      {consulta.data?.some((n) => !n.leida) && (
        <Button
          variant="outline"
          disabled={leer.isPending}
          onClick={() => leer.mutate(null)}
        >
          Marcar todas como leídas
        </Button>
      )}
      {consulta.data?.length === 0 && <p>Sin notificaciones registradas.</p>}
      {consulta.data?.map((n) => (
        <article key={n.id} className={`${panelTrabajador} space-y-3`}>
          <h2 className="text-lg font-bold">{n.titulo}</h2>
          <p>{n.mensaje}</p>
          <p className="text-sm text-zinc-400">
            {formatDate(n.fechaCreacion)} · {n.leida ? 'Leída' : 'Sin leer'}
          </p>
          {!n.leida && n.id && (
            <Button
              variant="outline"
              disabled={leer.isPending}
              onClick={() => leer.mutate(n.id!)}
            >
              Marcar como leída
            </Button>
          )}
        </article>
      ))}
    </TrabajadorPage>
  );
}
