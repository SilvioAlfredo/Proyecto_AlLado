import { beforeEach, describe, expect, it, vi } from 'vitest';
import { renderHook, waitFor } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { useForo } from './useForo';
import { foroService } from '../services/foroService';

/**
 * HU-13: Participación en el foro vecinal.
 *
 * Integración del hook useForo con React Query y el servicio de foro.
 */
vi.mock('../services/foroService', () => ({
  foroService: {
    listar: vi.fn(),
    obtener: vi.fn(),
    crear: vi.fn(),
    eliminar: vi.fn(),
  },
}));

const mockForoService = vi.mocked(foroService);

function crearWrapper() {
  const cliente = new QueryClient({ defaultOptions: { queries: { retry: false } } });
  return ({ children }: { children: React.ReactNode }) => (
    <QueryClientProvider client={cliente}>{children}</QueryClientProvider>
  );
}

describe('useForo', () => {
  let wrapper: ReturnType<typeof crearWrapper>;

  beforeEach(() => {
    wrapper = crearWrapper();
    mockForoService.listar.mockReset();
    mockForoService.obtener.mockReset();
    mockForoService.crear.mockReset();
    mockForoService.eliminar.mockReset();
  });

  it('devuelve la lista de publicaciones del foro', async () => {
    const publicaciones = [
      { id: 1, titulo: 'Publicación 1', contenido: 'Contenido 1' },
      { id: 2, titulo: 'Publicación 2', contenido: 'Contenido 2' },
    ];
    mockForoService.listar.mockResolvedValue(publicaciones);

    const { result } = renderHook(() => useForo.listar(), { wrapper });

    await waitFor(() => expect(result.current.isSuccess).toBe(true));
    expect(result.current.data).toEqual(publicaciones);
    expect(mockForoService.listar).toHaveBeenCalledTimes(1);
  });

  it('devuelve una publicación por su ID', async () => {
    const publicacion = { id: 1, titulo: 'Publicación 1', contenido: 'Contenido 1' };
    mockForoService.obtener.mockResolvedValue(publicacion);

    const { result } = renderHook(() => useForo.obtener(1), { wrapper });

    await waitFor(() => expect(result.current.isSuccess).toBe(true));
    expect(result.current.data).toEqual(publicacion);
    expect(mockForoService.obtener).toHaveBeenCalledWith(1);
  });

  it('crea una nueva publicación', async () => {
    const dto = { titulo: 'Nueva publicación', contenido: 'Contenido de prueba' };
    const creada = { id: 1, ...dto };
    mockForoService.crear.mockResolvedValue(creada);

    const { result } = renderHook(() => useForo.crear(), { wrapper });

    await act(async () => {
      await result.current.createPublicacion(dto);
    });

    expect(mockForoService.crear).toHaveBeenCalledWith(dto);
  });
});
