import type { SuscripcionDTO } from '@/features/usuario/services/aportesService';

/**
 * Determina la vigencia de una suscripción sin modificarla.
 *
 * La vigencia llega del backend para compartir la política de calendario.
 * El cálculo local solo se usa mientras se despliega una versión compatible del backend.
 *
 * @param sub - Suscripción que se quiere evaluar.
 * @param today - Fecha YYYY-MM-DD. Si se omite, utiliza la fecha local actual.
 * @returns Estado de activación y vigencia de la suscripción.
 */
export function subscriptionState(
  sub: SuscripcionDTO,
  today?: string,
): 'Activa y vigente' | 'Activa fuera de vigencia' | 'Inactiva' {
  if (!sub.activa) {
    return 'Inactiva';
  }
  const now = new Date();
  const date =
    today ??
    [
      now.getFullYear(),
      String(now.getMonth() + 1).padStart(2, '0'),
      String(now.getDate()).padStart(2, '0'),
    ].join('-');

  const vigente =
    sub.vigente ??
    Boolean(
      sub.fechaInicio &&
        sub.fechaInicio <= date &&
        (!sub.fechaFin || sub.fechaFin >= date),
    );

  return vigente ? 'Activa y vigente' : 'Activa fuera de vigencia';
}

/**
 * Presenta una fecha de calendario YYYY-MM-DD como DD/MM/YYYY.
 *
 * Separa sus partes sin convertirla a Date, para evitar cambios por zona horaria.
 * No valida fechas ni admite marcas de tiempo completas.
 *
 * @param value - Fecha de calendario, o ausencia de fecha.
 * @returns Fecha formateada o «Sin fecha registrada» cuando falta el valor.
 */
export function calendarDate(value?: string): string {
  if (!value) {
    return 'Sin fecha registrada';
  }
  const [year, month, day] = value.split('-');

  return `${day}/${month}/${year}`;
}

/**
 * Formatea un importe con las convenciones de Argentina.
 *
 * @param amount - Importe que se mostrará; no se usa para validar una transferencia.
 * @param currency - Código de moneda. Por defecto, ARS.
 * @returns Importe formateado; si la moneda no es válida, muestra importe y código.
 */
export function money(amount: number, currency = 'ARS'): string {
  try {
    return new Intl.NumberFormat('es-AR', { style: 'currency', currency }).format(amount);
  } catch {
    return `${amount.toLocaleString('es-AR')} ${currency}`;
  }
}

/**
 * Valida y normaliza el monto observado en una transferencia bancaria.
 *
 * Acepta coma o punto decimal, hasta ocho dígitos enteros y dos decimales.
 * Rechaza cero, negativos, separadores de miles y notación exponencial.
 *
 * @param value - Monto ingresado por el administrador.
 * @returns Cadena decimal con punto; conserva la precisión al enviarla al backend.
 * @throws Si el monto no cumple las reglas de validación.
 * @example
 * bankAmount('15000,50'); // '15000.50'
 */
export function bankAmount(value: string): string {
  const normalized = value.trim().replace(',', '.');

  if (!/^\d{1,8}(\.\d{1,2})?$/.test(normalized) || Number(normalized) <= 0) {
    throw new Error(
      'Ingresá un monto positivo, sin separadores de miles y con hasta dos decimales.',
    );
  }
  return normalized;
}
