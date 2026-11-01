import { describe, expect, it, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react-native';
import React from 'react';
import { Postular } from './postular';
import { postulacionService } from '../../../../src/api/services';
import { useAuth } from '../../../../src/auth/AuthContext';

/**
 * HU-02: Trabajador se postula a tareas.
 *
 * Pruebas del componente de postulación: envío de propuesta, validación de
 * campos obligatorios y redirección tras éxito.
 */
vi.mock('../../../../src/api/services', () => ({
  postulacionService: { crear: vi.fn() },
}));

vi.mock('../../../../src/auth/AuthContext', () => ({
  useAuth: vi.fn(() => ({ userId: 7 })),
}));

vi.mock('expo-router', () => ({
  useLocalSearchParams: vi.fn(() => ({ id: '7' })),
  router: { replace: vi.fn() },
}));

const mockCrear = vi.mocked(postulacionService.crear);
const mockUseAuth = useAuth as unknown as ReturnType<typeof vi.fn>;

beforeEach(() => {
  mockCrear.mockReset();
  mockUseAuth.mockReset();
});

function renderPostular() {
  return render(<Postular />);
}

describe('Postular', () => {
  it('envía título, contenido y remuneración al postularse', async () => {
    mockCrear.mockResolvedValue({ id: 1, estado: 'PENDIENTE' });

    renderPostular();

    fireEvent.changeText(screen.getByPlaceholderText('Título de la propuesta *'), 'Tengo experiencia');
    fireEvent.changeText(screen.getByPlaceholderText('Contenido *'), 'Puedo hacer el trabajo');
    fireEvent.changeText(screen.getByPlaceholderText('50000'), '12000');
    fireEvent.press(screen.getByText('Enviar postulación'));

    await waitFor(() => expect(mockCrear).toHaveBeenCalledWith({
      titulo: 'Tengo experiencia',
      contenido: 'Puedo hacer el trabajo',
      remuneracionPretendida: 12000,
      oportunidadLaboral: { id: 7 },
      autor: { id: 7 },
    }));
  });

  it('rechaza si faltan título o contenido', async () => {
    mockCrear.mockResolvedValue({});

    renderPostular();

    fireEvent.press(screen.getByText('Enviar postulación'));

    await waitFor(() =>
      expect(screen.getByText('Título y contenido son obligatorios.')).toBeInTheDocument(),
    );
    expect(mockCrear).not.toHaveBeenCalled();
  });

  it('muestra error cuando el servidor rechaza la postulación', async () => {
    mockCrear.mockRejectedValue(new Error('Ya te encuentras postulado a esta oportunidad'));

    renderPostular();

    fireEvent.changeText(screen.getByPlaceholderText('Título de la propuesta *'), 'Mensaje');
    fireEvent.changeText(screen.getByPlaceholderText('Contenido *'), 'Contenido');
    fireEvent.changeText(screen.getByPlaceholderText('50000'), '10000');
    fireEvent.press(screen.getByText('Enviar postulación'));

    await waitFor(() =>
      expect(screen.getByText(/ya te encuentras postulado/i)).toBeInTheDocument(),
    );
  });

  it('redirige a postulaciones tras enviar con éxito', async () => {
    mockCrear.mockResolvedValue({ id: 1, estado: 'PENDIENTE' });
    const replaceMock = vi.mocked(require('expo-router').router).replace;

    renderPostular();

    fireEvent.changeText(screen.getByPlaceholderText('Título de la propuesta *'), 'Mensaje');
    fireEvent.changeText(screen.getByPlaceholderText('Contenido *'), 'Contenido');
    fireEvent.changeText(screen.getByPlaceholderText('50000'), '10000');
    fireEvent.press(screen.getByText('Enviar postulación'));

    await waitFor(() => expect(replaceMock).toHaveBeenCalledWith('/trabajador/postulaciones'));
  });

  it('muestra error si no hay sesión activa', async () => {
    mockUseAuth.mockReturnValue({ userId: null });
    mockCrear.mockResolvedValue({});

    renderPostular();

    fireEvent.changeText(screen.getByPlaceholderText('Título de la propuesta *'), 'Mensaje');
    fireEvent.changeText(screen.getByPlaceholderText('Contenido *'), 'Contenido');
    fireEvent.press(screen.getByText('Enviar postulación'));

    await waitFor(() =>
      expect(screen.getByText('No hay sesión activa.')).toBeInTheDocument(),
    );
    expect(mockCrear).not.toHaveBeenCalled();
  });
});
