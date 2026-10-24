#!/usr/bin/env python3
"""Genera el informe consolidado de resultados de pruebas en PDF.

Recolecta:
  * Suite E2E de interfaz (Selenium + JUnit 5)  -> Testing/e2e/target/surefire-reports
  * Suite de backend (JUnit 5 + Spring Boot Test) -> Backend/target/surefire-reports
  * Planilla de casos ejecutada con Postman     -> xlsx de la planilla

y los vuelca en una tabla general con el formato de la planilla oficial
(ID, Trazabilidad, Descripcion, Tipo, Tecnica, Precondiciones, Datos de Entrada,
Pasos de Ejecucion, Resultado Esperado, Prioridad, Resultado Real, Estado, Evidencia),
separada por tipo de prueba y categorizada por area tecnica.

Uso:  python3 generar_informe_pruebas.py [--salida RUTA.pdf]
"""
import argparse
import datetime
import json
import os
import re
import xml.etree.ElementTree as ET
from collections import OrderedDict

import openpyxl
from reportlab.lib import colors
from reportlab.lib.enums import TA_CENTER, TA_LEFT
from reportlab.lib.pagesizes import A4, landscape
from reportlab.lib.styles import ParagraphStyle, getSampleStyleSheet
from reportlab.lib.units import mm
from reportlab.platypus import (BaseDocTemplate, Frame, PageBreak, PageTemplate,
                                Paragraph, Spacer, Table, TableStyle)

AL_LADO = "/home/Alfredo/Documentos/AlLado/AlLado"
# La planilla vive en el repositorio (artefacto versionado). La copia de
# Descargas se usa solo como respaldo si el repo no la tiene.
PLANILLA_REPO = f"{AL_LADO}/Testing/Planilla de Test Cases v2(2).xlsx"
PLANILLA_DESCARGAS = "/home/Alfredo/Descargas/AlLado - Planilla de Test Cases v2(2).xlsx"
PLANILLA = PLANILLA_REPO if os.path.isfile(PLANILLA_REPO) else PLANILLA_DESCARGAS
DESTINO_DIR = "/home/Alfredo/Documentos/AlLado/plan_de_pruebas"
ORIGEN_EVIDENCIAS = f"{AL_LADO}/Testing/e2e/target/e2e-evidencias"
CARPETA_EVIDENCIAS = "evidencias"

COLUMNAS = [
    "ID Caso", "Trazabilidad (HU / CA)", "Descripción", "Tipo", "Técnica",
    "Precondiciones", "Datos de Entrada", "Pasos de Ejecución",
    "Resultado Esperado", "Prioridad", "Resultado Real", "Estado", "Evidencia",
]

# Anchos en puntos (A4 apaisado: 842 x 595, margen útil ~790)
ANCHOS = [58, 62, 92, 44, 46, 78, 78, 78, 78, 30, 84, 30, 62]

CATEGORIAS = OrderedDict([
    ("Frontend - E2E de interfaz (Selenium)", []),
    ("Frontend web - Pruebas unitarias (Vitest)", []),
    ("Frontend movil - Pruebas unitarias (jest-expo)", []),
    ("Backend - Seguridad y autorizacion", []),
    ("Backend - Funcional (servicios de dominio)", []),
    ("Backend - Contrato de API (controladores)", []),
    ("Backend - Integracion con contexto Spring real", []),
    ("API - Casos de la planilla ejecutados con Postman", []),
    ("Base de datos - Integracion y persistencia", []),
])

# Clases del backend que son de seguridad por naturaleza.
SEGURIDAD_BACKEND = {
    "SecurityUtilsTest", "AdminDashboardSecurityTest", "PostulacionControllerSecurityTest",
    "ReporteControllerSecurityTest", "AdminGestionControllerTest", "AuthControllerTest",
    "PostulacionAuthorizationTest", "OportunidadAuthorizationTest",
}
CONTRATO_API_BACKEND = {"OportunidadControllerContractTest"}


def id_base(id_caso):
    """Quita el sufijo de variante (a, b, c...) de un identificador de caso.

    El passage de unicidad agrega ese sufijo cuando un mismo TC cubre varias
    verificaciones (por ejemplo TC-SEC-001a, TC-SEC-001b). Para comparar la
    planilla con los bloques de E2E y de backend hay que comparar la base.
    """
    texto = str(id_caso or "").strip()
    if len(texto) > 2 and texto[-1].islower() and texto[-2].isdigit():
        return texto[:-1]
    return texto


def variantes_de_id(id_caso):
    """El identificador tal cual y el mismo sin la letra de variante.

    El passage de unicidad agrega una letra final cuando un caso tiene varias
    verificaciones; la planilla puede guardar el identificador con la letra y
    el bloque que lo genera el nombre base (o al reves).
    """
    texto = str(id_caso or "").strip()
    ids = {texto}
    if len(texto) > 2 and texto[-1].islower():
        ids.add(texto[:-1])
    return ids


def descripcion_normalizada(texto):
    """Descripcion comparable: sin el prefijo 'TC-XXX-NNN: ' ni '[variante A]'."""
    limpio = " ".join(str(texto or "").split()).lower()
    if ":" in limpio:
        prefijo, resto = limpio.split(":", 1)
        if prefijo.strip().startswith("tc-"):
            limpio = resto.strip()
    limpio = re.sub(r"\s*\[variante\s+[a-z]\]\s*$", "", limpio)
    return limpio


def clave_caso(caso):
    """Clave de cruce entre la planilla y los bloques del informe.

    Es la descripcion normalizada del caso: es lo que identifica un caso aunque
    su identificador cambie entre corridas (por ejemplo, cuando el de la E2E se
    corrige de 'E2E-RegisterTesta' a 'TC-LIM-003a').
    """
    return descripcion_normalizada(caso.get("descripcion", ""))


def texto_planilla(valor):
    if valor is None:
        return ""
    return str(valor).replace("\r\n", "\n").strip()


def leer_planilla():
    """Lee los casos de la planilla oficial con su resultado real."""
    wb = openpyxl.load_workbook(PLANILLA, data_only=True)
    ws = wb["AlLado - Planilla de Test Cases"]
    casos = []
    for fila in range(2, ws.max_row + 1):
        id_caso = ws.cell(row=fila, column=1).value
        if not id_caso:
            continue
        casos.append({
            "id": texto_planilla(id_caso),
            "trazabilidad": texto_planilla(ws.cell(row=fila, column=2).value),
            "descripcion": texto_planilla(ws.cell(row=fila, column=3).value),
            "tipo": texto_planilla(ws.cell(row=fila, column=4).value),
            "tecnica": texto_planilla(ws.cell(row=fila, column=5).value),
            "precondiciones": texto_planilla(ws.cell(row=fila, column=6).value),
            "datos": texto_planilla(ws.cell(row=fila, column=7).value),
            "pasos": texto_planilla(ws.cell(row=fila, column=8).value),
            "esperado": texto_planilla(ws.cell(row=fila, column=9).value),
            "prioridad": texto_planilla(ws.cell(row=fila, column=10).value),
            "real": texto_planilla(ws.cell(row=fila, column=11).value),
            "estado": texto_planilla(ws.cell(row=fila, column=12).value).upper(),
            "evidencia": texto_planilla(ws.cell(row=fila, column=13).value),
        })
    return casos


def leer_surefire(directorio):
    """Lee los resultados XML de Surefire de una suite."""
    resultados = []
    if not os.path.isdir(directorio):
        return resultados
    for archivo in sorted(os.listdir(directorio)):
        if not archivo.startswith("TEST-") or not archivo.endswith(".xml"):
            continue
        try:
            raiz = ET.parse(os.path.join(directorio, archivo)).getroot()
        except ET.ParseError:
            continue
        for caso in raiz.findall("testcase"):
            error = caso.find("error")
            fallo = caso.find("failure")
            omitido = caso.find("skipped")
            if error is not None:
                estado, detalle = "ERROR", (error.get("message") or "")[:400]
            elif fallo is not None:
                estado, detalle = "FAIL", (fallo.get("message") or "")[:400]
            elif omitido is not None:
                estado, detalle = "SKIP", "Caso deshabilitado: defecto abierto referenciado."
            else:
                estado, detalle = "PASS", ""
            resultados.append({
                "clase": raiz.get("name", ""),
                "metodo": caso.get("name", ""),
                "tiempo": float(caso.get("time", 0) or 0),
                "estado": estado,
                "detalle": detalle,
            })
    return resultados


def leer_display_names(raiz_java):
    """Extrae los @DisplayName de clase y de metodo de los archivos Java."""
    mapa = {}
    if not os.path.isdir(raiz_java):
        return mapa
    for carpeta, _, archivos in os.walk(raiz_java):
        for archivo in archivos:
            if not archivo.endswith(".java"):
                continue
            ruta = os.path.join(carpeta, archivo)
            with open(ruta, encoding="utf-8") as f:
                texto = f.read()
            clase = os.path.splitext(archivo)[0]
            bloque_clase = re.search(
                r"@DisplayName\(\"([^\"]+)\"\)\s*(?:@[\w.]+\([^)]*\)\s*)*class\s+" + clase,
                texto)
            nombre_clase = bloque_clase.group(1) if bloque_clase else clase
            metodos = {}
            for coincidencia in re.finditer(
                    r"@DisplayName\(\"([^\"]+)\"\)\s*(?:@[\w.]+\([^)]*\)\s*)*void\s+(\w+)\s*\(",
                    texto):
                metodos[coincidencia.group(2)] = coincidencia.group(1)
            mapa[clase] = {"clase": nombre_clase, "metodos": metodos}
    return mapa


def SplitCamelCase(texto):
    """'rechazaOportunidadAjena' -> 'rechaza Oportunidad Ajena'."""
    texto = texto.replace("_", " ")
    return re.sub(r"(?<=[a-záéíóúñ])(?=[A-ZÁÉÍÓÚÑ])", " ", texto).strip()


def resultado_real(resultado):
    """Redacta el resultado real a partir del resultado de Surefire."""
    if resultado["estado"] == "PASS":
        return (f"Verificado. El caso se ejecuto sobre la aplicacion desplegada "
                f"y cumplio la expectativa en {resultado['tiempo']:.2f} s.")
    if resultado["estado"] == "SKIP":
        return "No ejecutado: caso deshabilitado por defecto abierto documentado."
    return f"{resultado['estado']}: {resultado['detalle']}".strip()


def valor_esperado(descripcion, estado):
    """Redacta el resultado esperado a partir de la descripcion y el estado."""
    if estado == "PASS":
        return f"La pantalla refleja {descripcion[0].lower() + descripcion[1:]}."
    return f"El sistema debe impedir o corregir: {descripcion[0].lower() + descripcion[1:]}."


def clasificar_e2e(resultado, display):
    """Determina la categoria y el tipo de un caso E2E."""
    clase = resultado["clase"]
    if "AdminAccess" in clase or "ProtectedRoute" in clase:
        return "Frontend - E2E de interfaz (Selenium)", "Seguridad", "Negativa (RBAC)"
    if "Login" in clase:
        return "Frontend - E2E de interfaz (Selenium)", "Funcional", "Positivo"
    if "Register" in clase:
        return "Frontend - E2E de interfaz (Selenium)", "Valor Límite", "Positivo y negativa"
    if "Postulation" in clase:
        return "Frontend - E2E de interfaz (Selenium)", "Funcional", "Positivo y negativa"
    if "Opportunities" in clase:
        return "Frontend - E2E de interfaz (Selenium)", "Funcional", "Positivo"
    if "Community" in clase or "Forum" in clase:
        return "Frontend - E2E de interfaz (Selenium)", "Funcional", "Positivo"
    return "Frontend - E2E de interfaz (Selenium)", "Funcional", "Positivo"


# Area E2E -> (rol de sesion, datos de entrada, pasos de ejecucion)
AREA_E2E = {
    "Login": (
        "Sin sesion en el navegador.",
        "trabajador@demo.com / empleador@demo.com / Clave123!",
        "1. Abrir /login.\n"
        "2. Cargar el email y la contrasena.\n"
        "3. Enviar el formulario.\n"
        "4. Verificar la URL de destino y la sesion persistida."),
    "AdminAccess": (
        "Sin sesion en el navegador.",
        "admin@allado.com y trabajador@demo.com / Clave123!",
        "1. Abrir /acceso-admin (o /admin/dashboard sin sesion).\n"
        "2. Cargar las credenciales del rol evaluado.\n"
        "3. Enviar el formulario.\n"
        "4. Verificar el acceso permitido o el mensaje de rechazo."),
    "ProtectedRoute": (
        "Sesion iniciada con el rol indicado.",
        "empleador@demo.com / Clave123!",
        "1. Iniciar sesion con el rol del caso.\n"
        "2. Navegar a la ruta protegida del area de trabajador.\n"
        "3. Verificar la redireccion aplicada por el guard."),
    "Register": (
        "Sin sesion en el navegador.",
        "Nombre, apellido, email unico, telefono y contrasena de 7 u 8 caracteres.",
        "1. Abrir /register.\n"
        "2. Elegir el rol de la cuenta.\n"
        "3. Completar los datos personales y la contrasena.\n"
        "4. Enviar y verificar el alta o el rechazo."),
    "Opportunities": (
        "Sesion iniciada como ROLE_TRABAJADOR.",
        "Listado de oportunidades activas de la base QA (dump del proyecto).",
        "1. Abrir /trabajador/oportunidades.\n"
        "2. Aplicar el filtro o la busqueda evaluada.\n"
        "3. Contrastar las tarjetas visibles con lo que devuelve la API."),
    "Postulation": (
        "Sesion iniciada como ROLE_TRABAJADOR; oportunidad creada por la API.",
        "Remuneracion, plazo en dias y mensaje de la propuesta.",
        "1. Abrir el detalle de la oportunidad.\n"
        "2. Completar la propuesta en el modal.\n"
        "3. Enviar y verificar el resultado."),
    "Community": (
        "Sesion iniciada como ROLE_TRABAJADOR.",
        "Publicaciones del foro cargadas desde la base QA.",
        "1. Abrir /trabajador/comunidad.\n"
        "2. Contrastar las publicaciones visibles con las de la API.\n"
        "3. Crear una publicacion y verificar la confirmacion."),
    "Forum": (
        "Sesion iniciada con una cuenta de otro barrio.",
        "Publicacion creada por una cuenta del barrio Centro.",
        "1. Iniciar sesion con la cuenta de otro barrio.\n"
        "2. Abrir /trabajador/comunidad.\n"
        "3. Verificar que no se listan publicaciones ajenas."),
}


def area_e2e(clase):
    for clave in AREA_E2E:
        if clave in clase:
            return clave
    return "Login"


def copiar_evidencias(destino_dir):
    """Copia las capturas de la suite E2E junto al informe.

    Se fusiona con lo que ya este en la carpeta (las evidencias de API, base de
    datos y backend las genera otro script), de modo que no se pisa nada.
    Asi la ruta que aparece en la celda "Evidencia" ("evidencias/.../captura.png")
    resuelve desde la carpeta del informe, y no desde el modulo E2E.
    """
    import shutil
    origen = ORIGEN_EVIDENCIAS
    destino = os.path.join(destino_dir, CARPETA_EVIDENCIAS)
    if os.path.isdir(origen):
        os.makedirs(destino, exist_ok=True)
        shutil.copytree(origen, destino, dirs_exist_ok=True)
    return destino


def construir_casos_e2e(resultados, display, entorno):
    casos = []
    for resultado in resultados:
        clase_simple = resultado["clase"].split(".")[-1]
        info = display.get(clase_simple, {})
        nombre = info.get("metodos", {}).get(resultado["metodo"])
        if not nombre:
            nombre = SplitCamelCase(resultado["metodo"])
        coincidencia = re.match(r"(TC-[A-Z]+-\d+[a-z]?)\s*:\s*(.+)", nombre)
        id_caso = coincidencia.group(1) if coincidencia else f"E2E-{clase_simple}"
        descripcion = coincidencia.group(2) if coincidencia else nombre
        categoria, tipo, tecnica = clasificar_e2e(resultado, info)
        precondiciones, datos, pasos = AREA_E2E.get(area_e2e(clase_simple), AREA_E2E["Login"])
        carpeta = f"{clase_simple}.{resultado['metodo']}"
        evidencia_rel = f"{CARPETA_EVIDENCIAS}/{carpeta}/captura.png"
        evidencia_abs = os.path.join(ORIGEN_EVIDENCIAS, carpeta, "captura.png")
        if resultado["estado"] == "SKIP" or not os.path.isfile(evidencia_abs):
            evidencia_rel = "Sin captura: caso deshabilitado"
            evidencia_abs = ""
        casos.append({
            "id": id_caso,
            "trazabilidad": info.get("clase", ""),
            "descripcion": descripcion[0].upper() + descripcion[1:] if descripcion else descripcion,
            "tipo": f"{tipo} (E2E)",
            "tecnica": tecnica,
            "precondiciones": precondiciones + "\nAmbiente QA desplegado (ver seccion 3).",
            "datos": datos,
            "pasos": pasos,
            "esperado": valor_esperado(descripcion, resultado["estado"]),
            "prioridad": "Alta",
            "real": resultado_real(resultado),
            "estado": resultado["estado"],
            "evidencia": evidencia_rel,
            "evidencia_abs": evidencia_abs,
            "categoria": categoria,
        })
    return casos


# Paquete/clase del backend -> (trazabilidad, pasos)
AREA_BACKEND = {
    "SecurityUtilsTest": ("RF-10 / HU-04", "1. Construir el contexto de seguridad.\n"
                                              "2. Invocar el metodo bajo prueba con los roles evaluados.\n"
                                              "3. Verificar la excepcion o el resultado esperado."),
    "AuthControllerTest": ("HU-04 CA-01 / RF-01", "1. Montar el controlador con el servicio simulado.\n"
                                                   "2. Enviarly peticion HTTP al endpoint.\n"
                                                   "3. Verificar codigo de estado y cuerpo."),
    "PostulacionControllerSecurityTest": ("RF-10 / HU-02", "1. Montar el controlador con el servicio simulado.\n"
                                                           "2. Enviarlapeticion con cada rol evaluado.\n"
                                                           "3. Verificar el codigo de estado (401/403/200)."),
    "ReporteControllerSecurityTest": ("RF-10 / HU-13", "1. Montar el controlador con el servicio simulado.\n"
                                                        "2. Enviarlapeticion con cada rol evaluado.\n"
                                                        "3. Verificar el codigo de estado."),
    "AdminDashboardSecurityTest": ("RF-10 / HU-12", "1. Montar el controlador con el servicio simulado.\n"
                                                      "2. Enviarlapeticion con cada rol evaluado.\n"
                                                      "3. Verificar el codigo de estado."),
    "AdminGestionControllerTest": ("RF-10 / HU-12", "1. Montar el controlador con el servicio simulado.\n"
                                                     "2. Enviarlapeticion con cada rol evaluado.\n"
                                                     "3. Verificar el codigo de estado y el cuerpo."),
    "OportunidadControllerContractTest": ("HU-01 CA-01 / RF-04", "1. Montar el controlador con el servicio simulado.\n"
                                                                "2. Enviarlapeticion al endpoint.\n"
                                                                "3. Verificar el contrato de la respuesta."),
    "OportunidadAuthorizationTest": ("RF-10 / HU-01", "1. Construir el servicio con los repositorios simulados.\n"
                                                      "2. Invocar la operacion con cada actor evaluado.\n"
                                                      "3. Verificar la excepcion de negocio esperada."),
    "PostulacionAuthorizationTest": ("RF-10 / HU-02", "1. Construir el servicio con los repositorios simulados.\n"
                                                       "2. Invocar la operacion con cada actor evaluado.\n"
                                                       "3. Verificar la excepcion de negocio esperada."),
    "PostulacionRetirarTest": ("HU-02 CA-02 / RF-04", "1. Construir el servicio con los repositorios simulados.\n"
                                                       "2. Retirar la postulacion evaluada.\n"
                                                       "3. Verificar el estado final."),
    "PostulacionServiceImplTest": ("HU-02 CA-01 / RF-04", "1. Construir el servicio con los repositorios simulados.\n"
                                                          "2. Ejecutar la operacion evaluada.\n"
                                                          "3. Verificar el resultado y las llamadas al repositorio."),
    "EtiquetaEliminarTest": ("HU-01 CA-02 / RF-06", "1. Construir el servicio con los repositorios simulados.\n"
                                                    "2. Eliminar la etiqueta evaluada.\n"
                                                    "3. Verificar el resultado."),
    "SuscripcionCancelarAdminTest": ("HU-12 CA-02 / RF-12", "1. Construir el servicio con los repositorios simulados.\n"
                                                             "2. Cancelar la suscripcion evaluada.\n"
                                                             "3. Verificar el resultado."),
    "SuscripcionProVigenteTest": ("HU-12 CA-01 / RF-12", "1. Construir el servicio con los repositorios simulados.\n"
                                                        "2. Consultar la vigencia del plan PRO.\n"
                                                        "3. Verificar el resultado."),
    "EmpresaVerificacionTest": ("HU-05 CA-01 / RF-03", "1. Construir el servicio con los repositorios simulados.\n"
                                                       "2. Verificar la empresa evaluada.\n"
                                                       "3. Verificar el resultado."),
    "TransferenciaVerificacionTest": ("HU-09 CA-01 / RF-11", "1. Construir el servicio con los repositorios simulados.\n"
                                                             "2. Verificar la transferencia evaluada.\n"
                                                             "3. Verificar el resultado."),
    "ReporteModeracionTest": ("HU-13 CA-02 / RF-13", "1. Construir el servicio con los repositorios simulados.\n"
                                                      "2. Moderar el reporte evaluado.\n"
                                                      "3. Verificar el resultado."),
    "AlLadoProjectApplicationTests": ("HU-00 / Integracion global", "1. Levantar el contexto completo de Spring Boot.\n"
                                                                    "2. Verificar que el contexto carga sin errores.\n"
                                                                    "3. Cerrar el contexto."),
}


# Trazabilidad de las suites unitarias de los frontends, por archivo de prueba.
TRAZABILIDAD_FRONTEND = {
    "jwt": "RF-01 / HU-04 CA-01 (AL-API-005)",
    "usuarioRef": "RF-01 / HU-04 (AL-API-005)",
    "api": "RF-01 / HU-04 (AL-API-005)",
    "AuthProvider": "RF-01 / HU-04 CA-01 (AL-API-005)",
    "serviciosPerfil": "RF-02 / RF-03 (AL-API-005)",
    "oportunidadService": "RF-04 / HU-01 (AL-SEC-006)",
    "CandidatosList": "RF-04 / HU-02 (AL-SEC-006)",
    "AuthContext": "RF-01 / HU-04 CA-01 (AL-API-005)",
    "client": "RF-01 / HU-04 (AL-API-005)",
    "services": "RF-01 / HU-04 (AL-API-005)",
    "comunidad": "RF-13 / HU-13 CA-02 (AL-SEC-006)",
}

PASOS_FRONTEND = ("1. Ejecutar la suite unitaria del frontend.\n"
                   "2. Montar el componente o llamar al servicio con el estado simulado.\n"
                   "3. Verificar el oraculo del caso.")


def leer_junit(ruta):
    """Lee un JUnit XML de Vitest o de Jest."""
    if not os.path.isfile(ruta):
        return []
    try:
        raiz = ET.parse(ruta).getroot()
    except ET.ParseError:
        return []
    resultados = []
    for suite in raiz.iter("testsuite"):
        for caso in suite.findall("testcase"):
            error = caso.find("error")
            fallo = caso.find("failure")
            omitido = caso.find("skipped")
            if error is not None:
                estado, detalle = "ERROR", (error.get("message") or "")[:400]
            elif fallo is not None:
                estado, detalle = "FAIL", (fallo.get("message") or "")[:400]
            elif omitido is not None:
                estado, detalle = "SKIP", "Caso deshabilitado."
            else:
                estado, detalle = "PASS", ""
            resultados.append({
                "clase": caso.get("classname") or suite.get("name") or "",
                "suite": suite.get("name", ""),
                "metodo": caso.get("name", ""),
                "tiempo": float(caso.get("time", 0) or 0),
                "estado": estado,
                "detalle": detalle,
            })
    return resultados


def clave_de_prueba(nombre):
    """Nombre del archivo de prueba sin extension ni ruta: 'jwt.test.ts' -> 'jwt'."""
    base = os.path.basename(str(nombre or "").strip().split("::")[0])
    for sufijo in (".test.tsx", ".test.ts", ".spec.tsx", ".spec.ts"):
        if base.endswith(sufijo):
            return base[: -len(sufijo)]
    return base


def titulo_de_prueba(nombre):
    """Titulo del caso: lo que va despues de '::' en el classname de Jest."""
    partes = str(nombre or "").split("::")
    return partes[-1].strip() if len(partes) > 1 else str(nombre or "").strip()


def construir_casos_frontend(resultados, prefijo, categoria, entorno):
    """Arma los casos de una suite unitaria de frontend a partir de su JUnit XML."""
    casos = []
    contador = 0
    for resultado in resultados:
        contador += 1
        id_caso = f"{prefijo}-{contador:03d}"
        clave = clave_de_prueba(resultado["clase"])
        metodo = resultado["metodo"]
        descripcion = metodo.split(" > ")[-1] if " > " in metodo else titulo_de_prueba(metodo)
        suite = resultado.get("suite", "")
        if suite and descripcion.lower().startswith(suite.lower() + " "):
            descripcion = descripcion[len(suite) + 1:]
        evidencia_rel = f"{CARPETA_EVIDENCIAS}/FRONT-{id_caso}/captura.png"
        evidencia_abs = os.path.join(DESTINO_DIR, CARPETA_EVIDENCIAS,
                                      f"FRONT-{id_caso}", "captura.png")
        if not os.path.isfile(evidencia_abs):
            evidencia_rel = "Sin captura: la evidencia se genero en otra corrida"
            evidencia_abs = ""
        casos.append({
            "id": id_caso,
            "trazabilidad": TRAZABILIDAD_FRONTEND.get(clave, "RF / HU"),
            "descripcion": f"{clave}: {descripcion}",
            "tipo": "Unitaria",
            "tecnica": "Positivo y negativa",
            "precondiciones": (f"Archivo de prueba: {resultado['clase']}\n"
                                f"Suite unitaria en ejecucion local."),
            "datos": metodo,
            "pasos": PASOS_FRONTEND,
            "esperado": valor_esperado(descripcion, resultado["estado"]),
            "prioridad": "Alta",
            "real": resultado_real(resultado),
            "estado": resultado["estado"],
            "evidencia": evidencia_rel,
            "evidencia_abs": evidencia_abs,
            "categoria": categoria,
        })
    return casos


def construir_casos_backend(resultados, entorno):
    casos = []
    contador = {}
    for resultado in resultados:
        clase_completa = resultado["clase"]
        clase_simple = clase_completa.split(".")[-1]
        metodo = resultado["metodo"]
        descripcion = SplitCamelCase(metodo)
        clave = clase_simple.replace("Test", "")
        contador[clave] = contador.get(clave, 0) + 1
        id_caso = f"BE-{clave}-{contador[clave]:02d}"
        if clase_simple in SEGURIDAD_BACKEND:
            categoria = "Backend - Seguridad y autorizacion"
            tipo, tecnica = "Seguridad", "Negativa (RBAC)"
        elif clase_simple in CONTRATO_API_BACKEND:
            categoria = "Backend - Contrato de API (controladores)"
            tipo, tecnica = "Integración API", "Positivo y negativa"
        elif clase_simple == "AlLadoProjectApplicationTests":
            categoria = "Backend - Integracion con contexto Spring real"
            tipo, tecnica = "Integración", "Positivo"
        else:
            categoria = "Backend - Funcional (servicios de dominio)"
            tipo, tecnica = "Funcional", "Positivo y negativa"
        trazabilidad, pasos = AREA_BACKEND.get(clase_simple, ("RF / HU", "Ejecutar el metodo de prueba."))
        # La evidencia es la ficha de resultado real del caso, con su captura.
        evidencia_rel = f"{CARPETA_EVIDENCIAS}/BACK-{id_caso}/captura.png"
        evidencia_abs = os.path.join(DESTINO_DIR, CARPETA_EVIDENCIAS,
                                      f"BACK-{id_caso}", "captura.png")
        if not os.path.isfile(evidencia_abs):
            evidencia_rel = (f"Backend/surefire-reports/TEST-{clase_completa}.xml")
            evidencia_abs = ""
        casos.append({
            "id": id_caso,
            "trazabilidad": trazabilidad,
            "descripcion": f"{clase_simple}: {descripcion}",
            "tipo": tipo,
            "tecnica": tecnica,
            "precondiciones": (f"Codigo bajo prueba: {clase_completa}\n"
                               f"Perfil de ejecucion: {entorno['perfil_backend']}"),
            "datos": ("Fixtures y mocks definidos en la propia clase de prueba.\n"
                      "Base PostgreSQL de QA para los casos con contexto real."),
            "pasos": pasos,
            "esperado": valor_esperado(descripcion, resultado["estado"]),
            "prioridad": "Media",
            "real": resultado_real(resultado),
            "estado": resultado["estado"],
            "evidencia": evidencia_rel,
            "evidencia_abs": evidencia_abs,
            "categoria": categoria,
        })
    return casos


RUTA_RESULTADOS_API = f"{AL_LADO}/Testing/e2e/target/resultados_api.json"
RUTA_CATALOGO_API = f"{AL_LADO}/Testing/e2e/casos_api.json"


def leer_resultados_api():
    """Estado y evidencia de los casos de API ejecutados en la ultima corrida."""
    if not os.path.isfile(RUTA_RESULTADOS_API):
        return {}
    try:
        with open(RUTA_RESULTADOS_API, encoding="utf-8") as archivo:
            return json.load(archivo)
    except (OSError, ValueError):
        return {}


def leer_catalogo_api():
    """Definicion de los casos de API que aportan trazabilidad propia."""
    if not os.path.isfile(RUTA_CATALOGO_API):
        return {}
    try:
        with open(RUTA_CATALOGO_API, encoding="utf-8") as archivo:
            return json.load(archivo)["casos"]
    except (OSError, KeyError, ValueError):
        return {}


def construir_casos_catalogo(casos, entorno):
    """Casos de API definidos en el catalogo, con el resultado de la corrida."""
    resultados = leer_resultados_api()
    casos = []
    for id_caso, definicion in sorted(leer_catalogo_api().items()):
        if id_caso not in resultados:
            continue
        resultado = resultados[id_caso]
        evidencia_rel = f"{CARPETA_EVIDENCIAS}/API-{id_caso}/captura.png"
        evidencia_abs = os.path.join(DESTINO_DIR, CARPETA_EVIDENCIAS,
                                      f"API-{id_caso}", "captura.png")
        if not os.path.isfile(evidencia_abs):
            evidencia_rel = "Sin captura: la evidencia se genero en otra corrida"
            evidencia_abs = ""
        casos.append({
            "id": id_caso,
            "trazabilidad": definicion.get("trazabilidad", ""),
            "descripcion": definicion.get("titulo", id_caso),
            "tipo": definicion.get("tipo", "Integracion API"),
            "tecnica": definicion.get("tecnica", "Negativa"),
            "precondiciones": definicion.get("precondiciones", ""),
            "datos": definicion.get("datos", ""),
            "pasos": definicion.get("pasos", ""),
            "esperado": definicion.get("esperado", ""),
            "prioridad": definicion.get("prioridad", "Alta"),
            "real": f"Verificado en la corrida: {resultado['estado']}.",
            "estado": resultado["estado"],
            "evidencia": evidencia_rel,
            "evidencia_abs": evidencia_abs,
            "categoria": "API - Casos de la planilla ejecutados con Postman",
        })
    return casos


def construir_casos_planilla(casos):
    """Asigna categoria a los casos de la planilla segun su prefijo."""
    for caso in casos:
        if caso["id"].startswith("TC-DB"):
            caso["categoria"] = "Base de datos - Integracion y persistencia"
            prefijo = "DB"
        else:
            caso["categoria"] = "API - Casos de la planilla ejecutados con Postman"
            prefijo = "API"
        # La evidencia es la captura del intercambio HTTP real, re-ejecutado
        # contra el backend de QA.
        relativa = f"{CARPETA_EVIDENCIAS}/{prefijo}-{caso['id']}/captura.png"
        absoluta = os.path.join(DESTINO_DIR, CARPETA_EVIDENCIAS,
                                f"{prefijo}-{caso['id']}", "captura.png")
        if os.path.isfile(absoluta):
            caso["evidencia"] = relativa
            caso["evidencia_abs"] = absoluta
    return casos


def construir_pdf(casos, entorno, ruta_salida):
    from reportlab.lib.pagesizes import A4 as A4_P
    estilos = getSampleStyleSheet()
    titulo = ParagraphStyle("titulo", parent=estilos["Title"], fontSize=20,
                             spaceAfter=4, textColor=colors.HexColor("#1a1a2e"))
    subtitulo = ParagraphStyle("subtitulo", parent=estilos["Normal"], fontSize=11,
                                alignment=TA_CENTER, textColor=colors.HexColor("#444444"))
    seccion = ParagraphStyle("seccion", parent=estilos["Heading2"], fontSize=13,
                              spaceBefore=12, spaceAfter=6,
                              textColor=colors.HexColor("#1a1a2e"))
    cuerpo = ParagraphStyle("cuerpo", parent=estilos["Normal"], fontSize=9, leading=12)
    celda = ParagraphStyle("celda", parent=estilos["Normal"], fontSize=5.6, leading=6.6,
                           spaceAfter=0, spaceBefore=0)
    celda_estado = ParagraphStyle("celda_estado", parent=celda, alignment=TA_CENTER,
                                  fontSize=6, leading=7)
    celda_titulo = ParagraphStyle("celda_titulo", parent=celda, fontName="Helvetica-Bold")

    def par(valor, estilo=celda):
        texto = "" if valor is None else str(valor)
        texto = texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
        return Paragraph(texto.replace("\n", "<br/>"), estilo)

    story = []

    # ---------------- Portada ----------------
    story.append(Paragraph("AlLado - Informe de Resultados de Pruebas", titulo))
    story.append(Paragraph(
        "Plataforma deVinculo Laboral Local - Grupo D", subtitulo))
    story.append(Spacer(1, 8))
    story.append(Paragraph(
        f"Fecha de ejecucion: {entorno['fecha']}", subtitulo))
    story.append(Paragraph(
        f"Total de casos: {len(casos)} | Automatizados: "
        f"{sum(1 for c in casos if c['id'].startswith(('BE-', 'TC-L', 'TC-SEC', 'TC-COB', 'TC-NEG', 'TC-M', 'E2E')))}",
        subtitulo))
    story.append(Spacer(1, 14))

    # ---------------- Resumen ----------------
    story.append(Paragraph("1. Resumen por categoria", seccion))
    filas = [["Categoria", "Casos", "PASS", "FAIL", "ERROR", "SKIP", "% Aprobacion"]]
    for categoria, lista in CATEGORIAS.items():
        if not lista:
            continue
        pass_ = sum(1 for c in lista if c["estado"] == "PASS")
        fail = sum(1 for c in lista if c["estado"] == "FAIL")
        error = sum(1 for c in lista if c["estado"] == "ERROR")
        skip = sum(1 for c in lista if c["estado"] == "SKIP")
        total = len(lista)
        pct = 100.0 * pass_ / total if total else 0.0
        filas.append([categoria, str(total), str(pass_), str(fail), str(error),
                      str(skip), f"{pct:.1f}%"])
    total_p = sum(1 for c in casos if c["estado"] == "PASS")
    total_f = sum(1 for c in casos if c["estado"] == "FAIL")
    total_e = sum(1 for c in casos if c["estado"] == "ERROR")
    total_s = sum(1 for c in casos if c["estado"] == "SKIP")
    filas.append(["TOTAL", str(len(casos)), str(total_p), str(total_f), str(total_e),
                  str(total_s),
                  f"{100.0 * total_p / len(casos):.1f}%"])
    tabla_resumen = Table(filas, colWidths=[250, 45, 45, 45, 45, 45, 70])
    tabla_resumen.setStyle(TableStyle([
        ("BACKGROUND", (0, 0), (-1, 0), colors.HexColor("#1a1a2e")),
        ("TEXTCOLOR", (0, 0), (-1, 0), colors.white),
        ("FONTNAME", (0, 0), (-1, 0), "Helvetica-Bold"),
        ("FONTSIZE", (0, 0), (-1, -1), 7.5),
        ("GRID", (0, 0), (-1, -1), 0.4, colors.HexColor("#bbbbbb")),
        ("BACKGROUND", (0, 1), (-1, -1), colors.HexColor("#f6f6f8")),
        ("ALIGN", (1, 1), (-1, -1), "CENTER"),
        ("FONTNAME", (0, -1), (-1, -1), "Helvetica-Bold"),
        ("BACKGROUND", (0, -1), (-1, -1), colors.HexColor("#e3e3ea")),
        ("VALIGN", (0, 0), (-1, -1), "MIDDLE"),
        ("TOPPADDING", (0, 0), (-1, -1), 3),
        ("BOTTOMPADDING", (0, 0), (-1, -1), 3),
    ]))
    story.append(tabla_resumen)
    story.append(Spacer(1, 10))

    # ---------------- Defectos ----------------
    story.append(Paragraph("2. Defectos abiertos detectados", seccion))
    filas_defectos = [
        ["ID", "Defecto", "Origen", "Estado"],
        ["TC-POS-004",
         "El backend acepta remuneracion pretendida menor o igual a cero: la "
         "validacion @Positive no esta aplicada en PostulacionDTO.",
         "Planilla TC-POS-004 / caso E2E TC-NEG-004 deshabilitado", "Abierto"],
        ["TC-SEC-002",
         "El foro no filtra por barrio: una cuenta de otro sector ve las "
         "publicaciones de los demas (PublicacionForoServiceImpl.obtenerTodas "
         "devuelve todas sin filtrar por sector).",
         "Caso E2E TC-SEC-002 deshabilitado", "Abierto"],
        ["TC-DB-002",
         "La integridad referencial al asociar postulaciones no se valida en el "
         "origen de datos.",
         "Planilla TC-DB-002 (ejecucion previa con Postman)", "Abierto"],
    ]
    tabla_defectos = Table([[par(c, celda_titulo) for c in fila] for fila in filas_defectos],
                            colWidths=[62, 380, 260, 60])
    tabla_defectos.setStyle(TableStyle([
        ("BACKGROUND", (0, 0), (-1, 0), colors.HexColor("#8b1a1a")),
        ("TEXTCOLOR", (0, 0), (-1, 0), colors.white),
        ("GRID", (0, 0), (-1, -1), 0.4, colors.HexColor("#bbbbbb")),
        ("VALIGN", (0, 0), (-1, -1), "TOP"),
        ("TOPPADDING", (0, 0), (-1, -1), 3),
        ("BOTTOMPADDING", (0, 0), (-1, -1), 3),
    ]))
    story.append(tabla_defectos)
    story.append(Spacer(1, 10))

    # ---------------- Entorno ----------------
    story.append(Paragraph("3. Entorno de ejecucion", seccion))
    filas_entorno = [["Componente", "Detalle"]]
    for clave, valor in entorno.items():
        if clave == "fecha":
            continue
        filas_entorno.append([clave.replace("_", " ").capitalize(), str(valor)])
    tabla_entorno = Table([[par(c, celda) for c in fila] for fila in filas_entorno],
                           colWidths=[140, 622])
    tabla_entorno.setStyle(TableStyle([
        ("BACKGROUND", (0, 0), (-1, 0), colors.HexColor("#1a1a2e")),
        ("TEXTCOLOR", (0, 0), (-1, 0), colors.white),
        ("GRID", (0, 0), (-1, -1), 0.4, colors.HexColor("#bbbbbb")),
        ("VALIGN", (0, 0), (-1, -1), "TOP"),
        ("TOPPADDING", (0, 0), (-1, -1), 2),
        ("BOTTOMPADDING", (0, 0), (-1, -1), 2),
    ]))
    story.append(tabla_entorno)
    story.append(PageBreak())

    # ---------------- Tablas por categoria ----------------
    story.append(Paragraph("4. Tabla general de resultados por tipo de prueba", seccion))
    story.append(Paragraph(
        "Formato de la planilla oficial de test cases. Cada categoria se presenta "
        "en su propia tabla, con los mismos trece campos.", cuerpo))
    story.append(Spacer(1, 6))

    colores_estado = {
        "PASS": colors.HexColor("#e6f4ea"),
        "FAIL": colors.HexColor("#fdecea"),
        "ERROR": colors.HexColor("#fdecea"),
        "SKIP": colors.HexColor("#fef7e0"),
    }

    for categoria, lista in CATEGORIAS.items():
        if not lista:
            continue
        story.append(Paragraph(categoria, seccion))
        filas = [[par(c, celda_titulo) for c in COLUMNAS]]
        estilos_fila = [
            ("BACKGROUND", (0, 0), (-1, 0), colors.HexColor("#1a1a2e")),
            ("TEXTCOLOR", (0, 0), (-1, 0), colors.white),
            ("GRID", (0, 0), (-1, -1), 0.35, colors.HexColor("#cccccc")),
            ("VALIGN", (0, 0), (-1, -1), "TOP"),
            ("TOPPADDING", (0, 0), (-1, -1), 2),
            ("BOTTOMPADDING", (0, 0), (-1, -1), 2),
            ("FONTSIZE", (0, 0), (-1, -1), 5.6),
        ]
        for indice, caso in enumerate(lista, start=1):
            valores = [caso["id"], caso["trazabilidad"], caso["descripcion"],
                       caso["tipo"], caso["tecnica"], caso["precondiciones"],
                       caso["datos"], caso["pasos"], caso["esperado"],
                       caso["prioridad"], caso["real"], caso["estado"],
                       caso["evidencia"]]
            filas.append([par(v) for v in valores[:11]] + [par(caso["estado"], celda_estado)]
                         + [par(caso["evidencia"])])
            fondo = colores_estado.get(caso["estado"], colors.white)
            estilos_fila.append(("BACKGROUND", (11, indice), (11, indice), fondo))
            estilos_fila.append(("ALIGN", (11, indice), (11, indice), "CENTER"))
            if indice % 2 == 0:
                estilos_fila.append(("BACKGROUND", (0, indice), (10, indice),
                                     colors.HexColor("#fafafc")))
                estilos_fila.append(("BACKGROUND", (12, indice), (12, indice),
                                     colors.HexColor("#fafafc")))
        tabla = Table(filas, colWidths=ANCHOS, repeatRows=1)
        tabla.setStyle(TableStyle(estilos_fila))
        story.append(tabla)
        story.append(Spacer(1, 10))

    # ---------------- Anexo: capturas de pantalla ----------------
    casos_con_captura = [c for c in casos
                         if c.get("evidencia_abs") and os.path.isfile(c["evidencia_abs"])]
    if casos_con_captura:
        story.append(PageBreak())
        story.append(Paragraph("5. Anexo: capturas de pantalla por caso", seccion))
        story.append(Paragraph(
            "Una pagina por caso de la suite E2E, con la captura que quedo "
            "registrada durante la ejecucion. Es la evidencia que el plan de "
            "pruebas exige linkear por caso (seccion 9 de Testing.md).", cuerpo))
        story.append(Spacer(1, 6))

        for caso in casos_con_captura:
            story.append(PageBreak())
            story.append(Paragraph(f"{caso['id']} - {caso['descripcion']}", seccion))
            story.append(Paragraph(
                f"Trazabilidad: {caso['trazabilidad']} | Tipo: {caso['tipo']} | "
                f"Prioridad: {caso['prioridad']} | Estado: {caso['estado']}", cuerpo))
            story.append(Paragraph(
                f"Resultado real: {caso['real']}", cuerpo))
            story.append(Spacer(1, 4))
            try:
                from reportlab.platypus import Image as PdfImage
                imagen = PdfImage(caso["evidencia_abs"])
                ancho_disponible = landscape(A4)[0] - 24 * mm
                alto_disponible = landscape(A4)[1] - 120
                escala = min(ancho_disponible / imagen.imageWidth,
                              alto_disponible / imagen.imageHeight)
                imagen.drawWidth = imagen.imageWidth * escala
                imagen.drawHeight = imagen.imageHeight * escala
                story.append(imagen)
            except Exception as exc:
                story.append(Paragraph(
                    f"No se pudo incrustar la captura: {exc}", cuerpo))
            story.append(Spacer(1, 4))
            story.append(Paragraph(
                f"Archivo: {caso['evidencia']}", cuerpo))

    # ---------------- Pie de pagina ----------------
    def pie(canvas, doc):
        canvas.saveState()
        canvas.setFont("Helvetica", 7)
        canvas.setFillColor(colors.HexColor("#666666"))
        canvas.drawString(12 * mm, 8 * mm,
                          "AlLado - Informe de Resultados de Pruebas | Generado el "
                          + entorno["fecha"])
        canvas.drawRightString(landscape(A4)[0] - 12 * mm, 8 * mm, "Pagina %d" % doc.page)
        canvas.restoreState()

    documento = BaseDocTemplate(
        ruta_salida, pagesize=landscape(A4),
        leftMargin=10 * mm, rightMargin=10 * mm, topMargin=10 * mm, bottomMargin=12 * mm,
        title="AlLado - Informe de Resultados de Pruebas",
        author="Equipo QA AlLado")
    marco = Frame(documento.leftMargin, documento.bottomMargin,
                  documento.width, documento.height, id="normal")
    documento.addPageTemplates([PageTemplate(id="todos", frames=[marco], onPage=pie)])
    documento.build(story)
    return ruta_salida


def construir_xml(casos, entorno, ruta_salida):
    """Exporta los resultados en XML con formato de suite de pruebas (JUnit)."""
    import xml.etree.ElementTree as ET

    raiz = ET.Element("testsuites", {
        "name": "AlLado",
        "tests": str(len(casos)),
        "failures": str(sum(1 for c in casos if c["estado"] == "FAIL")),
        "errors": str(sum(1 for c in casos if c["estado"] == "ERROR")),
        "skipped": str(sum(1 for c in casos if c["estado"] == "SKIP")),
        "timestamp": entorno["fecha"],
    })

    for categoria, lista in CATEGORIAS.items():
        if not lista:
            continue
        suite = ET.SubElement(raiz, "testsuite", {
            "name": categoria,
            "tests": str(len(lista)),
            "failures": str(sum(1 for c in lista if c["estado"] == "FAIL")),
            "errors": str(sum(1 for c in lista if c["estado"] == "ERROR")),
            "skipped": str(sum(1 for c in lista if c["estado"] == "SKIP")),
        })
        for caso in lista:
            nodo = ET.SubElement(suite, "testcase", {
                "name": caso["id"],
                "classname": caso["categoria"],
                "descripcion": caso["descripcion"],
                "trazabilidad": caso["trazabilidad"],
                "tipo": caso["tipo"],
                "tecnica": caso["tecnica"],
                "prioridad": caso["prioridad"],
                "estado": caso["estado"],
            })
            ET.SubElement(nodo, "precondiciones").text = caso["precondiciones"]
            ET.SubElement(nodo, "datosEntrada").text = caso["datos"]
            ET.SubElement(nodo, "pasosEjecucion").text = caso["pasos"]
            ET.SubElement(nodo, "resultadoEsperado").text = caso["esperado"]
            ET.SubElement(nodo, "resultadoReal").text = caso["real"]
            ET.SubElement(nodo, "evidencia").text = caso["evidencia"]
            if caso.get("evidencia_abs"):
                ET.SubElement(nodo, "evidenciaRutaAbsoluta").text = caso["evidencia_abs"]
            if caso["estado"] in ("FAIL", "ERROR"):
                ET.SubElement(nodo, "failure", {"message": caso["real"][:300]})
            elif caso["estado"] == "SKIP":
                ET.SubElement(nodo, "skipped", {"message": caso["real"][:300]})

    ET.indent(raiz, space="  ")
    arbol = ET.ElementTree(raiz)
    arbol.write(ruta_salida, encoding="utf-8", xml_declaration=True)
    return ruta_salida


def construir_xlsx(casos, entorno, ruta_salida):
    """Exporta los resultados en hoja de calculo con el formato de la planilla."""
    import openpyxl
    from openpyxl.drawing.image import Image as XLImage
    from openpyxl.styles import Alignment, Font, PatternFill
    from openpyxl.utils import get_column_letter

    libro = openpyxl.Workbook()

    # --- Hoja de resumen ---
    hoja = libro.active
    hoja.title = "Resumen"
    hoja["A1"] = "AlLado - Informe de Resultados de Pruebas"
    hoja["A1"].font = Font(bold=True, size=14)
    hoja["A2"] = f"Fecha de ejecucion: {entorno['fecha']}"
    filas = [["Categoria", "Casos", "PASS", "FAIL", "ERROR", "SKIP", "% Aprobacion"]]
    for categoria, lista in CATEGORIAS.items():
        if not lista:
            continue
        pass_ = sum(1 for c in lista if c["estado"] == "PASS")
        total = len(lista)
        filas.append([
            categoria, total, pass_,
            sum(1 for c in lista if c["estado"] == "FAIL"),
            sum(1 for c in lista if c["estado"] == "ERROR"),
            sum(1 for c in lista if c["estado"] == "SKIP"),
            round(100.0 * pass_ / total, 1),
        ])
    filas.append([
        "TOTAL", len(casos),
        sum(1 for c in casos if c["estado"] == "PASS"),
        sum(1 for c in casos if c["estado"] == "FAIL"),
        sum(1 for c in casos if c["estado"] == "ERROR"),
        sum(1 for c in casos if c["estado"] == "SKIP"),
        round(100.0 * sum(1 for c in casos if c["estado"] == "PASS") / len(casos), 1),
    ])
    for fila in filas:
        hoja.append(fila)
    for celda in hoja[4]:
        celda.font = Font(bold=True, color="FFFFFF")
        celda.fill = PatternFill("solid", fgColor="1A1A2E")
    for columna in hoja.columns:
        hoja.column_dimensions[get_column_letter(columna[0].column)].width = \
            min(max(len(str(c.value or "")) for c in columna) + 2, 70)

    # --- Una hoja por categoria, con las 13 columnas de la planilla ---
    for categoria, lista in CATEGORIAS.items():
        if not lista:
            continue
        hoja = libro.create_sheet(categoria[:31])
        hoja.append(COLUMNAS)
        for celda in hoja[1]:
            celda.font = Font(bold=True, color="FFFFFF")
            celda.fill = PatternFill("solid", fgColor="1A1A2E")
            celda.alignment = Alignment(vertical="center", wrap_text=True)
        for caso in lista:
            hoja.append([
                caso["id"], caso["trazabilidad"], caso["descripcion"], caso["tipo"],
                caso["tecnica"], caso["precondiciones"], caso["datos"], caso["pasos"],
                caso["esperado"], caso["prioridad"], caso["real"], caso["estado"],
                caso["evidencia"],
            ])
        # La captura se incrusta dentro de la celda "Evidencia": el archivo se ve
        # sin tener que abrir nada ni resolver rutas.
        for indice, caso in enumerate(lista, start=2):
            ruta = caso.get("evidencia_abs")
            if not ruta or not os.path.isfile(ruta):
                continue
            try:
                imagen = XLImage(ruta)
            except Exception:
                continue
            escala = min(1.0, 300 / imagen.width)
            imagen.width = int(imagen.width * escala)
            imagen.height = int(imagen.height * escala)
            hoja.add_image(imagen, f"M{indice}")
            hoja.row_dimensions[indice].height = imagen.height * 0.78
        for fila in hoja.iter_rows(min_row=2):
            for celda in fila:
                celda.alignment = Alignment(vertical="top", wrap_text=True)
        for indice, caso in enumerate(lista, start=2):
            color = {"PASS": "E6F4EA", "FAIL": "FDECEA", "ERROR": "FDECEA",
                     "SKIP": "FEF7E0"}.get(caso["estado"])
            if color:
                hoja.cell(row=indice, column=12).fill = PatternFill("solid", fgColor=color)
        anchos = [16, 22, 38, 16, 18, 40, 40, 40, 40, 11, 40, 9, 34]
        for indice, ancho in enumerate(anchos, start=1):
            hoja.column_dimensions[get_column_letter(indice)].width = ancho
        hoja.column_dimensions["M"].width = 42
        hoja.freeze_panes = "A2"
        # Impresion: apaisado, una hoja de ancho y encabezado repetido.
        hoja.page_setup.orientation = "landscape"
        hoja.page_setup.fitToWidth = 1
        hoja.page_setup.fitToHeight = 0
        hoja.sheet_properties.pageSetUpPr.fitToPage = True
        hoja.print_title_rows = "1:1"

    libro.save(ruta_salida)
    return ruta_salida


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--salida", default=os.path.join(
        DESTINO_DIR, "AlLado - Informe de Resultados de Pruebas.pdf"))
    args = parser.parse_args()

    entorno = {
        "fecha": datetime.datetime.now().strftime("%d/%m/%Y %H:%M"),
        "frontend": os.environ.get("E2E_BASE_URL", "http://localhost:3001"),
        "backend": os.environ.get("E2E_API_URL", "http://localhost:8081"),
        "bd": "jdbc:postgresql://localhost:5433/al_lado_db (dump del proyecto)",
        "perfil_backend": "spring.profiles.active=e2e",
        "navegador": os.environ.get(
            "E2E_BROWSER", "chrome").capitalize() + " (Chrome for Testing, headless)",
        "suite_e2e": "Selenium 4.49 + JUnit 5.13 (Testing/e2e)",
        "suite_backend": "JUnit 5.13 + Spring Boot Test 3.2.5 (Backend)",
    }

    casos = []

    resultados_e2e = leer_surefire(f"{AL_LADO}/Testing/e2e/target/surefire-reports")
    display = leer_display_names(f"{AL_LADO}/Testing/e2e/src/test/java")
    casos_e2e = construir_casos_e2e(resultados_e2e, display, entorno)

    resultados_back = leer_surefire(f"{AL_LADO}/Backend/target/surefire-reports")
    casos_back = construir_casos_backend(resultados_back, entorno)

    # La planilla oficial ya contiene (tras `actualizar_planilla.py`) los casos de
    # E2E y de regresion del backend. Al informe solo se le agregan los que aporta
    # ella y que no cubren ya los otros dos bloques, para no duplicar filas.
    # La comparacion ignora el sufijo de variante (a, b, c...) que agrega el
    # passage de unicidad mas abajo.
    casos += construir_casos_frontend(
        leer_junit(f"{AL_LADO}/Frontend/al_lado-app/test-results/junit.xml"),
        "FEW", "Frontend web - Pruebas unitarias (Vitest)", entorno)
    casos += construir_casos_frontend(
        leer_junit(f"{AL_LADO}/Frontend/al-lado-mobile/test-results/junit.xml"),
        "FEM", "Frontend movil - Pruebas unitarias (jest-expo)", entorno)

    casos_catalogo = construir_casos_catalogo(casos, entorno)

    ya_cubiertos_clave = {clave_caso(caso) for caso in casos_e2e}
    ya_cubiertos_clave |= {clave_caso(caso) for caso in casos_back}
    ya_cubiertos_clave |= {clave_caso(caso) for caso in casos if caso["id"].startswith("FE")}
    ya_cubiertos_id = {id_base(caso["id"]) for caso in casos_e2e}
    ya_cubiertos_id |= {id_base(caso["id"]) for caso in casos_back}
    ya_cubiertos_id |= {id_base(caso["id"]) for caso in casos if caso["id"].startswith("FE")}
    ya_cubiertos_id |= {v for caso in casos_e2e for v in variantes_de_id(caso["id"])}
    ya_cubiertos_id |= {v for caso in casos_back for v in variantes_de_id(caso["id"])}
    ya_cubiertos_id |= {v for caso in casos if caso["id"].startswith("FE")
                        for v in variantes_de_id(caso["id"])}
    ya_cubiertos_clave |= {clave_caso(caso) for caso in casos_catalogo}
    ya_cubiertos_id |= {v for caso in casos_catalogo for v in variantes_de_id(caso["id"])}
    casos += construir_casos_planilla(
        [caso for caso in leer_planilla()
         if clave_caso(caso) not in ya_cubiertos_clave
         and not (variantes_de_id(caso["id"]) & ya_cubiertos_id)]
    )
    casos += casos_catalogo
    casos += casos_e2e
    casos += casos_back

    for caso in casos:
        CATEGORIAS[caso["categoria"]].append(caso)

    # Control de unicidad del identificador de caso. Un mismo TC del plan puede
    # cubrir varias verificaciones: se las distingue con un sufijo (a, b, c...)
    # para que cada fila de la tabla sea un caso identificable.
    por_id = OrderedDict()
    for caso in casos:
        por_id.setdefault(caso["id"], []).append(caso)
    for id_caso, grupo in por_id.items():
        if len(grupo) == 1:
            continue
        for indice, caso in enumerate(grupo):
            sufijo = chr(ord("a") + indice)
            caso["id"] = f"{id_caso}{sufijo}"
            caso["descripcion"] = f"{caso['descripcion']} [variante {sufijo.upper()}]"

    os.makedirs(os.path.dirname(args.salida), exist_ok=True)
    copiar_evidencias(os.path.dirname(args.salida))
    base = os.path.splitext(args.salida)[0]
    construir_pdf(casos, entorno, args.salida)
    construir_xml(casos, entorno, base + ".xml")
    construir_xlsx(casos, entorno, base + ".xlsx")

    total_p = sum(1 for c in casos if c["estado"] == "PASS")
    print(f"PDF  generado: {args.salida}")
    print(f"XML  generado: {base}.xml")
    print(f"XLSX generado: {base}.xlsx")
    print(f"Casos totales: {len(casos)} | PASS: {total_p} | "
          f"FAIL: {sum(1 for c in casos if c['estado'] == 'FAIL')} | "
          f"ERROR: {sum(1 for c in casos if c['estado'] == 'ERROR')} | "
          f"SKIP: {sum(1 for c in casos if c['estado'] == 'SKIP')}")


if __name__ == "__main__":
    main()
