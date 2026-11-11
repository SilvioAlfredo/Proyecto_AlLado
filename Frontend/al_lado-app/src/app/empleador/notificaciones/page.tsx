'use client';

import { useEffect, useState } from 'react';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { obtenerRefPorEmail } from '@/lib/usuarioRef';
import { NotificacionesList } from '@/features/notificaciones/components/NotificacionesList';

// Endpoints: GET /notificaciones/usuario/{id} (titular)
export default function EmpleadorNotificacionesPage() {
  const { user } = useAuth();
  const [usuarioId, setUsuarioId] = useState<number | null>(null);

  useEffect(() => {
    if (user?.email) {
      obtenerRefPorEmail(user.email).then((ref) => setUsuarioId(ref?.id ?? null)).catch(() => {});
    }
  }, [user?.email]);

  if (!usuarioId) return <p className="text-sm text-zinc-500">Cargando notificaciones...</p>;
  return <NotificacionesList usuarioId={usuarioId} />;
}
