import { beforeEach, describe, expect, it, vi } from 'vitest';
import api from '@/lib/api';
import { notificacionesService } from './notificacionesService';
import { comoApiMock } from '@/test/mocks';

/**
 * HU-09: Trabajador recibe notificaciones de postulaciones.
 *
 * Contrato del cliente de notificaciones: listar, contar no leídas,
 * marcar como leída y marcar todas como leídas.
 */
vi.mock('@/lib/api', () => ({
  default: { get: vi.fn(), patch: vi.fn(), delete: vi.fn() },
}));

const getApi = comoApiMock(api);

describe('notificacionesService', () => {
  beforeEach(() => {
    getApi.get.mockReset();
    getApi.patch.mockReset();
  });

  it('listar pide las notificaciones de un usuario', async () => {
    getApi.get.mockResolvedValue({ data: [{ id: 1, titulo: 'Nueva postulación', leida: false }] });

    const notificaciones = await notificacionesService.listar(7);

    expect(getApi.get).toHaveBeenCalledWith('/notificaciones/usuario/7');
    expect(notificaciones[0].titulo).toBe('Nueva postulación');
  });

  it('noLeidas cuenta las notificaciones sin leer', async () => {
    getApi.get.mockResolvedValue({ data: { noLeidas: 3 } });

    const contador = await notificacionesService.noLeidas(7);

    expect(getApi.get).toHaveBeenCalledWith('/notificaciones/usuario/7/no-leidas');
    expect(contador).toBe(3);
  });

  it('marcarLeida marca una notificación individual como leída', async () => {
    getApi.patch.mockResolvedValue({ data: { id: 11, leida: true } });

    const actualizada = await notificacionesService.marcarLeida(11);

    expect(getApi.patch).toHaveBeenCalledWith('/notificaciones/11/leer');
    expect(actualizada.leida).toBe(true);
  });

  it('marcarTodasLeidas marca todas las notificaciones del usuario como leídas', async () => {
    getApi.patch.mockResolvedValue(undefined);

    await notificacionesService.marcarTodasLeidas(7);

    expect(getApi.patch).toHaveBeenCalledWith('/notificaciones/usuario/7/leer-todas');
  });
});
