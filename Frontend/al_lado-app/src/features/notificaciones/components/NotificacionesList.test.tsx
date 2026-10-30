import { QueryClient, QueryClientProvider, useQuery } from '@tanstack/react-query';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { notificacionesService } from '../services/notificacionesService';
import { notificacionesKeys } from '../queryKeys';
import { useNotificaciones } from '../hooks/useNotificaciones';
import { NotificacionesList } from './NotificacionesList';

vi.mock('../services/notificacionesService', () => ({
  notificacionesService: {
    listar: vi.fn(),
    noLeidas: vi.fn(),
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

const todasLeidas = notificaciones.map((n) => ({ ...n, leida: true }));

/** Contenedor real: el estado y las mutaciones viven en `useNotificaciones`. */
function Lista({ usuarioId }: { usuarioId: number }) {
  const consulta = useNotificaciones(usuarioId);
  return <NotificacionesList consulta={consulta} />;
}

/** Lee el mismo contador que la Navbar para verificar que se invalida. */
function Contador({ usuarioId }: { usuarioId: number }) {
  const { data } = useQuery({
    queryKey: notificacionesKeys.contador(usuarioId),
    queryFn: () => notificacionesService.noLeidas(usuarioId),
  });
  return <span data-testid="contador">{data ?? 0}</span>;
}

function renderizar(usuarioId = 7) {
  const cliente = new QueryClient({
    defaultOptions: { queries: { retry: false } },
  });
  return render(
    <QueryClientProvider client={cliente}>
      <Contador usuarioId={usuarioId} />
      <Lista usuarioId={usuarioId} />
    </QueryClientProvider>,
  );
}

describe('NotificacionesList', () => {
  beforeEach(() => {
    vi.mocked(notificacionesService.listar).mockResolvedValue(notificaciones);
    vi.mocked(notificacionesService.noLeidas).mockResolvedValue(1);
    vi.mocked(notificacionesService.marcarLeida).mockResolvedValue({
      ...notificaciones[0],
      leida: true,
    });
    vi.mocked(notificacionesService.marcarTodasLeidas).mockResolvedValue(undefined);
  });

  it('TC-NOTIF-001 marca una notificación individual como leída', async () => {
    vi.mocked(notificacionesService.listar)
      .mockResolvedValueOnce(notificaciones)
      .mockResolvedValue(todasLeidas);

    renderizar();

    fireEvent.click(await screen.findByRole('button', { name: 'Marcar como leída' }));

    await waitFor(() => expect(notificacionesService.marcarLeida).toHaveBeenCalledWith(11));
    expect(screen.queryByRole('button', { name: 'Marcar como leída' })).not.toBeInTheDocument();
    expect(screen.getByText('Aviso anterior')).toBeInTheDocument();
  });

  it('TC-NOTIF-002 marca todas las notificaciones como leídas para el usuario', async () => {
    vi.mocked(notificacionesService.listar)
      .mockResolvedValueOnce(notificaciones)
      .mockResolvedValue(todasLeidas);

    renderizar();

    fireEvent.click(await screen.findByRole('button', { name: 'Marcar todas como leídas' }));

    await waitFor(() => expect(notificacionesService.marcarTodasLeidas).toHaveBeenCalledWith(7));
    await waitFor(() =>
      expect(
        screen.queryByRole('button', { name: 'Marcar todas como leídas' }),
      ).not.toBeInTheDocument(),
    );
    expect(screen.queryByRole('button', { name: 'Marcar como leída' })).not.toBeInTheDocument();
  });

  it('TC-NOTIF-003 invalida el contador de no leídas al marcar una notificación', async () => {
    vi.mocked(notificacionesService.listar)
      .mockResolvedValueOnce(notificaciones)
      .mockResolvedValue(todasLeidas);
    vi.mocked(notificacionesService.noLeidas)
      .mockResolvedValueOnce(1)
      .mockResolvedValue(0);

    renderizar();

    await waitFor(() => expect(screen.getByTestId('contador')).toHaveTextContent('1'));

    fireEvent.click(screen.getByRole('button', { name: 'Marcar como leída' }));

    await waitFor(() => expect(screen.getByTestId('contador')).toHaveTextContent('0'));
  });

  it('TC-NOTIF-004 muestra el error de lectura sin dejar la lista a medias', async () => {
    vi.mocked(notificacionesService.marcarLeida).mockRejectedValue(
      new Error('No tenés permiso para modificar esta notificación.'),
    );

    renderizar();

    fireEvent.click(await screen.findByRole('button', { name: 'Marcar como leída' }));

    expect(await screen.findByRole('alert')).toHaveTextContent(
      'No tenés permiso para modificar esta notificación.',
    );
  });
});
