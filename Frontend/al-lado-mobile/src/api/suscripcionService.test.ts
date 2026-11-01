import { suscripcionService } from './services';

/**
 * HU-16: Adquirir nivel PRO por transferencia.
 *
 * Contrato de `suscripcionService`: obtener por usuario, crear, cancelar,
 * donar (aporte) y listar donaciones del usuario.
 */
describe('suscripcionService', () => {
  const fetchOriginal = global.fetch;
  let mock: jest.Mock;

  beforeEach(() => {
    mock = jest.fn().mockResolvedValue({
      ok: true,
      status: 200,
      text: jest.fn().mockResolvedValue('[]'),
    });
    global.fetch = mock as unknown as typeof fetch;
  });

  afterEach(() => {
    global.fetch = fetchOriginal;
    jest.restoreAllMocks();
  });

  it('suscripcionService.porUsuario pide la suscripción de un usuario', async () => {
    await suscripcionService.porUsuario(7).catch(() => undefined);

    const rutas = mock.mock.calls.map(([url]) => String(url));
    expect(rutas.some((ruta) => ruta.endsWith('/suscripciones/usuario/7'))).toBe(true);
  });

  it('suscripcionService.crear registra una nueva suscripción', async () => {
    mock.mockResolvedValueOnce({
      ok: true,
      status: 201,
      text: jest.fn().mockResolvedValue(JSON.stringify({ id: 1, nivel: 'BASICO', activa: true })),
    });
    await suscripcionService.crear({ nivel: 'BASICO', usuario: { id: 7 } }).catch(() => undefined);

    const rutas = mock.mock.calls.map(([url, init]) => [String(url), JSON.stringify(init?.body)]);
    expect(rutas.some(([ruta]) => ruta.endsWith('/suscripciones'))).toBe(true);
    expect(rutas.some(([, body]) => body.includes('BASICO'))).toBe(true);
  });

  it('suscripcionService.cancelar desactiva una suscripción', async () => {
    mock.mockResolvedValueOnce({ ok: true, status: 200, text: jest.fn().mockResolvedValue(JSON.stringify({ id: 1, activa: false })) });
    await suscripcionService.cancelar(1).catch(() => undefined);

    const rutas = mock.mock.calls.map(([url, init]) => [String(url), init?.method]);
    expect(rutas.some(([ruta, metodo]) => ruta.endsWith('/suscripciones/cancelar/1') && metodo === 'PUT')).toBe(true);
  });

  it('suscripcionService.donar registra un aporte por transferencia', async () => {
    mock.mockResolvedValueOnce({
      ok: true,
      status: 201,
      text: jest.fn().mockResolvedValue(JSON.stringify({ id: 1, monto: 15000, estado: 'PENDIENTE' })),
    });
    await suscripcionService.donar({ monto: 15000, asunto: 'APORTE PRO', usuario: { id: 7 } }).catch(() => undefined);

    const rutas = mock.mock.calls.map(([url, init]) => [String(url), JSON.stringify(init?.body)]);
    expect(rutas.some(([ruta]) => ruta.endsWith('/donaciones'))).toBe(true);
    expect(rutas.some(([, body]) => body.includes('15000') && body.includes('APORTE PRO'))).toBe(true);
  });

  it('suscripcionService.donacionesDe pide donaciones de un usuario', async () => {
    await suscripcionService.donacionesDe(7).catch(() => undefined);

    const rutas = mock.mock.calls.map(([url]) => String(url));
    expect(rutas.some((ruta) => ruta.endsWith('/donaciones/usuario/7'))).toBe(true);
  });
});
