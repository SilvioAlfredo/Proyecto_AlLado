import { beforeEach, describe, expect, it, vi } from 'vitest';
import api from '@/lib/api';
import { postulacionesService } from './postulacionesService';
import { comoApiMock } from '@/test/mocks';

/**
 * HU-02: Trabajador se postula a tareas.
 *
 * Contrato del cliente de postulaciones: crear, seleccionar, rechazar,
 * completar y retirar, usando PATCH para los cambios de estado.
 */
vi.mock('@/lib/api', () => ({
  default: { get: vi.fn(), post: vi.fn(), put: vi.fn(), patch: vi.fn(), delete: vi.fn() },
}));

const getApi = comoApiMock(api);

describe('postulacionesService', () => {
  beforeEach(() => {
    getApi.get.mockReset();
    getApi.post.mockReset();
    getApi.patch.mockReset();
  });

  it('obtenerTodas lista las postulaciones del usuario autenticado', async () => {
    getApi.get.mockResolvedValue({ data: [] });

    await postulacionesService.obtenerTodas();

    expect(getApi.get).toHaveBeenCalledWith('/postulaciones');
  });

  it('obtenerPorId pide una postulación por su ID', async () => {
    getApi.get.mockResolvedValue({ data: { id: 1, estado: 'PENDIENTE' } });

    const postulacion = await postulacionesService.obtenerPorId(1);

    expect(getApi.get).toHaveBeenCalledWith('/postulaciones/1');
    expect(postulacion.estado).toBe('PENDIENTE');
  });

  it('crear envía una nueva postulación al backend', async () => {
    const dto = {
      titulo: 'Postulación',
      contenido: 'Mensaje de postulación',
      estado: 'PENDIENTE',
      remuneracionPretendida: 1000,
      oportunidadLaboral: {
        id: 7,
        titulo: 'Postulación',
        contenido: 'Mensaje de postulación',
        ubicacion: 'Barrio Centro',
        modalidad: 'PRESENCIAL',
        rangoSalarial: '$1000',
        activa: true,
        fechaCreacion: '2026-01-01',
      },
    };
    getApi.post.mockResolvedValue({ data: dto });

    const creada = await postulacionesService.crear(dto);

    expect(getApi.post).toHaveBeenCalledWith('/postulaciones', dto);
    expect(creada.estado).toBe('PENDIENTE');
  });

  it('seleccionar usa PATCH /postulaciones/{id}/seleccionar', async () => {
    getApi.patch.mockResolvedValue({ data: { id: 1, estado: 'SELECCIONADA' } });

    const seleccionada = await postulacionesService.seleccionar(1);

    expect(getApi.patch).toHaveBeenCalledWith('/postulaciones/1/seleccionar');
    expect(seleccionada.estado).toBe('SELECCIONADA');
  });

  it('rechazar usa PATCH /postulaciones/{id}/rechazar', async () => {
    getApi.patch.mockResolvedValue({ data: { id: 1, estado: 'RECHAZADA' } });

    const rechazada = await postulacionesService.rechazar(1);

    expect(getApi.patch).toHaveBeenCalledWith('/postulaciones/1/rechazar');
    expect(rechazada.estado).toBe('RECHAZADA');
  });

  it('completar usa PATCH /postulaciones/{id}/completar', async () => {
    getApi.patch.mockResolvedValue({ data: { id: 1, estado: 'COMPLETADA' } });

    const completada = await postulacionesService.completar(1);

    expect(getApi.patch).toHaveBeenCalledWith('/postulaciones/1/completar');
    expect(completada.estado).toBe('COMPLETADA');
  });

  it('retirar usa PATCH /postulaciones/{id}/retirar', async () => {
    getApi.patch.mockResolvedValue({ data: { id: 1, estado: 'RETIRADA' } });

    const retirada = await postulacionesService.retirar(1);

    expect(getApi.patch).toHaveBeenCalledWith('/postulaciones/1/retirar');
    expect(retirada.estado).toBe('RETIRADA');
  });
});
