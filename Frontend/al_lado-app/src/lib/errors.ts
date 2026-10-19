import { isAxiosError } from 'axios';
export function errorMessage(error: unknown, fallback = 'No se pudo completar la operación.'): string {
  if (isAxiosError(error)) {
    const data = error.response?.data;
    if (data?.errors) return Object.values(data.errors).join(' ');
    return data?.message || fallback;
  }
  return error instanceof Error ? error.message : fallback;
}
