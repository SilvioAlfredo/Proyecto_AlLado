import { beforeEach, describe, expect, it, vi } from 'vitest';
import { renderHook, waitFor } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { useComunicados } from './useComunicados';
import { comunicadosService } from '../services/comunicadosService';

/**
 * HU-20: Usuario lee comunicados institucionales.
 *
 * Integración del hook useComunicados con React Query y el servicio de
 * comunicados.
 */
vi.mock('../services/comunicadosService', () => ({
  comunicadosService: {
    listar: vi.fn(),
    porAlcance: vi.fn(),
    crear: vi.fn(),
    eliminar: vi.fn(),
  },
}));

const mockComunicadosService = vi.mocked(comunicadosService);

function crearWrapper() {
  const cliente = new QueryClient({ defaultOptions: { queries: { retry: false } } });
  return ({ children }: { children: React.ReactNode }) => (
    <QueryClientProvider client={cliente}>{children}</QueryClientProvider>
  );
}

describe('useComunicados', () => {
  let wrapper: ReturnType<typeof crearWrapper>;

  beforeEach(() => {
    wrapper = crearWrapper();
    mockComunicadosService.listar.mockReset();
    mockComunicadosService.porAlcance.mockReset();
    mockComunicadosService.crear.mockReset();
    mockComunicadosService.eliminar.mockReset();
  });

  it('devuelve la lista de comunicados', async () => {
    const comunicados = [
      { id: 1, titulo: 'Comunicado 1', contenido: 'Contenido 1' },
      { id: 2, titulo: 'Comunicado 2', contenido: 'Contenido 2' },
    ];
    mockComunicadosService.listar.mockResolvedValue(comunicados);

    const { result } = renderHook(() => useComunicados.listar(), { wrapper });

    await waitFor(() => expect(result.current.isSuccess).toBe(true));
    expect(result.current.data).toEqual(comunicados);
    expect(mockComunicadosService.listar).toHaveBeenCalledTimes(1);
  });

  it('devuelve comunicados filtrados por alcance', async () => {
    const comunicados = [{ id: 1, titulo: 'Comunicado GLOBAL', alcance: 'GLOBAL' }];
    mockComunicadosService.porAlcance.mockResolvedValue(comunicados);

    const { result } = renderHook(() => useComunicados.porAlcance('GLOBAL'), { wrapper });

    await waitFor(() => expect(result.current.isSuccess).toBe(true));
    expect(result.current.data).toEqual(comunicados);
    expect(mockComunicadosService.porAlcance).toHaveBeenCalledWith('GLOBAL');
  });

  it('crea un nuevo comunicado', async () => {
    const dto = { titulo: 'Nuevo comunicado', contenido: 'Contenido de prueba' };
    const creado = { id: 1, ...dto };
    mockComunicadosService.crear.mockResolvedValue(creado);

    const { result } = renderHook(() => useComunicados.crear(), { wrapper });

    await act(async () => {
      await result.current.crearComunicado(dto);
    });

    expect(mockComunicadosService.crear).toHaveBeenCalledWith(dto);
  });
});
