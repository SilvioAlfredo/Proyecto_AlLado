#!/usr/bin/env python3
"""Vuelca los resultados de la ultima corrida a la planilla oficial de test cases.

La planilla es el artefacto de QA: sus columnas `Resultado Real`, `Estado` y
`Evidencia` deben reflejar la ultima corrida. Este script toma los resultados del
informe consolidado (XML) y los escribe en las filas que comparten identificador;
los casos de la corrida que todavia no estaban en la planilla se anexan con su
trazabilidad, sus pasos y su evidencia.

Uso:
  python3 Testing/e2e/scripts/actualizar_planilla.py

Opciones:
  --informe    XML del informe consolidado (por defecto, el ultimo generado)
  --planilla   ruta de la planilla a actualizar (por defecto, la del repositorio)
  --sin-agregar  no anexa los casos nuevos
  --simular    no escribe nada: muestra que se actualizaria
"""
import argparse
import datetime
import json
import os
import re
import sys

import openpyxl

# El catalogo estatico de los casos de API (Testing/e2e/casos_api.json) es la
# fuente de las definiciones: la planilla y el ejecutor leen el mismo archivo.
CATALOGO_API = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "casos_api.json")


def catalogo_api():
    try:
        with open(CATALOGO_API, encoding="utf-8") as archivo:
            return json.load(archivo)["casos"]
    except (OSError, KeyError, ValueError):
        return {}

RAIZ = "/home/Alfredo/Documentos/AlLado/AlLado"
DESTINO_INFORME = "/home/Alfredo/Documentos/AlLado/plan_de_pruebas"
PLANILLA_REPO = f"{RAIZ}/Testing/Planilla de Test Cases v2(2).xlsx"
PLANILLA_DESCARGAS = "/home/Alfredo/Descargas/AlLado - Planilla de Test Cases v2(2).xlsx"
INFORME_XML = f"{DESTINO_INFORME}/AlLado - Informe de Resultados de Pruebas.xml"
HOJA = "AlLado - Planilla de Test Cases"

# Columnas de la planilla (1-indexadas).
COL_ID = 1
COL_TRAZABILIDAD = 2
COL_DESCRIPCION = 3
COL_TIPO = 4
COL_TECNICA = 5
COL_PRECONDICIONES = 6
COL_DATOS = 7
COL_PASOS = 8
COL_ESPERADO = 9
COL_PRIORIDAD = 10
COL_RESULTADO_REAL = 11
COL_ESTADO = 12
COL_EVIDENCIA = 13


def leer_informe(ruta):
    """Devuelve {id_caso: {columnas de la planilla}} del informe consolidado."""
    import xml.etree.ElementTree as ET

    if not os.path.isfile(ruta):
        raise SystemExit(f"No existe el informe: {ruta}")

    raiz = ET.parse(ruta).getroot()
    resultados = {}
    for caso in raiz.iter("testcase"):
        id_caso = caso.get("name")
        if not id_caso:
            continue
        resultados[id_caso] = {
            "trazabilidad": (caso.get("trazabilidad") or "").strip(),
            "descripcion": (caso.get("descripcion") or "").strip(),
            "tipo": (caso.get("tipo") or "").strip(),
            "tecnica": (caso.get("tecnica") or "").strip(),
            "precondiciones": (caso.findtext("precondiciones") or "").strip(),
            "datos": (caso.findtext("datosEntrada") or "").strip(),
            "pasos": (caso.findtext("pasosEjecucion") or "").strip(),
            "esperado": (caso.findtext("resultadoEsperado") or "").strip(),
            "prioridad": (caso.get("prioridad") or "").strip(),
            "real": (caso.findtext("resultadoReal") or "").strip(),
            "estado": (caso.get("estado") or "").upper(),
            "evidencia": (caso.findtext("evidencia") or "").strip(),
        }
    return resultados


def elegir_planilla(ruta=None):
    if ruta:
        return ruta
    for candidata in (PLANILLA_REPO, PLANILLA_DESCARGAS):
        if os.path.isfile(candidata):
            return candidata
    raise SystemExit(
        "No se encontro la planilla. Busque "
        f"{PLANILLA_REPO} o {PLANILLA_DESCARGAS}."
    )


def escribir_resultado(hoja, fila, resultado, solo_resultado):
    """Escribe el resultado de un caso. `solo_resultado` no pisa la definicion."""
    if solo_resultado:
        hoja.cell(row=fila, column=COL_RESULTADO_REAL).value = resultado["real"]
        hoja.cell(row=fila, column=COL_ESTADO).value = resultado["estado"]
        hoja.cell(row=fila, column=COL_EVIDENCIA).value = resultado["evidencia"]
        return
    for columna, clave in (
        (COL_TRAZABILIDAD, "trazabilidad"),
        (COL_DESCRIPCION, "descripcion"),
        (COL_TIPO, "tipo"),
        (COL_TECNICA, "tecnica"),
        (COL_PRECONDICIONES, "precondiciones"),
        (COL_DATOS, "datos"),
        (COL_PASOS, "pasos"),
        (COL_ESPERADO, "esperado"),
        (COL_PRIORIDAD, "prioridad"),
        (COL_RESULTADO_REAL, "real"),
        (COL_ESTADO, "estado"),
        (COL_EVIDENCIA, "evidencia"),
    ):
        if resultado[clave]:
            hoja.cell(row=fila, column=columna).value = resultado[clave]


def descripcion_normalizada(texto):
    """Descripcion comparable entre la planilla y el informe.

    Se quitan el prefijo 'TC-XXX-NNN: ' y el sufijo '[variante A]' que agrega el
    passage de unicidad del informe: sin eso, el mismo caso aparece con dos
    descripciones distintas y la planilla acumula filas huerfanas.
    """
    limpio = " ".join(str(texto or "").split()).lower()
    if ":" in limpio:
        prefijo, resto = limpio.split(":", 1)
        if prefijo.strip().startswith("tc-"):
            limpio = resto.strip()
    limpio = re.sub(r"\s*\[variante\s+[a-z]\]\s*$", "", limpio)
    return limpio


def clave_caso(caso):
    """Clave de cruce entre la planilla y los bloques del informe.

    Es la descripcion normalizada del caso: identifica el caso aunque su
    identificador cambie entre corridas.
    """
    return descripcion_normalizada(caso.get("descripcion", ""))


def id_base_y_variante(id_caso):
    """Separa el identificador de la variante: ("TC-SEC-001", "a") o ("TC-SEC-001", "")."""
    texto = str(id_caso or "").strip()
    if len(texto) > 2 and texto[-1].islower() and texto[-2].isdigit():
        return texto[:-1], texto[-1]
    return texto, ""


def actualizar(ruta_planilla, resultados, simular, agregar):
    libro = openpyxl.load_workbook(ruta_planilla)
    if HOJA not in libro.sheetnames:
        raise SystemExit(f"La planilla no tiene la hoja '{HOJA}'.")
    hoja = libro[HOJA]

    actualizados = 0
    actualizados_ids = set()
    sin_cubrir = 0
    # Indice de la planilla: identificador completo y, por base, sus variantes.
    por_id = {}
    por_base = {}
    por_descripcion = {}
    for fila in range(2, hoja.max_row + 1):
        id_caso = hoja.cell(row=fila, column=COL_ID).value
        if not id_caso:
            continue
        id_caso = str(id_caso).strip()
        base, variante = id_base_y_variante(id_caso)
        por_id[id_caso] = fila
        por_base.setdefault(base, []).append((variante, fila))
        descripcion = descripcion_normalizada(
            hoja.cell(row=fila, column=COL_DESCRIPCION).value)
        if descripcion:
            por_descripcion.setdefault(descripcion, fila)

    usadas = set()
    for id_caso, resultado in resultados.items():
        base, variante = id_base_y_variante(id_caso)
        fila = por_id.get(id_caso)
        if fila is None:
            # El identificador pudo cambiar (por ejemplo, al corregir el de la E2E):
            # se busca la fila por descripcion y se actualiza su identificador.
            descripcion = descripcion_normalizada(resultado.get("descripcion", ""))
            fila = por_descripcion.get(descripcion)
            if fila is not None:
                hoja.cell(row=fila, column=COL_ID).value = id_caso
                por_id[id_caso] = fila
        if fila is None:
            # Mismo caso base con otra variante: se reusa la fila libre de esa base.
            candidatas = [f for v, f in por_base.get(base, []) if f not in usadas]
            fila = candidatas[0] if candidatas else None
        if fila is None:
            continue
        usadas.add(fila)
        actualizados_ids.add(id_caso)
        # Las filas que genera el pipeline (backend y frontends) se refrescan
        # completas; las escritas a mano por QA conservan su definicion.
        generada = id_caso.startswith(("BE-", "FEW-", "FEM-"))
        escribir_resultado(hoja, fila, resultado, solo_resultado=not generada)
        actualizados += 1

    # Los casos de la corrida que no tenian fila propia se anexan.
    # Casos de API con definicion pero sin fila: se crean con su trazabilidad,
    # sus pasos y su resultado esperado (la columna de estado la completa la corrida).
    definiciones = catalogo_api()
    creadas = 0
    for id_caso, caso in definiciones.items():
        if id_caso in por_id or not caso.get("esperado"):
            continue
        fila = hoja.max_row + 1
        hoja.cell(row=fila, column=COL_ID).value = id_caso
        hoja.cell(row=fila, column=COL_TRAZABILIDAD).value = caso.get("trazabilidad", "")
        hoja.cell(row=fila, column=COL_DESCRIPCION).value = caso.get("titulo", id_caso)
        hoja.cell(row=fila, column=COL_TIPO).value = caso.get("tipo", "")
        hoja.cell(row=fila, column=COL_TECNICA).value = caso.get("tecnica", "")
        hoja.cell(row=fila, column=COL_PRECONDICIONES).value = caso.get("precondiciones", "")
        hoja.cell(row=fila, column=COL_DATOS).value = caso.get("datos", "")
        hoja.cell(row=fila, column=COL_PASOS).value = caso.get("pasos", "")
        hoja.cell(row=fila, column=COL_ESPERADO).value = caso["esperado"]
        hoja.cell(row=fila, column=COL_PRIORIDAD).value = caso.get("prioridad", "Alta")
        por_id[id_caso] = fila
        creadas += 1

    agregados = 0
    if agregar:
        for id_caso, resultado in sorted(resultados.items()):
            if id_caso in actualizados_ids:
                continue
            fila = hoja.max_row + 1
            hoja.cell(row=fila, column=COL_ID).value = id_caso
            escribir_resultado(hoja, fila, resultado, solo_resultado=False)
            por_base.setdefault(base, []).append((variante, fila))
            usadas.add(fila)
            agregados += 1

    # Filas huerfanas: mismo caso (misma descripcion) con un identificador viejo que
    # la corrida ya no usa (por ejemplo, cuando el identificador de la E2E se corrige).
    descripciones_corrida = {
        descripcion_normalizada(resultado.get("descripcion", ""))
        for resultado in resultados.values()
    }
    huerfanas = []
    for id_caso, fila in por_id.items():
        if fila in usadas or id_caso in resultados:
            continue
        descripcion = descripcion_normalizada(
            hoja.cell(row=fila, column=COL_DESCRIPCION).value)
        if descripcion and descripcion in descripciones_corrida:
            huerfanas.append(fila)
    for fila in sorted(huerfanas, reverse=True):
        hoja.delete_rows(fila)
    if huerfanas:
        print(f"Filas huerfanas eliminadas: {len(huerfanas)}")

    # Casos de la planilla que la corrida no cubrio: se aclara sin pisar lo anterior.
    for id_caso, fila in por_id.items():
        if fila in usadas:
            continue
        sin_cubrir += 1
        nota = hoja.cell(row=fila, column=COL_EVIDENCIA).value
        if not nota:
            hoja.cell(row=fila, column=COL_EVIDENCIA).value = (
                "No ejecutado en la ultima corrida (ver informe consolidado)."
            )

    if not simular:
        copia = f"{ruta_planilla}.bak"
        libro.save(copia)
        libro.save(ruta_planilla)
        print(f"Planilla actualizada: {ruta_planilla}")
        print(f"Copia de seguridad:   {copia}")
    print(f"Casos actualizados: {actualizados}")
    print(f"Casos agregados:   {agregados} (definiciones nuevas: {creadas})")
    print(f"Casos sin cubrir:   {sin_cubrir}")
    return actualizados, agregados, sin_cubrir


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--informe", default=INFORME_XML)
    parser.add_argument("--planilla", default=None)
    parser.add_argument("--sin-agregar", action="store_true",
                        help="no anexa los casos de la corrida que faltan en la planilla")
    parser.add_argument("--simular", action="store_true")
    args = parser.parse_args()

    resultados = leer_informe(args.informe)
    planilla = elegir_planilla(args.planilla)
    print(f"Planilla: {planilla}")
    print(f"Informe:  {args.informe} ({len(resultados)} casos)")
    actualizar(planilla, resultados, args.simular, not args.sin_agregar)
    print(f"Fecha:    {datetime.datetime.now():%d/%m/%Y %H:%M}")


if __name__ == "__main__":
    main()
