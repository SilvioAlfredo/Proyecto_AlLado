import { beforeEach, describe, expect, it, vi } from 'vitest';
import api from '@/lib/api';
import { foroService } from './moderacionService';
import { comoApiMock } from '@/test/mocks';

/**
 * HU-13: Participación en el foro vecinal.
 *
 * Contrato del cliente de foro: listar, obtener, filtrar por autor, crear y
 * eliminar publicaciones.
 */
vi.mock('@/lib/api', () => ({
  default: { get: vi.fn(), post: vi.fn(), delete: vi.fn() },
}));

const getApi = comoApiMock(api);

describe('foroService', () => {
  beforeEach(() => {
    getApi.get.mockReset();
    getApi.post.mockReset();
    getApi.delete.mockReset();
  });

  it('listar obtiene todas las publicaciones del foro', async () => {
    getApi.get.mockResolvedValue({ data: [{ id: 1, titulo: 'Publicación 1' }] });

    const publicaciones = await foroService.listar();

    expect(getApi.get).toHaveBeenCalledWith('/foro');
    expect(publicaciones[0].titulo).toBe('Publicación 1');
  });

  it('obtener pide una publicación por su ID', async () => {
    getApi.get.mockResolvedValue({ data: { id: 1, titulo: 'Publicación 1' } });

    const publicacion = await foroService.obtener(1);

    expect(getApi.get).toHaveBeenCalledWith('/foro/1');
    expect(publicacion.titulo).toBe('Publicación 1');
  });

  it('crear envía una nueva publicación al foro', async () => {
    const dto = { titulo: 'Nueva publicación', contenido: 'Contenido de prueba' };
    getApi.post.mockResolvedValue({ data: { id: 1, ...dto } });

    const creada = await foroService.crear(dto);

    expect(getApi.post).toHaveBeenCalledWith('/foro', dto);
    expect(creada.titulo).toBe('Nueva publicación');
  });

  it('eliminar borra una publicación por su ID', async () => {
    getApi.delete.mockResolvedValue(undefined);

    await foroService.eliminar(1);

    expect(getApi.delete).toHaveBeenCalledWith('/foro/1');
  });
});
