import type { Role } from './api';

/** Sufijo único para no chocar con datos de corridas anteriores. */
export function uniqueSuffix(): string {
  return `${Date.now()}${Math.floor(Math.random() * 1000)}`;
}

export interface TestUserInput {
  nombre: string;
  apellido: string;
  email: string;
  telefono: string;
  password: string;
  rol: Role;
}

/** Datos de una cuenta nueva para el flujo de registro por UI. */
export function buildWorkerRegistration(): TestUserInput {
  const suffix = uniqueSuffix();
  return {
    nombre: 'E2E',
    apellido: `Trabajador ${suffix}`,
    email: `e2e.registro.${suffix}@allado.test`,
    telefono: '+5491100000000',
    password: 'Clave123!',
    rol: 'TRABAJADOR',
  };
}

/** Título único de publicación para poder localizarla en el listado. */
export function uniqueTitle(base: string): string {
  return `${base} ${uniqueSuffix()}`;
}
