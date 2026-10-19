# Unificación: documentadas vs GitHub vs implementado

> Fecha: 15/10/2026. Fuentes: `Especificaciones/` (13 HU documentadas),
> GitHub `SilvioAlfredo/Proyecto_AlLado` (38 issues), código actual y
> `hg log` (82 changesets). Las 15 issues `HU-*` de GitHub **no corresponden**
> a las HU documentadas (ver §1).

## 1. Diagnóstico de GitHub

| Grupo GitHub | Cant. | Estado | Veredicto |
|---|---|---|---|
| `HU-01..HU-13: Automatizar ...` (#15–29, OPEN) + `HU-05`/`HU-07` (#19/#22, CLOSED vacías) | 15 | 13 OPEN, 2 CLOSED | **Basura**: describen "reglas de GitHub Projects" (crear issues, etiquetas…), nada del dominio AlLado. No referencian ninguna HU documentada |
| `TSK 11–18` + `TKS 14` (#34–42, OPEN) | 8 | OPEN asignadas | **Obsoletas-hechas**: scaffolding de junio (crear Backend, capas…). El trabajo existe en el código; quedaron abiertas sin seguimiento |
| `TK-01..TK-10`, Diseño, Wiki, RF-01 (#1–14, #2, #3) | 14 | 13 CLOSED, 1 OPEN | **Historia válida**: se conserva. Solo #4 `RF-01: Desarrollo Backend` sigue OPEN (asignada a los 4) y es real |
| `Config. Kallithea` (#38, OPEN sin asignar) | 1 | OPEN | **Obsoleta**: infra vieja, cerrar |

## 2. Unificación por HU documentada

Estado verificado en código el 15/10/2026. `Hizo` = autor hg del código existente.
`Debe` = responsable propuesto del faltante (por afinidad de área).

| HU doc. | Implementado (código) | Hizo (hg) | Falta | Debe (propuesto) |
|---|---|---|---|---|
| HU-01 Publicar ofertas | CA-01 ✅ (`OportunidadLaboralController`, mapa) · CA-02 ❌ sin límite 3/mes (sin conteo en services) | Silvio R. | Límite freemium + modal Pro | SilvioAlfredo |
| HU-02 Postularse | CA-01 ✅ (`PostulacionServiceImpl`, estados incl. COMPLETADA) · CA-02 ❌ sin exclusividad 15 min (sin lógica temporal) | Silvio R. | Ventana Pro 15 min | SilvioAlfredo |
| HU-03 Perfil trabajador | Backend ✅ DTO · UI parcial (vistas Trabajador) | Silvio R./Williams G. + Chiara M. | Foto/bio/certificados + UI completa | ChiaraMadera (+juliolasa81) |
| HU-04 Registro+SMS | Campo `telefono` ✅ en `Usuario`/`UsuarioDTO` · SMS ❌, complejidad ❌ | Silvio R./Chiara M. | SMS <60s (mock), regla 8+complejidad | SilvioAlfredo |
| HU-05 Perfil empleador | DTO ✅ · sin página para el trabajador | Silvio R. | Página + historial paginado, ocultar contacto | ChiaraMadera / juliolasa81 |
| HU-06 Valoración | Núcleo ✅ (1–5, promedio) · falta gate por COMPLETADA + tip | Silvio R. | Gate finalización, reseña inmutable, modal tip | SilvioAlfredo (gate) + ChiaraMadera (modal) |
| HU-07 Búsqueda | Endpoints `/filtrar` ✅ (oportunidades + trabajadores) · sin UI, umbral sin medir | Silvio R. | UI filtros + medir <500 ms | juliolasa81 / ChiaraMadera |
| HU-08 Historial | Ciclo con COMPLETADA ✅ · `DELETE` sigue habilitado (viola inmutabilidad) | Silvio R. | Quitar DELETE, auditoría | SilvioAlfredo |
| HU-09 Notificaciones | Solo avisos internos (`NotificacionService`: "sin email/WhatsApp/push") | Silvio R./Chiara M. | Dispatch <30 s al publicar | SilvioAlfredo |
| HU-10 Horarios | CRUD ✅ · sin exclusión en búsqueda | Silvio R. | Excluir no-disponibles del `/filtrar` | SilvioAlfredo |
| HU-11 Favoritos | API ✅ · sin UI | Silvio R. | Ícono + página | juliolasa81 / ChiaraMadera |
| HU-12 Estadísticas | KPIs admin ✅ · sin panel trabajador ni candado Pro | Silvio R. | Endpoint ingresos + UI + candado | SilvioAlfredo (API) + juliolasa81 (UI) |
| HU-13 Reportes+foro | Reportes ✅ (`ReporteController`) · sin bloqueo cuentas, foro sin `sector` (campo inexistente) | Silvio R. | Bloqueo/suspensión + sector foro + UI | SilvioAlfredo (back) + ChiaraMadera (UI) |

Afinidad por hg (ficheros): Silvio = Backend/java 560 + web 864 (lead back);
Chiara = web 602 + docs (front/docs); Julio = web 343 + service (front/service);
Williams = java 197 + mobile (repository/mobile).

## 3. Plan de corrección en GitHub (requiere confirmación)

1. **Cerrar 14 obsoletas/basura**: #15,#16,#17,#18,#20,#21,#23,#24,#25,#26,#27,#28,#29
   (HU-automatizar) + #38 (Kallithea), con comentario de motivo.
2. **Cerrar 8 hechas sin seguimiento**: #34,#35,#36,#37,#39,#40,#41,#42 (TSK/TKS),
   con comentario + evidencia (rutas de código).
3. **Crear 13 issues reales** `[HU-01] … [HU-13]` con CA, estado y responsable
   `Debe` de §2, asignadas en GitHub.
4. **Mantener** historia CLOSED (#1–3,#5–14,#19,#22) y #4 OPEN.
5. **Re-sincronizar** `sinc-issues` (`sync_github.py`) para traer las nuevas.

Comandos preparados (no ejecutados):

```bash
# Cierres
gh issue close 15 16 17 18 20 21 23 24 25 26 27 28 29 38 \
  --repo SilvioAlfredo/Proyecto_AlLado \
  --comment "Limpieza: issue de automatización de GitHub Projects sin relación con las HU documentadas en Especificaciones/. Ver Unificacion_issues.md"
gh issue close 34 35 36 37 39 40 41 42 \
  --repo SilvioAlfredo/Proyecto_AlLado \
  --comment "Hecha sin seguimiento: el trabajo existe en el código. Se reemplaza por issues HU-01..HU-13 trazables. Ver Unificacion_issues.md"
# Creación (ejemplo HU-01; repetir por HU con su responsable)
gh issue create --repo SilvioAlfredo/Proyecto_AlLado \
  --title "[HU-01] Publicar ofertas — límite freemium 3/mes" \
  --assignee SilvioAlfredo \
  --body "CA-01 implementada (OportunidadLaboralController). Falta CA-02: bloquear 4.ª publicación mensual en Nivel Básico + modal Pro. Trazabilidad: RF-13 · Testing.md TC-LIM-001."
```
