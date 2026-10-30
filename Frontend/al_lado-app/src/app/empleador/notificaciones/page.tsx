'use client';

import { useAuth } from '@/features/auth/hooks/useAuth';
import { useNotificaciones } from '@/features/notificaciones/hooks/useNotificaciones';
import { NotificacionesList } from '@/features/notificaciones/components/NotificacionesList';
import { PageHeader } from '@/components/ui/shared';

// Endpoints: GET /notificaciones/usuario/{id} (titular). El id sale de la
// sesión (AL-API-005), no de listar el directorio de usuarios.
export default function EmpleadorNotificacionesPage() {
  const { userId } = useAuth();
  const consulta = useNotificaciones(userId);

  if (!userId) {
    return <p className="text-sm text-zinc-500">Cargando notificaciones...</p>;
  }

  return (
    <div className="space-y-4">
      <PageHeader eyebrow="Avisos" title="Notificaciones" />
      <NotificacionesList consulta={consulta} />
    </div>
  );
}
