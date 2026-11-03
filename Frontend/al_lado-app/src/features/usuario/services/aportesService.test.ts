import { beforeEach, describe, expect, it, vi } from 'vitest';
import api from '@/lib/api';
import { suscripcionesService, donacionesService } from './aportesService';
import { comoApiMock } from '@/test/mocks';

/**
 * HU-16: Adquirir nivel PRO por transferencia.
 *
 * Contrato del cliente de suscripciones y donaciones: listar, obtener por
 * usuario, crear, cancelar y verificar transferencia.
 */
vi.mock('@/lib/api', () => ({
  default: { get: vi.fn(), post: vi.fn(), put: vi.fn(), delete: vi.fn() },
}));

const getApi = comoApiMock(api);

describe('suscripcionesService', () => {
  beforeEach(() => {
    getApi.get.mockReset();
    getApi.post.mockReset();
    getApi.put.mockReset();
    getApi.delete.mockReset();
  });

  it('listar obtiene todas las suscripciones', async () => {
    getApi.get.mockResolvedValue({ data: [{ id: 1, nivel: 'PRO', activa: true }] });

    const suscripciones = await suscripcionesService.listar();

    expect(getApi.get).toHaveBeenCalledWith('/suscripciones');
    expect(suscripciones[0].nivel).toBe('PRO');
  });

  it('porUsuario obtiene la suscripción de un usuario', async () => {
    getApi.get.mockResolvedValue({ data: { id: 1, nivel: 'BASICO', activa: true } });

    const suscripcion = await suscripcionesService.porUsuario(7);

    expect(getApi.get).toHaveBeenCalledWith('/suscripciones/usuario/7');
    expect(suscripcion.nivel).toBe('BASICO');
  });

  it('crear registra una nueva suscripción', async () => {
    const dto = {
      nivel: 'BASICO',
      usuario: { id: 7, nombre: 'Ana', email: 'ana@mail.com' },
    };
    getApi.post.mockResolvedValue({ data: { id: 1, ...dto } });

    const creada = await suscripcionesService.crear(dto);

    expect(getApi.post).toHaveBeenCalledWith('/suscripciones', dto);
    expect(creada.nivel).toBe('BASICO');
  });

  it('cancelar desactiva una suscripción', async () => {
    getApi.put.mockResolvedValue({ data: { id: 1, activa: false } });

    const cancelada = await suscripcionesService.cancelar(1);

    expect(getApi.put).toHaveBeenCalledWith('/suscripciones/cancelar/1');
    expect(cancelada.activa).toBe(false);
  });

  it('eliminar borra una suscripción por su ID', async () => {
    getApi.delete.mockResolvedValue(undefined);

    await suscripcionesService.eliminar(1);

    expect(getApi.delete).toHaveBeenCalledWith('/suscripciones/1');
  });
});

describe('donacionesService', () => {
  beforeEach(() => {
    getApi.get.mockReset();
    getApi.post.mockReset();
  });

  it('listar obtiene todas las donaciones', async () => {
    getApi.get.mockResolvedValue({ data: [{ id: 1, monto: 15000, estado: 'PENDIENTE' }] });

    const donaciones = await donacionesService.listar();

    expect(getApi.get).toHaveBeenCalledWith('/donaciones');
    expect(donaciones[0].monto).toBe(15000);
  });

  it('porUsuario obtiene las donaciones de un usuario', async () => {
    getApi.get.mockResolvedValue({ data: [{ id: 1, monto: 15000 }] });

    const donaciones = await donacionesService.porUsuario(7);

    expect(getApi.get).toHaveBeenCalledWith('/donaciones/usuario/7', { params: { page: 0, size: 10 } });
    expect(donaciones[0].monto).toBe(15000);
  });

  it('crear registra una nueva donación', async () => {
    const dto = {
      monto: 15000,
      asunto: 'APORTE PRO',
      usuario: { id: 7, nombre: 'Ana', email: 'ana@mail.com' },
    };
    getApi.post.mockResolvedValue({ data: { id: 1, ...dto } });

    const creada = await donacionesService.crear(dto);

    expect(getApi.post).toHaveBeenCalledWith('/donaciones', dto);
    expect(creada.monto).toBe(15000);
  });

  it('verificar solicita verificar una transferencia con asunto y monto', async () => {
    getApi.post.mockResolvedValue({ data: { id: 1, estado: 'VERIFICADO' } });

    const verificada = await donacionesService.verificar(1, 'APORTE PRO', 15000);

    expect(getApi.post).toHaveBeenCalledWith(
      '/donaciones/1/verificar?asunto=APORTE%20PRO&monto=15000',
    );
    expect(verificada.estado).toBe('VERIFICADO');
  });
});
