'use client';

import { useState, useEffect, useCallback } from 'react';
import { PostulacionDTO } from '@/types/trabajo';
import { postulacionesService } from '../services/postulacionesService';

export function useCandidatosOportunidad(oportunidadId?: number | null) {
  const [candidatos, setCandidatos] = useState<PostulacionDTO[]>([]);
  const [loading, setLoading] = useState<boolean>(true);

  const fetchCandidatos = useCallback(async () => {
    if (!oportunidadId) {
      setCandidatos([]);
      setLoading(false);
      return;
    }
    setLoading(true);
    try {
      const data = await postulacionesService.obtenerTodas();
      const filtrados = data.filter((p) => p.oportunidadLaboral?.id === oportunidadId);
      setCandidatos(filtrados);
    } catch (error) {
      console.error('Error al cargar candidatos:', error);
    } finally {
      setLoading(false);
    }
  }, [oportunidadId]);

  useEffect(() => {
    fetchCandidatos();
  }, [fetchCandidatos]);

  return { candidatos, loading, refetch: fetchCandidatos };
}