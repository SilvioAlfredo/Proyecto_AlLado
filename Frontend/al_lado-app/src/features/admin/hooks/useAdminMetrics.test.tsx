import { beforeEach, describe, expect, it, vi } from 'vitest';
import { renderHook, waitFor } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { useAdminMetrics } from './useAdminMetrics';
import { adminService } from '../services/adminService';

/**
 * HU-19: Administrador consulta panel de métricas.
 *
 * Integración del hook useAdminMetrics con React Query y el servicio de métricas.
 */
vi.mock('../services/adminService', () => ({
  adminService: { getMetrics: vi.fn() },
}));

function crearWrapper() {
  const cliente = new QueryClient({ defaultOptions: { queries: { retry: false } } });
  function Wrapper({ children }: { children: React.ReactNode }) {
    return <QueryClientProvider client={cliente}>{children}</QueryClientProvider>;
  }
  return Wrapper;
}

describe('useAdminMetrics', () => {
  let wrapper: ReturnType<typeof crearWrapper>;

  beforeEach(() => {
    wrapper = crearWrapper();
    vi.mocked(adminService.getMetrics).mockReset();
  });

  it('devuelve las métricas del dashboard administrativo', async () => {
    const metricas = {
      totalIngresos: '$150000',
      metricContratos: 100,
      metricTrabajadores: 50,
      metricAvg: '$15000',
      dineroComunidad: '$75000',
      trabajosCompletados: 40,
      tendencias: [
        { nombre: 'Pintura', cantidad: 10, porcentaje: 50, colorClass: 'fill-yellow' },
      ],
      usuariosActivos: 45,
      usuariosInactivos: 5,
      postulacionesPorEstado: { PENDIENTE: 20, COMPLETADA: 40 },
    };
    vi.mocked(adminService.getMetrics).mockResolvedValue(metricas);

    const { result } = renderHook(() => useAdminMetrics(), { wrapper });

    await waitFor(() => expect(result.current.isSuccess).toBe(true));
    expect(result.current.data?.totalIngresos).toBe('$150000');
    expect(result.current.data?.metricContratos).toBe(100);
    expect(result.current.data?.trabajosCompletados).toBe(40);
    expect(adminService.getMetrics).toHaveBeenCalledTimes(1);
  });

  it('propaga el error cuando falla la consulta de métricas', async () => {
    vi.mocked(adminService.getMetrics).mockRejectedValue(new Error('Error del servidor'));

    const { result } = renderHook(() => useAdminMetrics(), { wrapper });

    await waitFor(() => expect(result.current.isError).toBe(true));
    expect(result.current.error?.message).toBe('Error del servidor');
  });
});
