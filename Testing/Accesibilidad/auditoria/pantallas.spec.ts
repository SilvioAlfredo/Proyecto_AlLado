
import { test, expect } from '@playwright/test';
import AxeBuilder from '@axe-core/playwright';
import fs from 'node:fs';
import path from 'node:path';

const root = process.cwd();
const app = path.resolve(root, '../../Frontend/al_lado-app/src/app');
const roles = ['trabajador', 'empleador', 'admin'];

function files(dir: string): string[] {
  return fs.readdirSync(dir, { withFileTypes: true }).flatMap(entry => {
    const p = path.join(dir, entry.name);

    return entry.isDirectory()
      ? files(p)
      : entry.name === 'page.tsx'
        ? [p]
        : [];
  });
}

if (!fs.existsSync(app)) {
  throw new Error(
    'Ejecutar desde Testing/Accesibilidad; no se encontró src/app del frontend.'
  );
}

const catalog = files(app)
  .map(file => {
    const route =
      '/' +
      path
        .relative(app, path.dirname(file))
        .split(path.sep)
        .filter(part => part && !part.startsWith('('))
        .join('/');

    const role =
      roles.find(r => route.startsWith('/' + r + '/')) ||
      (route === '/publicar' ? 'empleador' : 'publico');

    const excluded = route.includes('[')
      ? 'Requiere ID real y datos de prueba.'
      : ['/reset-password', '/verify-email'].includes(route)
        ? 'Requiere token válido y estado específico.'
        : '';

    return {
      route,
      role,
      excluded,
      file: path.relative(path.resolve(root, '../..'), file),
    };
  })
  .sort((a, b) => a.route.localeCompare(b.route));

for (const item of catalog) {
  const state = path.join(root, '.auth', item.role + '.json');

  const authenticated =
    item.role !== 'publico' && fs.existsSync(state);

  test.describe(item.route, () => {
    test.use({
      storageState: authenticated ? state : undefined,
    });

    test('axe WCAG 2.2 AA', async ({ page }, testInfo) => {
      await testInfo.attach('alcance.json', {
        body: JSON.stringify({
          ...item,
          project: testInfo.project.name,
          outputDir: testInfo.outputDir,
        }),
        contentType: 'application/json',
      });

      test.skip(Boolean(item.excluded), item.excluded);

      test.skip(
        item.role !== 'publico' && !authenticated,
        `Falta sesión ${item.role}: node auditar.mjs sesion ${item.role}`
      );

      const errors: string[] = [];
      const respuestasEsperadas: string[] = [];

      // Registrar respuestas HTTP de la aplicación.
      page.on('response', response => {
        const url = new URL(response.url());

        if (
          !['fetch', 'xhr'].includes(
            response.request().resourceType()
          ) ||
          !['localhost', '127.0.0.1'].includes(url.hostname) ||
          response.status() < 400
        ) {
          return;
        }

        const diagnostico =
          `${response.status()} ${url.pathname}`;

        const preferenciasAusentes =
          item.route === '/trabajador/preferencias' &&
          response.request().method() === 'GET' &&
          response.status() === 404 &&
          /^\/preferencias\/trabajador\/\d+$/.test(url.pathname);

        const propuestasSinPlan =
          item.route === '/trabajador/propuestas' &&
          response.request().method() === 'GET' &&
          response.status() === 400 &&
          url.pathname === '/oportunidades/propuestas-pro';

        const suscripcionAusente =
          item.route === '/trabajador/suscripcion' &&
          response.request().method() === 'GET' &&
          response.status() === 404 &&
          /^\/suscripciones\/usuario\/\d+$/.test(url.pathname);

        if (
          preferenciasAusentes ||
          propuestasSinPlan ||
          suscripcionAusente
        ) {
          respuestasEsperadas.push(diagnostico);
        } else {
          errors.push(diagnostico);
        }
      });

      // Navegar a la pantalla.
      const response = await page.goto(item.route, {
        waitUntil: 'load',
      });

      expect(
        response?.ok(),
        'La página debe cargar sin error HTTP'
      ).toBeTruthy();

      await expect
        .poll(
          () =>
            new URL(page.url()).pathname.replace(/\/$/, '') || '/',
          {
            message:
              'La ruta no debe redirigir al login ni a otra pantalla',
          }
        )
        .toBe(item.route.replace(/\/$/, '') || '/');

      await expect(page.getByRole('main')).toBeVisible();

      // Esperar a que desaparezca el estado de carga.
      await expect
        .poll(
          async () =>
            page
              .getByText(/^Cargando(?:\s.*|\.{3}|…)?$/i)
              .count(),
          {
            message:
              'Esperar a que desaparezca el estado de carga',
          }
        )
        .toBe(0);

      await page.evaluate(() =>
        document.fonts.ready.then(() => undefined)
      );

      // Verificar el rol de la sesión.
      if (item.role !== 'publico') {
        const actualRoles = await page.evaluate(() =>
          JSON.parse(
            localStorage.getItem('user_roles') || '[]'
          )
        );

        expect(actualRoles).toContain(
          'ROLE_' + item.role.toUpperCase()
        );
      }

      // Auditoría automática de accesibilidad.
      const results = await new AxeBuilder({ page })
        .withTags([
          'wcag2a',
          'wcag2aa',
          'wcag21a',
          'wcag21aa',
          'wcag22aa',
        ])
        .analyze();

      // Guardar evidencias.
      await testInfo.attach('resultado-axe.json', {
        body: JSON.stringify(results, null, 2),
        contentType: 'application/json',
      });

      await testInfo.attach('pantalla-auditada.png', {
        body: await page.screenshot({ fullPage: true }),
        contentType: 'image/png',
      });

      await testInfo.attach('diagnostico-http.json', {
        body: JSON.stringify(errors, null, 2),
        contentType: 'application/json',
      });

      await testInfo.attach('respuestas-http-esperadas.json', {
        body: JSON.stringify(respuestasEsperadas, null, 2),
        contentType: 'application/json',
      });

      // Guardar JSON para agregación de resultados.
      await fs.promises.writeFile(
        testInfo.outputPath('resultado-axe.json'),
        JSON.stringify(results, null, 2)
      );

      // Comprobar que la ruta no haya cambiado.
      await expect.soft(
        page,
        'No debe cambiar de ruta durante la auditoría'
      ).toHaveURL(
        new RegExp(
          item.route === '/'
            ? '/$'
            : item.route.replace(
                /[.*+?^${}()|[\]\\]/g,
                '\\$&'
              ) + '/?$'
        )
      );

      // Preferencias: registro opcional inexistente.
      if (
        item.route === '/trabajador/preferencias' &&
        respuestasEsperadas.some(r =>
          /^404 \/preferencias\/trabajador\/\d+$/.test(r)
        )
      ) {
        const mensaje = page.getByText(
          'No tenés preferencias guardadas. Elegí las opciones y guardalas.'
        );

        await expect(mensaje).toBeVisible();
      }

      // Propuestas PRO: usuario sin suscripción vigente.
      if (
        item.route === '/trabajador/propuestas' &&
        respuestasEsperadas.includes(
          '400 /oportunidades/propuestas-pro'
        )
      ) {
        const mensajePro = page.getByText(
          'Necesitás una suscripción PRO vigente para acceder a propuestas compatibles con tus especialidades.'
        );
        await expect(mensajePro).toBeVisible();
      }

      // Suscripción: registro opcional inexistente.
      if (
        item.route === '/trabajador/suscripcion' &&
        respuestasEsperadas.some(r =>
          /^404 \/suscripciones\/usuario\/\d+$/.test(r)
        )
      ) {
        await expect(
          page.getByRole('heading', {
            name: 'Sin suscripción registrada',
          })
        ).toBeVisible();
      }

      // Validaciones finales.
      expect.soft(
        errors,
        'Errores de API: revisar antes de acreditar la pantalla completa'
      ).toEqual([]);

      expect(
        results.violations,
        'Infracciones automáticas de accesibilidad'
      ).toEqual([]);

      // Los resultados "incomplete" quedan para revisión manual.
    });
  });
}
