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
    getApi.patch.mockReset();
  });

  it('contacto pide el contacto del empleador de la oferta', async () => {
    getApi.get.mockResolvedValue({
      data: { nombre: 'Empresa ABC', email: 'empleador@mail.com', telefono: '+5491100000000' },
    });

    const contacto = await oportunidadesService.contacto(7);

    expect(getApi.get).toHaveBeenCalledWith('/oportunidades/7/contacto');
    expect(contacto.email).toBe('empleador@mail.com');
  });

  it('listarConFiltros usa el POST público de filtrado con paginación', async () => {
    getApi.post.mockResolvedValue({ data: { content: [], page: 0, totalPages: 0, totalElements: 0 } });

    await oportunidadesService.listarConFiltros({ filtro: 'pintura' });

    expect(getApi.post).toHaveBeenCalledWith(
      '/oportunidades/filtrar?page=0&size=10',
      { filtro: 'pintura' }
    );
  });

  it('cerrar usa PATCH /oportunidades/{id}/cerrar', async () => {
    getApi.patch.mockResolvedValue({ data: { id: 7, titulo: 'T', activa: false } });

    const resultado = await oportunidadesService.cerrar(7);

    expect(getApi.patch).toHaveBeenCalledWith('/oportunidades/7/cerrar');
    expect(resultado.activa).toBe(false);
  });

  it('propuestasPro pide las ofertas compatibles con el trabajador', async () => {
    getApi.get.mockResolvedValue({ data: [] });

    await oportunidadesService.propuestasPro(9);

    expect(getApi.get).toHaveBeenCalledWith('/oportunidades/propuestas-pro?trabajadorId=9');
  });

  it('propuestasPro pide las ofertas compatibles con el trabajador', async () => {
    getApi.get.mockResolvedValue({ data: [] });

    await oportunidadesService.propuestasPro(9);

    expect(getApi.get).toHaveBeenCalledWith('/oportunidades/propuestas-pro?trabajadorId=9');
  });
});
