# Requerimientos Funcionales - Al_lado


### RF-01 | Registro de Usuarios

| Atributo | Detalle Técnico |
| :--- | :--- |
| **Identificador** | RF-01 |
| **Nombre** | Registro de Usuarios |
| **Descripción** | El sistema **debe permitir** a los visitantes crear una cuenta en la plataforma seleccionando el rol de Trabajador o Empleador mediante un formulario web que valide campos obligatorios: nombre completo, correo electrónico, número de teléfono celular y una contraseña. |
| **Prioridad** | Alta (Must Have) |
| **Actor / Fuente** | Visitante / Formulario de Registro (`UserRegistrationDTO`) |
| **Restricciones de Datos** | • Correo electrónico: Debe cumplir con la estructura estándar (ejemplo@dominio.com).<br>• Teléfono: Formato numérico según código de país.<br>• Contraseña: Mínimo 8 caracteres, incluyendo al menos 1 letra mayúscula, 1 número y 1 carácter especial. |
| **Criterio de Verificación** | Envío automatizado de un SMS o token de validación al número telefónico ingresado en un tiempo menor a 60 segundos. El registro no se consolidará en la base de datos hasta que el token sea verificado. |


-----

### RF-02 | Perfil del Trabajador

| Atributo | Detalle Técnico |
| :--- | :--- |
| **Identificador** | RF-02 |
| **Nombre** | Perfil del Trabajador |
| **Descripción** | El sistema **debe permitir** al usuario con rol Trabajador configurar su vitrina digital, ingresando su portafolio de trabajos anteriores, descripción de habilidades, selección de zona de cobertura geográfica y la carga de certificados de profesionalidad. |
| **Prioridad** | Alta (Must Have) |
| **Actor / Fuente** | Trabajador / Formulario de Perfil (`PerfilTrabajadorDTO`) / UC2 |
| **Criterio de Verificación** | El sistema debe guardar el perfil y renderizar en la interfaz pública los botones de contacto directo enlazados a la API de WhatsApp y marcado telefónico tradicional. |


---

### RF-03 | Perfil del Empleador

| Atributo | Detalle Técnico |
| :--- | :--- |
| **Identificador** | RF-03 |
| **Nombre** | Perfil del Empleador |
| **Descripción** | El sistema **debe compilar y mostrar** la información de perfil del usuario Empleador, incluyendo de forma obligatoria el historial público de tareas que ha publicado en la comunidad y sus preferencias generales de contratación. |
| **Prioridad** | Media (Should Have) |
| **Actor / Fuente** | Empleador / Panel de Perfil |
| **Criterio de Verificación** | Comprobación de que la consulta a la base de datos recupera y renderiza correctamente la lista de tareas del empleador sin comprometer sus datos privados de contacto inicial. |

---

### RF-04 | Solicitud de Mano de Obra (Postulación Inversa)

| Atributo | Detalle Técnico |
| :--- | :--- |
| **Identificador** | RF-04 |
| **Nombre** | Solicitud de Mano de Obra (Postulación Inversa) |
| **Descripción** | El sistema **debe permitir** a un Empleador autenticado publicar una necesidad puntual o avería en el barrio, especificando una descripción del problema, etiquetas de la especialidad requerida y coordenadas geográficas. |
| **Prioridad** | Alta (Must Have) |
| **Actor / Fuente** | Empleador / Formulario de Tarea (`CreateTareaDTO`) / UC1 / Diagrama de Actividad |
| **Criterio de Verificación** | Al confirmar la publicación, el sistema almacena la tarea en la base de datos en menos de 1 segundo e identifica en tiempo real a los trabajadores cuyas etiquetas de especialidad coincidan con la demanda para lanzar las alertas push/WA en un tiempo menor a 30 segundos. |

---

### RF-05 | Sistema de Valoraciones y Reseñas

| Atributo | Detalle Técnico |
| :--- | :--- |
| **Identificador** | RF-05 |
| **Nombre** | Sistema de Valoraciones y Reseñas |
| **Descripción** | El sistema **debe permitir** al Empleador calificar un trabajo marcado como completado por parte del consumidor final, requiriendo de forma obligatoria una asignación de estrellas y una reseña escrita para construir la jerarquía de confianza vecinal. |
| **Prioridad** | Alta (Must Have) |
| **Actor / Fuente** | Empleador / Formulario de Feedback (`ValoracionDTO`) / UC5 / Diagrama de Secuencia |
| **Criterio de Verificación** | Al guardar la reseña, el sistema debe ejecutar una tarea asíncrona que recalcule el "nivel de recomendación" general del trabajador e impacte su perfil en tiempo real, permitiendo además filtros de búsqueda basados en dicha métrica. |


---

### RF-06 | Categorización por Especialidad

| Atributo | Detalle Técnico |
| :--- | :--- |
| **Identificador** | RF-06 |
| **Nombre** | Categorización por Especialidad |
| **Descripción** | El sistema **debe proveer y mantener** un catálogo cerrado y estandarizado de etiquetas de especialidad (ej. Electricidad, Cuidado de personas, Programación) para indexar de manera unívoca los perfiles y las demandas. |
| **Prioridad** | Alta (Must Have) |
| **Actor / Fuente** | Sistema / Base de Datos / UC1 / UC3 |
| **Criterio de Verificación** | Los filtros de búsqueda del frontend deben realizar la indexación utilizando exclusivamente este catálogo, retornando los resultados correspondientes en menos de 500 milisegundos. |


---

### RF-07 | Favoritos

| Atributo | Detalle Técnico |
| :--- | :--- |
| **Identificador** | RF-07 |
| **Nombre** | Favoritos |
| **Descripción** | El sistema **debe permitir** a los Empleadores guardar perfiles de trabajadores específicos en una lista personalizada para facilitar accesos directos y contrataciones rápidas en el futuro. |
| **Prioridad** | Baja (Could Have) |
| **Actor / Fuente** | Empleador / Interfaz de Usuario / UC6 |
| **Criterio de Verificación** | Al presionar el ícono de marcador en el perfil del trabajador, este debe aparecer listado instantáneamente en la sección correspondiente del panel del empleador. |

---

### RF-08 | Historial de Contratos

| Atributo | Detalle Técnico |
| :--- | :--- |
| **Identificador** | RF-08 |
| **Nombre** | Historial de Contratos |
| **Descripción** | El sistema **debe registrar de forma inmutable** cada interacción contractual, guardando los identificadores de los participantes, fechas críticas, etiquetas del servicio y el estado de la transacción para el seguimiento de la actividad económica y social. |
| **Prioridad** | Alta (Must Have) |
| **Actor / Fuente** | Sistema AlLado / Persistencia de Datos / UC1 / Diagrama de Secuencia |
| **Criterio de Verificación** | Comprobación mediante auditoría directa en la base de datos de que cada ciclo de trabajo cerrado genera de forma exitosa una entrada inalterable asociada al ID de ambos usuarios. |

---

### RF-09 | Panel de Estadísticas

| Atributo | Detalle Técnico |
| :--- | :--- |
| **Identificador** | RF-09 |
| **Nombre** | Panel de Estadísticas |
| **Descripción** | El sistema **debe calcular y desplegar** visualizaciones gráficas de datos de rendimiento diferenciados: métricas para el usuario (trabajos completados, ingresos mensuales acumulados) y para la administración (categorías más demandadas en el barrio). |
| **Prioridad** | Media (Should Have) |
| **Actor / Fuente** | Trabajador / Administrador / Dashboard Técnico (`EstadisticasDTO`) / UC9 |
| **Criterio de Verificación** | Los datos numéricos expuestos en el frontend deben coincidir exactamente con las funciones de agregación (`SUM`, `COUNT`) ejecutadas sobre la base de datos de contratos. |

---

### RF-10 | Panel de Reportes

| Atributo | Detalle Técnico |
| :--- | :--- |
| **Identificador** | RF-10 |
| **Nombre** | Panel de Reportes |
| **Descripción** | El sistema **debe proveer** un módulo de seguridad para que cualquier usuario autenticado reporte comportamientos inapropiados o incumplimientos, almacenando la alerta en la base de datos para auditoría y moderación. |
| **Prioridad** | Alta (Must Have) |
| **Actor / Fuente** | Usuario General / Administrador / Módulo de Denuncias / UC8 y UC12 |
| **Criterio de Verificación** | El envío de una denuncia debe crear un registro con estado `PENDIENTE` en el panel de moderación, habilitando al administrador la ejecución de acciones correctivas o bloqueos de cuenta. |

---

### RF-11 | Integración con redes sociales

| Atributo | Detalle Técnico |
| :--- | :--- |
| **Identificador** | RF-11 |
| **Nombre** | Integración con redes sociales |
| **Descripción** | El sistema **debe proveer** botones de acceso directo dinámicos para que los Trabajadores puedan compartir el hipervínculo de su perfil público o sus reseñas recibidas en plataformas externas como Facebook e Instagram para ganar más visibilidad. |
| **Prioridad** | Baja (Could Have) |
| **Actor / Fuente** | Trabajador / API de Compartido Externo |
| **Criterio de Verificación** | Inclusión correcta de etiquetas Open Graph (`og:title`, `og:description`) en la cabecera HTML para asegurar un despliegue visual estético al compartirse externamente. |

---

### RF-12 | Notificaciones Personalizadas

| Atributo | Detalle Técnico |
| :--- | :--- |
| **Identificador** | RF-12 |
| **Nombre** | Notificaciones Personalizadas |
| **Descripción** | El sistema **debe despachar** alertas automáticas a los usuarios basadas estrictamente en las preferencias de configuración individuales introducidas por el usuario (ejemplo: “avisarme solo de trabajos de electricidad en mi zona”). |
| **Prioridad** | Alta (Must Have) |
| **Actor / Fuente** | Sistema AlLado / Panel de Preferencias / UC4 |
| **Criterio de Verificación** | Despacho automatizado de notificaciones push o mensajes integrados en un tiempo menor a 30 segundos tras la creación del evento que dispare la condición configurada. |

---

### RF-13 | Soporte Comunitario

| Atributo | Detalle Técnico |
| :--- | :--- |
| **Identificador** | RF-13 |
| **Nombre** | Soporte Comunitario |
| **Descripción** | El sistema **debe habilitar** foros o espacios centralizados de interacción vecinal donde los usuarios validados puedan publicar recomendaciones, dar consejos o compartir experiencias abiertas dentro de la red. |
| **Prioridad** | Baja (Could Have) |
| **Actor / Fuente** | Empleador / Trabajador / Módulo de Comunidad |
| **Criterio de Verificación** | El sistema debe asociar cada entrada al área geográfica del usuario y renderizar los hilos de conversación de manera exclusiva para los vecinos pertenecientes a esa misma zona. |
