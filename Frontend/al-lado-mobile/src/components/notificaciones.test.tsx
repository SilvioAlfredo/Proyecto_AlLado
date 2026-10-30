import { act, fireEvent, render, screen, waitFor } from '@testing-library/react-native';
import React from 'react';
import { notificacionService } from '../api/services';
import { NotificacionesList } from './notificaciones';
import { NoLeidasContext, etiquetaBadge } from '../notificaciones/NoLeidasContext';
import type { Notificacion } from '../types';

jest.mock('../api/services', () => ({
  notificacionService: {
    listar: jest.fn(),
    noLeidas: jest.fn(),
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

const refrescar = jest.fn();

/** Monta la lista con el contador simulado, igual que en la app real. */
function renderizar() {
  return render(
    <NoLeidasContext.Provider value={{ noLeidas: 1, refrescar }}>
      <NotificacionesList usuarioId={7} />
    </NoLeidasContext.Provider>,
  );
}

describe('NotificacionesList móvil', () => {
  beforeEach(() => {
    refrescar.mockReset();
    refrescar.mockResolvedValue(undefined);
    jest.mocked(notificacionService.listar).mockResolvedValue(notificaciones);
    jest.mocked(notificacionService.marcarLeida).mockResolvedValue({ ...notificaciones[0], leida: true });
    jest.mocked(notificacionService.marcarTodasLeidas).mockResolvedValue(undefined);
  });

  it('TC-NOTIF-001 marca una notificación individual como leída', async () => {
    await renderizar();

    const marcarLeida = await screen.findByText('Marcar como leída');
    await act(async () => fireEvent.press(marcarLeida));

    await waitFor(() => expect(notificacionService.marcarLeida).toHaveBeenCalledWith(11));
    expect(screen.queryByText('Marcar como leída')).toBeNull();
    expect(screen.getByText('Aviso anterior')).toBeTruthy();
  });

  it('TC-NOTIF-002 marca todas las notificaciones como leídas para el usuario', async () => {
    await renderizar();

    const marcarTodas = await screen.findByText('Marcar todas como leídas');
    await act(async () => fireEvent.press(marcarTodas));

    await waitFor(() => expect(notificacionService.marcarTodasLeidas).toHaveBeenCalledWith(7));
    expect(screen.queryByText('Marcar todas como leídas')).toBeNull();
    expect(screen.queryByText('Marcar como leída')).toBeNull();
  });

  it('TC-NOTIF-003 refresca el contador de la tab al marcar una notificación', async () => {
    await renderizar();

    const marcarLeida = await screen.findByText('Marcar como leída');
    refrescar.mockClear();
    await act(async () => fireEvent.press(marcarLeida));

    await waitFor(() => expect(refrescar).toHaveBeenCalled());
  });

  it('TC-NOTIF-004 muestra el error de lectura sin dejar la lista a medias', async () => {
    jest.mocked(notificacionService.marcarLeida).mockRejectedValue(
      new Error('No tenés permiso para modificar esta notificación.'),
    );

    await renderizar();

    const marcarLeida = await screen.findByText('Marcar como leída');
    await act(async () => fireEvent.press(marcarLeida));

    expect(
      await screen.findByText('No tenés permiso para modificar esta notificación.'),
    ).toBeTruthy();
  });

  it('TC-NOTIF-005 la etiqueta del badge acota el contador a 9+', () => {
    expect(etiquetaBadge(0)).toBeUndefined();
    expect(etiquetaBadge(3)).toBe('3');
    expect(etiquetaBadge(12)).toBe('9+');
  });
});
