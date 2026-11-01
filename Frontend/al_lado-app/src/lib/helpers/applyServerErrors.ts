import { isAxiosError } from 'axios';

/**
 * Aplica los errores de validación del servidor (mapa `errors` por campo
 * devuelto por el backend) a los campos de un formulario de react-hook-form.
 *
 * El backend responde, en 400, un objeto como:
 *   { errors: { email: ["El email ya existe"], contrasena: ["Mínimo 8 caracteres"] } }
 *
 * @param error - Error de axios (o cualquier error)
 * @param setFieldError - Callback (campo, mensaje) para registrar el error en el campo
 * @param fallback - Mensaje genérico si no hay errores por campo
 * @returns El mensaje genérico si no se pudieron aplicar errores por campo, o null si ya se mostraron campo por campo
 */
export function applyServerErrors(
  error: unknown,
  setFieldError: (campo: string, mensaje: string) => void,
  fallback = 'No se pudo completar la operación.',
): string | null {
  if (isAxiosError(error)) {
    const data = error.response?.data;
    if (data?.errors && typeof data.errors === 'object') {
      for (const [campo, mensajes] of Object.entries(data.errors)) {
        const msgs = Array.isArray(mensajes) ? mensajes : [mensajes];
        setFieldError(campo, msgs.join(' '));
      }
      return null; // Los errores ya se mostraron campo por campo
    }
    const mensaje = data?.message || fallback;
    return mensaje;
  }
  const msg = error instanceof Error ? error.message : fallback;
  return msg;
}
