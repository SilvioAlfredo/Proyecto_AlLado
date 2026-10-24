'use client';

import { useAuth } from '@/features/auth/hooks/useAuth';
import { NotificacionesList } from '@/features/notificaciones/components/NotificacionesList';

// Endpoints: GET /notificaciones/usuario/{id} (titular). El id sale de la
// sesión (AL-API-005), no de listar el directorio de usuarios.
export default function EmpleadorNotificacionesPage() {
  const { userId } = useAuth();

  if (!userId) return <p className="text-sm text-zinc-500">Cargando notificaciones...</p>;
  return <NotificacionesList usuarioId={userId} />;
}
