import { comunidadService } from './services';

/**
 * HU-14: Publicar el perfil de mi comercio y obtener el Sello de Confianza.
 *
 * Contrato de `comunidadService.empresa.perfil`.
 */
describe('comunidadService.empresa.perfil', () => {
  const fetchOriginal = global.fetch;
  let mock: jest.Mock;

  beforeEach(() => {
    mock = jest.fn().mockResolvedValue({
      ok: true,
      status: 200,
      text: jest.fn().mockResolvedValue(JSON.stringify({})),
    });
    global.fetch = mock as unknown as typeof fetch;
  });

  afterEach(() => {
    global.fetch = fetchOriginal;
    jest.restoreAllMocks();
  });

  it('perfil pide el perfil empresarial de un empleador', async () => {
    await comunidadService.empresa.perfil(7).catch(() => undefined);

    const rutas = mock.mock.calls.map(([url]) => String(url));
    expect(rutas.some((ruta) => ruta.endsWith('/perfiles-empresariales/empleador/7'))).toBe(true);
  });
});
