import { beforeEach, describe, expect, it, vi } from 'vitest';
import api from '@/lib/api';
import { contratosService } from './contratosService';
import { comoApiMock } from '@/test/mocks';

/**
 * HU-08: Historial de contratos del empleador y del trabajador.
 *
 * Contrato del cliente de contratos: historial del empleador, historial del
 * trabajador y detalle de un contrato.
 */
vi.mock('@/lib/api', () => ({
  default: { get: vi.fn(), post: vi.fn(), put: vi.fn(), patch: vi.fn(), delete: vi.fn() },
}));

const getApi = comoApiMock(api);

describe('contratosService', () => {
  beforeEach(() => {
    getApi.get.mockReset();
  });

  it('historialEmpleador pide el historial de contratos del empleador autenticado', async () => {
    getApi.get.mockResolvedValue({ data: [{ id: 100, estado: 'COMPLETADA' }] });

    const contratos = await contratosService.historialEmpleador();

    expect(getApi.get).toHaveBeenCalledWith('/contratos/historial/empleador');
    expect(contratos[0].id).toBe(100);
    expect(contratos[0].estado).toBe('COMPLETADA');
  });

  it('historialTrabajador pide el historial de contratos de un trabajador', async () => {
    getApi.get.mockResolvedValue({ data: [{ id: 101, estado: 'SELECCIONADA' }] });

    const contratos = await contratosService.historialTrabajador(3);

    expect(getApi.get).toHaveBeenCalledWith('/contratos/historial/trabajador/3');
    expect(contratos[0].id).toBe(101);
    expect(contratos[0].estado).toBe('SELECCIONADA');
  });

  it('obtenerPorId pide el detalle de un contrato por su ID', async () => {
    getApi.get.mockResolvedValue({ data: { id: 100, estado: 'COMPLETADA' } });

    const contrato = await contratosService.obtenerPorId(100);

    expect(getApi.get).toHaveBeenCalledWith('/contratos/100');
    expect(contrato.id).toBe(100);
  });
});
