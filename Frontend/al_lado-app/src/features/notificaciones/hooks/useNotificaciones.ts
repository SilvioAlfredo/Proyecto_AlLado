'use client';

import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { notificacionesService } from '@/features/notificaciones/services/notificacionesService';
import { notificacionesKeys } from '@/features/notificaciones/queryKeys';

/**
 * Lista de notificaciones del usuario de la sesión y las mutaciones de lectura.
 *
 * Es la única fuente de estado de la pantalla: trabajador y empleador consumen
 * este hook, así no hay dos implementaciones que se desincronicen.
 *
 * Marcar una notificación (o todas) invalida `notificacionesKeys.todo`, que
 * incluye el contador de la Navbar. Sin eso, el badge conservaba el valor viejo
 * hasta que expiraba el `staleTime`.
 */
export function useNotificaciones(usuarioId: number | null) {
  const cache = useQueryClient();

  const consulta = useQuery({
    queryKey: notificacionesKeys.lista(usuarioId ?? 0),
    enabled: usuarioId != null,
    queryFn: () => notificacionesService.listar(usuarioId!),
    retry: false,
  });

  const invalidar = () =>
    cache.invalidateQueries({ queryKey: notificacionesKeys.todo });

  const marcarUna = useMutation({
    mutationFn: (id: number) => notificacionesService.marcarLeida(id),
    onSuccess: invalidar,
  });

  const marcarTodas = useMutation({
    mutationFn: () => {
      if (usuarioId == null) {
        throw new Error('No se pudo identificar tu usuario.');
      }
      return notificacionesService.marcarTodasLeidas(usuarioId);
    },
    onSuccess: invalidar,
  });

  return {
    ...consulta,
    leer: (id: number) => marcarUna.mutate(id),
    leerTodas: () => marcarTodas.mutate(),
    marcando: marcarUna.isPending || marcarTodas.isPending,
    errorLectura: marcarUna.error ?? marcarTodas.error,
  };
}

export type ConsultaNotificaciones = ReturnType<typeof useNotificaciones>;
