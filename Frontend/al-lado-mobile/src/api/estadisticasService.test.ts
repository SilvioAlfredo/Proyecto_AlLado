import { trabajadorService } from './services';

/**
 * HU-12: Trabajador visualiza estadísticas de ingresos.
 *
 * Contrato de `trabajadorService.estadisticas`: estadísticas del trabajador.
 */
describe('trabajadorService.estadisticas', () => {
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

  it('estadisticas pide las estadísticas de un trabajador por su ID', async () => {
    await trabajadorService.estadisticas(3).catch(() => undefined);

    const rutas = mock.mock.calls.map(([url]) => String(url));
    expect(rutas.some((ruta) => ruta.endsWith('/trabajadores/3/estadisticas'))).toBe(true);
  });
});
