import { beforeEach, describe, expect, it, vi } from 'vitest';
import api from '@/lib/api';
import { valoracionesService } from './valoracionesService';
import { comoApiMock } from '@/test/mocks';

/**
 * HU-06: Empleador califica con estrellas y deja reseña.
 *
 * Contrato del cliente de valoraciones: listar por trabajador, promedio,
 * listar por autor, crear y eliminar.
 */
vi.mock('@/lib/api', () => ({
  default: { get: vi.fn(), post: vi.fn(), delete: vi.fn() },
}));

const getApi = comoApiMock(api);

describe('valoracionesService', () => {
  beforeEach(() => {
    getApi.get.mockReset();
    getApi.post.mockReset();
    getApi.delete.mockReset();
  });

  it('porTrabajador lista las valoraciones recibidas por un trabajador', async () => {
    getApi.get.mockResolvedValue({ data: [{ id: 1, puntuacion: 5, comentario: 'Excelente' }] });

    const valoraciones = await valoracionesService.porTrabajador(3);

    expect(getApi.get).toHaveBeenCalledWith('/valoraciones/trabajador/3');
    expect(valoraciones[0].puntuacion).toBe(5);
  });

  it('promedio devuelve el promedio de puntuación de un trabajador', async () => {
    getApi.get.mockResolvedValue({ data: 4.5 });

    const promedio = await valoracionesService.promedio(3);

    expect(getApi.get).toHaveBeenCalledWith('/valoraciones/trabajador/3/promedio');
    expect(promedio).toBe(4.5);
  });

  it('porAutor lista las valoraciones otorgadas por un empleador', async () => {
    getApi.get.mockResolvedValue({ data: [] });

    await valoracionesService.porAutor(7);

    expect(getApi.get).toHaveBeenCalledWith('/valoraciones/autor/7');
  });

  it('crear envía una nueva valoración al backend', async () => {
    const dto = { puntuacion: 5, comentario: 'Muy buen trabajo' };
    getApi.post.mockResolvedValue({ data: { id: 1, ...dto } });

    const creada = await valoracionesService.crear(dto);

    expect(getApi.post).toHaveBeenCalledWith('/valoraciones', dto);
    expect(creada.puntuacion).toBe(5);
  });

  it('eliminar borra una valoración por su ID', async () => {
    getApi.delete.mockResolvedValue(undefined);

    await valoracionesService.eliminar(1);

    expect(getApi.delete).toHaveBeenCalledWith('/valoraciones/1');
  });
});
