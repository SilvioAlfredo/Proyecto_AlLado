import { beforeEach, describe, expect, it, vi } from 'vitest';
import { renderHook, waitFor } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { useStats } from './useStats';
import { AuthContext, type AuthContextValue } from '@/features/auth/hooks/useAuth';
import { trabajadorService } from '@/features/usuario/services/trabajadorService';
import { comoApiMock } from '@/test/mocks';
import api from '@/lib/api';

/**
 * HU-12: Trabajador visualiza estadísticas de ingresos.
 *
 * Integración del hook useStats con React Query y el servicio de estadísticas.
 */
vi.mock('@/lib/api', () => ({
  default: { get: vi.fn(), post: vi.fn(), put: vi.fn(), patch: vi.fn(), delete: vi.fn() },
}));
vi.mock('@/features/usuario/services/trabajadorService', () => ({
  trabajadorService: { obtenerEstadisticas: vi.fn() },
}));

const getApi = comoApiMock(api);

function crearWrapper(userId: number | null = null) {
  const cliente = new QueryClient({ defaultOptions: { queries: { retry: false } } });
  // useStats lee userId de la sesión; el wrapper provee un contexto mínimo.
  const valorAuth: AuthContextValue = {
    user: userId ? { email: 'test@mail.com', roles: ['ROLE_TRABAJADOR'] } : null,
    userId,
    nombre: null,
    loading: false,
    login: vi.fn(),
    register: vi.fn(),
    logout: vi.fn(),
  };
  function Wrapper({ children }: { children: React.ReactNode }) {
    return (
      <QueryClientProvider client={cliente}>
        <AuthContext.Provider value={valorAuth}>{children}</AuthContext.Provider>
      </QueryClientProvider>
    );
  }
  return Wrapper;
}

describe('useStats', () => {
  let wrapper: ReturnType<typeof crearWrapper>;

  beforeEach(() => {
    wrapper = crearWrapper();
    getApi.get.mockReset();
    vi.mocked(trabajadorService.obtenerEstadisticas).mockReset();
  });

  it('devuelve las estadísticas del trabajador autenticado', async () => {
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
    vi.mocked(trabajadorService.obtenerEstadisticas).mockResolvedValue(estadisticas);

    const { result } = renderHook(() => useStats(7), { wrapper });

    await waitFor(() => expect(result.current.isSuccess).toBe(true));
    expect(result.current.data?.nombre).toBe('Ana');
    expect(result.current.data?.ingresosTotales).toBe(50000);
    expect(trabajadorService.obtenerEstadisticas).toHaveBeenCalledWith(7);
  });

  it('usa el userId del auth cuando no se pasa trabajadorId', async () => {
    vi.mocked(trabajadorService.obtenerEstadisticas).mockResolvedValue({
      id: 99, nombre: 'Demo', email: 'demo@mail.com',
      ingresosTotales: 0, ingresosUltimos3Meses: 0, ingresosDisponibles: false,
      valoracionesCount: 0, promedioValoraciones: 0,
      ofertasPublicadas: 0, postulacionesEnviadas: 0, postulacionesSeleccionadas: 0,
      postulacionesCompletadas: 0, nivelRecomendacion: 0, perfilCompleto: false,
      ultimoLogin: null, suscripcionActiva: false,
    });

    const { result } = renderHook(() => useStats(), { wrapper: crearWrapper(99) });

    await waitFor(() => expect(result.current.isSuccess).toBe(true));
    expect(trabajadorService.obtenerEstadisticas).toHaveBeenCalledWith(99);
  });

  it('no consulta si no hay id de trabajador ni de sesión', async () => {
    const { result } = renderHook(() => useStats(), { wrapper });

    await waitFor(() => expect(result.current.isSuccess).toBe(false));
    expect(trabajadorService.obtenerEstadisticas).not.toHaveBeenCalled();
  });
});
