# Historias de Usuario - AlLado

**Índice**

- [**HU-01**](#hu-01)
- [**HU-02**](#hu-02)
- [**HU-03**](#hu-03)
- [**HU-04**](#hu-04)
- [**HU-05**](#hu-05)
- [**HU-06**](#hu-06)
- [**HU-07**](#hu-07)
- [**HU-08**](#hu-08)
- [**HU-09**](#hu-09)
- [**HU-10**](#hu-10)
- [**HU-11**](#hu-11)
- [**HU-12**](#hu-12)
- [**HU-13**](#hu-13)
- [**HU-14**](#hu-14)
- [**HU-15**](#hu-15)
- [**HU-16**](#hu-16)
- [**HU-17**](#hu-17)
- [**HU-18**](#hu-18)
- [**HU-19**](#hu-19)
- [**HU-20**](#hu-20)

---

## HU-01 <a name="hu-01"></a>

| Componente | Detalle |
| :---- | :---- |
| **Como** | Empleador |
| **Quiero** | Publicar ofertas de trabajo indicando la necesidad del hogar |
| **Para** | Conseguir profesionales cercanos que ayuden a solucionar los problemas rápidamente |

### Criterios de Aceptación

| CA | Escenario | Dado que | Cuando | Entonces |
| :--- | :--- | :--- | :--- | :--- |
| **CA-01** | Publicación exitosa de tarea | El Empleador se encuentra autenticado y abre el formulario de creación | Completa título, descripción, ubicación, modalidad, rango salarial y etiquetas, y confirma la publicación | El sistema almacena la `OportunidadLaboral` y la deja visible en el listado de oportunidades. |
| **CA-02** | Control de límites comerciales | El Empleador posee una cuenta en "Nivel Básico" y ya registra 3 ofertas activas en el mes | Intenta confirmar una nueva publicación | El sistema bloquea el almacenamiento y muestra un mensaje invitándolo a actualizar al "Nivel Pro". |

---

## HU-02 <a name="hu-02"></a>

| Componente | Detalle |
| :---- | :---- |
| **Como** | Trabajador |
| **Quiero** | Postularme a las solicitudes de tareas activas de mi barrio |
| **Para** | Obtener remuneración y construir una reputación dentro de mi comunidad |

### Criterios de Aceptación

| CA | Escenario | Dado que | Cuando | Entonces |
| :--- | :--- | :--- | :--- | :--- |
| **CA-01** | Envío de postulación | El Trabajador visualiza una necesidad compatible con sus especialidades | Completa su postulación con un mensaje y una remuneración pretendida y presiona "Enviar" | El sistema crea la `Postulacion` en estado `PENDIENTE`, la vincula a la oportunidad y notifica al Empleador. |
| **CA-02** | Retiro de postulación | La postulación del Trabajador sigue en estado `PENDIENTE` o `SELECCIONADA` | Presiona "Retirar" | El sistema cambia el estado a `RETIRADA` y la retira del seguimiento activo. |
| **CA-03** | Acceso PRO a propuestas | El Trabajador posee una suscripción PRO vigente | Ingresa a la sección "Propuestas PRO" | El sistema le devuelve las oportunidades reservadas para usuarios PRO. |

---

## HU-03 <a name="hu-03"></a>

| Componente | Detalle |
| :---- | :---- |
| **Como** | Empleador |
| **Quiero** | Visualizar perfiles detallados de los trabajadores postulados |
| **Para** | Evaluar sus habilidades y seleccionar al profesional idóneo para el trabajo |

### Criterios de Aceptación

| CA | Escenario | Dado que | Cuando | Entonces |
| :--- | :--- | :--- | :--- | :--- |
| **CA-01** | Despliegue de información de confianza | El Empleador revisa la lista de postulantes de su necesidad | Selecciona a uno de los Trabajadores | El sistema recupera el `TrabajadorDTO` y renderiza su fotografía, biografía, certificados, especialidades y valoraciones recibidas. |

---

## HU-04 <a name="hu-04"></a>

| Componente | Detalle |
| :---- | :---- |
| **Como** | Trabajador |
| **Quiero** | Registrarme en la plataforma y validar mi correo electrónico |
| **Para** | Generar confianza en los clientes potenciales de la zona y habilitar mi contacto |

### Criterios de Aceptación

| CA | Escenario | Dado que | Cuando | Entonces |
| :--- | :--- | :--- | :--- | :--- |
| **CA-01** | Validación de formatos obligatorios | Un visitante completa el formulario de registro | Ingresa una contraseña de menos de 8 caracteres o un correo con formato inválido | El sistema bloquea el envío y muestra las alertas de validación correspondientes. |
| **CA-02** | Verificación por correo electrónico | El visitante completó correctamente todos los campos del `RegisterRequestDTO` | Presiona "Confirmar Registro" | El sistema crea la cuenta, devuelve su token de sesión y emite un token de verificación por correo; la cuenta queda con `emailVerificado = false` hasta la confirmación, sin bloquear el inicio de sesión. |

---

## HU-05 <a name="hu-05"></a>

| Componente | Detalle |
| :---- | :---- |
| **Como** | Trabajador |
| **Quiero** | Visualizar el perfil del empleador que publicó una necesidad |
| **Para** | Conocer su historial de tareas vecinales y asegurar que es una oferta legítima antes de postularme |

### Criterios de Aceptación

| CA | Escenario | Dado que | Cuando | Entonces |
| :--- | :--- | :--- | :--- | :--- |
| **CA-01** | Visualización segura de datos del Empleador | El Trabajador revisa las solicitudes disponibles | Accede a la ficha del Empleador que generó la oferta | El sistema despliega su historial de oportunidades publicadas y, si existe, su perfil empresarial, sin exponer credenciales ni datos privados. |

---

## HU-06 <a name="hu-06"></a>

| Componente | Detalle |
| :---- | :---- |
| **Como** | Empleador |
| **Quiero** | Calificar con estrellas y dejar una reseña sobre el servicio recibido |
| **Para** | Reportar el desempeño del profesional y ayudar a construir reputación y confianza vecinal |

### Criterios de Aceptación

| CA | Escenario | Dado que | Cuando | Entonces |
| :--- | :--- | :--- | :--- | :--- |
| **CA-01** | Consolidación del feedback | El Empleador dio por completado el trabajo | Asigna una puntuación de 1 a 5 estrellas y, opcionalmente, un comentario, y presiona "Enviar" | El sistema persiste la `Valoracion` y actualiza el promedio del Trabajador. Un segundo intento de valorar al mismo Trabajador es rechazado. |
| **CA-02** | Propuesta voluntaria de donación | El Empleador completó la calificación del servicio | Se ofrece la opción de colaborar | El sistema permite registrar un aporte/donación desde la sección de transferencias, sujeto a la verificación del Administrador. |

---

## HU-07 <a name="hu-07"></a>

| Componente | Detalle |
| :---- | :---- |
| **Como** | Empleador |
| **Quiero** | Utilizar filtros de búsqueda basados en especialidades y zona |
| **Para** | Encontrar de forma exacta al profesional que se ajuste a mis requerimientos |

### Criterios de Aceptación

| CA | Escenario | Dado que | Cuando | Entonces |
| :--- | :--- | :--- | :--- | :--- |
| **CA-01** | Búsqueda indexada por catálogo | El Empleador accede al directorio de profesionales | Selecciona una etiqueta del catálogo cerrado y un sector | El sistema devuelve los profesionales que coinciden con los filtros aplicados. |

---

## HU-08 <a name="hu-08"></a>

| Componente | Detalle |
| :---- | :---- |
| **Como** | Empleador |
| **Quiero** | Consultar mi historial de contratos y trabajos pasados |
| **Para** | Verificar los detalles de las soluciones contratadas y los profesionales que asistieron a mi hogar |

### Criterios de Aceptación

| CA | Escenario | Dado que | Cuando | Entonces |
| :--- | :--- | :--- | :--- | :--- |
| **CA-01** | Integridad del historial | El Empleador ingresa a su panel | Hace clic en la sección de historial | El sistema renderiza un listado cronológico de las postulaciones con el nombre del Trabajador, las fechas y el estado de cada servicio (`PENDIENTE`, `SELECCIONADA`, `RECHAZADA`, `COMPLETADA`, `RETIRADA`). |

---

## HU-09 <a name="hu-09"></a>

| Componente | Detalle |
| :---- | :---- |
| **Como** | Trabajador |
| **Quiero** | Recibir notificaciones dentro de la aplicación sobre mis postulaciones |
| **Para** | Enterarme al instante cuando un empleador cambie el estado de mi propuesta |

### Criterios de Aceptación

| CA | Escenario | Dado que | Cuando | Entonces |
| :--- | :--- | :--- | :--- | :--- |
| **CA-01** | Aviso al Empleador por nueva postulación | Un Trabajador envía una postulación | El sistema persiste la postulación | Se genera una notificación de tipo `POSTULACION_RECIBIDA` para el Empleador. |
| **CA-02** | Aviso al Trabajador por cambio de estado | El Empleador selecciona, rechaza o completa una postulación | El sistema guarda el nuevo estado | Se genera una notificación de tipo `POSTULACION_ESTADO` para el Trabajador. |
| **CA-03** | Gestión de no leídas | El usuario tiene notificaciones pendientes | Abre su bandeja y las marca como leídas | El contador de no leídas se actualiza en consecuencia. |

---

## HU-10 <a name="hu-10"></a>

| Componente | Detalle |
| :---- | :---- |
| **Como** | Trabajador |
| **Quiero** | Configurar mis días y rangos horarios de servicio |
| **Para** | Comunicar a los vecinos cuándo estoy disponible |

### Criterios de Aceptación

| CA | Escenario | Dado que | Cuando | Entonces |
| :--- | :--- | :--- | :--- | :--- |
| **CA-01** | Registro de franjas horarias | El Trabajador se encuentra en su panel de disponibilidad | Agrega una franja indicando día, hora de inicio y hora de fin, y guarda | El sistema persiste el `HorarioDisponibilidad` y lo lista en su perfil. |
| **CA-02** | Control de solapamiento | El Trabajador ya registró una franja para un día | Intenta agregar otra franja que se solapa con la anterior | El sistema rechaza el registro indicando que el horario se solapa con otro ya existente. |

---

## HU-11 <a name="hu-11"></a>

| Componente | Detalle |
| :---- | :---- |
| **Como** | Empleador |
| **Quiero** | Almacenar perfiles de trabajadores seleccionados en una sección de favoritos |
| **Para** | Agilizar su localización y contratarlos directamente ante futuras emergencias |

### Criterios de Aceptación

| CA | Escenario | Dado que | Cuando | Entonces |
| :--- | :--- | :--- | :--- | :--- |
| **CA-01** | Marcado e indexación instantánea | El Empleador se encuentra visualizando el perfil de un profesional | Presiona el ícono de "Favorito" | El sistema registra la relación y añade el acceso directo al panel del Empleador. |
| **CA-02** | Desmarcado | El profesional ya figura en la lista de favoritos | El Empleador presiona nuevamente el ícono de "Favorito" | El sistema elimina la relación y actualiza la lista. |

---

## HU-12 <a name="hu-12"></a>

| Componente | Detalle |
| :---- | :---- |
| **Como** | Trabajador |
| **Quiero** | Visualizar estadísticas de ingresos estimados y trabajos realizados en mi panel de control |
| **Para** | Controlar el rendimiento de mi actividad económica mensual dentro de la plataforma |

### Criterios de Aceptación

| CA | Escenario | Dado que | Cuando | Entonces |
| :--- | :--- | :--- | :--- | :--- |
| **CA-01** | Sincronización de métricas | El Trabajador abre su panel | El frontend consume `EstadisticasTrabajadorDTO` | El sistema despliega ingresos totales y de los últimos 3 meses, postulaciones enviadas/seleccionadas/completadas, cantidad y promedio de valoraciones y nivel de recomendación, coincidentes con sus transacciones. |
| **CA-02** | Indicador de plan | El Trabajador consulta su panel | El sistema evalúa su suscripción | El panel refleja si el usuario cuenta con una suscripción activa (`suscripcionActiva`). |

---

## HU-13 <a name="hu-13"></a>

| Componente | Detalle |
| :---- | :---- |
| **Como** | Usuario (Cualquiera) |
| **Quiero** | Disponer de un botón para reportar perfiles abusivos y participar en un foro vecinal |
| **Para** | Mantener la seguridad de la red y compartir experiencias de soporte comunitario en el barrio |

### Criterios de Aceptación

| CA | Escenario | Dado que | Cuando | Entonces |
| :--- | :--- | :--- | :--- | :--- |
| **CA-01** | Envío y flujo de reporte preventivo | Un Empleador o Trabajador autenticado detecta un fraude o acoso | Presiona "Reportar", completa motivo y descripción y confirma | El sistema persiste el reporte en estado `PENDIENTE` y lo pone a disposición del Administrador para su moderación. |
| **CA-02** | Participación en el foro | Un usuario autenticado ingresa al foro | Publica un mensaje con título y contenido | El sistema persiste la publicación en el foro global, visible para la comunidad; el autor o un Administrador puede eliminarla. |

---

## HU-14 <a name="hu-14"></a>

| Componente | Detalle |
| :---- | :---- |
| **Como** | Empleador |
| **Quiero** | Publicar el perfil de mi comercio y obtener el Sello de Confianza |
| **Para** | Dar mayor credibilidad a mis publicaciones frente a los profesionales del barrio |

### Criterios de Aceptación

| CA | Escenario | Dado que | Cuando | Entonces |
| :--- | :--- | :--- | :--- | :--- |
| **CA-01** | Alta del perfil empresarial | El Empleador posee una suscripción PRO vigente | Completa nombre de la empresa, CUIT, descripción, sitio web y logo, y guarda | El sistema almacena el `PerfilEmpresarial` asociado al Empleador. |
| **CA-02** | Restricción por plan | El Empleador no posee una suscripción PRO vigente | Intenta guardar su perfil empresarial | El sistema rechaza la operación indicando que requiere una suscripción PRO. |
| **CA-03** | Verificación del Sello de Confianza | El Administrador revisa los perfiles empresariales | Aprueba el perfil | El perfil queda marcado como `verificado`, otorgando el Sello de Confianza. |

---

## HU-15 <a name="hu-15"></a>

| Componente | Detalle |
| :---- | :---- |
| **Como** | Empleador |
| **Quiero** | Publicar anuncios de mi comercio en la plataforma |
| **Para** | Llegar a los vecinos que buscan servicios relacionados con mi rubro |

### Criterios de Aceptación

| CA | Escenario | Dado que | Cuando | Entonces |
| :--- | :--- | :--- | :--- | :--- |
| **CA-01** | Alta de publicidad | El Empleador posee una suscripción PRO vigente | Carga título, contenido, imagen, enlace de destino y vigencia, y guarda | El sistema almacena la `Publicidad` asociada al Empleador anunciante. |
| **CA-02** | Exposición de anuncios vigentes | Existen anuncios dentro de su rango de fechas y activos | Un usuario navega la plataforma | El sistema expone los anuncios vigentes a través de `GET /publicidades/vigentes`. |

---

## HU-16 <a name="hu-16"></a>

| Componente | Detalle |
| :---- | :---- |
| **Como** | Usuario |
| **Quiero** | Adquirir el nivel PRO realizando un aporte por transferencia |
| **Para** | Acceder a los beneficios de la suscripción anual |

### Criterios de Aceptación

| CA | Escenario | Dado que | Cuando | Entonces |
| :--- | :--- | :--- | :--- | :--- |
| **CA-01** | Registro del aporte | El usuario decide adquirir PRO | Informa monto, banco, tipo y asunto de la transferencia y confirma | El sistema registra el aporte en estado `PENDIENTE` con los datos de la transferencia. |
| **CA-02** | Verificación exitosa | El Administrador verifica el registro bancario | El asunto y el monto coinciden con el aporte declarado | El sistema marca el aporte como `VERIFICADO` y activa el nivel PRO del usuario por 1 año. |
| **CA-03** | Verificación fallida | El Administrador verifica el registro bancario | El asunto o el monto no coinciden | El sistema rechaza la verificación y el aporte permanece pendiente, sin activar el PRO. |

---

## HU-17 <a name="hu-17"></a>

| Componente | Detalle |
| :---- | :---- |
| **Como** | Trabajador |
| **Quiero** | Armar mi portafolio y acceder a las propuestas reservadas para usuarios PRO |
| **Para** | Diferenciarme del resto y acceder primero a las mejores oportunidades |

### Criterios de Aceptación

| CA | Escenario | Dado que | Cuando | Entonces |
| :--- | :--- | :--- | :--- | :--- |
| **CA-01** | Alta de portafolio | El Trabajador posee una suscripción PRO vigente | Agrega un ítem con título, descripción y enlace/archivo | El sistema almacena el `PortafolioItem` y lo muestra en su perfil. |
| **CA-02** | Restricción por plan | El Trabajador no posee una suscripción PRO vigente | Intenta agregar un ítem de portafolio o abrir "Propuestas PRO" | El sistema rechaza la operación indicando que requiere una suscripción PRO. |

---

## HU-18 <a name="hu-18"></a>

| Componente | Detalle |
| :---- | :---- |
| **Como** | Administrador |
| **Quiero** | Moderar reportes y gestionar la comunidad y la taxonomía |
| **Para** | Mantener la seguridad y el orden de la plataforma |

### Criterios de Aceptación

| CA | Escenario | Dado que | Cuando | Entonces |
| :--- | :--- | :--- | :--- | :--- |
| **CA-01** | Moderación de un reporte | Existe un reporte en estado `PENDIENTE` | El Administrador lo resuelve indicando `EN_REVISION`, `RESUELTO` o `DESCARTADO` | El sistema registra el nuevo estado y el moderador que actuó. Un reporte ya moderado no puede volver a resolverse. |
| **CA-02** | Gestión de taxonomía | Existe un catálogo de etiquetas | El Administrador crea o elimina una etiqueta | El sistema la agrega al catálogo o la elimina si no está en uso. |
| **CA-03** | Gestión de usuarios y comunicados | El Administrador accede a su panel | Habilita/deshabilita un usuario o emite un comunicado | El sistema aplica el cambio de estado o publica el comunicado para su lectura. |

---

## HU-19 <a name="hu-19"></a>

| Componente | Detalle |
| :---- | :---- |
| **Como** | Administrador |
| **Quiero** | Consultar un panel con métricas de la plataforma |
| **Para** | Supervisar el crecimiento y la actividad de la comunidad |

### Criterios de Aceptación

| CA | Escenario | Dado que | Cuando | Entonces |
| :--- | :--- | :--- | :--- | :--- |
| **CA-01** | Visualización del dashboard | El Administrador se encuentra autenticado con `ROLE_ADMIN` | Abre el panel de métricas | El sistema devuelve `DashboardMetricsDTO` con contratos, trabajadores, promedio de remuneración, usuarios activos/inactivos, postulaciones por estado y tendencias. |

---

## HU-20 <a name="hu-20"></a>

| Componente | Detalle |
| :---- | :---- |
| **Como** | Usuario |
| **Quiero** | Leer los comunicados institucionales de la plataforma |
| **Para** | Mantenerme informado sobre novedades y avisos de AlLado |

### Criterios de Aceptación

| CA | Escenario | Dado que | Cuando | Entonces |
| :--- | :--- | :--- | :--- | :--- |
| **CA-01** | Lectura de comunicados | El Administrador emitió comunicados | El usuario ingresa a la sección de comunicados | El sistema lista los comunicados publicados y permite filtrarlos por alcance. |
