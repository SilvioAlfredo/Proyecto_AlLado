import { beforeEach, describe, expect, it, vi } from 'vitest';
import api from '@/lib/api';
import { publicidadesService } from './moderacionService';
import { comoApiMock } from '@/test/mocks';

/**
 * HU-15: Publicar anuncios de comercio en la plataforma.
 *
 * Contrato del cliente de publicidades: listar, vigentes, por anunciante,
 * crear, actualizar y eliminar.
 */
vi.mock('@/lib/api', () => ({
  default: { get: vi.fn(), post: vi.fn(), put: vi.fn(), delete: vi.fn() },
}));

const getApi = comoApiMock(api);

describe('publicidadesService', () => {
  beforeEach(() => {
    getApi.get.mockReset();
    getApi.post.mockReset();
    getApi.put.mockReset();
    getApi.delete.mockReset();
  });

  it('listar obtiene todas las publicidades', async () => {
    getApi.get.mockResolvedValue({ data: [{ id: 1, titulo: 'Anuncio 1', activa: true }] });

    const publicidades = await publicidadesService.listar();

    expect(getApi.get).toHaveBeenCalledWith('/publicidades');
    expect(publicidades[0].titulo).toBe('Anuncio 1');
  });

  it('vigentes obtiene los anuncios vigentes', async () => {
    getApi.get.mockResolvedValue({ data: [{ id: 1, titulo: 'Anuncio Vigente', activa: true }] });

    const vigentes = await publicidadesService.vigentes();

    expect(getApi.get).toHaveBeenCalledWith('/publicidades/vigentes');
    expect(vigentes[0].activa).toBe(true);
  });

  it('porAnunciante obtiene los anuncios de un empleador', async () => {
    getApi.get.mockResolvedValue({ data: [{ id: 1, titulo: 'Anuncio 1' }] });

    const anuncios = await publicidadesService.porAnunciante(7);

    expect(getApi.get).toHaveBeenCalledWith('/publicidades/anunciante/7');
    expect(anuncios[0].titulo).toBe('Anuncio 1');
  });

  it('crear envía un nuevo anuncio al backend', async () => {
    const dto = { titulo: 'Nuevo Anuncio', contenido: 'Descripción', activa: true };
    getApi.post.mockResolvedValue({ data: { id: 1, ...dto } });

    const creado = await publicidadesService.crear(dto);

    expect(getApi.post).toHaveBeenCalledWith('/publicidades', dto);
    expect(creado.titulo).toBe('Nuevo Anuncio');
  });

  it('actualizar modifica un anuncio existente', async () => {
    const dto = { titulo: 'Anuncio Modificado', activa: true };
    getApi.put.mockResolvedValue({ data: { id: 1, ...dto } });

    const actualizado = await publicidadesService.actualizar(1, dto);

    expect(getApi.put).toHaveBeenCalledWith('/publicidades/1', dto);
    expect(actualizado.titulo).toBe('Anuncio Modificado');
  });

  it('eliminar borra un anuncio por su ID', async () => {
    getApi.delete.mockResolvedValue(undefined);

    await publicidadesService.eliminar(1);

    expect(getApi.delete).toHaveBeenCalledWith('/publicidades/1');
  });
});
