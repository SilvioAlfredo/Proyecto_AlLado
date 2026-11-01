import { beforeEach, describe, expect, it, vi } from 'vitest';
import api from '@/lib/api';
import { authService } from './authService';
import { comoApiMock } from '@/test/mocks';

/**
 * HU-04: Trabajador se registra y valida correo electrónico.
 *
 * Contrato del servicio de autenticación: login, register, forgotPassword,
 * resetPassword, logout, verifyEmail, changePassword.
 */
vi.mock('@/lib/api', () => ({
  default: { post: vi.fn(), get: vi.fn() },
}));

const getApi = comoApiMock(api);

describe('authService', () => {
  beforeEach(() => {
    getApi.post.mockReset();
    getApi.get.mockReset();
  });

  it('login envía credenciales y devuelve token y roles', async () => {
    const respuesta = {
      token: 'jwt-de-prueba',
      id: 7,
      email: 'ana@mail.com',
      nombre: 'Ana Gomez',
      roles: ['ROLE_TRABAJADOR'],
    };
    getApi.post.mockResolvedValue({ data: respuesta });

    const resultado = await authService.login({ email: 'ana@mail.com', contrasena: 'Clave123!' });

    expect(getApi.post).toHaveBeenCalledWith('/auth/login', {
      email: 'ana@mail.com',
      password: 'Clave123!',
    });
    expect(resultado.token).toBe('jwt-de-prueba');
    expect(resultado.roles).toContain('ROLE_TRABAJADOR');
  });

  it('register envía nombre, email, password, telefono y rol', async () => {
    const respuesta = {
      token: 'jwt-de-prueba',
      id: 7,
      email: 'ana@mail.com',
      roles: ['ROLE_TRABAJADOR'],
    };
    getApi.post.mockResolvedValue({ data: respuesta });

    const resultado = await authService.register({
      nombre: 'Ana',
      apellido: 'Gomez',
      email: 'ana@mail.com',
      contrasena: 'Clave123!',
      telefono: '+5491100000000',
      rol: 'TRABAJADOR',
    });

    expect(getApi.post).toHaveBeenCalledWith('/auth/register', {
      nombre: 'Ana Gomez',
      email: 'ana@mail.com',
      password: 'Clave123!',
      telefono: '+5491100000000',
      rol: 'TRABAJADOR',
    });
    expect(resultado.id).toBe(7);
  });

  it('forgotPassword pide token de recuperación para un email', async () => {
    getApi.post.mockResolvedValue(undefined);

    await authService.forgotPassword('ana@mail.com');

    expect(getApi.post).toHaveBeenCalledWith('/auth/password/forgot', { email: 'ana@mail.com' });
  });

  it('resetPassword restablece contraseña con token', async () => {
    getApi.post.mockResolvedValue(undefined);

    await authService.resetPassword('token-de-recuperacion', 'NuevaClave123!');

    expect(getApi.post).toHaveBeenCalledWith('/auth/password/reset', {
      token: 'token-de-recuperacion',
      newPassword: 'NuevaClave123!',
    });
  });

  it('logout llama al endpoint de revocación', async () => {
    getApi.post.mockResolvedValue(undefined);

    await authService.logout();

    expect(getApi.post).toHaveBeenCalledWith('/auth/logout');
  });

  it('verifyEmail valida el token de verificación de correo', async () => {
    getApi.get.mockResolvedValue(undefined);

    await authService.verifyEmail('token-de-verificacion');

    expect(getApi.get).toHaveBeenCalledWith('/auth/verify-email?token=token-de-verificacion');
  });

  it('changePassword verifica y cambia la contraseña', async () => {
    getApi.post.mockResolvedValue(undefined);

    await authService.changePassword('ana@mail.com', 'ClaveActual123!', 'NuevaClave123!');

    expect(getApi.post).toHaveBeenCalledWith('/auth/password', {
      email: 'ana@mail.com',
      currentPassword: 'ClaveActual123!',
      newPassword: 'NuevaClave123!',
    });
  });
});
