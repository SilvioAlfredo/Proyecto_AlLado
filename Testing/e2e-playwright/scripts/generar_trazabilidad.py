# -*- coding: utf-8 -*-
"""Genera la trazabilidad E2E en TSV (para pegar en la planilla) y en Markdown."""
import csv
from pathlib import Path

OUT = Path(__file__).resolve().parent.parent / "results"

HEADERS = [
    "ID Caso",
    "Trazabilidad (HU / CA)",
    "Descripción",
    "Tipo",
    "Técnica",
    "Precondiciones",
    "Datos de Entrada",
    "Pasos de Ejecución",
    "Resultado Esperado",
    "Prioridad",
    "Resultado Real",
    "Estado",
    "Evidencia",
    "Etiquetas (tipo de testing)",
    "Capa de la pirámide",
    "¿Automatizable?",
    "Justificación",
    "Herramienta / ID / ruta del test",
]

BASE_PRE = "Stack QA operativo (frontend 3001, backend 8081, PostgreSQL al_lado_e2e con seed). "
EVID = "Testing/e2e-playwright/results"
JUST = (
    "Flujo crítico de negocio, repetitivo y con resultado determinista; "
    "el costo de automatizarlo es menor al de ejecutarlo manualmente en cada entrega."
)

ROWS = [
    [
        "TC-PW-FLOW-01",
        "HU-04 CA-01/CA-02 · RF-01, RF-11",
        "Registro de un trabajador, login, acceso al dashboard y logout; la ruta protegida redirige al login.",
        "Funcional",
        "Positivo (E2E)",
        BASE_PRE + "Email de registro inexistente.",
        "Nombre, apellido y email únicos; teléfono +5491100000000; contraseña Clave123!; rol TRABAJADOR.",
        "1) Abrir /register y elegir 'Quiero trabajar'. 2) Completar los datos y crear la cuenta. "
        "3) Ir a /login e ingresar con la cuenta creada. 4) Verificar /trabajador/dashboard. "
        "5) Cerrar sesión. 6) Abrir /trabajador/dashboard sin sesión.",
        "Cuenta creada, sesión iniciada y dashboard visible; tras el logout la ruta protegida redirige a /login.",
        "Crítica",
        "PASS — 1,3 s. Dashboard del trabajador visible y redirección a /login verificada.",
        "PASS",
        f"{EVID}/capturas-flujos/flow-01-01-dashboard-trabajador.png; "
        f"{EVID}/capturas-flujos/flow-01-02-ruta-protegida-redirige.png; "
        f"{EVID}/html-report/index.html",
        "funcional; smoke; regresión",
        "UI / E2E",
        "Sí",
        JUST,
        "Playwright Test 1.64.1 + TypeScript + POM | "
        "Testing/e2e-playwright/tests/flow-01-registro-login.spec.ts::TC-PW-FLOW-01: Registro → Login → Dashboard → Logout",
    ],
    [
        "TC-PW-FLOW-02",
        "HU-01 CA-01, HU-02 CA-01/CA-02 · RF-04, RF-15",
        "Publicación de una solicitud por el empleador, postulación del trabajador y selección de la propuesta; el historial refleja el estado.",
        "Funcional",
        "Positivo (E2E)",
        BASE_PRE + "Cuentas nuevas de empleador y trabajador creadas por API.",
        "Título único de la solicitud; descripción; especialidad Electricidad; ubicación Centro; presupuesto 120000; "
        "postulación con $110000, plazo 2 días y mensaje.",
        "1) Login empleador y publicar solicitud en /empleador/solicitudes/nueva. "
        "2) Login trabajador, buscar la publicación y postularse. "
        "3) Verificar PENDIENTE en /trabajador/postulaciones. "
        "4) Login empleador y aceptar al postulante en /empleador/candidatos. "
        "5) Verificar 'Profesional seleccionado'. 6) Verificar SELECCIONADA en el historial del trabajador.",
        "La solicitud se publica, la postulación queda PENDIENTE, el empleador la selecciona y el historial del trabajador muestra SELECCIONADA.",
        "Crítica",
        "PASS — 3,5 s. Solicitud publicada, postulación PENDIENTE, SELECCIONADA en candidatos e historial.",
        "PASS",
        f"{EVID}/capturas-flujos/flow-02-01-solicitud-publicada.png; "
        f"{EVID}/capturas-flujos/flow-02-02-postulacion-enviada.png; "
        f"{EVID}/capturas-flujos/flow-02-03-postulante-seleccionado.png; "
        f"{EVID}/capturas-flujos/flow-02-04-historial-trabajador.png; "
        f"{EVID}/html-report/index.html",
        "funcional; regresión",
        "UI / E2E",
        "Sí",
        JUST,
        "Playwright Test 1.64.1 + TypeScript + POM | "
        "Testing/e2e-playwright/tests/flow-02-publicar-postular-seleccionar.spec.ts::TC-PW-FLOW-02: Publicar → Postular → Seleccionar",
    ],
    [
        "TC-PW-FLOW-03",
        "HU-06 CA-01 · RF-05, RF-08",
        "Valoración del trabajo completado por el empleador y actualización del historial y las valoraciones del trabajador.",
        "Funcional",
        "Positivo (E2E)",
        BASE_PRE + "Trabajo publicado, postulado, seleccionado y completado por API.",
        "Puntuación 5 estrellas; comentario único; trabajador y empleador de la precondición.",
        "1) Login empleador y abrir /empleador/valoraciones?trabajadorId=<id>. "
        "2) Elegir al trabajador, puntuar 5 estrellas, escribir el comentario y publicar. "
        "3) Verificar el mensaje de confirmación. 4) Login trabajador y abrir /trabajador/postulaciones. "
        "5) Verificar COMPLETADA. 6) Abrir /trabajador/valoraciones y verificar la reseña.",
        "La valoración se publica; el historial del trabajador muestra COMPLETADA y la reseña aparece en sus valoraciones.",
        "Crítica",
        "PASS — 1,8 s. Valoración 5★ publicada, historial COMPLETADA y reseña visible para el trabajador.",
        "PASS",
        f"{EVID}/capturas-flujos/flow-03-01-valoracion-publicada.png; "
        f"{EVID}/capturas-flujos/flow-03-02-valoraciones-trabajador.png; "
        f"{EVID}/html-report/index.html",
        "funcional; regresión",
        "UI / E2E",
        "Sí",
        JUST,
        "Playwright Test 1.64.1 + TypeScript + POM | "
        "Testing/e2e-playwright/tests/flow-03-valorar-historial.spec.ts::TC-PW-FLOW-03: Valorar → Historial actualiza",
    ],
    [
        "TC-PW-FLOW-04",
        "HU-02 CA-02 · RF-04",
        "Borrado lógico: el trabajador retira su postulación PENDIENTE y el historial refleja el estado RETIRADA.",
        "Funcional",
        "Negativo (E2E)",
        BASE_PRE + "Publicación del empleador y postulación PENDIENTE del trabajador creadas por API.",
        "Postulación propia en estado PENDIENTE; confirmación del diálogo del navegador.",
        "1) Login trabajador. 2) Abrir /trabajador/postulaciones/<id>. 3) Verificar estado PENDIENTE. "
        "4) Pulsar 'Retirar postulación' y aceptar el diálogo. 5) Verificar estado RETIRADA y ausencia del botón. "
        "6) Abrir /trabajador/postulaciones y verificar RETIRADA.",
        "La postulación pasa a RETIRADA, el botón de retiro desaparece y el historial muestra el nuevo estado.",
        "Crítica",
        "PASS — 1,3 s. Postulación retirada y estado RETIRADA verificado en el historial.",
        "PASS",
        f"{EVID}/capturas-flujos/flow-04-01-postulacion-pendiente.png; "
        f"{EVID}/capturas-flujos/flow-04-02-postulacion-retirada.png; "
        f"{EVID}/capturas-flujos/flow-04-03-historial-retirada.png; "
        f"{EVID}/html-report/index.html",
        "funcional; negativa; regresión",
        "UI / E2E",
        "Sí",
        JUST,
        "Playwright Test 1.64.1 + TypeScript + POM | "
        "Testing/e2e-playwright/tests/flow-04-retirar-postulacion.spec.ts::TC-PW-FLOW-04: Retirar postulación → estado RETIRADA",
    ],
    [
        "TC-E2E-LOGIN-01",
        "HU-04 CA-02 · RF-01, RF-11",
        "Login con cuenta sembrada, dashboard del rol y logout.",
        "Funcional",
        "Positivo (E2E)",
        BASE_PRE + "Cuenta empleador@demo.com sembrada.",
        "empleador@demo.com / Clave123!",
        "1) Abrir /login. 2) Ingresar con la cuenta demo. 3) Verificar /empleador/dashboard. 4) Cerrar sesión.",
        "El login redirige al panel del empleador y el logout vuelve a /login.",
        "Alta",
        "PASS — 1,0 s. Login de empleador demo, dashboard y logout verificados.",
        "PASS",
        f"{EVID}/html-report/index.html; {EVID}/ejecucion.log",
        "funcional; smoke",
        "UI / E2E",
        "Sí",
        JUST,
        "Playwright Test 1.64.1 + TypeScript + POM | "
        "Testing/e2e-playwright/tests/p1-casos-complementarios.spec.ts::TC-E2E-LOGIN-01",
    ],
    [
        "TC-E2E-PROT-01",
        "HU-04 CA-02 · RF-01, RF-11",
        "Ruta protegida sin sesión y separación de roles: un trabajador no accede al panel de administración.",
        "Seguridad",
        "Negativo (E2E)",
        BASE_PRE + "Cuenta trabajador@demo.com sembrada.",
        "trabajador@demo.com / Clave123!; URL /admin/dashboard.",
        "1) Abrir /trabajador/dashboard sin sesión. 2) Verificar redirección a /login. "
        "3) Login como trabajador. 4) Abrir /admin/dashboard. 5) Verificar redirección al panel del trabajador.",
        "Sin sesión redirige a /login; un trabajador autenticado no accede a /admin/dashboard y vuelve a su panel.",
        "Alta",
        "PASS — 0,8 s. Redirección sin sesión y bloqueo de rol verificados.",
        "PASS",
        f"{EVID}/html-report/index.html; {EVID}/ejecucion.log",
        "seguridad; negativa",
        "UI / E2E",
        "Sí",
        JUST,
        "Playwright Test 1.64.1 + TypeScript + POM | "
        "Testing/e2e-playwright/tests/p1-casos-complementarios.spec.ts::TC-E2E-PROT-01",
    ],
    [
        "TC-E2E-ADMIN-01",
        "HU-18 CA-03, HU-19 CA-01 · RF-19",
        "Acceso de administración por /acceso-admin y visualización de las métricas del dashboard.",
        "Funcional",
        "Positivo (E2E)",
        BASE_PRE + "Cuenta admin@allado.com sembrada.",
        "admin@allado.com / Clave123!",
        "1) Abrir /acceso-admin. 2) Ingresar con la cuenta admin. 3) Verificar /admin/dashboard. "
        "4) Verificar las tarjetas de métricas (Trabajadores, Postulaciones).",
        "El administrador accede a /admin/dashboard y ve las métricas cargadas.",
        "Alta",
        "PASS — 1,5 s. Acceso administrador y métricas visibles.",
        "PASS",
        f"{EVID}/capturas-flujos/p1-admin-dashboard-metricas.png; {EVID}/html-report/index.html",
        "funcional; smoke",
        "UI / E2E",
        "Sí",
        JUST,
        "Playwright Test 1.64.1 + TypeScript + POM | "
        "Testing/e2e-playwright/tests/p1-casos-complementarios.spec.ts::TC-E2E-ADMIN-01",
    ],
]

assert all(len(row) == len(HEADERS) for row in ROWS), "Cada fila debe tener 18 columnas"

tsv = OUT / "TRAZABILIDAD.tsv"
with tsv.open("w", encoding="utf-8-sig", newline="") as fh:
    writer = csv.writer(fh, delimiter="\t", quoting=csv.QUOTE_MINIMAL)
    writer.writerow(HEADERS)
    writer.writerows(ROWS)

md = OUT / "TRAZABILIDAD.md"
with md.open("w", encoding="utf-8") as fh:
    fh.write("# Trazabilidad E2E — Sprint 4\n\n")
    fh.write(
        "Filas listas para pegar en `Testing/Planilla de Test Cases v2(2).xlsx` "
        "(las 18 columnas respetan el orden de la planilla).\n\n"
    )
    fh.write("> Versión para pegar: `results/TRAZABILIDAD.tsv` (columnas separadas por tabulación).\n\n")
    fh.write("| ID Caso | Trazabilidad | Estado | Resultado | Evidencia | Test automatizado |\n")
    fh.write("|---|---|---|---|---|---|\n")
    for row in ROWS:
        fh.write(
            f"| **{row[0]}** | {row[1]} | {row[11]} | {row[10]} | "
            f"`{row[12].split(';')[0].strip()}` | `{row[17].split('|')[-1].strip()}` |\n"
        )

print(f"OK: {tsv} ({len(ROWS)} filas x {len(HEADERS)} columnas)")
print(f"OK: {md}")
