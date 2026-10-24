import { authService } from './services';
import { ApiError, errMsg, param, setToken } from './client';

/**
 * Capa HTTP del cliente móvil: cabecera de autorización, errores y utilidades.
 *
 * La sesión se apoya en el token: si el cliente deja de mandarlo, cada pantalla
 * cae en 401 y el usuario ve la app sin sesión.
 */

describe('errMsg', () => {
  it('devuelve el mensaje de un ApiError', () => {
    expect(errMsg(new ApiError('No se pudo publicar', 400, null))).toBe('No se pudo publicar');
  });

  it('devuelve el mensaje de un Error común', () => {
    expect(errMsg(new Error('falló'))).toBe('falló');
  });

  it('convierte cualquier otro valor en texto', () => {
    expect(errMsg('texto plano')).toBe('texto plano');
    expect(errMsg(42)).toBe('42');
  });
});

describe('param', () => {
  it('normaliza un parámetro de expo-router', () => {
    expect(param('7')).toBe('7');
    expect(param(['7', '8'])).toBe('7');
    expect(param(undefined)).toBe('');
  });
});

describe('authService', () => {
  const fetchOriginal = global.fetch;

  afterEach(() => {
    global.fetch = fetchOriginal;
    jest.restoreAllMocks();
  });

  function responderCon(cuerpo: unknown, estado = 200) {
    const mock = jest.fn().mockResolvedValue({
      ok: estado >= 200 && estado < 300,
      status: estado,
      text: jest.fn().mockResolvedValue(cuerpo ? JSON.stringify(cuerpo) : ''),
    });
    global.fetch = mock as unknown as typeof fetch;
    return mock;
  }

  it('el login pide la identidad y devuelve la respuesta del backend', async () => {
    const mock = responderCon({ token: 'jwt', id: 7, email: 'ana@mail.com', roles: ['ROLE_TRABAJADOR'] });

    const respuesta = await authService.login('ana@mail.com', 'Clave123!');

    expect(respuesta.id).toBe(7);
    const [url, opciones] = mock.mock.calls[0];
    expect(url).toContain('/auth/login');
    expect(opciones.method).toBe('POST');
    expect(JSON.parse(opciones.body)).toEqual({ email: 'ana@mail.com', password: 'Clave123!' });
  });

  it('el login manda el token de la sesión en la cabecera', async () => {
    const mock = responderCon({ token: 'jwt', id: 7, email: 'ana@mail.com', roles: [] });
    await setToken('token-guardado');

    await authService.login('ana@mail.com', 'Clave123!');

    const [, opciones] = mock.mock.calls[0];
    expect(opciones.headers.Authorization).toBe('Bearer token-guardado');
  });

  it('me pide la identidad de la sesión', async () => {
    const mock = responderCon({ id: 7, email: 'ana@mail.com', roles: [] });

    const identidad = await authService.me();

    expect(mock.mock.calls[0][0]).toContain('/auth/me');
    expect(identidad.id).toBe(7);
  });

  it('el registro manda el rol y el teléfono', async () => {
    const mock = responderCon({ token: 'jwt', id: 3, email: 'nuevo@mail.com', roles: ['ROLE_TRABAJADOR'] });

    await authService.register({
      nombre: 'Nuevo',
      email: 'nuevo@mail.com',
      password: 'Clave123!',
      telefono: '+5491100000000',
      rol: 'TRABAJADOR',
    });

    const [, opciones] = mock.mock.calls[0];
    expect(JSON.parse(opciones.body)).toEqual({
      nombre: 'Nuevo',
      email: 'nuevo@mail.com',
      password: 'Clave123!',
      telefono: '+5491100000000',
      rol: 'TRABAJADOR',
    });
  });

  it('un error de la API se traduce en ApiError con su mensaje', async () => {
    responderCon({ message: 'Credenciales de acceso inválidas.' }, 401);

    await expect(authService.login('ana@mail.com', 'mala')).rejects.toBeInstanceOf(ApiError);
    await expect(authService.login('ana@mail.com', 'mala')).rejects.toMatchObject({
      status: 401,
      message: 'Credenciales de acceso inválidas.',
    });
  });
});
