#!/usr/bin/env python3
"""Genera la evidencia visual de los casos que no son de interfaz.

Cubre tres bloques:

  * API / Base de datos: re-ejecuta cada caso de la planilla oficial contra el
    backend de QA y captura el intercambio HTTP real (peticion, codigo de
    estado y cuerpo de respuesta), que es la "Captura Postman" que pide el plan
    de pruebas.
  * Backend: arma una ficha por cada caso de la suite JUnit con su resultado real
    (estado, duracion y reporte de Surefire).

Uso:  python3 generar_evidencias.py
"""
import datetime
import json
import os
import sys
import urllib.error
import urllib.parse
import urllib.request
import xml.etree.ElementTree as ET

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import evidencias_util as ev  # noqa: E402

API = os.environ.get("E2E_API_URL", "http://localhost:8081")
AL_LADO = "/home/Alfredo/Documentos/AlLado/AlLado"
# La planilla vive en el repositorio (artefacto versionado). La copia de
# Descargas se usa solo como respaldo si el repo no la tiene.
PLANILLA_REPO = f"{AL_LADO}/Testing/Planilla de Test Cases v2(2).xlsx"
PLANILLA_DESCARGAS = "/home/Alfredo/Descargas/AlLado - Planilla de Test Cases v2(2).xlsx"
PLANILLA = PLANILLA_REPO if os.path.isfile(PLANILLA_REPO) else PLANILLA_DESCARGAS

SUFIXO = datetime.datetime.now().strftime("%d/%m/%Y %H:%M")

CUENTAS = {
    "trabajador": ("trabajador@demo.com", "Clave123!"),
    "empleador": ("empleador@demo.com", "Clave123!"),
    "admin": ("admin@allado.com", "Clave123!"),
}

_cache_tokens = {}


def token(rol):
    if rol not in _cache_tokens:
        correo, clave = CUENTAS[rol]
        datos = json.dumps({"email": correo, "password": clave}).encode()
        peticion = urllib.request.Request(
            API + "/auth/login", data=datos,
            headers={"Content-Type": "application/json"}, method="POST")
        with urllib.request.urlopen(peticion) as respuesta:
            _cache_tokens[rol] = json.loads(respuesta.read())["token"]
    return _cache_tokens[rol]


def llamar(metodo, ruta, cuerpo=None, rol=None, token_directo=None):
    """Ejecuta una llamada y devuelve (peticion, respuesta, cuerpo)."""
    url = API + ruta
    cuerpo_texto = json.dumps(cuerpo, ensure_ascii=False, indent=2) if cuerpo is not None else ""
    datos = cuerpo_texto.encode() if cuerpo is not None else None
    cabeceras = {"Content-Type": "application/json"}
    if token_directo:
        cabeceras["Authorization"] = "Bearer " + token_directo
    elif rol:
        cabeceras["Authorization"] = "Bearer " + token(rol)
    peticion = urllib.request.Request(url, data=datos, headers=cabeceras, method=metodo)
    try:
        with urllib.request.urlopen(peticion) as respuesta:
            texto = respuesta.read().decode("utf-8", "replace")
            estado = respuesta.status
    except urllib.error.HTTPError as error:
        texto = error.read().decode("utf-8", "replace")
        estado = error.code
    except urllib.error.URLError as error:
        texto = f"ERROR DE CONEXION: {error}"
        estado = 0
    encabezado = f"{metodo} {ruta}"
    if token_directo:
        encabezado += "\nAuthorization: Bearer <token_propio>"
    elif rol:
        encabezado += f"\nAuthorization: Bearer <token_{rol}>"
    if cuerpo is not None:
        encabezado += f"\nContent-Type: application/json"
    return encabezado + "\n\n" + cuerpo_texto, estado, texto


CATALOGO_API = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "casos_api.json")


def catalogo_api():
    """Metadatos estaticos de los casos de API (compartidos con la planilla)."""
    try:
        with open(CATALOGO_API, encoding="utf-8") as archivo:
            return json.load(archivo)["casos"]
    except (OSError, KeyError, json.JSONDecodeError):
        return {}


def obtener_oportunidades():
    _, estado, texto = llamar("GET", "/oportunidades")
    if estado != 200:
        return []
    try:
        return json.loads(texto)
    except json.JSONDecodeError:
        return []


def id_valido(oportunidades, con_postulacion):
    """Busca una oportunidad activa segun tenga o no postulaciones previas."""
    for oportunidad in oportunidades:
        if not oportunidad.get("activa"):
            continue
        tiene = bool(oportunidad.get("postulaciones"))
        if tiene == con_postulacion:
            return oportunidad["id"]
    return None


def id_de_rol(rol):
    """Identificador del usuario del rol, segun la respuesta del login.

    El listado publico ya no expone los emails (AL-SEC-006), asi que la identidad
    se toma de `POST /auth/login`, que devuelve el id del usuario (AL-API-005).
    """
    _, _, texto = llamar("POST", "/auth/login",
                         {"email": CUENTAS[rol][0], "password": CUENTAS[rol][1]})
    try:
        return json.loads(texto)["id"]
    except (json.JSONDecodeError, KeyError, TypeError):
        return None


def id_trabajador():
    return id_de_rol("trabajador") or 3


def registrar_empleador(marca):
    """Alta de un empleador temporal.

    Devuelve (id, token, peticion, estado, respuesta). Se usa en los casos que
    necesitan un tercero o una cuota mensual en cero, para no depender del estado
    que dejo la corrida anterior.
    """
    # El correo lleva la marca de tiempo: cada corrida usa una cuenta nueva y no
    # choca con el alta de la corrida anterior.
    correo = f"qa-{marca}-{datetime.datetime.now().strftime('%H%M%S')}@demo.com"
    peticion, estado, texto = llamar(
        "POST", "/auth/register",
        {"nombre": f"QA {marca}", "email": correo, "password": "Clave123!",
         "telefono": "+5491100000000", "rol": "EMPLEADOR"}, None)
    try:
        datos = json.loads(texto)
        return datos["id"], datos["token"], peticion, estado, texto
    except (json.JSONDecodeError, KeyError, TypeError):
        return None, None, peticion, estado, texto


def leer_planilla():
    """Lee la planilla oficial para contrastar esperado y estado planificado."""
    import openpyxl
    ruta = PLANILLA
    if not os.path.isfile(ruta):
        return {}
    libro = openpyxl.load_workbook(ruta, data_only=True)
    hoja = libro["AlLado - Planilla de Test Cases"]
    casos = {}
    for fila in range(2, hoja.max_row + 1):
        id_caso = hoja.cell(row=fila, column=1).value
        if not id_caso:
            continue
        casos[str(id_caso)] = {
            "esperado": str(hoja.cell(row=fila, column=9).value or ""),
            "estado": str(hoja.cell(row=fila, column=12).value or ""),
            "real": str(hoja.cell(row=fila, column=11).value or ""),
        }
    return casos


def crear_oportunidad(titulo):
    cuerpo = {
        "titulo": titulo,
        "contenido": "Tarea creada por la generacion automatica de evidencias.",
        "ubicacion": "Centro",
        "modalidad": "PRESENCIAL",
        "rangoSalarial": "80000-120000",
        "activa": True,
        "destacada": False,
        "autor": {"id": 2, "nombre": "Demo Empleador", "email": "empleador@demo.com"},
    }
    peticion, estado, texto = llamar("POST", "/oportunidades", cuerpo, "empleador")
    try:
        return json.loads(texto)["id"], peticion, estado, texto
    except (json.JSONDecodeError, KeyError, TypeError):
        return None, peticion, estado, texto


# ---------------------------------------------------------------------------
# Definicion de los casos de la planilla
# ---------------------------------------------------------------------------
def casos_api():
    oportunidades = obtener_oportunidades()
    id_trab = id_trabajador()
    id_libre = id_valido(oportunidades, False)
    primera = oportunidades[0]["id"] if oportunidades else 1

    # Los casos que necesitan un estado limpio crean su propia oportunidad, para
    # que la evidencia sea reproducible aunque la base ya tenga postulaciones.
    marca = datetime.datetime.now().strftime("%H%M%S")
    id_pos001, _, _, _ = crear_oportunidad(f"Oportunidad para TC-POS-001 {marca}")
    id_pos002, _, _, _ = crear_oportunidad(f"Oportunidad para TC-POS-002 {marca}")
    id_pos004, _, _, _ = crear_oportunidad(f"Oportunidad para TC-POS-004 {marca}")
    id_pub007, _, _, _ = crear_oportunidad(f"Oportunidad para TC-PUB-007 {marca}")

    casos = []

    catalogo = catalogo_api()

    def agregar(id_caso, titulo, metodo, ruta, cuerpo, rol, nota="", preparacion=None,
               trazabilidad="", tecnica="Negativa", tipo="Integración API",
               precondiciones="", datos="", pasos="", esperado="", prioridad="Alta",
               comparar_preparacion=False):
        # Los casos del catalogo traen su definicion completa: se usa la que este,
        # para que la planilla y el ejecutor no diverjan.
        definicion = catalogo.get(id_caso, {})
        casos.append(dict(
            id=id_caso, titulo=titulo, metodo=metodo, ruta=ruta,
            cuerpo=cuerpo, rol=rol, nota=nota, preparacion=preparacion,
            trazabilidad=definicion.get("trazabilidad", trazabilidad),
            tecnica=definicion.get("tecnica", tecnica),
            tipo=definicion.get("tipo", tipo),
            precondiciones=definicion.get("precondiciones", precondiciones),
            datos=definicion.get("datos", datos),
            pasos=definicion.get("pasos", pasos),
            esperado=definicion.get("esperado", esperado),
            prioridad=definicion.get("prioridad", prioridad),
            comparar_preparacion=comparar_preparacion))

    agregar("TC-POS-001", "Postulacion exitosa a una oportunidad laboral activa",
            "POST", "/postulaciones",
            {"titulo": "Postulacion a tarea de pintura", "contenido": "Puedo realizar la tarea.",
             "remuneracionPretendida": 15000.0,
             "oportunidadLaboral": {"id": id_pos001}, "autor": {"id": id_trab}},
            "trabajador", f"oportunidad propia {id_pos001}, sin postulacion previa")

    agregar("TC-POS-002", "Postulacion duplicada a la misma oportunidad",
            "POST", "/postulaciones",
            {"titulo": "Postulacion a tarea de pintura", "contenido": "Segundo intento.",
             "remuneracionPretendida": 15000.0,
             "oportunidadLaboral": {"id": id_pos002}, "autor": {"id": id_trab}},
            "trabajador", f"oportunidad propia {id_pos002}: la primera postulacion "
                          f"se creo al preparar el caso",
            preparacion=("Preparacion: primera postulacion (debe devolver HTTP 201)",
                         "POST", "/postulaciones",
                         {"titulo": "Postulacion a tarea de pintura",
                          "contenido": "Primera propuesta.",
                          "remuneracionPretendida": 15000.0,
                          "oportunidadLaboral": {"id": id_pos002},
                          "autor": {"id": id_trab}}, "trabajador"))

    agregar("TC-POS-003", "Intento de postulacion sin autenticacion (Token ausente)",
            "POST", "/postulaciones",
            {"titulo": "Postulacion sin token", "contenido": "Sin sesion.",
             "remuneracionPretendida": 15000.0,
             "oportunidadLaboral": {"id": primera}, "autor": {"id": id_trab}},
            None, "sin cabecera Authorization")

    agregar("TC-POS-004", "Validacion de valor limite en remuneracion (Monto negativo)",
            "POST", "/postulaciones",
            {"titulo": "Postulacion con monto negativo", "contenido": "Monto -15000.",
             "remuneracionPretendida": -15000.0,
             "oportunidadLaboral": {"id": id_pos004}, "autor": {"id": id_trab}},
            "trabajador", f"oportunidad propia {id_pos004}; el requerimiento pide HTTP 400")

    agregar("TC-POS-005", "Postulacion a una oportunidad laboral inexistente",
            "POST", "/postulaciones",
            {"titulo": "Postulacion a oportunidad inexistente", "contenido": "ID 9999.",
             "remuneracionPretendida": 15000.0,
             "oportunidadLaboral": {"id": 9999}, "autor": {"id": id_trab}},
            "trabajador", "oportunidad ID 9999 inexistente")

    agregar("TC-POS-006", "Restriccion de postulacion para rol no autorizado (EMPLEADOR)",
            "POST", "/postulaciones",
            {"titulo": "Postulacion con rol empleador", "contenido": "Rol no habilitado.",
             "remuneracionPretendida": 15000.0,
             "oportunidadLaboral": {"id": primera}, "autor": {"id": 2}},
            "empleador", "se espera HTTP 403 por RBAC")

    agregar("TC-POS-007", "Consulta y listado general de postulaciones registradas",
            "GET", "/postulaciones", None, "trabajador")

    agregar("TC-PUB-001", "Creacion exitosa de oportunidad laboral activa",
            "POST", "/oportunidades",
            {"titulo": "Reparacion electrica - evidencia QA",
             "contenido": "Tarea creada para la evidencia del plan de pruebas.",
             "ubicacion": "Centro", "modalidad": "PRESENCIAL",
             "rangoSalarial": "25000-30000", "activa": True, "destacada": False,
             "autor": {"id": 2, "nombre": "Demo Empleador", "email": "empleador@demo.com"}},
            "empleador", "se espera HTTP 201")

    agregar("TC-PUB-002", "Rechazo de creacion de oportunidad con rol de trabajador",
            "POST", "/oportunidades",
            {"titulo": "Oportunidad con rol trabajador", "contenido": "No permitido.",
             "ubicacion": "Centro", "modalidad": "PRESENCIAL", "rangoSalarial": "10000",
             "activa": True, "destacada": False,
             "autor": {"id": id_trab, "nombre": "Demo Trabajador", "email": "trabajador@demo.com"}},
            "trabajador", "se espera HTTP 403")

    agregar("TC-PUB-003", "Listado general de oportunidades laborales",
            "GET", "/oportunidades", None, None)

    agregar("TC-PUB-004", "Consulta puntual de oportunidad laboral por ID existente",
            "GET", f"/oportunidades/{primera}", None, None,
            f"oportunidad existente ID {primera}")

    agregar("TC-PUB-005", "Manejo de error al consultar oportunidad inexistente",
            "GET", "/oportunidades/99999", None, None, "ID 99999 inexistente")

    agregar("TC-PUB-006", "Busqueda y filtrado de oportunidades por termino de texto",
            "GET", "/oportunidades?filtro=" + urllib.parse.quote("eléctrica"),
            None, None, "termino 'electrica'")

    agregar("TC-PUB-007", "Actualizacion exitosa de oportunidad laboral existente",
            "PUT", f"/oportunidades/{id_pub007}",
            {"titulo": "Reparacion electrica urgente - Modificada",
             "contenido": "Oportunidad actualizada por la evidencia del plan de pruebas.",
             "ubicacion": "Centro", "modalidad": "PRESENCIAL",
             "rangoSalarial": "32000-40000", "activa": True, "destacada": False,
             "autor": {"id": 2, "nombre": "Demo Empleador", "email": "empleador@demo.com"}},
            "empleador", f"oportunidad propia {id_pub007}; se espera HTTP 200")

    agregar("TC-DB-001", "Rechazo de payload incompleto en creacion de postulacion",
            "POST", "/postulaciones",
            {"oportunidadId": primera, "mensaje": "...",
             "remuneracionPretendida": 15000.0},
            "trabajador", "el DTO exige titulo, contenido, autor y oportunidad")

    agregar("TC-DB-002", "Validacion de integridad referencial con categoria inexistente",
            "POST", "/oportunidades",
            {"titulo": "Oportunidad con categoria inexistente", "contenido": "Etiqueta 99999.",
             "ubicacion": "Centro", "modalidad": "PRESENCIAL", "rangoSalarial": "10000",
             "activa": True, "destacada": False,
             "etiquetas": [{"id": 99999, "nombre": "Inexistente"}],
             "autor": {"id": 2, "nombre": "Demo Empleador", "email": "empleador@demo.com"}},
            "empleador", "referencia a etiqueta ID 99999 inexistente")

    agregar("TC-DB-003", "Validacion de constraint UNIQUE en correo de usuario",
            "POST", "/auth/register",
            {"nombre": "Usuario Duplicado", "email": "empleador@demo.com",
             "password": "Clave123!", "telefono": "3571123456", "rol": "EMPLEADOR"},
            None, "email ya registrado")

    agregar("TC-DB-004", "Rechazo de persistencia ante campos NOT NULL vacios",
            "POST", "/oportunidades",
            {"titulo": "", "contenido": "", "ubicacion": "", "modalidad": "",
             "rangoSalarial": "", "activa": True, "destacada": False,
             "autor": {"id": 2, "nombre": "Demo Empleador", "email": "empleador@demo.com"}},
            "empleador", "titulo y contenido vacios")

    # =====================================================================
    # Semana 1: cierres de seguridad (AL-SEC-004/005/006/007 y AL-API-005).
    # Verifican el contrato HTTP de lo que se corrigio.
    # =====================================================================
    id_empleador = id_de_rol("empleador") or 2

    agregar("TC-SEC-010", "GET /auth/me devuelve la identidad de la sesion",
            "GET", "/auth/me", None, "trabajador",
            "La sesion autenticada devuelve id, email, nombre y roles",
            trazabilidad="RF-01 / HU-04 CA-01 (AL-API-005)",
            tecnica="Positivo", tipo="Seguridad",
            precondiciones="Trabajador autenticado con token valido.",
            datos="GET /auth/me con Authorization: Bearer <token_trabajador>",
            pasos="1. Autenticarse como trabajador.\n"
                  "2. Consultar GET /auth/me con el token.\n"
                  "3. Verificar que la respuesta trae id, email y roles.",
            esperado="HTTP 200 OK con {id, email, nombre, roles} del usuario autenticado.",
            prioridad="Critica")

    agregar("TC-SEC-011", "GET /auth/me sin token responde 401",
            "GET", "/auth/me", None, None,
            "Sin cabecera Authorization la peticion se rechaza",
            trazabilidad="RF-01 / HU-04 (AL-API-005)",
            tecnica="Negativa (RBAC)", tipo="Seguridad",
            precondiciones="Sin sesion.",
            datos="GET /auth/me sin cabecera Authorization",
            pasos="1. Consultar GET /auth/me sin token.\n"
                  "2. Verificar el codigo de respuesta.",
            esperado="HTTP 401 Unauthorized.",
            prioridad="Critica")

    agregar("TC-SEC-012", "La recuperacion de contrasena no enumera cuentas",
            "POST", "/auth/password/forgot",
            {"email": "nadie@demo.com"}, None,
            "La respuesta con una cuenta inexistente debe ser identica a la de una existente",
            preparacion=("Preparacion: recuperacion con una cuenta existente",
                         "POST", "/auth/password/forgot",
                         {"email": "empleador@demo.com"}, None),
            trazabilidad="RF-01 / HU-04 CA-02 (AL-SEC-005)",
            tecnica="Negativa", tipo="Seguridad",
            precondiciones="Sin sesion. Se comparan las dos respuestas.",
            datos="POST /auth/password/forgot {email}",
            pasos="1. Pedir la recuperacion para un email registrado.\n"
                  "2. Pedir la recuperacion para un email inexistente.\n"
                  "3. Comparar ambos cuerpos: deben ser identicos.",
            esperado="HTTP 200 OK en ambos casos y respuestas identicas.",
            prioridad="Critica", comparar_preparacion=True)

    agregar("TC-SEC-013", "Un token de sesion no verifica un correo",
            "GET", "/auth/verify-email", None, "trabajador",
            "El token de sesion no es un token de verificacion: debe rechazarse",
            trazabilidad="RF-01 / HU-04 CA-02 (AL-SEC-004)",
            tecnica="Negativa", tipo="Seguridad",
            precondiciones="Token de sesion de un trabajador.",
            datos="GET /auth/verify-email?token=<token_de_sesion>",
            pasos="1. Autenticarse como trabajador.\n"
                  "2. Usar ese token en GET /auth/verify-email.\n"
                  "3. Verificar el codigo de respuesta.",
            esperado="HTTP 400 Bad Request con mensaje de token invalido.",
            prioridad="Critica")

    agregar("TC-SEC-014", "Un usuario no puede auto-otorgarse el plan PRO",
            "POST", "/suscripciones",
            {"usuario": {"id": id_empleador}, "nivel": "PRO",
             "fechaInicio": "2026-10-22"}, "empleador",
            "El nivel PRO solo lo concede un administrador",
            trazabilidad="RF-09 / HU-12 CA-02 (AL-SEC-007)",
            tecnica="Negativa (RBAC)", tipo="Seguridad",
            precondiciones="Empleador autenticado sin rol ADMIN.",
            datos="POST /suscripciones {usuario, nivel: PRO, fechaInicio}",
            pasos="1. Autenticarse como empleador.\n"
                  "2. Intentar crear una suscripcion de nivel PRO a su nombre.\n"
                  "3. Verificar el codigo de respuesta.",
            esperado="HTTP 403 Forbidden: el plan PRO lo concede un administrador.",
            prioridad="Critica")

    agregar("TC-SEC-015", "El contacto del empleador exige sesion",
            "GET", f"/oportunidades/{primera}/contacto", None, None,
            "El contacto no es publico: sin token responde 401",
            trazabilidad="RF-04 / HU-01 (AL-SEC-006)",
            tecnica="Negativa", tipo="Seguridad",
            precondiciones="Sin sesion.",
            datos=f"GET /oportunidades/{primera}/contacto sin Authorization",
            pasos="1. Consultar el contacto de la oferta sin token.\n"
                  "2. Verificar el codigo de respuesta.",
            esperado="HTTP 401 Unauthorized.",
            prioridad="Critica")

    agregar("TC-SEC-016", "El directorio publico no expone el email de los trabajadores",
            "GET", "/trabajadores", None, None,
            "La proyeccion publica omite los datos de contacto",
            trazabilidad="RF-02 / RF-03 (AL-SEC-006)",
            tecnica="Positivo y negativa", tipo="Seguridad",
            precondiciones="Sin sesion.",
            datos="GET /trabajadores",
            pasos="1. Consultar el listado publico de trabajadores.\n"
                  "2. Verificar que ningun elemento trae email ni telefono.",
            esperado="HTTP 200 OK y ningun elemento con email ni telefono.",
            prioridad="Critica")

    agregar("TC-SEC-017", "Las preferencias de notificacion ya no son publicas",
            "GET", f"/preferencias/trabajador/{id_trab}", None, None,
            "Las preferencias son datos privados: exigen sesion",
            trazabilidad="RF-12 / HU-12 (AL-SEC-006)",
            tecnica="Negativa", tipo="Seguridad",
            precondiciones="Sin sesion.",
            datos=f"GET /preferencias/trabajador/{id_trab} sin Authorization",
            pasos="1. Consultar las preferencias sin token.\n"
                  "2. Verificar el codigo de respuesta.",
            esperado="HTTP 401 Unauthorized.",
            prioridad="Critica")

    # =====================================================================
    # Casos que el plan IEEE 829 prometia y no tenian caso ejecutable.
    # =====================================================================
    # Para el caso de titularidad hace falta una oferta de otro empleador: la del
    # empleador temporal de este ciclo.
    id_ajeno, token_ajeno, _, _, _ = registrar_empleador("ajeno")
    id_ajeno_oportunidad = None
    if id_ajeno:
        cuerpo_ajena = {
            "titulo": f"Oferta de otro empleador {marca}",
            "contenido": "Oferta ajena para verificar la titularidad.",
            "ubicacion": "Centro", "modalidad": "PRESENCIAL",
            "rangoSalarial": "1000", "activa": True, "destacada": False,
            "autor": {"id": id_ajeno, "nombre": "QA Ajeno", "email": "qa-ajeno@demo.com"},
        }
        peticion, estado, texto = llamar("POST", "/oportunidades", cuerpo_ajena,
                                         None, token_directo=token_ajeno)
        try:
            id_ajeno_oportunidad = json.loads(texto)["id"]
        except (json.JSONDecodeError, KeyError, TypeError):
            id_ajeno_oportunidad = None

    agregar("TC-NEG-001", "No se puede editar una oportunidad de otro empleador",
            "PUT", f"/oportunidades/{id_ajeno_oportunidad or primera}",
            {"titulo": "Intento de edicion ajena", "contenido": "No corresponde.",
             "ubicacion": "Centro", "modalidad": "PRESENCIAL",
             "rangoSalarial": "1000", "activa": True,
             "autor": {"id": id_empleador}}, "empleador",
            "La titularidad se verifica antes de mutar (AL-SEC-003)",
            trazabilidad="RF-04 / HU-01 CA-01 (AL-SEC-003)",
            tecnica="Negativa (RBAC)", tipo="Seguridad",
            precondiciones="La oportunidad pertenece a otro empleador.",
            datos=f"PUT /oportunidades/{id_ajeno_oportunidad or primera} como empleador no titular",
            pasos="1. Autenticarse como empleador.\n"
                  "2. Editar una oferta que no es suya.\n"
                  "3. Verificar el codigo de respuesta.",
            esperado="HTTP 403 Forbidden.",
            prioridad="Critica")

    agregar("TC-LIM-004", "Una valoracion con puntuacion fuera de rango se rechaza",
            "POST", "/valoraciones",
            {"puntuacion": 0, "comentario": "Puntuacion invalida.",
             "autor": {"id": id_empleador}, "trabajador": {"id": id_trab}},
            "empleador",
            "La puntuacion admite de 1 a 5 estrellas",
            trazabilidad="RF-05 / HU-06 CA-01",
            tecnica="Valor limite", tipo="Integracion API",
            precondiciones="Empleador y trabajador autenticados.",
            datos="POST /valoraciones {puntuacion: 0, autor, trabajador}",
            pasos="1. Autenticarse como empleador.\n"
                  "2. Crear una valoracion con puntuacion 0.\n"
                  "3. Verificar el codigo de respuesta.",
            esperado="HTTP 400 Bad Request por puntuacion fuera de rango.",
            prioridad="Alta")

    return casos


def casos_db_dinamicos():
    """TC-DB-005 y TC-DB-006 se ejecutan en cadena sobre una oportunidad propia."""
    titulo = "Oportunidad para verificar eliminacion " + datetime.datetime.now().strftime("%H%M%S")
    id_creada, peticion_crear, estado_crear, texto_crear = crear_oportunidad(titulo)
    casos = []
    if id_creada is None:
        return casos

    peticion_borrado, estado_borrado, texto_borrado = llamar(
        "DELETE", f"/oportunidades/{id_creada}", None, "empleador")
    peticion_consulta, estado_consulta, texto_consulta = llamar(
        "GET", f"/oportunidades/{id_creada}", None, None)

    casos.append(dict(
        id="TC-DB-005", titulo="Eliminacion de oportunidad laboral en base de datos",
        intercambios=[
            ("Preparacion: alta de la oportunidad que se va a eliminar",
             peticion_crear, estado_crear, texto_crear),
            ("Operacion bajo prueba: DELETE de la oportunidad",
             peticion_borrado, estado_borrado, texto_borrado),
        ],
        nota=f"oportunidad creada para la prueba: ID {id_creada}"))
    casos.append(dict(
        id="TC-DB-006", titulo="Comprobacion de no persistencia tras eliminacion",
        intercambios=[
            ("Operacion bajo prueba: GET de la oportunidad eliminada",
             peticion_consulta, estado_consulta, texto_consulta),
        ],
        nota=f"oportunidad eliminada en TC-DB-005: ID {id_creada}"))

    # TC-LIM-001: el plan basico permite 3 ofertas por mes y bloquea la cuarta.
    # Se usa un empleador recien creado para partir con la cuota del mes en cero.
    marca = datetime.datetime.now().strftime("%H%M%S")
    id_empleador_limite, token_limite, peticion_alta, estado_alta, texto_alta = \
        registrar_empleador(f"limite-{marca}")

    if id_empleador_limite and token_limite:
        intercambios = [("Preparacion: alta del empleador con cuota mensual en cero",
                         peticion_alta, estado_alta, texto_alta)]
        estados = []
        cuerpo_base = {"contenido": "Oferta del mes para agotar la cuota.",
                       "ubicacion": "Centro", "modalidad": "PRESENCIAL",
                       "rangoSalarial": "1000", "activa": True, "destacada": False,
                       "autor": {"id": id_empleador_limite}}
        for numero in (1, 2, 3):
            peticion, estado, texto = llamar(
                "POST", "/oportunidades",
                dict(cuerpo_base, titulo=f"Oferta mensual {numero} de TC-LIM-001 {marca}"),
                None, token_directo=token_limite)
            estados.append(estado)
            intercambios.append((f"Preparacion: oferta mensual numero {numero}",
                                  peticion, estado, texto))
        peticion, estado, texto = llamar(
            "POST", "/oportunidades",
            dict(cuerpo_base, titulo=f"Cuarta oferta mensual de TC-LIM-001 {marca}"),
            None, token_directo=token_limite)
        intercambios.append(("Operacion bajo prueba: cuarta oferta del mes",
                              peticion, estado, texto))
        casos.append(dict(
            id="TC-LIM-001",
            titulo="El plan basico permite 3 ofertas por mes y bloquea la cuarta",
            intercambios=intercambios,
            nota=f"empleador creado para el caso: ID {id_empleador_limite}; "
                 f"ofertas 1 a 3 aceptadas (HTTP {', '.join(str(e) for e in estados)})"))
    return casos


# ---------------------------------------------------------------------------
# Render de la evidencia
# ---------------------------------------------------------------------------
def clase_estado(estado):
    if estado == 0:
        return "mal"
    if 200 <= estado < 300:
        return "ok"
    if 400 <= estado < 500:
        return "mal"
    return "mal"


def html_intercambio(peticion, estado, respuesta):
    metodo = peticion.split(" ")[0] if peticion else "?"
    clase_metodo = {"POST": "m-post", "GET": "m-get", "PUT": "m-put",
                    "DELETE": "m-del"}.get(metodo, "")
    lineas = peticion.split("\n\n", 1)
    cabecera = lineas[0]
    cuerpo_peticion = lineas[1] if len(lineas) > 1 else ""
    return (
        "<div class='bloque'><h2>Intercambio HTTP</h2><div class='cuerpo-b'>"
        f"<div style='margin-bottom:8px'><span class='metodo {clase_metodo}'>{metodo}</span>"
        f" <span class='url'>{_esc(cabecera.split(chr(10))[0].split(' ', 1)[-1])}</span></div>"
        "<table>"
        f"<tr><td class='k'>Cabeceras</td><td>{ev.pre(cabecera.split(chr(10), 1)[-1])}</td></tr>"
        f"<tr><td class='k'>Cuerpo de la peticion</td><td>{ev.pre(cuerpo_peticion or '(vacio)')}</td></tr>"
        f"<tr><td class='k'>Codigo de respuesta</td><td class='{clase_estado(estado)}'>"
        f"{estado} {_motivo(estado)}</td></tr>"
        f"<tr><td class='k'>Cuerpo de la respuesta</td><td>{ev.pre(respuesta or '(vacio)')}</td></tr>"
        "</table></div></div>")


def _esc(texto):
    return ev._etiqueta_html(texto)


def _motivo(estado):
    return {
        200: "OK", 201: "Created", 204: "No Content", 400: "Bad Request",
        401: "Unauthorized", 403: "Forbidden", 404: "Not Found",
        409: "Conflict", 500: "Internal Server Error",
    }.get(estado, "")


def prefijo_evidencia(id_caso):
    """Carpeta de evidencia segun el area del caso."""
    return "DB" if id_caso.startswith("TC-DB") else "API"


def carpeta_caso(id_caso):
    return os.path.join(ev.DESTINO, f"{prefijo_evidencia(id_caso)}-{id_caso}")


RUTA_RESULTADOS_API = os.path.join(
    os.path.dirname(os.path.abspath(__file__)), "..", "target", "resultados_api.json")


def guardar_resultados_api(resultados):
    """Deja el resultado de los casos de API en un JSON.

    Es lo que consume el generador del informe: asi el informe y la planilla
    toman el resultado de la ejecucion real sin depender uno del otro.
    """
    try:
        os.makedirs(os.path.dirname(RUTA_RESULTADOS_API), exist_ok=True)
        with open(RUTA_RESULTADOS_API, "w", encoding="utf-8") as archivo:
            json.dump({id_caso: {"estado": estado, "evidencia": ruta}
                       for id_caso, estado, ruta in resultados}, archivo,
                      ensure_ascii=False, indent=2)
    except OSError:
        pass


def estado_pasillo(caso, codigo_http):
    """PASS/FAIL comparando el codigo HTTP obtenido con el esperado del catalogo.

    Los casos que no declaran codigo esperado se reportan con el codigo HTTP.
    """
    esperado = caso.get("codigo_esperado")
    if esperado is None:
        definicion = catalogo_api().get(caso["id"], {})
        esperado = definicion.get("codigo_esperado")
    if esperado is None:
        return str(codigo_http)
    return "PASS" if int(codigo_http) == int(esperado) else "FAIL"


def generar_api():
    resultados = []
    planilla = leer_planilla()
    for caso in casos_api():
        intercambios = []
        if caso.get("preparacion"):
            etiqueta, metodo, ruta, cuerpo, rol = caso["preparacion"]
            peticion_prep, estado_prep, respuesta_prep = llamar(metodo, ruta, cuerpo, rol)
            intercambios.append((etiqueta, peticion_prep, estado_prep, respuesta_prep))
        peticion, estado, respuesta = llamar(
            caso["metodo"], caso["ruta"], caso["cuerpo"], caso["rol"])
        intercambios.append(("Operacion bajo prueba", peticion, estado, respuesta))
        # Oraculo opcional: la respuesta de la operacion debe coincidir con la de la
        # preparacion (por ejemplo, dos respuestas identicas para no enumerar cuentas).
        if caso.get("comparar_preparacion") and caso.get("preparacion"):
            if respuesta.strip() != respuesta_prep.strip():
                estado = "FAIL"
                caso = dict(caso)
                caso["nota"] = (caso["nota"] + " Las dos respuestas difieren: "
                                "la API permite distinguir los casos.").strip()

        referencia = planilla.get(caso["id"], {})
        filas = [
            ("Caso de prueba", f"<b>{caso['id']}</b>"),
            ("Descripcion", ev._etiqueta_html(caso["titulo"])),
            ("Resultado real de esta ejecucion", f"<span class='{clase_estado(estado)}'>HTTP {estado}</span>"),
            ("Nota de ejecucion", ev._etiqueta_html(caso["nota"])),
            ("Ambiente", f"{API} (perfil QA, PostgreSQL con el dump del proyecto)"),
        ]
        if referencia:
            filas.append(("Resultado esperado (planilla)",
                          ev._etiqueta_html(referencia["esperado"])))
            filas.append(("Estado en la planilla", f"<b>{referencia['estado']}</b>"))
        bloques = [ev.filas(filas)]
        for etiqueta, peticion_i, estado_i, respuesta_i in intercambios:
            bloques.append(ev.bloque(etiqueta, html_intercambio(peticion_i, estado_i, respuesta_i)))
        html = ev.pagina(f"{caso['id']} - {caso['titulo']}",
                          f"API / Base de datos · {SUFIXO}", bloques)
        ruta = ev.capturar(html, os.path.join(carpeta_caso(caso["id"]), "captura.png"))
        resultados.append((caso["id"], estado_pasillo(caso, estado), ruta))
    for caso in casos_db_dinamicos():
        referencia = planilla.get(caso["id"], {})
        filas = [
            ("Caso de prueba", f"<b>{caso['id']}</b>"),
            ("Descripcion", ev._etiqueta_html(caso["titulo"])),
            ("Nota de ejecucion", ev._etiqueta_html(caso["nota"])),
            ("Ambiente", f"{API} (perfil QA, PostgreSQL con el dump del proyecto)"),
        ]
        if referencia:
            filas.append(("Resultado esperado (planilla)",
                          ev._etiqueta_html(referencia["esperado"])))
            filas.append(("Estado en la planilla", f"<b>{referencia['estado']}</b>"))
        bloques = [ev.filas(filas)]
        for etiqueta, peticion, estado, respuesta in caso["intercambios"]:
            bloques.append(ev.bloque(etiqueta, html_intercambio(peticion, estado, respuesta)))
        html = ev.pagina(f"{caso['id']} - {caso['titulo']}",
                          f"Base de datos · {SUFIXO}", bloques)
        ruta = ev.capturar(html, os.path.join(carpeta_caso(caso["id"]), "captura.png"))
        resultados.append((caso["id"], estado_pasillo(caso, caso["intercambios"][-1][2]), ruta))
    guardar_resultados_api(resultados)
    return resultados


# ---------------------------------------------------------------------------
# Evidencia de la suite de backend (JUnit + Mockito)
# ---------------------------------------------------------------------------
AREAS_BACKEND = {
    "security": ("Seguridad", "Reglas transversales de autorizacion"),
    "service": ("Funcional", "Reglas de negocio de los servicios de dominio"),
    "web": ("Contrato API", "Codigo de estado y cuerpo de la capa HTTP"),
    "repository": ("Base de datos", "Persistencia, constraints e integridad"),
}


# Suites unitarias de los frontends: area por tipo de archivo de test.
AREAS_FRONTEND = {
    "web": ("Frontend web", "Suite unitaria del frontend web (Vitest + React Testing Library)"),
    "movil": ("Frontend movil", "Suite unitaria del frontend movil (jest-expo + RNTL)"),
}

# Origen de cada suite: etiqueta, prefijo de identificador y archivo JUnit XML.
SUITES_FRONTEND = [
    ("web", "FEW", f"{AL_LADO}/Frontend/al_lado-app/test-results/junit.xml"),
    ("movil", "FEM", f"{AL_LADO}/Frontend/al-lado-mobile/test-results/junit.xml"),
]


def generar_frontend():
    """Ficha de resultado por cada caso de las suites unitarias de los frontends."""
    resultados = []
    for app, prefijo, ruta_xml in SUITES_FRONTEND:
        if not os.path.isfile(ruta_xml):
            continue
        try:
            raiz = ET.parse(ruta_xml).getroot()
        except ET.ParseError:
            continue
        tipo, detalle = AREAS_FRONTEND[app]
        contador = 0
        for suite in raiz.iter("testsuite"):
            for caso in suite.findall("testcase"):
                contador += 1
                metodo = caso.get("name", "")
                tiempo = float(caso.get("time", 0) or 0)
                fallo = caso.find("failure")
                error = caso.find("error")
                omitido = caso.find("skipped")
                if error is not None:
                    estado, detalle_estado = "ERROR", (error.get("message") or "")[:400]
                elif fallo is not None:
                    estado, detalle_estado = "FAIL", (fallo.get("message") or "")[:400]
                elif omitido is not None:
                    estado, detalle_estado = "SKIP", "Caso deshabilitado."
                else:
                    estado, detalle_estado = "PASS", "Todos los asserts se cumplieron."

                id_caso = f"{prefijo}-{contador:03d}"
                html = ev.pagina(f"{id_caso} - {metodo}",
                                 f"Frontend (suite unitaria) · {SUFIXO}", [
                    ev.filas([
                        ("Caso de prueba", f"<b>{id_caso}</b>"),
                        ("Aplicacion", tipo),
                        ("Metodo", f"<span class='url'>{metodo}</span>"),
                        ("Tipo", tipo),
                        ("Naturaleza", detalle),
                        ("Resultado", f"<span class='pill p-{estado.lower()}'>{estado}</span>"),
                        ("Duracion", f"{tiempo:.3f} s"),
                        ("Detalle", ev._etiqueta_html(detalle_estado)),
                        ("Ambiente", "Suite unitaria local (Node + jsdom / React Native)"),
                        ("Reporte JUnit",
                         f"<span class='url'>{ruta_xml.replace(AL_LADO, 'AlLado')}</span>"),
                    ]),
                ])
                ruta = ev.capturar(html, os.path.join(ev.carpeta_evidencia(f"FRONT-{id_caso}"),
                                                      "captura.png"))
                resultados.append((id_caso, estado, ruta))
    return resultados


def generar_backend():
    directorio = f"{AL_LADO}/Backend/target/surefire-reports"
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
        clase = raiz.get("name", "")
        clase_simple = clase.split(".")[-1]
        area = "web" if ".web." in clase else (
            "security" if ".security." in clase else (
                "repository" if ".repository." in clase else "service"))
        tipo, detalle = AREAS_BACKEND[area]
        contador = 0
        for caso in raiz.findall("testcase"):
            contador += 1
            metodo = caso.get("name", "")
            tiempo = float(caso.get("time", 0) or 0)
            fallo = caso.find("failure")
            error = caso.find("error")
            omitido = caso.find("skipped")
            if error is not None:
                estado, detalle_estado = "ERROR", (error.get("message") or "")[:400]
            elif fallo is not None:
                estado, detalle_estado = "FAIL", (fallo.get("message") or "")[:400]
            elif omitido is not None:
                estado, detalle_estado = "SKIP", "Caso deshabilitado."
            else:
                estado, detalle_estado = "PASS", "Todos los asserts se cumplieron."

            id_caso = f"BE-{clase_simple.replace('Test', '')}-{contador:02d}"
            html = ev.pagina(f"{id_caso} - {metodo}",
                             f"Backend (JUnit 5 + Mockito) · {SUFIXO}", [
                ev.filas([
                    ("Caso de prueba", f"<b>{id_caso}</b>"),
                    ("Clase de prueba", f"<span class='url'>{clase}</span>"),
                    ("Metodo", f"<span class='url'>{metodo}</span>"),
                    ("Tipo", tipo),
                    ("Naturaleza", detalle),
                    ("Resultado", f"<span class='pill p-{estado.lower()}'>{estado}</span>"),
                    ("Duracion", f"{tiempo:.3f} s"),
                    ("Detalle", ev._etiqueta_html(detalle_estado)),
                    ("Ambiente", "Perfil e2e: PostgreSQL QA con el dump del proyecto"),
                    ("Reporte de Surefire",
                     f"<span class='url'>Backend/target/surefire-reports/TEST-{clase}.xml</span>"),
                ]),
            ])
            ruta = ev.capturar(html, os.path.join(ev.carpeta_evidencia(f"BACK-{id_caso}"),
                                                  "captura.png"))
            resultados.append((id_caso, estado, ruta))
    return resultados


def resetear_base_qa():
    """Recrea la base de QA desde el dump del proyecto.

    Los casos consumen la cuota mensual de ofertas del plan basico, asi que sin
    este reset la corrida depende de cuantas veces se ejecuto antes.
    """
    import subprocess

    script = os.path.join(os.path.dirname(os.path.abspath(__file__)),
                           "preparar_base_qa.py")
    entorno = dict(os.environ)
    # AL-SEC-002 / issue 75: la contrasena de la base no vive en el repositorio.
    # Hay que exportarla en el entorno (ver Backend/.env.example).
    if not entorno.get("PGPASSWORD"):
        raise SystemExit(
            "Falta PGPASSWORD: exporta la contrasena de la base de QA antes de "
            "recrearla (no se guarda en el repositorio).")
    entorno.setdefault("QA_PG_PORT", "5432")
    print("Recreando la base de QA desde el dump...")
    subprocess.run([sys.executable, script], env=entorno, check=False)


def main():
    if "--reset-base" in sys.argv:
        resetear_base_qa()

    print("Generando evidencia de API y base de datos...")
    for id_caso, estado, ruta in generar_api():
        print(f"  {id_caso:12} HTTP {estado:<4} -> {ruta}")
    print("Generando evidencia de las suites unitarias de los frontends...")
    for id_caso, estado, ruta in generar_frontend():
        print(f"  {id_caso:12} {estado:<6} -> {ruta}")
    print("Generando evidencia de la suite de backend...")
    for id_caso, estado, ruta in generar_backend():
        print(f"  {id_caso:28} {estado:<6} -> {ruta}")
    print("Listo.")


if __name__ == "__main__":
    main()
