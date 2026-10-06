'use client';

import { useState, useEffect, useCallback } from 'react';
import { PostulacionDTO } from '@/types/trabajo';
import { postulacionesService } from '../services/postulacionesService';

export function usePostulacionesTrabajador() {
  const [postulaciones, setPostulaciones] = useState<PostulacionDTO[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  const fetchPostulaciones = useCallback(async () => {
    setLoading(true);
    try {
      const data = await postulacionesService.obtenerTodas();
      setPostulaciones(data);
    } catch (err: any) {
      setError(err?.response?.data?.message || 'Error al cargar postulaciones');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchPostulaciones();
  }, [fetchPostulaciones]);

  return { postulaciones, loading, error, refetch: fetchPostulaciones };
}