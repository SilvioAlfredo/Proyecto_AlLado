import { beforeEach, describe, expect, it, vi } from 'vitest';
import api from '@/lib/api';
import { oportunidadesService } from './oportunidadService';
import { comoApiMock } from '@/test/mocks';

/**
 * Contrato del cliente de ofertas.
 *
 * - `contacto` es el endpoint nuevo de AL-SEC-006: el contacto del empleador no
 *   viene en la oferta pública, se pide aparte.
 * - `cerrar` sigue usando el PUT con `activa=false` (no hay endpoint dedicated).
 */

vi.mock('@/lib/api', () => ({
  default: { get: vi.fn(), post: vi.fn(), put: vi.fn(), patch: vi.fn(), delete: vi.fn() },
}));

const getApi = comoApiMock(api);

describe('oportunidadesService', () => {
  beforeEach(() => {
    getApi.get.mockReset();
    getApi.post.mockReset();
    getApi.put.mockReset();
  });

  it('contacto pide el contacto del empleador de la oferta', async () => {
    getApi.get.mockResolvedValue({
      data: { nombre: 'Empresa ABC', email: 'empleador@mail.com', telefono: '+5491100000000' },
    });

    const contacto = await oportunidadesService.contacto(7);

    expect(getApi.get).toHaveBeenCalledWith('/oportunidades/7/contacto');
    expect(contacto.email).toBe('empleador@mail.com');
  });

  it('listarConFiltros usa el POST público de filtrado', async () => {
    getApi.post.mockResolvedValue({ data: [] });

    await oportunidadesService.listarConFiltros({ filtro: 'pintura' });

    expect(getApi.post).toHaveBeenCalledWith('/oportunidades/filtrar', { filtro: 'pintura' });
  });

  it('cerrar relee la oferta y la actualiza con activa=false', async () => {
    getApi.get.mockResolvedValue({ data: { id: 7, titulo: 'T', activa: true } });
    getApi.put.mockResolvedValue({ data: { id: 7, titulo: 'T', activa: false } });

    const resultado = await oportunidadesService.cerrar(7);

    expect(getApi.put).toHaveBeenCalledWith('/oportunidades/7', expect.objectContaining({ id: 7, activa: false }));
    expect(resultado.activa).toBe(false);
  });

  it('propuestasPro pide las ofertas compatibles con el trabajador', async () => {
    getApi.get.mockResolvedValue({ data: [] });

    await oportunidadesService.propuestasPro(9);

    expect(getApi.get).toHaveBeenCalledWith('/oportunidades/propuestas-pro?trabajadorId=9');
  });
});
