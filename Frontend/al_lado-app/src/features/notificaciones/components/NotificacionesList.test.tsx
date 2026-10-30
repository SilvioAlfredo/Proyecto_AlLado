import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { notificacionesService } from '../services/notificacionesService';
import { NotificacionesList } from './NotificacionesList';

vi.mock('../services/notificacionesService', () => ({
  notificacionesService: {
    listar: vi.fn(),
    marcarLeida: vi.fn(),
    marcarTodasLeidas: vi.fn(),
  },
}));

const notificaciones = [
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

describe('NotificacionesList', () => {
  beforeEach(() => {
    vi.mocked(notificacionesService.listar).mockResolvedValue(notificaciones);
    vi.mocked(notificacionesService.marcarLeida).mockResolvedValue({ ...notificaciones[0], leida: true });
    vi.mocked(notificacionesService.marcarTodasLeidas).mockResolvedValue();
  });

  it('TC-NOTIF-001 marca una notificación individual como leída', async () => {
    render(<NotificacionesList usuarioId={7} />);

    fireEvent.click(await screen.findByRole('button', { name: 'Marcar como leída' }));

    await waitFor(() => expect(notificacionesService.marcarLeida).toHaveBeenCalledWith(11));
    expect(screen.queryByRole('button', { name: 'Marcar como leída' })).not.toBeInTheDocument();
    expect(screen.getByText('Aviso anterior')).toBeInTheDocument();
  });

  it('TC-NOTIF-002 marca todas las notificaciones como leídas para el usuario', async () => {
    render(<NotificacionesList usuarioId={7} />);

    fireEvent.click(await screen.findByRole('button', { name: 'Marcar todas como leídas' }));

    await waitFor(() => expect(notificacionesService.marcarTodasLeidas).toHaveBeenCalledWith(7));
    expect(screen.queryByRole('button', { name: 'Marcar todas como leídas' })).not.toBeInTheDocument();
    expect(screen.queryByRole('button', { name: 'Marcar como leída' })).not.toBeInTheDocument();
  });
});