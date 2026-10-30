import { describe, expect, it } from 'vitest';
import { emailSesion, obtenerRefSesion } from './usuarioRef';

/**
 * AL-API-005: la referencia del usuario en sesión sale de lo guardado en el
 * navegador, no de listar usuarios y comparando emails.
 */

describe('obtenerRefSesion', () => {
  it('devuelve id, nombre y email de la sesión guardada', () => {
    localStorage.setItem('user_id', '7');
    localStorage.setItem('user_email', 'ana@mail.com');
    localStorage.setItem('user_nombre', 'Ana Gomez');

    expect(obtenerRefSesion()).toEqual({ id: 7, nombre: 'Ana Gomez', email: 'ana@mail.com' });
  });

  it('devuelve id 0 cuando la sesión no tiene user_id', () => {
    localStorage.setItem('user_email', 'ana@mail.com');

    expect(obtenerRefSesion()?.id).toBe(0);
  });

  it('devuelve null si no hay sesión', () => {
    expect(obtenerRefSesion()).toBeNull();
  });

  it('devuelve null si la sesión no tiene email', () => {
    localStorage.setItem('user_id', '7');

    expect(obtenerRefSesion()).toBeNull();
  });
});

describe('emailSesion', () => {
  it('devuelve el email de la sesión', () => {
    localStorage.setItem('user_email', 'ana@mail.com');

    expect(emailSesion()).toBe('ana@mail.com');
  });

  it('devuelve null si no hay sesión', () => {
    expect(emailSesion()).toBeNull();
  });
});
