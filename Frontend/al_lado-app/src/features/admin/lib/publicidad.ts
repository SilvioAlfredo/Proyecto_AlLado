import type { PublicidadDTO } from '@/types/comunidad';

/**
 * Admite únicamente URLs absolutas con protocolo HTTP o HTTPS.
 *
 * Filtra protocolos ejecutables como javascript:; no verifica el contenido
 * ni la disponibilidad del sitio de destino.
 *
 * @param value - URL configurada en el anuncio o perfil empresarial.
 * @returns URL normalizada, o undefined si el valor es inválido o no está permitido.
 */
export function safeWebUrl(value?: string) {
  try {
    const url = new URL(value || '');
    return ['http:', 'https:'].includes(url.protocol) ? url.href : undefined;
  } catch {
    return undefined;
  }
}

/**
 * Describe el estado de un anuncio a partir de su activación y fechas.
 *
 * Una publicidad desactivada se muestra como inactiva. Para las activas,
 * las fechas de inicio y fin son inclusivas; una fecha ausente no aplica ese límite.
 *
 * @param item - Publicidad que se quiere evaluar.
 * @param today - Fecha YYYY-MM-DD. Por defecto, la fecha local actual.
 * @returns Estado para presentar en el listado de publicidad.
 */
export function publicidadState(
  item: PublicidadDTO,
  today = new Date().toLocaleDateString('sv-SE'),
) {
  if (!item.activa) {
    return 'Inactiva';
  }
  if (item.fechaFin && item.fechaFin < today) {
    return 'Vencida';
  }
  if (item.fechaInicio && item.fechaInicio > today) {
    return 'Programada';
  }
  return 'Vigente';
}
