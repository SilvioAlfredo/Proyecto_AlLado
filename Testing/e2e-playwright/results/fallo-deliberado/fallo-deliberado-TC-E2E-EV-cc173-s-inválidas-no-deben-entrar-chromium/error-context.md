# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: fallo-deliberado.spec.ts >> TC-E2E-EVIDENCIA-01 (fallo deliberado): credenciales inválidas no deben entrar
- Location: evidencia\fallo-deliberado.spec.ts:13:5

# Error details

```
Error: expect(page).toHaveURL(expected) failed

Expected pattern: /\/dashboard$/
Received string:  "http://localhost:3001/login"
Timeout: 5000ms

Call log:
  - Expect "toHaveURL" with timeout 5000ms
    14 × locator resolved to <html lang="es">…</html>
       - unexpected value "http://localhost:3001/login"

```

```yaml
- banner:
  - link "◉ AlLado SERVICIOS LOCALES":
    - /url: /
  - navigation "Navegación principal":
    - link "Oportunidades":
      - /url: /#oportunidades
    - link "Cómo funciona":
      - /url: /#como-funciona
    - link "Ingresar":
      - /url: /login
      - button "Ingresar"
    - link "Sumate":
      - /url: /register
      - button "Sumate"
- main:
  - paragraph: Qué bueno tenerte de vuelta
  - heading "Tu oficio. Tu gente. Tu lugar." [level=2]
  - paragraph: Volvé a conectar con las oportunidades y las personas que hacen crecer tu trabajo.
  - link "Volver al inicio":
    - /url: /
  - paragraph: Tu espacio en AlLado
  - heading "Iniciá sesión." [level=1]
  - paragraph: Ingresá a tu cuenta y retomá lo que empezaste.
  - alert: Credenciales de acceso inválidas.
  - text: Correo electrónico
  - textbox "Correo electrónico": cuenta.invalida@allado.test
  - text: Contraseña
  - textbox "Contraseña": ClaveIncorrecta!
  - button "Mostrar contraseña"
  - link "¿Olvidaste tu contraseña?":
    - /url: /forgot-password
  - button "Ingresar"
  - paragraph:
    - text: ¿Todavía no tenés cuenta?
    - link "Sumate a AlLado":
      - /url: /register
  - link "Acceso de administración":
    - /url: /acceso-admin
- alert
```

# Test source

```ts
  1  | import { expect, test } from '@playwright/test';
  2  | 
  3  | /**
  4  |  * FALLO DELIBERADO — evidencia exigida por AUT-E2E-07.
  5  |  *
  6  |  * Este caso NO forma parte de la corrida normal (la config principal ignora
  7  |  * `evidencia/`). Se ejecuta aparte con `npm run test:fallo` para generar
  8  |  * screenshot, video y trace de un fallo real y reproducible:
  9  |  *
  10 |  *   credenciales inválidas → la app muestra el error y permanece en /login,
  11 |  *   pero el caso exige por error que haya llegado al dashboard.
  12 |  */
  13 | test('TC-E2E-EVIDENCIA-01 (fallo deliberado): credenciales inválidas no deben entrar', async ({
  14 |   page,
  15 | }) => {
  16 |   await page.goto('/login');
  17 |   await page.locator('#login-email').fill('cuenta.invalida@allado.test');
  18 |   await page.locator('#login-password').fill('ClaveIncorrecta!');
  19 |   await page.locator("form button[type='submit']").click();
  20 | 
  21 |   await expect(page.getByRole('alert')).toBeVisible();
  22 | 
  23 |   // Aserción deliberadamente incorrecta: produce el fallo que se documenta.
> 24 |   await expect(page).toHaveURL(/\/dashboard$/, { timeout: 5_000 });
     |                      ^ Error: expect(page).toHaveURL(expected) failed
  25 | });
  26 | 
```