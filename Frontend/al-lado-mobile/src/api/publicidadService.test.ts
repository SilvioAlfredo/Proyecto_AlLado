import { comunidadService } from './services';

/**
 * HU-15: Publicar anuncios de comercio en la plataforma.
 *
 * Contrato de `comunidadService.publicidad`.
 */
describe('comunidadService.publicidad', () => {
  const fetchOriginal = global.fetch;
  let mock: jest.Mock;

  beforeEach(() => {
    mock = jest.fn().mockResolvedValue({
      ok: true,
      status: 200,
      text: jest.fn().mockResolvedValue(JSON.stringify([])),
    });
    global.fetch = mock as unknown as typeof fetch;
  });

  afterEach(() => {
    global.fetch = fetchOriginal;
    jest.restoreAllMocks();
  });

  it('listar obtiene todas las publicidades', async () => {
    await comunidadService.publicidad.listar().catch(() => undefined);

    const rutas = mock.mock.calls.map(([url]) => String(url));
    expect(rutas.some((ruta) => ruta.endsWith('/publicidades'))).toBe(true);
  });

  it('vigentes obtiene los anuncios vigentes', async () => {
    await comunidadService.publicidad.vigentes().catch(() => undefined);

    const rutas = mock.mock.calls.map(([url]) => String(url));
    expect(rutas.some((ruta) => ruta.endsWith('/publicidades/vigentes'))).toBe(true);
  });
});
