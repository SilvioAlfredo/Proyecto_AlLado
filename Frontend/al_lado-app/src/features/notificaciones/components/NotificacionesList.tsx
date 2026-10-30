'use client';

import { Empty } from '@/components/ui/shared';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { errorMessage } from '@/lib/errors';
import { formatDate } from '@/lib/utils';
import type { ConsultaNotificaciones } from '@/features/notificaciones/hooks/useNotificaciones';

const TIPO_LABEL: Record<string, string> = {
  POSTULACION_RECIBIDA: 'Postulación',
  POSTULACION_ESTADO: 'Estado',
  SISTEMA: 'Sistema',
};

/**
 * Lista de notificaciones compartida por trabajador y empleador.
 *
 * Es presentacional a propósito: el estado y las mutaciones viven en
 * `useNotificaciones`, que es quien invalida la lista y el contador de la
 * Navbar. Así las dos pantallas no vuelven a divergir.
 */
export function NotificacionesList({
  consulta,
}: {
  consulta: ConsultaNotificaciones;
}) {
  const items = consulta.data ?? [];
  const haySinLeer = items.some((n) => !n.leida);

  if (consulta.isPending) {
    return <p className="text-sm text-zinc-500">Cargando notificaciones...</p>;
  }

  return (
    <div className="space-y-4">
      {consulta.error && (
        <p role="alert" className="text-sm text-red-300">
          {errorMessage(consulta.error)}
        </p>
      )}
      {consulta.errorLectura && (
        <p role="alert" className="text-sm text-red-300">
          {errorMessage(consulta.errorLectura)}
        </p>
      )}

      {haySinLeer && (
        <div className="flex justify-end">
          <Button
            size="sm"
            variant="outline"
            disabled={consulta.marcando}
            onClick={consulta.leerTodas}
          >
            Marcar todas como leídas
          </Button>
        </div>
      )}

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
                {!n.leida && n.id != null && (
                  <button
                    onClick={() => consulta.leer(n.id!)}
                    disabled={consulta.marcando}
                    className="text-xs font-bold text-[#ffc107] hover:underline disabled:opacity-50"
                  >
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
