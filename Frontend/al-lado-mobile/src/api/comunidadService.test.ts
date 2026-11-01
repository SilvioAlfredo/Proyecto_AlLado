import { comunidadService } from './services';

/**
 * HU-06, HU-11, HU-13, HU-14, HU-15: Valoraciones, favoritos, reportes,
 * perfil empresarial, publicidad y comunicados.
 *
 * Contrato de `comunidadService`: valoraciones, favoritos, reportes,
 * perfil empresarial, publicidad y comunicados.
 */
describe('comunidadService', () => {
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

  it('comunidadService.valoracionesDe pide valoraciones de un trabajador', async () => {
    await comunidadService.valoracionesDe(7).catch(() => undefined);

    const rutas = mock.mock.calls.map(([url]) => String(url));
    expect(rutas.some((ruta) => ruta.endsWith('/valoraciones/trabajador/7'))).toBe(true);
  });

  it('comunidadService.valorar envía una nueva valoración', async () => {
    mock.mockResolvedValueOnce({
      ok: true,
      status: 201,
      text: jest.fn().mockResolvedValue(JSON.stringify({ id: 1 })),
    });
    await comunidadService.valorar({ puntuacion: 5, comentario: 'Bien' }).catch(() => undefined);

    const rutas = mock.mock.calls.map(([url, init]) => [String(url), JSON.stringify(init?.body)]);
    expect(rutas.some(([ruta]) => ruta.endsWith('/valoraciones'))).toBe(true);
    expect(rutas.some(([, body]) => body.includes('5'))).toBe(true);
  });

  it('comunidadService.favoritosDe pide favoritos de un empleador', async () => {
    await comunidadService.favoritosDe(7).catch(() => undefined);

    const rutas = mock.mock.calls.map(([url]) => String(url));
    expect(rutas.some((ruta) => ruta.endsWith('/favoritos/empleador/7'))).toBe(true);
  });

  it('comunidadService.guardarFavorito agrega un trabajador a favoritos', async () => {
    mock.mockResolvedValueOnce({
      ok: true,
      status: 201,
      text: jest.fn().mockResolvedValue('{}'),
    });
    await comunidadService.guardarFavorito(7, 3).catch(() => undefined);

    const rutas = mock.mock.calls.map(([url]) => String(url));
    expect(rutas.some((ruta) => ruta.endsWith('/favoritos/empleador/7/trabajador/3'))).toBe(true);
  });

  it('comunidadService.quitarFavorito elimina un favorito', async () => {
    mock.mockResolvedValueOnce({ ok: true, status: 204, text: jest.fn().mockResolvedValue('') });
    await comunidadService.quitarFavorito(7, 3).catch(() => undefined);

    const rutas = mock.mock.calls.map(([url]) => String(url));
    expect(rutas.some((ruta) => ruta.endsWith('/favoritos/empleador/7/trabajador/3'))).toBe(true);
  });

  it('comunidadService.empresa.perfil pide el perfil empresarial de un empleador', async () => {
    await comunidadService.empresa.perfil(7).catch(() => undefined);

    const rutas = mock.mock.calls.map(([url]) => String(url));
    expect(rutas.some((ruta) => ruta.endsWith('/perfiles-empresariales/empleador/7'))).toBe(true);
  });

  it('comunidadService.publicidad.vigentes pide anuncios vigentes', async () => {
    await comunidadService.publicidad.vigentes().catch(() => undefined);

    const rutas = mock.mock.calls.map(([url]) => String(url));
    expect(rutas.some((ruta) => ruta.endsWith('/publicidades/vigentes'))).toBe(true);
  });

  it('comunidadService.comunicados pide comunicados institucionales', async () => {
    await comunidadService.comunicados().catch(() => undefined);

    const rutas = mock.mock.calls.map(([url]) => String(url));
    expect(rutas.some((ruta) => ruta.endsWith('/comunicados'))).toBe(true);
  });

  it('comunidadService.reportar envía un reporte de perfil abusivo', async () => {
    mock.mockResolvedValueOnce({
      ok: true,
      status: 201,
      text: jest.fn().mockResolvedValue(JSON.stringify({ id: 1 })),
    });
    await comunidadService.reportar({ motivo: 'Acoso', descripcion: 'Comportamiento inadecuado', reportante: { id: 7 } }).catch(() => undefined);

    const rutas = mock.mock.calls.map(([url, init]) => [String(url), JSON.stringify(init?.body)]);
    expect(rutas.some(([ruta]) => ruta.endsWith('/reportes'))).toBe(true);
    expect(rutas.some(([, body]) => body.includes('Acoso'))).toBe(true);
  });
});
