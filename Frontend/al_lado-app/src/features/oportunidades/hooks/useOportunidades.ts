'use client';

import { useQuery } from '@tanstack/react-query';
import { useState } from 'react';
import { OportunidadLaboralDTO } from '@/types/trabajo';
import { PaginaDTO } from '@/types/admin';
import { oportunidadesService } from '../services/oportunidadService';
import { oportunidadesKeys } from '../queryKeys';
import { errorMessage } from '@/lib/errors';

const OPORTUNIDADES_PAGE_SIZE = 100;

export interface FiltrosOportunidades {
  filtro?: string;
  modalidad?: string;
  etiquetas?: string[];
  soloDestacadas?: boolean;
  ubicacion?: string;
  sector?: string;
}

export interface useOportunidadesReturn {
  oportunidades: OportunidadLaboralDTO[];
  page: number;
  totalPages: number;
  totalElements: number;
  loading: boolean;
  error: string | null;
  refetch: () => Promise<void>;
  cargarConFiltros: (filtros: FiltrosOportunidades, page?: number) => Promise<void>;
  filtro: string | undefined;
  setFiltro: (f: string) => void;
  modalidad: string | undefined;
  setModalidad: (m: string) => void;
  etiquetas: string[] | undefined;
  setEtiquetas: (etqs: string[]) => void;
}

/**
 * Lista de oportunidades con React Query.
 *
 * Los filtros y la página viven en la `queryKey`, así cada combinación se cachea
 * por separado y `placeholderData` evita parpadeos al cambiar de página.
 * No hay `useEffect` ni `useState` para traer datos: React Query gestiona el
 * ciclo de vida, el caché y los reintentos (solo red o 5xx, nunca 4xx).
 */
export function useOportunidades(initialFiltros: FiltrosOportunidades = {}): useOportunidadesReturn {
  const [filtros, setFiltrosState] = useState<FiltrosOportunidades>(initialFiltros);
  const [page, setPage] = useState(0);

  const consulta = useQuery<PaginaDTO<OportunidadLaboralDTO>, Error>({
    queryKey: oportunidadesKeys.lista(filtros, page),
    queryFn: async () => {
      return await oportunidadesService.listarConFiltros(
        filtros,
        page,
        OPORTUNIDADES_PAGE_SIZE,
      );
    },
    placeholderData: (previousData) => previousData,
    staleTime: 30_000,
  });

  const pagina = consulta.data;
  const cargarConFiltros = async (nuevosFiltros: FiltrosOportunidades, nuevaPage = 0) => {
    setFiltrosState(nuevosFiltros);
    setPage(nuevaPage);
    await consulta.refetch();
  };

  return {
    oportunidades: pagina?.content ?? [],
    page,
    totalPages: pagina?.totalPages ?? 0,
    totalElements: pagina?.totalElements ?? 0,
    loading: consulta.isPending,
    error: consulta.error ? errorMessage(consulta.error, 'Error al cargar oportunidades') : null,
    refetch: async () => {
      await consulta.refetch();
    },
    cargarConFiltros,
    filtro: filtros.filtro,
    setFiltro: (f: string) => setFiltrosState({ ...filtros, filtro: f }),
    modalidad: filtros.modalidad,
    setModalidad: (m: string) => setFiltrosState({ ...filtros, modalidad: m }),
    etiquetas: filtros.etiquetas,
    setEtiquetas: (etqs: string[]) => setFiltrosState({ ...filtros, etiquetas: etqs }),
  };
}
