import { describe, expect, it, vi } from 'vitest';
import { applyServerErrors } from './applyServerErrors';

describe('applyServerErrors', () => {
  it('devuelve el fallback cuando el error no es de axios', () => {
    const setFieldError = vi.fn();
    const mensaje = applyServerErrors(new Error('falló'), setFieldError);
    expect(mensaje).toBe('falló');
    expect(setFieldError).not.toHaveBeenCalled();
  });

  it('aplica errores por campo del backend (mapa `errors`)', () => {
    const setFieldError = vi.fn();
    const error = {
      isAxiosError: true,
      response: {
        status: 400,
        data: {
          errors: {
            email: ['El email ya existe'],
            contrasena: ['Mínimo 8 caracteres', 'Debe incluir un número'],
          },
        },
      },
    };

    const mensaje = applyServerErrors(error, setFieldError, 'Fallo genérico');

    // Ya se mostraron campo por campo: no hay mensaje genérico
    expect(mensaje).toBeNull();
    expect(setFieldError).toHaveBeenCalledTimes(2);
    expect(setFieldError).toHaveBeenCalledWith('email', 'El email ya existe');
    expect(setFieldError).toHaveBeenCalledWith(
      'contrasena',
      'Mínimo 8 caracteres Debe incluir un número'
    );
  });

  it('usa el mensaje del cuerpo cuando no hay mapa de errores', () => {
    const setFieldError = vi.fn();
    const error = {
      isAxiosError: true,
      response: { status: 500, data: { message: 'Error interno del servidor' } },
    };

    const mensaje = applyServerErrors(error, setFieldError, 'Fallo genérico');

    expect(mensaje).toBe('Error interno del servidor');
    expect(setFieldError).not.toHaveBeenCalled();
  });
});
