/**
 * Configuración de la suite tomada del entorno.
 * Los valores por defecto coinciden con el entorno de QA documentado en el README.
 */
export const env = {
  baseUrl: process.env.E2E_BASE_URL ?? 'http://localhost:3001',
  apiUrl: process.env.E2E_API_URL ?? 'http://localhost:8081',
  adminEmail: process.env.E2E_ADMIN_EMAIL ?? 'admin@allado.com',
  adminPassword: process.env.E2E_ADMIN_PASSWORD ?? 'Clave123!',
  demoEmployerEmail: process.env.E2E_EMPLOYER_EMAIL ?? 'empleador@demo.com',
  demoWorkerEmail: process.env.E2E_WORKER_EMAIL ?? 'trabajador@demo.com',
  defaultPassword: process.env.E2E_DEFAULT_PASSWORD ?? 'Clave123!',
} as const;
