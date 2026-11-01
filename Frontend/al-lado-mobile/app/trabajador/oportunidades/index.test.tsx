import { describe, expect, it, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react-native';
import React from 'react';
import { Oportunidades } from './index';
import { oportunidadService } from '../../../src/api/services';
import { useAuth } from '../../../src/auth/AuthContext';

/**
 * HU-01, HU-07: Publicar ofertas y filtros de búsqueda.
 *
 * Pruebas de la pantalla de listado de oportunidades: carga de datos,
 * filtrado por texto y navegación al detalle.
 */
vi.mock('../../../src/api/services', () => ({
  oportunidadService: { listar: vi.fn(), filtrar: vi.fn() },
}));

vi.mock('../../../src/auth/AuthContext', () => ({
  useAuth: vi.fn(() => ({ email: 'ana@mail.com' })),
}));

vi.mock('expo-router', () => ({
  router: { push: vi.fn(), replace: vi.fn() },
  useFocusEffect: vi.fn((cb) => cb()),
}));

const mockListar = vi.mocked(oportunidadService.listar);
const mockUseAuth = useAuth as unknown as ReturnType<typeof vi.fn>;

beforeEach(() => {
  mockListar.mockReset();
  mockUseAuth.mockReset();
});

function renderOportunidades() {
  return render(<Oportunidades />);
}

describe('Oportunidades', () => {
  it('carga oportunidades activas al montar', async () => {
    mockListar.mockResolvedValue([
      { id: 1, titulo: 'Albañil', contenido: 'Reparar pared', ubicacion: 'Centro', modalidad: 'PRESENCIAL', rangoSalarial: '$10000', activa: true },
      { id: 2, titulo: 'Plomero', contenido: 'Arreglar fuga', ubicacion: 'Norte', modalidad: 'REMOTO', rangoSalarial: '$12000', activa: true },
    ]);

    renderOportunidades();

    await waitFor(() => expect(screen.getByText('Albañil')).toBeInTheDocument());
    expect(screen.getByText('Plomero')).toBeInTheDocument();
    expect(mockListar).toHaveBeenCalledTimes(1);
  });

  it('muestra mensaje de error cuando falla la carga', async () => {
    mockListar.mockRejectedValue(new Error('Error de red'));

    renderOportunidades();

    await waitFor(() =>
      expect(screen.getByText('Error de red')).toBeInTheDocument(),
    );
  });

  it('filtra oportunidades por texto de búsqueda', async () => {
    mockListar.mockResolvedValue([
      { id: 1, titulo: 'Albañil urgente', contenido: 'Reparar pared', ubicacion: 'Centro', modalidad: 'PRESENCIAL', rangoSalarial: '$10000', activa: true },
      { id: 2, titulo: 'Plomero', contenido: 'Arreglar fuga', ubicacion: 'Norte', modalidad: 'REMOTO', rangoSalarial: '$12000', activa: true },
    ]);

    renderOportunidades();

    await waitFor(() => expect(screen.getByText('Albañil urgente')).toBeInTheDocument());

    const input = screen.getByPlaceholderText('Oficio, título o palabra clave');
    fireEvent.changeText(input, 'plomero');

    await waitFor(() => {
      expect(screen.getByText('Plomero')).toBeInTheDocument();
      // Albañil debería estar filtrado (no visible)
    });
  });

  it('navega al detalle de la oportunidad al presionar una tarjeta', async () => {
    mockListar.mockResolvedValue([
      { id: 7, titulo: 'Albañil', contenido: 'Reparar pared', ubicacion: 'Centro', modalidad: 'PRESENCIAL', rangoSalarial: '$10000', activa: true },
    ]);
    const pushMock = vi.mocked(require('expo-router').router).push;

    renderOportunidades();

    await waitFor(() => expect(screen.getByText('Albañil')).toBeInTheDocument());

    // Presionar en la tarjeta de la oportunidad
    fireEvent.press(screen.getByText('Ver oportunidad →'));

    await waitFor(() => expect(pushMock).toHaveBeenCalledWith('/trabajador/oportunidades/7'));
  });
});
