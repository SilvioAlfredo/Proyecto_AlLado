import { comunidadService } from './services';

/**
 * HU-06: Empleador califica con estrellas y deja reseña.
 *
 * Contrato de `comunidadService.valoracionesDe`, `comunidadService.promedio`,
 * `comunidadService.valorar`.
 */
describe('comunidadService.valoraciones', () => {
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

  it('valoracionesDe pide las valoraciones de un trabajador', async () => {
    await comunidadService.valoracionesDe(3).catch(() => undefined);

    const rutas = mock.mock.calls.map(([url]) => String(url));
    expect(rutas.some((ruta) => ruta.endsWith('/valoraciones/trabajador/3'))).toBe(true);
  });

  it('promedio pide el promedio de puntuación de un trabajador', async () => {
    await comunidadService.promedio(3).catch(() => undefined);

    const rutas = mock.mock.calls.map(([url]) => String(url));
    expect(rutas.some((ruta) => ruta.endsWith('/valoraciones/trabajador/3/promedio'))).toBe(true);
  });

  it('valorar envía una nueva valoración al backend', async () => {
    mock.mockResolvedValueOnce({
      ok: true,
      status: 201,
      text: jest.fn().mockResolvedValue(JSON.stringify({ id: 1 })),
    });
    await comunidadService.valorar({ puntuacion: 5, comentario: 'Bien' }).catch(() => undefined);

    const rutas = mock.mock.calls.map(([url]) => String(url));
    expect(rutas.some((ruta) => ruta.endsWith('/valoraciones'))).toBe(true);
  });
});
