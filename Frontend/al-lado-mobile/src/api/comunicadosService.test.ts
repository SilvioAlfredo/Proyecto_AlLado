import { comunidadService } from './services';

/**
 * HU-20: Usuario lee comunicados institucionales.
 *
 * Contrato de `comunidadService.comunicados`.
 */
describe('comunidadService.comunicados', () => {
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

  it('comunicados obtiene los comunicados institucionales', async () => {
    await comunidadService.comunicados().catch(() => undefined);

    const rutas = mock.mock.calls.map(([url]) => String(url));
    expect(rutas.some((ruta) => ruta.endsWith('/comunicados'))).toBe(true);
  });
});
