import { beforeEach, describe, expect, it, vi } from 'vitest';
import api from '@/lib/api';
import { comunicadosService } from './moderacionService';
import { comoApiMock } from '@/test/mocks';

/**
 * HU-20: Usuario lee comunicados institucionales.
 *
 * Contrato del cliente de comunicados: listar, filtrar por alcance, crear y
 * eliminar.
 */
vi.mock('@/lib/api', () => ({
  default: { get: vi.fn(), post: vi.fn(), delete: vi.fn() },
}));

const getApi = comoApiMock(api);

describe('comunicadosService', () => {
  beforeEach(() => {
    getApi.get.mockReset();
    getApi.post.mockReset();
    getApi.delete.mockReset();
  });

  it('listar obtiene todos los comunicados', async () => {
    getApi.get.mockResolvedValue({ data: [{ id: 1, titulo: 'Comunicado 1' }] });

    const comunicados = await comunicadosService.listar();

    expect(getApi.get).toHaveBeenCalledWith('/comunicados');
    expect(comunicados[0].titulo).toBe('Comunicado 1');
  });

  it('porAlcance filtra comunicados por alcance', async () => {
    getApi.get.mockResolvedValue({ data: [{ id: 1, titulo: 'Comunicado GLOBAL', alcance: 'GLOBAL' }] });

    const comunicados = await comunicadosService.porAlcance('GLOBAL');

    expect(getApi.get).toHaveBeenCalledWith('/comunicados/alcance/GLOBAL');
    expect(comunicados[0].alcance).toBe('GLOBAL');
  });

  it('crear envía un nuevo comunicado al backend', async () => {
    const dto = { titulo: 'Nuevo comunicado', contenido: 'Contenido de prueba' };
    getApi.post.mockResolvedValue({ data: { id: 1, ...dto } });

    const creado = await comunicadosService.crear(dto);

    expect(getApi.post).toHaveBeenCalledWith('/comunicados', dto);
    expect(creado.titulo).toBe('Nuevo comunicado');
  });

  it('eliminar borra un comunicado por su ID', async () => {
    getApi.delete.mockResolvedValue(undefined);

    await comunicadosService.eliminar(1);

    expect(getApi.delete).toHaveBeenCalledWith('/comunicados/1');
  });
});
