import { adminService } from './services';

/**
 * HU-18, HU-19: Administración y métricas del panel admin.
 *
 * Contrato de `adminService`: métricas, usuarios, reportes, resolver reporte,
 * donaciones, verificar donación, etiquetas y comunicados.
 */
describe('adminService', () => {
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

  it('adminService.metrics pide métricas del dashboard', async () => {
    await adminService.metrics().catch(() => undefined);

    const rutas = mock.mock.calls.map(([url]) => String(url));
    expect(rutas.some((ruta) => ruta.endsWith('/api/admin/dashboard/metrics'))).toBe(true);
  });

  it('adminService.reportes lista reportes para moderación', async () => {
    await adminService.reportes().catch(() => undefined);

    const rutas = mock.mock.calls.map(([url]) => String(url));
    expect(rutas.some((ruta) => ruta.endsWith('/reportes'))).toBe(true);
  });

  it('adminService.resolverReporte resuelve un reporte con estado y moderador', async () => {
    mock.mockResolvedValueOnce({ ok: true, status: 200, text: jest.fn().mockResolvedValue('{}') });
    await adminService.resolverReporte(1, 99, 'RESUELTO').catch(() => undefined);

    const rutas = mock.mock.calls.map(([url, init]) => [String(url), init?.method, JSON.stringify(init?.body)]);
    expect(rutas.some(([ruta, metodo]) => ruta.endsWith('/reportes/1/resolver') && metodo === 'PUT')).toBe(true);
  });

  it('adminService.verificarDonacion verifica una transferencia con asunto y monto', async () => {
    mock.mockResolvedValueOnce({ ok: true, status: 200, text: jest.fn().mockResolvedValue('{}') });
    await adminService.verificarDonacion(1, 'APORTE PRO', 15000).catch(() => undefined);

    const rutas = mock.mock.calls.map(([url]) => String(url));
    expect(rutas.some((ruta) => ruta.includes('/donaciones/1/verificar'))).toBe(true);
    expect(rutas.some((ruta) => ruta.includes('asunto=APORTE%20PRO') && ruta.includes('monto=15000'))).toBe(true);
  });

  it('adminService.crearEtiqueta crea una nueva etiqueta', async () => {
    mock.mockResolvedValueOnce({ ok: true, status: 201, text: jest.fn().mockResolvedValue(JSON.stringify({ id: 1, nombre: 'Plomeria' })) });
    await adminService.crearEtiqueta('Plomeria').catch(() => undefined);

    const rutas = mock.mock.calls.map(([url, init]) => [String(url), JSON.stringify(init?.body)]);
    expect(rutas.some(([ruta]) => ruta.endsWith('/etiquetas'))).toBe(true);
    expect(rutas.some(([, body]) => body.includes('Plomeria'))).toBe(true);
  });

  it('adminService.crearComunicado crea un comunicado institucional', async () => {
    mock.mockResolvedValueOnce({ ok: true, status: 201, text: jest.fn().mockResolvedValue(JSON.stringify({ id: 1 })) });
    await adminService.crearComunicado({ titulo: 'Aviso', contenido: 'Novedades' }).catch(() => undefined);

    const rutas = mock.mock.calls.map(([url, init]) => [String(url), JSON.stringify(init?.body)]);
    expect(rutas.some(([ruta]) => ruta.endsWith('/comunicados'))).toBe(true);
    expect(rutas.some(([, body]) => body.includes('Aviso'))).toBe(true);
  });
});
