"""Utilidades compartidas para generar evidencias visuales de las pruebas.

Renderiza una pagina HTML con el detalle de una ejecucion y la captura con
Chrome, dejando el PNG en la carpeta de evidencias del caso.
"""
import os
import shutil
import tempfile
import time

CHROME_BINARY = os.environ.get(
    "E2E_CHROME_BINARY", "/home/Alfredo/.local/chrome/chrome-linux64/chrome")
CHROMEDRIVER = os.environ.get(
    "E2E_CHROMEDRIVER", "/home/Alfredo/.local/chrome/chromedriver-linux64/chromedriver")

DESTINO = "/home/Alfredo/Documentos/AlLado/plan_de_pruebas/evidencias"

# --- Plantilla visual comun -------------------------------------------------
ESTILO = """
<style>
  * { box-sizing: border-box; }
  body { margin:0; font-family: 'DejaVu Sans', Arial, sans-serif; background:#0f1115; color:#e6e6e6; }
  .barra { background:#161a22; padding:14px 22px; border-bottom:1px solid #262c38;
           display:flex; align-items:center; gap:14px; }
  .marca { width:26px; height:26px; border-radius:50%%; background:#ffc107; color:#111;
           display:flex; align-items:center; justify-content:center; font-weight:800; font-size:13px; }
  .titulo { font-size:16px; font-weight:700; color:#ffc107; }
  .sub { font-size:12px; color:#8a93a5; margin-left:auto; }
  .cuerpo { padding:18px 22px; }
  .bloque { background:#161a22; border:1px solid #262c38; border-radius:8px;
            margin-bottom:14px; overflow:hidden; }
  .bloque h2 { margin:0; padding:9px 14px; font-size:12px; font-weight:700;
               letter-spacing:.08em; text-transform:uppercase; color:#8a93a5;
               background:#1b2029; border-bottom:1px solid #262c38; }
  .bloque .cuerpo-b { padding:12px 14px; }
  table { border-collapse:collapse; width:100%%; font-size:12.5px; }
  td { padding:4px 8px; vertical-align:top; border-bottom:1px solid #202631; }
  td.k { color:#8a93a5; width:190px; }
  pre { margin:0; white-space:pre-wrap; word-break:break-word; font-size:12px;
        font-family:'DejaVu Sans Mono', monospace; color:#cfd6e4; }
  .metodo { display:inline-block; padding:2px 9px; border-radius:4px; font-weight:800;
            font-size:11.5px; color:#111; }
  .m-post { background:#ffc107; } .m-get { background:#4caf50; }
  .m-put { background:#4a9eff; } .m-del { background:#ff5252; }
  .ok { color:#66bb6a; font-weight:700; } .mal { color:#ef5350; font-weight:700; }
  .pill { display:inline-block; padding:2px 8px; border-radius:10px; font-size:11px; font-weight:700; }
  .p-pass { background:#1b3a24; color:#66bb6a; border:1px solid #2c5c39; }
  .p-fail { background:#3a1b1b; color:#ef5350; border:1px solid #5c2c2c; }
  .p-skip { background:#3a341b; color:#e0c060; border:1px solid #5c522c; }
  .url { font-family:'DejaVu Sans Mono', monospace; font-size:13px; color:#cfd6e4; }
</style>
"""


def _etiqueta_html(texto):
    return (texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;"))


def pagina(titulo, subtitulo, bloques):
    """Arma el HTML completo de una ficha de evidencia."""
    partes = [
        "<!DOCTYPE html><html lang='es'><head><meta charset='utf-8'>",
        f"<title>{_etiqueta_html(titulo)}</title>", ESTILO, "</head><body>",
        "<div class='barra'><div class='marca'>A</div>",
        f"<div class='titulo'>{_etiqueta_html(titulo)}</div>",
        f"<div class='sub'>{_etiqueta_html(subtitulo)}</div></div>",
        "<div class='cuerpo'>",
    ]
    for bloque in bloques:
        partes.append(bloque)
    partes.append("</div></body></html>")
    return "".join(partes)


def bloque(titulo, contenido_html):
    return (f"<div class='bloque'><h2>{_etiqueta_html(titulo)}</h2>"
            f"<div class='cuerpo-b'>{contenido_html}</div></div>")


def filas(datos):
    cuerpo = "".join(
        f"<tr><td class='k'>{_etiqueta_html(str(k))}</td><td>{v}</td></tr>"
        for k, v in datos)
    return f"<table>{cuerpo}</table>"


def pre(texto):
    return f"<pre>{_etiqueta_html(str(texto))}</pre>"


def capturar(html, ruta_destino, alto=900):
    """Renderiza el HTML con Chrome y guarda la captura en ruta_destino."""
    from selenium import webdriver
    from selenium.webdriver.chrome.options import Options
    from selenium.webdriver.chrome.service import Service

    os.makedirs(os.path.dirname(ruta_destino), exist_ok=True)
    temporal = tempfile.NamedTemporaryFile("w", suffix=".html", delete=False,
                                           encoding="utf-8")
    temporal.write(html)
    temporal.close()

    opciones = Options()
    opciones.add_argument("--headless=new")
    opciones.add_argument("--no-sandbox")
    opciones.add_argument("--disable-dev-shm-usage")
    opciones.add_argument(f"--window-size=1500,{alto}")
    opciones.add_argument("--hide-scrollbars")
    if os.path.isfile(CHROME_BINARY):
        opciones.binary_location = CHROME_BINARY
    servicio = Service(CHROMEDRIVER) if os.path.isfile(CHROMEDRIVER) else None

    navegador = webdriver.Chrome(service=servicio, options=opciones) if servicio \
        else webdriver.Chrome(options=opciones)
    try:
        navegador.get("file://" + temporal.name)
        time.sleep(0.6)
        altura = navegador.execute_script(
            "return Math.max(document.body.scrollHeight, 600);")
        if altura > alto:
            navegador.set_window_size(1500, min(altura + 120, 6000))
            time.sleep(0.3)
        navegador.save_screenshot(ruta_destino)
    finally:
        navegador.quit()
        os.unlink(temporal.name)
    return ruta_destino


def carpeta_evidencia(nombre_caso):
    return os.path.join(DESTINO, nombre_caso)
