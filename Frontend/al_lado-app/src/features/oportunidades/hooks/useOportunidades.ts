'use client';

import { useState, useEffect, useCallback } from 'react';
import { OportunidadLaboralDTO } from '@/types/trabajo';
import { oportunidadesService } from '../services/oportunidadService';

export interface FiltrosOportunidades {
  filtro?: string;
  modalidad?: string;
  etiquetas?: string[];
}

export interface useOportunidadesReturn {
  oportunidades: OportunidadLaboralDTO[];
  loading: boolean;
  error: string | null;
  refetch: () => Promise<void>;
  cargarConFiltros: (filtros: FiltrosOportunidades) => Promise<void>;
}

export function useOportunidades() {
  const [oportunidades, setOportunidades] = useState<OportunidadLaboralDTO[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [filtros, setFiltros] = useState<FiltrosOportunidades>({});

  const fetchOportunidades = useCallback(async (filtros?: FiltrosOportunidades) => {
    setLoading(true);
    try {
      const data = await oportunidadesService.listarConFiltros(filtros);
      setOportunidades(data);
    } catch (err: any) {
      setError(err?.response?.data?.message || 'Error al cargar oportunidades');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchOportunidades();
  }, [fetchOportunidades]);

  const crearOportunidad = async (dto: OportunidadLaboralDTO) => {
    const nueva = await oportunidadesService.crear(dto);
    setOportunidades((prev) => [nueva, ...prev]);
    return nueva;
  };

  return {
    oportunidades,
    loading,
    error,
    refetch: () => fetchOportunidades(),
    cargarConFiltros: (filtros: FiltrosOportunidades) => fetchOportunidades(filtros),
    filtro: filtros.filtro,
    setFiltro: (f: string) => setFiltros({ ...filtros, filtro: f }),
    modalidad: filtros.modalidad,
    setModalidad: (m: string) => setFiltros({ ...filtros, modalidad: m }),
    etiquetas: filtros.etiquetas,
    setEtiquetas: (etqs: string[]) => setFiltros({ ...filtros, etiquetas: etqs }),
  };
}
