'use client';

import { useEffect, useState } from 'react';
import { notificacionesService, NotificacionDTO } from '@/features/notificaciones/services/notificacionesService';
import { PageHeader, Empty } from '@/components/ui/shared';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { formatDate } from '@/lib/utils';

const TIPO_LABEL: Record<string, string> = {
  POSTULACION_RECIBIDA: 'Postulación',
  POSTULACION_ESTADO: 'Estado',
  SISTEMA: 'Sistema',
};

// Lista compartida trabajador/empleador. Endpoints: GET /notificaciones/usuario/{id},
// PATCH /notificaciones/{id}/leer, PATCH /notificaciones/usuario/{id}/leer-todas
export function NotificacionesList({ usuarioId }: { usuarioId: number }) {
  const [items, setItems] = useState<NotificacionDTO[]>([]);
  const [loading, setLoading] = useState(true);

  const cargar = async () => {
    setLoading(true);
    try {
      setItems(await notificacionesService.listar(usuarioId));
    } catch {
      setItems([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    cargar();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [usuarioId]);

  const leer = async (id?: number) => {
    if (!id) return;
    await notificacionesService.marcarLeida(id);
    setItems((prev) => prev.map((n) => (n.id === id ? { ...n, leida: true } : n)));
  };

  const leerTodas = async () => {
    await notificacionesService.marcarTodasLeidas(usuarioId);
    setItems((prev) => prev.map((n) => ({ ...n, leida: true })));
  };

  if (loading) return <p className="text-sm text-zinc-500">Cargando notificaciones...</p>;

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <PageHeader eyebrow="Avisos" title="Notificaciones" />
        {items.some((n) => !n.leida) && (
          <Button size="sm" variant="outline" onClick={leerTodas}>
            Marcar todas como leídas
          </Button>
        )}
      </div>
      {items.length === 0 ? (
        <Empty mensaje="No tenés notificaciones todavía." />
      ) : (
        <div className="space-y-3">
          {items.map((n) => (
            <article
              key={n.id}
              className={`rounded-2xl border p-4 ${n.leida ? 'border-[#2e2e33] bg-[#1a1a1c]' : 'border-[#ffc107]/50 bg-[#1a1a1c]'}`}
            >
              <div className="flex items-center justify-between gap-2">
                <p className="font-bold text-white">{n.titulo}</p>
                <Badge variant={n.leida ? 'default' : 'warning'}>
                  {TIPO_LABEL[n.tipo ?? ''] ?? n.tipo ?? 'Aviso'}
                </Badge>
              </div>
              <p className="mt-1 text-sm text-zinc-300">{n.mensaje}</p>
              <div className="mt-2 flex items-center justify-between">
                <span className="text-xs text-zinc-500">
                  {n.fechaCreacion ? formatDate(n.fechaCreacion) : ''}
                </span>
                {!n.leida && (
                  <button onClick={() => leer(n.id)} className="text-xs font-bold text-[#ffc107] hover:underline">
                    Marcar como leída
                  </button>
                )}
              </div>
            </article>
          ))}
        </div>
      )}
    </div>
  );
}
