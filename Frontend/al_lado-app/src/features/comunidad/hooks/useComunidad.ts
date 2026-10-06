'use client';

import { useState, useEffect } from 'react';
import { PublicacionForoDTO } from '@/types/comunidad';
import { comunidadService } from '../services/comunidadService';

export function useComunidad() {
  const [publicaciones, setPublicaciones] = useState<PublicacionForoDTO[]>([]);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    comunidadService
      .getPostsForo()
      .then(setPublicaciones)
      .finally(() => setLoading(false));
  }, []);

  const crearPost = async (dto: Partial<PublicacionForoDTO>) => {
    const nuevo = await comunidadService.crearPostForo(dto);
    setPublicaciones((prev) => [nuevo, ...prev]);
  };

  return { publicaciones, loading, crearPost };
}