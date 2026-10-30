import { describe, expect, it } from 'vitest';
import { decodeJwtPayload, userIdFromToken } from './jwt';

/**
 * AL-API-005: el identificador del usuario viaja en el token (claim `uid`).
 *
 * El cliente lee ese claim para rehidratar la sesión sin listar usuarios: si el
 * parseo se rompe, la sesión vuelve a depender de listar el directorio completo.
 */

function tokenCon(payload: Record<string, unknown>): string {
  const base64 = (valor: object) =>
    btoa(JSON.stringify(valor)).replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '');
  return `${base64({ alg: 'HS256' })}.${base64(payload)}.firma`;
}

describe('decodeJwtPayload', () => {
  it('lee el payload de un token válido', () => {
    const token = tokenCon({ uid: 7, sub: 'ana@mail.com' });

    expect(decodeJwtPayload(token)).toEqual({ uid: 7, sub: 'ana@mail.com' });
  });

  it('devuelve null si el token no tiene payload', () => {
    expect(decodeJwtPayload('no-es-un-jwt')).toBeNull();
  });

  it('devuelve null si el token es null o vacío', () => {
    expect(decodeJwtPayload(null)).toBeNull();
    expect(decodeJwtPayload('')).toBeNull();
  });

  it('devuelve null si el payload no es JSON válido', () => {
    expect(decodeJwtPayload('aaa.bm90LWpzb24.ccc')).toBeNull();
  });
});

describe('userIdFromToken', () => {
  it('devuelve el uid numérico del token', () => {
    expect(userIdFromToken(tokenCon({ uid: 42 }))).toBe(42);
  });

  it('acepta un uid numérico en texto', () => {
    expect(userIdFromToken(tokenCon({ uid: '42' }))).toBe(42);
  });

  it('devuelve null si el token no declara uid', () => {
    // Tokens emitidos antes del claim: la sesión se rehidrata con GET /auth/me.
    expect(userIdFromToken(tokenCon({ sub: 'ana@mail.com' }))).toBeNull();
  });

  it('devuelve null si el uid no es un número', () => {
    expect(userIdFromToken(tokenCon({ uid: 'no-es-un-id' }))).toBeNull();
  });

  it('devuelve null con un token inválido', () => {
    expect(userIdFromToken('basura')).toBeNull();
    expect(userIdFromToken(null)).toBeNull();
  });
});
