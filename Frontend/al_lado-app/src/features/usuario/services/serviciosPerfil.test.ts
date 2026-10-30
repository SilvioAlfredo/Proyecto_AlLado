import { beforeEach, describe, expect, it, vi } from 'vitest';
import api from '@/lib/api';
import { trabajadorService } from './trabajadorService';
import { empleadorService } from './empleadorService';
import { comoApiMock } from '@/test/mocks';

/**
 * AL-API-005: el perfil propio se pide con el identificador de la sesión.
 *
 * Antes estos servicios resolvían el id listando todos los trabajadores (o
 * empleadores) y comparando emails. Eso exponía los datos de contacto de terceros
 * en una lectura pública y descargaba el directorio completo en cada pantalla.
 */

vi.mock('@/lib/api', () => ({
  default: { get: vi.fn(), post: vi.fn(), put: vi.fn(), patch: vi.fn(), delete: vi.fn() },
}));

const getApi = comoApiMock(api);

function tokenConUid(uid: number): string {
  const base64 = (valor: object) =>
    btoa(JSON.stringify(valor)).replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '');
  return `${base64({ alg: 'HS256' })}.${base64({ uid })}.firma`;
}

describe('trabajadorService', () => {
  beforeEach(() => {
    getApi.get.mockReset();
    getApi.put.mockReset();
  });

  it('obtenerPerfil pide el perfil con el id del token', async () => {
    localStorage.setItem('auth_token', tokenConUid(7));
    getApi.get.mockResolvedValue({ data: { id: 7, nombre: 'Ana' } });

    const perfil = await trabajadorService.obtenerPerfil();

    expect(getApi.get).toHaveBeenCalledWith('/trabajadores/7');
    expect(perfil.id).toBe(7);
  });

  it('obtenerPerfil no lista el directorio de trabajadores', async () => {
    localStorage.setItem('auth_token', tokenConUid(7));
    getApi.get.mockResolvedValue({ data: { id: 7, nombre: 'Ana' } });

    await trabajadorService.obtenerPerfil();

    expect(getApi.get).not.toHaveBeenCalledWith('/trabajadores');
  });

  it('obtenerPerfil falla claro si la sesión no tiene token', async () => {
    await expect(trabajadorService.obtenerPerfil()).rejects.toThrow(/identificar/i);
    expect(getApi.get).not.toHaveBeenCalled();
  });

  it('actualizarPerfil envía el DTO completo con el id de la ruta', async () => {
    getApi.get.mockResolvedValue({ data: { id: 7, nombre: 'Ana', email: 'ana@mail.com' } });
    getApi.put.mockResolvedValue({ data: { id: 7, nombre: 'Ana Gomez' } });

    await trabajadorService.actualizarPerfil(7, { nombre: 'Ana Gomez' });

    expect(getApi.put).toHaveBeenCalledWith('/trabajadores/7', {
      id: 7,
      nombre: 'Ana Gomez',
      email: 'ana@mail.com',
    });
  });
});

describe('empleadorService', () => {
  beforeEach(() => {
    getApi.get.mockReset();
    getApi.put.mockReset();
  });

  it('obtenerPerfil pide el perfil con el id del token', async () => {
    localStorage.setItem('auth_token', tokenConUid(9));
    getApi.get.mockResolvedValue({ data: { id: 9, nombre: 'Empresa ABC' } });

    const perfil = await empleadorService.obtenerPerfil();

    expect(getApi.get).toHaveBeenCalledWith('/empleadores/9');
    expect(perfil.id).toBe(9);
  });

  it('obtenerPerfil no lista el directorio de empleadores', async () => {
    localStorage.setItem('auth_token', tokenConUid(9));
    getApi.get.mockResolvedValue({ data: { id: 9, nombre: 'Empresa ABC' } });

    await empleadorService.obtenerPerfil();

    expect(getApi.get).not.toHaveBeenCalledWith('/empleadores');
  });

  it('obtenerPerfil falla claro si la sesión no tiene token', async () => {
    await expect(empleadorService.obtenerPerfil()).rejects.toThrow(/identificar/i);
    expect(getApi.get).not.toHaveBeenCalled();
  });
});
