import { act, fireEvent, render, screen, waitFor } from '@testing-library/react-native';
import React from 'react';
import { notificacionService } from '../api/services';
import { NotificacionesList } from './notificaciones';
import type { Notificacion } from '../types';

jest.mock('../api/services', () => ({
  notificacionService: {
    listar: jest.fn(),
    marcarLeida: jest.fn(),
    marcarTodasLeidas: jest.fn(),
  },
}));

const notificaciones: Notificacion[] = [
  {
    id: 11,
    titulo: 'Nueva postulación recibida',
    mensaje: 'Ana se postuló a tu oferta.',
    tipo: 'POSTULACION_RECIBIDA',
    leida: false,
  },
  {
    id: 12,
    titulo: 'Aviso anterior',
    mensaje: 'La publicación fue actualizada.',
    tipo: 'SISTEMA',
    leida: true,
  },
];

describe('NotificacionesList móvil', () => {
  beforeEach(() => {
    jest.mocked(notificacionService.listar).mockResolvedValue(notificaciones);
    jest.mocked(notificacionService.marcarLeida).mockResolvedValue({ ...notificaciones[0], leida: true });
    jest.mocked(notificacionService.marcarTodasLeidas).mockResolvedValue(undefined);
  });

  it('TC-NOTIF-001 marca una notificación individual como leída', async () => {
    await render(<NotificacionesList usuarioId={7} />);

    const marcarLeida = await screen.findByText('Marcar como leída');
    await act(async () => fireEvent.press(marcarLeida));

    await waitFor(() => expect(notificacionService.marcarLeida).toHaveBeenCalledWith(11));
    expect(screen.queryByText('Marcar como leída')).toBeNull();
    expect(screen.getByText('Aviso anterior')).toBeTruthy();
  });

  it('TC-NOTIF-002 marca todas las notificaciones como leídas para el usuario', async () => {
    await render(<NotificacionesList usuarioId={7} />);

    const marcarTodas = await screen.findByText('Marcar todas como leídas');
    await act(async () => fireEvent.press(marcarTodas));

    await waitFor(() => expect(notificacionService.marcarTodasLeidas).toHaveBeenCalledWith(7));
    expect(screen.queryByText('Marcar todas como leídas')).toBeNull();
    expect(screen.queryByText('Marcar como leída')).toBeNull();
  });
});