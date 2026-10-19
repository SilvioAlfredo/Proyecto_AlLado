# Modelo de negocio

AlLado combina un modelo **freemium** con ingresos por **publicidad de cercanía** y un canal de **aportes y donaciones** que también sirve para activar el nivel pago.

---

## A. Estrategia Freemium

La plataforma define dos niveles de suscripción, `BASICO` y `PRO`, sobre la entidad `Suscripcion` (nivel, fecha de inicio, fecha de fin y estado activo). Todo usuario sin suscripción vigente opera en **Nivel Básico**.

### Nivel Básico (gratuito)

- Acceso al **directorio de profesionales** y a sus perfiles públicos.
- Publicación de **hasta 3 oportunidades activas por mes** para el Empleador (RF-04). Al superar el límite, el sistema bloquea la publicación e invita a pasar a PRO.
- Gestión de **favoritos**, **valoraciones**, **notificaciones dentro de la aplicación**, participación en el **foro** y lectura de **comunicados**.
- Panel de **estadísticas básico** del Trabajador.

### Nivel PRO (suscripción anual)

Se activa por un **aporte verificado** por el Administrador y tiene una **vigencia de 1 año** (RF-17).

**Beneficios para el Empleador**

- **Publicaciones ilimitadas** de oportunidades.
- **Destacar ofertas** para darles prioridad en los listados.
- **Perfil empresarial** con datos del comercio y el **Sello de Confianza** (verificación otorgada por el Administrador).
- **Publicidad de cercanía:** publicación de anuncios propios.

**Beneficios para el Trabajador**

- Armado de **portafolio** con trabajos y certificados.
- Acceso a **Propuestas PRO**: oportunidades reservadas/priorizadas para usuarios PRO.

> La suscripción puede cancelarse (`PUT /suscripciones/cancelar/{id}`), lo que desactiva el nivel PRO y devuelve al usuario al Nivel Básico.

---

## B. Publicidad de cercanía (Hyper-local Ads)

En lugar de anuncios genéricos, AlLado ofrece espacios publicitarios a comercios del barrio (ferreterías, pinturerías, casas de repuestos) a través de la entidad `Publicidad` (título, contenido, imagen, enlace de destino, vigencia y estado activo).

- El **Empleador con plan PRO** (o el Administrador) crea y administra sus anuncios indicando fecha de inicio y de fin.
- La plataforma expone los anuncios **vigentes** (`GET /publicidades/vigentes`) para su despliegue discreto en la interfaz.
- **Ejemplo de uso:** una maderera local publica un anuncio que acompaña las búsquedas de carpinteros de la zona.

---

## C. Sistema de Donaciones y Aportes

- **Tip de Gratitud:** al finalizar un trabajo exitoso, se invita al empleador a realizar una pequeña donación para mantener los servidores de la plataforma ("Invita un café al equipo de AlLado").
- **Activación de PRO:** el mismo mecanismo de aporte (`Donacion` con `mecanismoPago = "SUSCRIPCION"`) permite pagar la suscripción anual. El usuario informa banco, tipo y asunto de la transferencia; el Administrador **verifica manualmente** que el asunto y el monto coincidan con el registro bancario y, si coincide, activa el nivel PRO por 1 año. Si no coincide, el aporte permanece pendiente.
- **Trazabilidad:** cada aporte registra monto, moneda, mecanismo de pago, estado (`PENDIENTE`, `VERIFICADO`, `RECHAZADO`) y los datos de la transferencia para su auditoría.
- **Sponsorship:** alianzas con centros vecinales o municipios interesados en digitalizar la bolsa de empleo local (canal comercial, sin impacto en el código actual).
