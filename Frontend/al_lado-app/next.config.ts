import type { NextConfig } from 'next';

/**
 * Cabeceras de seguridad del frontend web (AL-SEC-011).
 *
 * Se aplican a todas las rutas desde `next.config.ts`, así que valen tanto para
 * las páginas como para los estáticos de `/public`.
 *
 * - `Content-Security-Policy`: limita de dónde se puede cargar cada recurso.
 * - `Strict-Transport-Security`: solo en producción. En desarrollo obligaría al
 *   navegador a pedir HTTPS contra el dev server, que escucha en HTTP.
 * - `frame-ancestors` y `X-Frame-Options` cubren clickjacking; el primero lo
 *   respetan los navegadores modernos, el segundo los antiguos.
 */

const isDev = process.env.NODE_ENV !== 'production';

/**
 * Origen de la API para `connect-src`.
 *
 * axios usa `NEXT_PUBLIC_API_URL` (ver `src/lib/api.ts`), así que el navegador
 * necesita ese origen permitido. Si la variable falta o no es una URL válida se
 * cae al valor por defecto de la app.
 */
function apiOrigin(): string {
  const value = process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8080';
  try {
    return new URL(value).origin;
  } catch {
    return 'http://localhost:8080';
  }
}

/**
 * La CSP se arma una sola vez al arrancar (build en producción, start del dev
 * server en desarrollo).
 *
 * `script-src` necesita `'unsafe-inline'`: Next inyecta el bootstrap y el payload
 * RSC como scripts inline, y sin un nonce emitido por middleware no hay forma de
 * autorizarlos. Migrar a nonce + `strict-dynamic` queda como mejora aparte porque
 * obliga a renderizar todas las rutas de forma dinámica.
 */
const contentSecurityPolicy = [
  "default-src 'self'",
  `script-src 'self' 'unsafe-inline'${isDev ? " 'unsafe-eval'" : ''}`,
  // Tailwind compila a CSS propio, pero los formularios usan style={{...}} y
  // React inyecta estilos inline: sin esto se rompe la hidratación visual.
  "style-src 'self' 'unsafe-inline'",
  // Las publicidades admiten imágenes de dominios externos (urlImagen).
  "img-src 'self' data: blob: https:",
  "font-src 'self'",
  `connect-src 'self' ${apiOrigin()}${isDev ? ' ws://localhost:* ws://127.0.0.1:*' : ''}`,
  "object-src 'none'",
  "base-uri 'self'",
  "form-action 'self'",
  "frame-ancestors 'none'",
  "manifest-src 'self'",
  "worker-src 'self' blob:",
].join('; ');

const securityHeaders = [
  { key: 'Content-Security-Policy', value: contentSecurityPolicy },
  { key: 'X-Frame-Options', value: 'DENY' },
  { key: 'X-Content-Type-Options', value: 'nosniff' },
  { key: 'Referrer-Policy', value: 'strict-origin-when-cross-origin' },
  { key: 'Cross-Origin-Opener-Policy', value: 'same-origin' },
  { key: 'Permissions-Policy', value: 'camera=(), microphone=(), geolocation=()' },
  ...(isDev
    ? []
    : [{ key: 'Strict-Transport-Security', value: 'max-age=63072000; includeSubDomains' }]),
];

const nextConfig: NextConfig = {
  async headers() {
    return [
      {
        source: '/:path*',
        headers: securityHeaders,
      },
    ];
  },
};

export default nextConfig;
