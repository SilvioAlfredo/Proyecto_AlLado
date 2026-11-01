import { perfilService } from './services';

/**
 * HU-10, HU-17: Horarios de disponibilidad y portafolio.
 *
 * Contrato de `perfilService`: horarios y portafolio por trabajador, sin
 * depender de listados de usuarios ajenos.
 */
describe('perfilService', () => {
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

  it('perfilService.horarios pide horarios de un trabajador', async () => {
    await perfilService.horarios(7).catch(() => undefined);

    const rutas = mock.mock.calls.map(([url]) => String(url));
    expect(rutas.some((ruta) => ruta.endsWith('/horarios/trabajador/7'))).toBe(true);
  });

  it('perfilService.agregarHorario envía datos al endpoint de horarios', async () => {
    mock.mockResolvedValueOnce({
      ok: true,
      status: 201,
      text: jest.fn().mockResolvedValue(JSON.stringify({ id: 1 })),
    });
    await perfilService.agregarHorario(7, { diaSemana: 'Lunes', horaInicio: '09:00', horaFin: '12:00' }).catch(() => undefined);

    const rutas = mock.mock.calls.map(([url, init]) => [String(url), JSON.stringify(init?.body)]);
    expect(rutas.some(([ruta]) => ruta.endsWith('/horarios/trabajador/7'))).toBe(true);
    expect(rutas.some(([, body]) => body.includes('Lunes'))).toBe(true);
  });

  it('perfilService.portafolio pide ítems de portafolio de un trabajador', async () => {
    await perfilService.portafolio(7).catch(() => undefined);

    const rutas = mock.mock.calls.map(([url]) => String(url));
    expect(rutas.some((ruta) => ruta.endsWith('/portafolio-items/trabajador/7'))).toBe(true);
  });

  it('perfilService.preferencias pide preferencias de notificación', async () => {
    await perfilService.preferencias(7).catch(() => undefined);

    const rutas = mock.mock.calls.map(([url]) => String(url));
    expect(rutas.some((ruta) => ruta.endsWith('/preferencias/trabajador/7'))).toBe(true);
  });
});
