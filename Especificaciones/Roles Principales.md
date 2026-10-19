# Roles principales del sistema

AlLado distingue **tres roles autenticados** —Trabajador, Empleador y Administrador— y un **Visitante** sin sesión. El rol se elige obligatoriamente durante el registro (`RegisterRequestDTO.rol`) y **no puede modificarse** una vez creada la cuenta. En persistencia se representa mediante herencia (una tabla por subtipo) y en seguridad mediante autoridades con prefijo `ROLE_` (`ROLE_TRABAJADOR`, `ROLE_EMPLEADOR`, `ROLE_ADMIN`).

| Rol | Autoridad | Alcance principal |
| :--- | :--- | :--- |
| Visitante | (sin sesión) | Registro, inicio de sesión y recuperación de contraseña |
| Empleador | `ROLE_EMPLEADOR` | Oportunidades, postulaciones, favoritos, valoraciones, perfil empresarial y publicidad |
| Trabajador | `ROLE_TRABAJADOR` | Postulaciones, perfil, portafolio, horarios, preferencias y estadísticas |
| Administrador | `ROLE_ADMIN` | Gestión integral de la plataforma y moderación |

---

## 1. El Empleador (Vecino con una necesidad)

No es un espectador: es el motor de oportunidades. Publica sus necesidades y recibe propuestas de profesionales.

- **Publica necesidades:** crea, edita, activa/desactiva y elimina oportunidades laborales (`POST/PUT/DELETE /oportunidades`) indicando título, descripción, ubicación, modalidad, rango salarial estimado y etiquetas de especialidad.
- **Explora el directorio:** busca y filtra profesionales por especialidad, modalidad y texto (`GET /trabajadores/filtrar`) y consulta perfiles completos.
- **Gestiona postulaciones:** revisa las propuestas recibidas y las marca como *seleccionada*, *rechazada* o *completada*.
- **Guarda favoritos:** marca profesionales para acceder rápido a ellos en el futuro.
- **Valora:** al completar un trabajo, califica al trabajador con 1 a 5 estrellas y un comentario opcional.
- **Escala con PRO:** con suscripción vigente publica ofertas ilimitadas, destaca ofertas, mantiene un **perfil empresarial** (Sello de Confianza) y publica **publicidad**.
- **Consulta su historial:** revisa el detalle de las soluciones contratadas y los profesionales que asistieron.

## 2. El Trabajador (Profesional de la zona)

Tiene una vitrina digital para exponer sus habilidades y postularse a las necesidades del barrio.

- **Postula:** se presenta a las oportunidades activas con un mensaje y una remuneración pretendida (`POST /postulaciones`); puede retirar su postulación mientras siga abierta.
- **Construye su perfil:** carga nombre, teléfono, fotografía, biografía, certificados, sector barrial y sus especialidades del catálogo de etiquetas.
- **Configura su disponibilidad:** define franjas horarias por día de la semana.
- **Recibe notificaciones:** recibe avisos dentro de la aplicación sobre el estado de sus postulaciones y nuevas oportunidades compatibles.
- **Controla su rendimiento:** consulta un panel de estadísticas con postulaciones enviadas, seleccionadas y completadas, ingresos y promedio de valoraciones.
- **Escala con PRO:** con suscripción vigente arma su **portafolio** y accede a las **Propuestas PRO**.
- **Recibe valoraciones:** acumula reseñas de los empleadores que contrataron sus servicios.

## 3. El Administrador

Actor técnico interno encargado del mantenimiento operativo, la moderación y la trazabilidad económica de la plataforma.

- **Gestiona personas y empresas:** administra usuarios (habilitar/deshabilitar) y consulta el padrón de empresas.
- **Mantiene la taxonomía:** crea y elimina etiquetas de especialidad.
- **Modera:** resuelve reportes de comportamiento inapropiado (`PUT /reportes/{id}/resolver`), elimina publicaciones del foro y valoraciones indebidas.
- **Verifica:** aprueba perfiles empresariales (otorga el Sello de Confianza) y verifica aportes/suscripciones por transferencia.
- **Gestiona la comunidad:** emite comunicados institucionales y supervisa publicidades.
- **Audita:** accede al panel de métricas de la plataforma (usuarios, postulaciones, ingresos estimados y tendencias).

## 4. El Visitante

Usuario sin sesión iniciada. Únicamente puede registrarse, iniciar sesión y recuperar su contraseña. No accede a ningún recurso protegido.

---

## Matriz de capacidades

| Capacidad | Visitante | Empleador | Trabajador | Administrador |
| :--- | :---: | :---: | :---: | :---: |
| Registrarse / iniciar sesión / recuperar contraseña | ✔ | — | — | — |
| Publicar oportunidades | — | ✔ | — | ✔ |
| Postularse a oportunidades | — | — | ✔ | — |
| Gestionar estado de postulaciones | — | ✔ | — | ✔ |
| Buscar y filtrar profesionales | ✔ | ✔ | ✔ | ✔ |
| Gestionar favoritos | — | ✔ | — | ✔ |
| Valorar trabajadores | — | ✔ | — | ✔ |
| Reportar contenido | — | ✔ | ✔ | — |
| Publicar en el foro / leer comunicados | — | ✔ | ✔ | ✔ |
| Portafolio y Propuestas PRO | — | — | ✔ *(PRO)* | — |
| Perfil empresarial / Sello de Confianza | — | ✔ *(PRO)* | — | ✔ *(verifica)* |
| Publicidad de cercanía | — | ✔ *(PRO)* | — | ✔ *(gestiona)* |
| Panel de estadísticas propio | — | — | ✔ | — |
| Panel de métricas globales | — | — | — | ✔ |
| Moderar reportes, foro, usuarios y etiquetas | — | — | — | ✔ |
| Verificar aportes y suscripciones | — | — | — | ✔ |
