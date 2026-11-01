import { beforeEach, describe, expect, it, vi } from 'vitest';
import api from '@/lib/api';
import { reportesService } from './moderacionService';
import { comoApiMock } from '@/test/mocks';

/**
 * HU-13: Reportar perfiles abusivos y foro vecinal.
 *
 * Contrato del cliente de reportes: listar, filtrar por estado, obtener por
 * ID, crear y resolver.
 */
vi.mock('@/lib/api', () => ({
  default: { get: vi.fn(), post: vi.fn(), put: vi.fn(), delete: vi.fn() },
}));

const getApi = comoApiMock(api);

describe('reportesService', () => {
  beforeEach(() => {
    getApi.get.mockReset();
    getApi.post.mockReset();
    getApi.put.mockReset();
  });

  it('listar obtiene todos los reportes', async () => {
    getApi.get.mockResolvedValue({ data: [{ id: 1, motivo: 'Fraude', estado: 'PENDIENTE' }] });

    const reportes = await reportesService.listar();

    expect(getApi.get).toHaveBeenCalledWith('/reportes');
    expect(reportes[0].motivo).toBe('Fraude');
  });

  it('porEstado filtra reportes por estado', async () => {
    getApi.get.mockResolvedValue({ data: [{ id: 1, estado: 'RESUELTO' }] });

    const reportes = await reportesService.porEstado('RESUELTO');

    expect(getApi.get).toHaveBeenCalledWith('/reportes/estado/RESUELTO');
    expect(reportes[0].estado).toBe('RESUELTO');
  });

  it('obtener pide un reporte por su ID', async () => {
    getApi.get.mockResolvedValue({ data: { id: 1, motivo: 'Fraude' } });

    const reporte = await reportesService.obtener(1);

    expect(getApi.get).toHaveBeenCalledWith('/reportes/1');
    expect(reporte.motivo).toBe('Fraude');
  });

  it('crear envía un nuevo reporte al backend', async () => {
    const dto = { motivo: 'Acoso', descripcion: 'Comportamiento inadecuado' };
    getApi.post.mockResolvedValue({ data: { id: 1, ...dto } });

    const creado = await reportesService.crear(dto);

    expect(getApi.post).toHaveBeenCalledWith('/reportes', dto);
    expect(creado.motivo).toBe('Acoso');
  });

  it('resolver marca un reporte como resuelto', async () => {
    getApi.put.mockResolvedValue({ data: { id: 1, estado: 'RESUELTO' } });

    const resuelto = await reportesService.resolver(1, 'RESUELTO');

    expect(getApi.put).toHaveBeenCalledWith('/reportes/1/resolver?estado=RESUELTO');
    expect(resuelto.estado).toBe('RESUELTO');
  });
});
