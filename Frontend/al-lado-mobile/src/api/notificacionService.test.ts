import { notificacionService } from './services';

/**
 * HU-09: Trabajador recibe notificaciones de postulaciones.
 *
 * Contrato de `notificacionService`: listar, contar no leídas, marcar como
 * leída y marcar todas como leídas.
 */
describe('notificacionService', () => {
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

  it('notificacionService.listar pide notificaciones de un usuario', async () => {
    await notificacionService.listar(7).catch(() => undefined);

    const rutas = mock.mock.calls.map(([url]) => String(url));
    expect(rutas.some((ruta) => ruta.endsWith('/notificaciones/usuario/7'))).toBe(true);
  });

  it('notificacionService.noLeidas cuenta notificaciones sin leer', async () => {
    mock.mockResolvedValueOnce({
      ok: true,
      status: 200,
      text: jest.fn().mockResolvedValue(JSON.stringify({ noLeidas: 3 })),
    });
    const contador = await notificacionService.noLeidas(7).catch(() => -1);

    expect(contador).toBe(3);
    const rutas = mock.mock.calls.map(([url]) => String(url));
    expect(rutas.some((ruta) => ruta.endsWith('/notificaciones/usuario/7/no-leidas'))).toBe(true);
  });

  it('notificacionService.marcarLeida marca una notificación como leída', async () => {
    mock.mockResolvedValueOnce({
      ok: true,
      status: 200,
      text: jest.fn().mockResolvedValue(JSON.stringify({ id: 11, leida: true })),
    });
    await notificacionService.marcarLeida(11).catch(() => undefined);

    const rutas = mock.mock.calls.map(([url]) => String(url));
    expect(rutas.some((ruta) => ruta.endsWith('/notificaciones/11/leer'))).toBe(true);
  });

  it('notificacionService.marcarTodasLeidas marca todas como leídas', async () => {
    mock.mockResolvedValueOnce({ ok: true, status: 204, text: jest.fn().mockResolvedValue('') });
    await notificacionService.marcarTodasLeidas(7).catch(() => undefined);

    const rutas = mock.mock.calls.map(([url]) => String(url));
    expect(rutas.some((ruta) => ruta.endsWith('/notificaciones/usuario/7/leer-todas'))).toBe(true);
  });
});
