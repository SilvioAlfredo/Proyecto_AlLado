# AlLado: Fortaleciendo el Vínculo Laboral Local

**AlLado** es una plataforma digital diseñada para transformar la manera en que los profesionales y los empleadores conectan dentro de su propia comunidad. En un mundo cada vez más globalizado, AlLado apuesta por la **proximidad**, visibilizando el talento local y facilitando soluciones rápidas a necesidades cotidianas.

Nuestra misión es dinamizar la economía de barrio, permitiendo que los vecinos se ayuden entre sí a través de un ecosistema de trabajo eficiente, transparente y cercano.

---

## 🛠️ ¿Cómo funciona?

El corazón de AlLado late gracias a una interacción bidireccional entre sus usuarios, eliminando las barreras tradicionales de búsqueda de empleo y servicios. La plataforma ofrece dos recorridos complementarios:

- **Directorio de profesionales:** cualquier usuario puede explorar y filtrar el catálogo de Trabajadores por especialidad, modalidad y texto, y consultar sus perfiles, valoraciones y trabajos anteriores.
- **Publicación de necesidades:** el Empleador describe una tarea concreta y recibe **postulaciones** de los profesionales interesados, a quienes luego evalúa y selecciona.

---

## ⚡ La Dinámica de "Postulación Inversa"

A diferencia de las bolsas de trabajo convencionales, AlLado implementa un modelo de **Postulación Inversa**. Este sistema optimiza el tiempo de todos:

- **Publicación de Necesidades:** un Empleador detalla un problema o tarea específica (ej. _"Necesito reparar una persiana este sábado"_), indicando ubicación, modalidad, rango salarial estimado y las **etiquetas de especialidad** requeridas.
- **Notificaciones Inteligentes:** el sistema registra la oportunidad y **notifica dentro de la aplicación** a los profesionales cuyas especialidades coinciden con la demanda.
- **Conexión Instantánea:** el Trabajador recibe el aviso y puede postularse de inmediato con un mensaje y su remuneración pretendida; el Empleador compara propuestas y elige a la persona ideal.

Una vez finalizado el trabajo, el Empleador **valora** al profesional y **completa** la postulación, cerrando el ciclo y alimentando la reputación del Trabajador dentro de la comunidad.

---

## 🧩 Componentes del sistema

| Componente | Tecnología | Rol |
| :--- | :--- | :--- |
| **Backend** | Spring Boot + Spring Security (JWT) + JPA | API REST, reglas de negocio, persistencia y seguridad |
| **Web** | Next.js | Aplicación principal para Empleadores, Trabajadores y Administración |
| **Móvil** | Aplicación móvil | Acceso a los recorridos principales desde el teléfono |

---

## 🚀 Objetivos del Proyecto

- **Visibilidad Local:** dar voz y presencia digital a técnicos, artesanos y profesionales que operan de forma independiente en su comunidad.
- **Agilidad:** reducir los tiempos de respuesta ante emergencias domésticas o proyectos rápidos.
- **Confianza:** fomentar relaciones laborales basadas en la cercanía y en un sistema de **valoraciones** mutuo entre vecinos.
- **Sostenibilidad:** al fomentar el trabajo local, reducimos los tiempos de desplazamiento y fortalecemos la microeconomía de la zona.

---

## 🎯 Alcance funcional

El sistema cubre el ciclo completo de la contratación vecinal:

1. **Registro y acceso** de Trabajadores y Empleadores, con verificación de correo electrónico y recuperación de contraseña.
2. **Perfiles** de Trabajador y de Empleador (con perfil empresarial y Sello de Confianza para los planes PRO).
3. **Oportunidades laborales** con límite mensual en el plan Básico y publicaciones ilimitadas en PRO.
4. **Postulaciones** con flujo de estados (pendiente, seleccionada, rechazada, completada, retirada).
5. **Valoraciones y reseñas** que construyen la reputación del profesional.
6. **Favoritos**, **notificaciones**, **foro vecinal** y **comunicados institucionales**.
7. **Suscripciones, aportes y donaciones** con verificación administrativa.
8. **Panel de administración** con moderación, gestión de taxonomía y métricas de la plataforma.
