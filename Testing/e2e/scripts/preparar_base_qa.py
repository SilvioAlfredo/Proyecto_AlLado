"""Prepara la base de QA: carga el dump del proyecto y fija las contrasenas de prueba.

Uso:  python3 preparar_base_qa.py
"""
import io
import os
import subprocess
import sys

import psycopg2

DUMP = "/home/Alfredo/Documentos/AlLado/AlLado/datos/al_lado_db.sql"
HOST = os.environ.get("QA_PG_HOST", "127.0.0.1")
PORT = os.environ.get("QA_PG_PORT", "5433")
USER = os.environ.get("QA_PG_USER", "postgres")
DB = os.environ.get("QA_PG_DB", "al_lado_db")

# BCrypt (strength 10) de "Clave123!": misma contrasena para las tres cuentas de rol.
HASH_CLAVE = "$2b$10$aVTMva26US8qERaKF.gX1OJFsVqtzWO1ZAiDxs939BW3bC3UkTMnu"

CUENTAS_QA = ["admin@allado.com", "empleador@demo.com", "trabajador@demo.com", "ana@lado.com"]


def conectar(dbname):
    return psycopg2.connect(f"host={HOST} port={PORT} user={USER} dbname={dbname}")


def recrear_base():
    conn = conectar("postgres")
    conn.autocommit = True
    cur = conn.cursor()
    # El backend puede tener conexiones abiertas: se cierran antes de recrear.
    cur.execute(
        "SELECT pg_terminate_backend(pid) FROM pg_stat_activity "
        "WHERE datname = %s AND pid <> pg_backend_pid()",
        (DB,),
    )
    cur.execute(f"SELECT 1 FROM pg_database WHERE datname='{DB}'")
    if cur.fetchone():
        cur.execute(f"DROP DATABASE {DB}")
        print(f"base eliminada: {DB}")
    cur.execute(f"CREATE DATABASE {DB}")
    print(f"base creada: {DB}")
    cur.close()
    conn.close()


def cargar_dump():
    conn = conectar(DB)
    conn.autocommit = True
    cur = conn.cursor()

    with open(DUMP, encoding="utf-8") as f:
        lines = f.readlines()

    statement = []
    executed = 0

    def flush(stmt):
        nonlocal executed
        text = "".join(stmt).strip()
        if not text or text == "--":
            return
        try:
            cur.execute(text)
            executed += 1
        except Exception as exc:
            print("  [aviso]", str(exc).splitlines()[0][:140])

    i = 0
    while i < len(lines):
        line = lines[i]
        if line.startswith("\\"):
            i += 1
            continue
        if line.upper().startswith("COPY "):
            block = []
            i += 1
            while i < len(lines) and not lines[i].startswith("\\."):
                block.append(lines[i])
                i += 1
            i += 1
            cur.copy_expert(line.rstrip("\n"), io.StringIO("".join(block)))
            continue
        statement.append(line)
        if line.rstrip().endswith(";"):
            flush(statement)
            statement = []
        i += 1
    flush(statement)

    print(f"dump cargado ({executed} sentencias)")
    cur.close()
    conn.close()


def fijar_contrasenas():
    conn = conectar(DB)
    conn.autocommit = True
    cur = conn.cursor()
    for email in CUENTAS_QA:
        cur.execute("UPDATE public.usuarios SET password=%s WHERE email=%s", (HASH_CLAVE, email))
    conn.commit()
    print("contrasenas QA fijadas a 'Clave123!' para:", ", ".join(CUENTAS_QA))
    cur.execute("SELECT count(*) FROM public.usuarios")
    print("usuarios en la base:", cur.fetchone()[0])
    cur.close()
    conn.close()


if __name__ == "__main__":
    if "--sin-recrear" not in sys.argv:
        recrear_base()
        cargar_dump()
    fijar_contrasenas()
    print("Base QA lista.")
