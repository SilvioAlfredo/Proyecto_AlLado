import { comunidadService } from './services';

/**
 * HU-13: Reportar perfiles abusivos y foro vecinal.
 *
 * Contrato de `comunidadService.reportar`.
 */
describe('comunidadService.reportar', () => {
  const fetchOriginal = global.fetch;
  let mock: jest.Mock;

  beforeEach(() => {
    mock = jest.fn().mockResolvedValue({
      ok: true,
      status: 201,
      text: jest.fn().mockResolvedValue(JSON.stringify({ id: 1 })),
    });
    global.fetch = mock as unknown as typeof fetch;
  });

  afterEach(() => {
    global.fetch = fetchOriginal;
    jest.restoreAllMocks();
  });

  it('reportar envía un reporte de perfil abusivo', async () => {
    await comunidadService.reportar({
      motivo: 'Acoso',
      descripcion: 'Comportamiento inadecuado',
      reportante: { id: 7 },
    }).catch(() => undefined);

    const rutas = mock.mock.calls.map(([url, init]) => [String(url), JSON.stringify(init?.body)]);
    expect(rutas.some(([ruta]) => ruta.endsWith('/reportes'))).toBe(true);
    expect(rutas.some(([, body]) => body.includes('Acoso'))).toBe(true);
  });
});
