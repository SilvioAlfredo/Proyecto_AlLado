import { trabajadorService } from './services';

/**
 * HU-03, HU-05: Perfil de trabajador y empleador.
 *
 * Contrato de `trabajadorService` y `empleadorService`: obtener detalle por id,
 * actualizar perfil y estadísticas, sin listar directorios de usuarios
 * (AL-API-005).
 */
describe('trabajadorService y empleadorService', () => {
  const fetchOriginal = global.fetch;
  let mock: jest.Mock;

  beforeEach(() => {
    mock = jest.fn().mockResolvedValue({
      ok: true,
      status: 200,
      text: jest.fn().mockResolvedValue(JSON.stringify({ id: 7, nombre: 'Demo', email: 'demo@mail.com' })),
    });
    global.fetch = mock as unknown as typeof fetch;
  });

  afterEach(() => {
    global.fetch = fetchOriginal;
    jest.restoreAllMocks();
  });

  it('trabajadorService.detalle pide el trabajador por su id', async () => {
    await trabajadorService.detalle(7).catch(() => undefined);

    const rutas = mock.mock.calls.map(([url]) => String(url));
    expect(rutas.some((ruta) => ruta.endsWith('/trabajadores/7'))).toBe(true);
    expect(rutas.some((ruta) => ruta.includes('/trabajadores?'))).toBe(false);
  });

  it('empleadorService.detalle pide el empleador por su id', async () => {
    const { empleadorService } = require('./services');
    await empleadorService.detalle(9).catch(() => undefined);

    const rutas = mock.mock.calls.map(([url]) => String(url));
    expect(rutas.some((ruta) => ruta.endsWith('/empleadores/9'))).toBe(true);
  });

  it('trabajadorService.estadisticasPorEmail pide estadísticas por email', async () => {
    await trabajadorService.estadisticasPorEmail('demo@mail.com').catch(() => undefined);

    const rutas = mock.mock.calls.map(([url]) => String(url));
    expect(rutas.some((ruta) => ruta.includes('/trabajadores/por-email/estadisticas'))).toBe(true);
  });
});
