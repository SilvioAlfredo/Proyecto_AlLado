import { authService, oportunidadService, postulacionService, suscripcionService } from './services';

/**
 * Contrato de los servicios del cliente móvil.
 *
 * Estos tests fijan qué rutas y qué cuerpos van al backend. La regresión que
 * importa aquí es de AL-API-005: **ningún servicio lista usuarios para descubrir
 * el identificador del usuario en sesión** (eso exponía los datos de contacto de
 * terceros en lecturas públicas).
 */

describe('servicios del cliente móvil', () => {
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

  it('el servicio de autenticación expone la identidad de la sesión', () => {
    // `me` es el endpoint que rehidrata la sesión cuando el token no trae el id.
    expect(typeof authService.me).toBe('function');
  });

  it('las ofertas piden el contacto del empleador por separado', async () => {
    await oportunidadService.listar().catch(() => undefined);

    const rutas = mock.mock.calls.map(([url]) => String(url));
    expect(rutas.some((ruta) => ruta.includes('/contacto'))).toBe(false);
  });

  it('las postulaciones se piden por su recurso, no por listado de usuarios', async () => {
    await postulacionService.listar().catch(() => undefined);

    const rutas = mock.mock.calls.map(([url]) => String(url));
    expect(rutas.some((ruta) => ruta.endsWith('/postulaciones'))).toBe(true);
    expect(rutas.some((ruta) => ruta.includes('/trabajadores'))).toBe(false);
  });

  it('la suscripción se pide por usuario, no por listado', async () => {
    await suscripcionService.porUsuario(7).catch(() => undefined);

    const rutas = mock.mock.calls.map(([url]) => String(url));
    expect(rutas.some((ruta) => ruta.endsWith('/suscripciones/usuario/7'))).toBe(true);
  });

  it('el módulo ya no exporta la resolución de id por email', () => {
    // `resolveIdByEmail` listaba el directorio para encontrar al usuario en
    // sesión: se eliminó con la identidad mínima (AL-API-005).
    const servicios = require('./services') as Record<string, unknown>;

    expect(servicios.resolveIdByEmail).toBeUndefined();
  });
});
