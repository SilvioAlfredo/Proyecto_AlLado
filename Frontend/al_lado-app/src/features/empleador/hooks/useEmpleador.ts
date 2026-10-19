'use client';

import { useQuery, useQueryClient } from '@tanstack/react-query';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { empleadorService } from '@/features/usuario/services/empleadorService';
import { oportunidadesService } from '@/features/oportunidades/services/oportunidadService';
import { postulacionesService } from '@/features/postulaciones/services/postulacionesService';
import { favoritosService } from '@/features/comunidad/services/favoritosService';

/** Usa las relaciones del empleador para incluir también sus solicitudes cerradas. */
export function useEmpleador() {
  const { userId, user } = useAuth();
  const client = useQueryClient();
  const perfil = useQuery({
    queryKey: ['empleador', userId, 'perfil'],
    enabled: !!userId,
    queryFn: () => empleadorService.obtenerPorId(userId!),
  });
  const solicitudes = useQuery({
    queryKey: ['empleador', userId, 'solicitudes'],
    enabled: !!userId,
    queryFn: async () => {
      const actual = await client.fetchQuery({
        queryKey: ['empleador', userId, 'perfil'],
        queryFn: () => empleadorService.obtenerPorId(userId!),
      });
      return Promise.all(
        (actual.oportunidades ?? []).map((o) => oportunidadesService.obtenerPorId(o.id)),
      );
    },
  });
  const propuestas = useQuery({
    queryKey: ['empleador', userId, 'propuestas'],
    enabled: !!userId,
    queryFn: async () => {
      const actual = await client.fetchQuery({
        queryKey: ['empleador', userId, 'perfil'],
        queryFn: () => empleadorService.obtenerPorId(userId!),
      });
      const ids = new Set(actual.oportunidades?.map((o) => o.id));
      return (await postulacionesService.obtenerTodas()).filter((p) =>
        ids.has(p.oportunidadLaboral?.id),
      );
    },
  });
  const favoritos = useQuery({
    queryKey: ['empleador', userId, 'favoritos'],
    enabled: !!userId,
    queryFn: () => favoritosService.porEmpleador(userId!),
  });
  return {
    userId,
    user,
    perfil,
    solicitudes,
    propuestas,
    favoritos,
    refresh: () => client.invalidateQueries({ queryKey: ['empleador', userId] }),
  };
}
