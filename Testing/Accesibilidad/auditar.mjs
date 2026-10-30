import { chromium } from '@playwright/test';
import { spawnSync } from 'node:child_process';
import { createRequire } from 'node:module';
import fs from 'node:fs/promises';
import path from 'node:path';
import readline from 'node:readline/promises';
import { fileURLToPath } from 'node:url';

process.chdir(path.dirname(fileURLToPath(import.meta.url)));
const require = createRequire(import.meta.url);
const [command, role] = process.argv.slice(2);
const baseURL = process.env.BASE_URL || 'http://localhost:3000';
if (command === 'sesion') {
  if (!['trabajador', 'empleador', 'admin'].includes(role)) {
    throw new Error('Uso: node auditar.mjs sesion trabajador|empleador|admin');
  }
  await fs.mkdir('.auth', { recursive: true });
  const ignore = '.auth/.gitignore';
  await fs.writeFile(ignore, '*\n');
  const browser = await chromium.launch({ headless: false });
  try {
    const context = await browser.newContext();
    const page = await context.newPage();
    await page.goto(new URL(role === 'admin' ? '/acceso-admin' : '/login', baseURL).href);
    const rl = readline.createInterface({ input: process.stdin, output: process.stdout });
    try {
      await rl.question(`Iniciá sesión como ${role} en el navegador y esperá a ver tu panel. Después presioná Enter aquí. `);
    } finally { rl.close(); }
    const sessionRoles = await page.evaluate(() => JSON.parse(localStorage.getItem('user_roles') || '[]'));
    if (!sessionRoles.includes('ROLE_' + role.toUpperCase())) {
      throw new Error(`No hay sesión del rol ${role}; no se guardó el estado.`);
    }
    const state = await context.storageState();
    const url = new URL(baseURL);
    const relevant = state.origins.find(origin => origin.origin === url.origin);
    if (!relevant?.localStorage.some(item => item.name === 'auth_token' && item.value)) {
      throw new Error('No se encontró token de sesión; volver a ingresar.');
    }
    await fs.writeFile(path.join('.auth', role + '.json'), JSON.stringify(state), { mode: 0o600 });
    console.log(`Sesión guardada para ${role}. Renovarla si vence o cambia BASE_URL.`);
  } finally { await browser.close(); }
} else {
  const cli = require.resolve('@playwright/test/cli');
  const args = command === 'reporte'
    ? [cli, 'show-report', 'reporte-accesibilidad']
    : [cli, 'test', '--config=playwright.auditoria.config.ts', ...process.argv.slice(2)];
  const result = spawnSync(process.execPath, args, { stdio: 'inherit', env: process.env });
  if (result.error) throw result.error;
  process.exitCode = result.status ?? 1;
}
