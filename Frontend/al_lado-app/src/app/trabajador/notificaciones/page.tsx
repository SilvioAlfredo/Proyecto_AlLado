'use client';

import { useAuth } from '@/features/auth/hooks/useAuth';
import { useNotificaciones } from '@/features/notificaciones/hooks/useNotificaciones';
import { NotificacionesList } from '@/features/notificaciones/components/NotificacionesList';
import {
  TrabajadorPage,
  EstadoTrabajador,
} from '@/features/usuario/components/TrabajadorPage';

export default function TrabajadorNotificacionesPage() {
  const { userId } = useAuth();
  const consulta = useNotificaciones(userId);

  return (
    <TrabajadorPage titulo="Notificaciones">
      <EstadoTrabajador consulta={consulta} />
      {!consulta.error && !consulta.isPending && (
        <NotificacionesList consulta={consulta} />
      )}
    </TrabajadorPage>
  );
}
