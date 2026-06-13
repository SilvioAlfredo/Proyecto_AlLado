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
| **CA-01** | Publicación exitosa de tarea | El Empleador se encuentra autenticado en el sistema y abre el formulario de creación | Completa la descripción, selecciona las etiquetas de especialidad y presiona 'Confirmar Publicación' | El sistema almacena el objeto `CreateTareaDTO` en la base de datos en menos de 1 segundo e indexa la tarea en el mapa. |
| **CA-02** | Control de límites comerciales | El Empleador posee una cuenta en "Nivel Básico" y ya completó las 3 ofertas mensuales | Intenta presionar el botón para confirmar una nueva publicación de trabajo | El sistema bloquea el almacenamiento de datos y despliega un mensaje modal invitándolo a actualizar al "Nivel Pro". |

---

## HU-02 <a name="hu-02"></a>

| Componente | Detalle |
| :---- | :---- |
| **Como** | Trabajador |
| **Quiero** | Postularme y aceptar solicitudes de tareas activas en mi barrio |
| **Para** | Obtener remuneración y construir una reputación dentro de mi comunidad |

### Criterios de Aceptación

| CA | Escenario | Dado que | Cuando | Entonces |
| :--- | :--- | :--- | :--- | :--- |
| **CA-01** | Envío de postulación directa | El Trabajador visualiza una necesidad de un Empleador compatible con su ubicación | Completa el formulario de propuesta económica `PostulacionDTO` y presiona 'Enviar Postulación' | El sistema vincula su postulación a la tarea y actualiza instantáneamente el panel de control del Empleador. |
| **CA-02** | Restricción de prioridad de postulación | Una nueva tarea barrial acaba de ser indexada en el sistema | Un Trabajador con cuenta en "Nivel Básico" intenta postularse dentro de los primeros 15 minutos de vigencia | El sistema bloquea la acción e indica que la oferta está en fase de exclusividad temporal para usuarios "Pro". |

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
| **CA-01** | Despliegue de información de confianza | El Empleador se encuentra revisando la lista de postulantes asignada a su necesidad | Selecciona y hace clic sobre el nombre de uno de los Trabajadores postulados | El sistema recupera el objeto `PerfilTrabajadorDTO` y renderiza de forma pública la fotografía, biografía, certificados y su nivel de recomendación. |

---

## HU-04 <a name="hu-04"></a>

| Componente | Detalle |
| :---- | :---- |
| **Como** | Trabajador |
| **Quiero** | Registrarme en la plataforma y validar mi número de teléfono |
| **Para** | Generar confianza en los clientes potenciales de la zona y habilitar mi contacto directo |

### Criterios de Aceptación

| CA | Escenario | Dado que | Cuando | Entonces |
| :--- | :--- | :--- | :--- | :--- |
| **CA-01** | Validación de formatos obligatorios | Un visitante completa el formulario web para crear una cuenta | Ingresa una contraseña de menos de 8 caracteres o introduce un correo electrónico con formato inválido | El sistema bloquea el envío, resalta los campos erróneos y muestra alertas de validación en tiempo real. |
| **CA-02** | Autenticación obligatoria por SMS | El visitante completa correctamente todos los campos obligatorios del `UserRegistrationDTO` | Presiona el botón 'Confirmar Registro' | El sistema envía un token numérico vía SMS en menos de 60 segundos, manteniendo la cuenta inactiva hasta su verificación. |

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
| **CA-01** | Visualización segura de datos del Empleador | El Trabajador se encuentra revisando las solicitudes de mano de obra disponibles en su zona | Accede a la ficha del Empleador que generó la oferta laboral | El sistema despliega el historial paginado de tareas pasadas y sus preferencias de contratación, ocultando sus datos privados de contacto. |

---

## HU-06 <a name="hu-06"></a>

| Componente | Detalle |
| :---- | :---- |
| **Como** | Empleador |
| **Quiero** | Calificar con estrellas y dejar una reseña escrita sobre el servicio recibido |
| **Para** | Reportar el desempeño del profesional y ayudar a construir reputación y confianza vecinal |

### Criterios de Aceptación

| CA | Escenario | Dado que | Cuando | Entonces |
| :--- | :--- | :--- | :--- | :--- |
| **CA-01** | Consolidación obligatoria del feedback | El Trabajador marcó la tarea como finalizada en el sistema | Completa el formulario de `ValoracionDTO` con estrellas (1-5) y una reseña escrita, y presiona 'Enviar' | El sistema almacena el feedback de forma inmutable, cierra el contrato y recalcula el nivel de recomendación pública del Trabajador. |
| **CA-02** | Propuesta voluntaria de donación | El Empleador ha completado satisfactoriamente el envío de la calificación del servicio | La ventana modal de valoraciones se cierra de forma exitosa | El sistema despliega una interfaz modal para realizar una micro-donación voluntaria (Tip de Gratitud) destinada al servidor. |

---

## HU-07 <a name="hu-07"></a>

| Componente | Detalle |
| :---- | :---- |
| **Como** | Empleador |
| **Quiero** | Utilizar filtros avanzados de búsqueda basados en especialidades y recomendación |
| **Para** | Encontrar de forma exacta al profesional que se ajuste a los requerimientos de mi avería |

### Criterios de Aceptación

| CA | Escenario | Dado que | Cuando | Entonces |
| :--- | :--- | :--- | :--- | :--- |
| **CA-01** | Búsqueda indexada eficiente | El Empleador accede al mapa o motor de búsqueda de la plataforma | Selecciona una etiqueta del catálogo cerrado (ej. Electricidad) y un nivel de estrellas mínimo | El sistema procesa los filtros en el backend y devuelve los profesionales que coinciden en un tiempo inferior a 500 ms. |

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
| **CA-01** | Integridad del historial de contratos | El Empleador ingresa a su panel de control privado | Hace clic sobre la sección de "Historial de Contratos" | El sistema renderiza un reporte cronológico inalterable con el ID, nombre del Trabajador, fechas y estados de cada servicio. |

---

## HU-09 <a name="hu-09"></a>

| Componente | Detalle |
| :---- | :---- |
| **Como** | Trabajador |
| **Quiero** | Recibir notificaciones push o alertas automatizadas basadas en mis etiquetas |
| **Para** | Enterarme al instante cuando alguien publique una necesidad compatible con mis habilidades y zona |

### Criterios de Aceptación

| CA | Escenario | Dado que | Cuando | Entonces |
| :--- | :--- | :--- | :--- | :--- |
| **CA-01** | Envío inmediato automatizado | Un Empleador consolida la publicación de una solicitud de mano de obra barrial | El motor del backend detecta que las etiquetas y geolocalización de la tarea coinciden con el perfil de un Trabajador | Despacha automáticamente una alerta push o un mensaje integrado a la API de WhatsApp en menos de 30 segundos. |

---

## HU-10 <a name="hu-10"></a>

| Componente | Detalle |
| :---- | :---- |
| **Como** | Trabajador |
| **Quiero** | Configurar mis días de descanso y mis rangos horarios de servicio |
| **Para** | Evitar que los vecinos me contacten o visualicen mi perfil activo cuando no estoy disponible |

### Criterios de Aceptación

| CA | Escenario | Dado que | Cuando | Entonces |
| :--- | :--- | :--- | :--- | :--- |
| **CA-01** | Exclusión del motor de búsquedas | El Trabajador se encuentra en su panel de configuración horaria | Desmarca un bloque de horas o un día específico de la semana como 'No Disponible' y guarda | El backend omite de forma automatizada su perfil de los resultados de búsqueda activos para ese lapso de tiempo. |

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
| **CA-01** | Marcado e indexación instantánea | El Empleador se encuentra visualizando la vitrina pública de un profesional | Presiona el ícono marcador de "Favorito" | El sistema inserta la relación en la tabla intermedia de la base de datos y añade el acceso directo al panel del Empleador. |

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
| **CA-01** | Sincronización gráfica de métricas | El Trabajador abre su panel de administración | El frontend consume el endpoint y mapea los atributos del objeto `EstadisticasDTO` | El sistema despliega gráficos cuyos totales coinciden de forma exacta con la suma de sus transacciones laborales completadas. |
| **CA-02** | Restricción de analíticas avanzadas | El Trabajador posee una cuenta gratuita en "Nivel Básico" | Intenta hacer clic en las pestañas de métricas avanzadas de visitas al perfil | El sistema restringe el acceso visual mostrando un candado informativo y un botón de actualización hacia el "Nivel Pro". |

---

## HU-13 <a name="hu-13"></a>

| Componente | Detalle |
| :---- | :---- |
| **Como** | Usuario (Cualquiera) |
| **Quiero** | Disponer de un botón para reportar perfiles abusivos y participar en foros vecinales |
| **Para** | Mantener la seguridad de la red y compartir experiencias de soporte comunitario en el barrio |

### Criterios de Aceptación

| CA | Escenario | Dado que | Cuando | Entonces |
| :--- | :--- | :--- | :--- | :--- |
| **CA-01** | Envío y flujo de reporte preventivo | Un Empleador o Trabajador autenticado detecta un fraude o acoso en la plataforma | Presiona el botón "Reportar", selecciona el motivo, añade evidencia y confirma el envío | El sistema persiste la denuncia en estado `PENDIENTE` y genera una alerta en la interfaz del Administrador para su moderación. |
| **CA-02** | Restricción de alcance geográfico del foro | Un usuario ingresa al foro de soporte e interacción vecinal | Intenta leer o escribir mensajes en los hilos de discusión del foro | El sistema valida automáticamente su ubicación de registro, permitiéndole interactuar solo en el foro asignado a su mismo sector barrial. |

