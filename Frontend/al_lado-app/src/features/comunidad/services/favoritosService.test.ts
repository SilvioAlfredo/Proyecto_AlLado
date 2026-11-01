import { beforeEach, describe, expect, it, vi } from 'vitest';
import api from '@/lib/api';
import { favoritosService } from './favoritosService';
import { comoApiMock } from '@/test/mocks';

/**
 * HU-11: Empleador almacena favoritos de trabajadores.
 *
 * Contrato del cliente de favoritos: listar por empleador, agregar y quitar.
 */
vi.mock('@/lib/api', () => ({
  default: { get: vi.fn(), post: vi.fn(), delete: vi.fn() },
}));

const getApi = comoApiMock(api);

describe('favoritosService', () => {
  beforeEach(() => {
    getApi.get.mockReset();
    getApi.post.mockReset();
    getApi.delete.mockReset();
  });

  it('porEmpleador lista los favoritos de un empleador', async () => {
    getApi.get.mockResolvedValue({ data: [{ id: 1, trabajador: { id: 3, nombre: 'Demo' } }] });

    const favoritos = await favoritosService.porEmpleador(7);

    expect(getApi.get).toHaveBeenCalledWith('/favoritos/empleador/7');
    expect(favoritos[0].trabajador?.id).toBe(3);
  });

  it('agregar añade un trabajador a los favoritos del empleador', async () => {
    getApi.post.mockResolvedValue({ data: { id: 1 } });

    await favoritosService.agregar(7, 3);

    expect(getApi.post).toHaveBeenCalledWith('/favoritos/empleador/7/trabajador/3');
  });

  it('quitar elimina un trabajador de los favoritos del empleador', async () => {
    getApi.delete.mockResolvedValue(undefined);

    await favoritosService.quitar(7, 3);

    expect(getApi.delete).toHaveBeenCalledWith('/favoritos/empleador/7/trabajador/3');
  });
});
