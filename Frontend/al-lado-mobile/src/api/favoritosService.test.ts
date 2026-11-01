import { comunidadService } from './services';

/**
 * HU-11: Empleador almacena favoritos de trabajadores.
 *
 * Contrato de `comunidadService.favoritosDe`, `comunidadService.guardarFavorito`,
 * `comunidadService.quitarFavorito`.
 */
describe('comunidadService.favoritos', () => {
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

  it('favoritosDe pide los favoritos de un empleador', async () => {
    await comunidadService.favoritosDe(7).catch(() => undefined);

    const rutas = mock.mock.calls.map(([url]) => String(url));
    expect(rutas.some((ruta) => ruta.endsWith('/favoritos/empleador/7'))).toBe(true);
  });

  it('guardarFavorito agrega un trabajador a favoritos', async () => {
    mock.mockResolvedValueOnce({
      ok: true,
      status: 201,
      text: jest.fn().mockResolvedValue('{}'),
    });
    await comunidadService.guardarFavorito(7, 3).catch(() => undefined);

    const rutas = mock.mock.calls.map(([url]) => String(url));
    expect(rutas.some((ruta) => ruta.endsWith('/favoritos/empleador/7/trabajador/3'))).toBe(true);
  });

  it('quitarFavorito elimina un favorito', async () => {
    mock.mockResolvedValueOnce({ ok: true, status: 204, text: jest.fn().mockResolvedValue('') });
    await comunidadService.quitarFavorito(7, 3).catch(() => undefined);

    const rutas = mock.mock.calls.map(([url]) => String(url));
    expect(rutas.some((ruta) => ruta.endsWith('/favoritos/empleador/7/trabajador/3'))).toBe(true);
  });
});
