import { beforeEach, describe, expect, it, vi } from 'vitest';
import { renderHook, waitFor } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { useContratos } from './useContratos';
import { contratosService } from '../services/contratosService';

/**
 * HU-08: Historial de contratos del empleador y del trabajador.
 *
 * Integración del hook useContratos con React Query y el servicio de contratos.
 */
vi.mock('../services/contratosService', () => ({
  contratosService: {
    historialEmpleador: vi.fn(),
    historialTrabajador: vi.fn(),
    obtenerPorId: vi.fn(),
  },
}));

const mockContratosService = vi.mocked(contratosService);

function crearWrapper() {
  const cliente = new QueryClient({ defaultOptions: { queries: { retry: false } } });
  return ({ children }: { children: React.ReactNode }) => (
    <QueryClientProvider client={cliente}>{children}</QueryClientProvider>
  );
}

describe('useContratos', () => {
  let wrapper: ReturnType<typeof crearWrapper>;

  beforeEach(() => {
    wrapper = crearWrapper();
    mockContratosService.historialEmpleador.mockReset();
    mockContratosService.historialTrabajador.mockReset();
    mockContratosService.obtenerPorId.mockReset();
  });

  it('devuelve el historial de contratos del empleador autenticado', async () => {
    const contratos = [
      { id: 100, estado: 'COMPLETADA', fechaCreacion: '2026-01-15T10:00:00' },
      { id: 101, estado: 'SELECCIONADA', fechaCreacion: '2026-02-01T10:00:00' },
    ];
    mockContratosService.historialEmpleador.mockResolvedValue(contratos);

    const { result } = renderHook(() => useContratos.empleador(), { wrapper });

    await waitFor(() => expect(result.current.isSuccess).toBe(true));
    expect(result.current.data).toEqual(contratos);
    expect(mockContratosService.historialEmpleador).toHaveBeenCalledTimes(1);
  });

  it('devuelve el historial de contratos de un trabajador', async () => {
    const contratos = [
      { id: 101, estado: 'SELECCIONADA', fechaCreacion: '2026-02-01T10:00:00' },
    ];
    mockContratosService.historialTrabajador.mockResolvedValue(contratos);

    const { result } = renderHook(() => useContratos.trabajador(3), { wrapper });

    await waitFor(() => expect(result.current.isSuccess).toBe(true));
    expect(result.current.data).toEqual(contratos);
    expect(mockContratosService.historialTrabajador).toHaveBeenCalledWith(3);
  });

  it('devuelve el detalle de un contrato por su ID', async () => {
    const contrato = { id: 100, estado: 'COMPLETADA', fechaCreacion: '2026-01-15T10:00:00' };
    mockContratosService.obtenerPorId.mockResolvedValue(contrato);

    const { result } = renderHook(() => useContratos.porId(100), { wrapper });

    await waitFor(() => expect(result.current.isSuccess).toBe(true));
    expect(result.current.data).toEqual(contrato);
    expect(mockContratosService.obtenerPorId).toHaveBeenCalledWith(100);
  });

  it('no consulta si no se proporciona trabajadorId', async () => {
    const { result } = renderHook(() => useContratos.trabajador(undefined), { wrapper });

    await waitFor(() => expect(result.current.isSuccess).toBe(false));
    expect(mockContratosService.historialTrabajador).not.toHaveBeenCalled();
  });
});
