import { beforeEach, describe, expect, it, vi } from 'vitest';
import api from '@/lib/api';
import { perfilEmpresarialService } from './perfilEmpresarialService';
import { comoApiMock } from '@/test/mocks';

/**
 * HU-14: Publicar el perfil de mi comercio y obtener el Sello de Confianza.
 *
 * Contrato del cliente de perfil empresarial: obtener, guardar y verificar.
 */
vi.mock('@/lib/api', () => ({
  default: { get: vi.fn(), put: vi.fn(), delete: vi.fn() },
}));

const getApi = comoApiMock(api);

describe('perfilEmpresarialService', () => {
  beforeEach(() => {
    getApi.get.mockReset();
    getApi.put.mockReset();
  });

  it('obtener pide el perfil empresarial de un empleador', async () => {
    getApi.get.mockResolvedValue({ data: { id: 1, nombreEmpresa: 'Mi Comercio', verificado: false } });

    const perfil = await perfilEmpresarialService.obtener(7);

    expect(getApi.get).toHaveBeenCalledWith('/perfiles-empresariales/empleador/7');
    expect(perfil.nombreEmpresa).toBe('Mi Comercio');
  });

  it('guardar crea o actualiza el perfil empresarial', async () => {
    const dto = { nombreEmpresa: 'Mi Comercio', cuit: '20-12345678-9' };
    getApi.put.mockResolvedValue({ data: { id: 1, ...dto, verificado: false } });

    const guardado = await perfilEmpresarialService.guardar(7, dto);

    expect(getApi.put).toHaveBeenCalledWith('/perfiles-empresariales/empleador/7', dto);
    expect(guardado.nombreEmpresa).toBe('Mi Comercio');
  });

  it('verificar marca el perfil como verificado (solo admin)', async () => {
    getApi.put.mockResolvedValue({ data: { id: 1, verificado: true } });

    const verificado = await perfilEmpresarialService.verificar(1, 7);

    expect(getApi.put).toHaveBeenCalledWith(
      '/perfiles-empresariales/1/verificar',
      undefined,
      { params: { supervisorId: 7 } },
    );
    expect(verificado.verificado).toBe(true);
  });
});
