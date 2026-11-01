import { beforeEach, describe, expect, it, vi } from 'vitest';
import { renderHook, waitFor } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { useReportes } from './useReportes';
import { reportesService } from '../services/reportesService';

/**
 * HU-13: Reportar perfiles abusivos y foro vecinal.
 *
 * Integración del hook useReportes con React Query y el servicio de reportes.
 */
vi.mock('../services/reportesService', () => ({
  reportesService: {
    listar: vi.fn(),
    porEstado: vi.fn(),
    obtener: vi.fn(),
    crear: vi.fn(),
    resolver: vi.fn(),
  },
}));

const mockReportesService = vi.mocked(reportesService);

function crearWrapper() {
  const cliente = new QueryClient({ defaultOptions: { queries: { retry: false } } });
  return ({ children }: { children: React.ReactNode }) => (
    <QueryClientProvider client={cliente}>{children}</QueryClientProvider>
  );
}

describe('useReportes', () => {
  let wrapper: ReturnType<typeof crearWrapper>;

  beforeEach(() => {
    wrapper = crearWrapper();
    mockReportesService.listar.mockReset();
    mockReportesService.porEstado.mockReset();
    mockReportesService.obtener.mockReset();
    mockReportesService.crear.mockReset();
    mockReportesService.resolver.mockReset();
  });

  it('devuelve la lista de reportes', async () => {
    const reportes = [
      { id: 1, motivo: 'Fraude', estado: 'PENDIENTE' },
      { id: 2, motivo: 'Acoso', estado: 'RESUELTO' },
    ];
    mockReportesService.listar.mockResolvedValue(reportes);

    const { result } = renderHook(() => useReportes.listar(), { wrapper });

    await waitFor(() => expect(result.current.isSuccess).toBe(true));
    expect(result.current.data).toEqual(reportes);
    expect(mockReportesService.listar).toHaveBeenCalledTimes(1);
  });

  it('devuelve reportes filtrados por estado', async () => {
    const reportes = [{ id: 1, motivo: 'Fraude', estado: 'PENDIENTE' }];
    mockReportesService.porEstado.mockResolvedValue(reportes);

    const { result } = renderHook(() => useReportes.porEstado('PENDIENTE'), { wrapper });

    await waitFor(() => expect(result.current.isSuccess).toBe(true));
    expect(result.current.data).toEqual(reportes);
    expect(mockReportesService.porEstado).toHaveBeenCalledWith('PENDIENTE');
  });

  it('devuelve el detalle de un reporte por su ID', async () => {
    const reporte = { id: 1, motivo: 'Fraude', estado: 'PENDIENTE' };
    mockReportesService.obtener.mockResolvedValue(reporte);

    const { result } = renderHook(() => useReportes.obtener(1), { wrapper });

    await waitFor(() => expect(result.current.isSuccess).toBe(true));
    expect(result.current.data).toEqual(reporte);
    expect(mockReportesService.obtener).toHaveBeenCalledWith(1);
  });
});
