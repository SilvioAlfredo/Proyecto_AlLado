'use client';

import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { PublicacionForoDTO } from '@/types/comunidad';
import { comunidadService } from '../services/comunidadService';
import { comunidadKeys } from '../queryKeys';
import { errorMessage } from '@/lib/errors';

export function useComunidad() {
  const cache = useQueryClient();

  const consulta = useQuery<PublicacionForoDTO[], Error>({
    queryKey: comunidadKeys.posts(),
    queryFn: () => comunidadService.getPostsForo(),
    staleTime: 30_000,
  });

  const crearPost = useMutation({
    mutationFn: (dto: Partial<PublicacionForoDTO>) =>
      comunidadService.crearPostForo(dto),
    onSuccess: (nuevo) => {
      cache.setQueryData<PublicacionForoDTO[]>(
        comunidadKeys.posts(),
        (prev = []) => [nuevo, ...prev]
      );
      cache.invalidateQueries({ queryKey: comunidadKeys.todo });
    },
  });

  return {
    publicaciones: consulta.data ?? [],
    loading: consulta.isPending,
    error: consulta.error
      ? errorMessage(consulta.error, 'Error al cargar publicaciones')
      : null,
    crearPost: (dto: Partial<PublicacionForoDTO>) => crearPost.mutateAsync(dto),
    creando: crearPost.isPending,
    errorCrear: crearPost.error
      ? errorMessage(crearPost.error, 'Error al crear publicación')
      : null,
  };
}
