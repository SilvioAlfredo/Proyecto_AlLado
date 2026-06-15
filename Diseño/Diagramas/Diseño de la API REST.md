### 1. Definición de Data Transfer Objects (DTOs)

Estos objetos son los que viajarán entre el Frontend y los Controladores de Spring. Están diseñados para no exponer las entidades de la base de datos (JPA Entities).

|**DTO**|**Atributos Principales**|**Propósito**|
|---|---|---|
|**UserRegistrationDTO**|`nombreCompleto`, `email`, `password`, `telefono`, `zonaCobertura`, `tipoUsuario`|Registro inicial de usuarios.|
|**PerfilTrabajadorDTO**|`fotografia`, `biografia`, `habilidades`, `nivelRecomendacion`, `certificados`|Información pública del profesional.|
|**CreateTareaDTO**|`descripcion`, `coordenadas`, `etiquetas`|Datos para la "Postulación Inversa".|
|**PostulacionDTO**|`mensaje`, `presupuestoEstimado`, `tareaId`|Respuesta de un trabajador a una oferta.|
|**ValoracionDTO**|`estrellas` (1-5), `comentario`, `tareaId`|Feedback tras finalizar un trabajo.|
|**EstadisticasDTO**|`ingresosTotales`, `trabajosCompletados`|Datos para el panel del trabajador.|

