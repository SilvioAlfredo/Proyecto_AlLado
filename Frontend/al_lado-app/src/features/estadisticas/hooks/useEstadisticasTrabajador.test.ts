import { beforeEach, describe, expect, it, vi } from 'vitest';
import { renderHook, waitFor } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { useEstadisticasTrabajador } from './useEstadisticasTrabajador';
import { trabajadorService } from '../../usuario/services/trabajadorService';

/**
 * HU-12: Trabajador visualiza estadísticas de ingresos.
 *
 * Integración del hook useEstadisticasTrabajador con React Query y el servicio
 * de estadísticas del trabajador.
 */
vi.mock('../../usuario/services/trabajadorService', () => ({
  trabajadorService: { obtenerEstadisticas: vi.fn() },
}));

const mockTrabajadorService = vi.mocked(trabajadorService);

function crearWrapper() {
  const cliente = new QueryClient({ defaultOptions: { queries: { retry: false } } });
  return ({ children }: { children: React.ReactNode }) => (
    <QueryClientProvider client={cliente}>{children}</QueryClientProvider>
  );
}

describe('useEstadisticasTrabajador', () => {
  let wrapper: ReturnType<typeof crearWrapper>;

  beforeEach(() => {
    wrapper = crearWrapper();
    mockTrabajadorService.obtenerEstadisticas.mockReset();
  });

  it('devuelve las estadísticas del trabajador', async () => {
    const estadisticas = {
      id: 7,
      nombre: 'Ana',
      email: 'ana@mail.com',
      ingresosTotales: 50000,
      ingresosUltimos3Meses: 30000,
      ingresosDisponibles: true,
      valoracionesCount: 4,
      promedioValoraciones: 4.5,
      ofertasPublicadas: 2,
      postulacionesEnviadas: 10,
      postulacionesSeleccionadas: 3,
      postulacionesCompletadas: 2,
      nivelRecomendacion: 5,
      perfilCompleto: true,
      ultimoLogin: '2026-10-01',
      suscripcionActiva: true,
    };
    mockTrabajadorService.obtenerEstadisticas.mockResolvedValue(estadisticas);

    const { result } = renderHook(() => useEstadisticasTrabajador(), { wrapper });

    await waitFor(() => expect(result.current.isSuccess).toBe(true));
    expect(result.current.data).toEqual(estadisticas);
    expect(mockTrabajadorService.obtenerEstadisticas).toHaveBeenCalledTimes(1);
  });
});
