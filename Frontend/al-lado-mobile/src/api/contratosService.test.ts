import { contratosService } from './services';

/**
 * HU-08: Historial de contratos del empleador y del trabajador.
 *
 * Contrato de `contratosService`: historial del empleador, historial del
 * trabajador y detalle de un contrato.
 */
describe('contratosService', () => {
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

  it('historialEmpleador pide el historial de contratos del empleador autenticado', async () => {
    await contratosService.historialEmpleador().catch(() => undefined);

    const rutas = mock.mock.calls.map(([url]) => String(url));
    expect(rutas.some((ruta) => ruta.endsWith('/contratos/historial/empleador'))).toBe(true);
  });

  it('historialTrabajador pide el historial de contratos de un trabajador', async () => {
    await contratosService.historialTrabajador(3).catch(() => undefined);

    const rutas = mock.mock.calls.map(([url]) => String(url));
    expect(rutas.some((ruta) => ruta.endsWith('/contratos/historial/trabajador/3'))).toBe(true);
  });

  it('detalle pide el detalle de un contrato por su ID', async () => {
    await contratosService.detalle(100).catch(() => undefined);

    const rutas = mock.mock.calls.map(([url]) => String(url));
    expect(rutas.some((ruta) => ruta.endsWith('/contratos/100'))).toBe(true);
  });
});
