-- Seed determinista para el perfil E2E (H2 en memoria, ver application-e2e.properties).
-- Datos minimos y estables para que la suite Selenium tenga precondiciones reproducibles.
--
-- Las contrasenas de todas las cuentas son "Clave123!" (BCrypt strength 10).
--
-- El modelo usa herencia JOINED: las tablas de subtipo (administradores, empleadores,
-- trabajadores) se enlazan a usuarios por la columna usuario_id, y las oportunidades
-- y postulaciones a posts_base por post_id. Los ids los genera la secuencia IDENTITY,
-- por lo que los enlaces se hacen por subconsulta sobre el email y nunca por id fijo.

-- =====================================================================
-- Usuarios
-- =====================================================================
INSERT INTO usuarios (nombre, email, telefono, password, habilitado, email_verificado)
VALUES ('Admin QA', 'admin@allado.com', '+549110000001',
        '$2b$10$aVTMva26US8qERaKF.gX1OJFsVqtzWO1ZAiDxs939BW3bC3UkTMnu', true, true);
INSERT INTO administradores (usuario_id, nivel_acceso)
SELECT id, 'SUPER' FROM usuarios WHERE email = 'admin@allado.com';

INSERT INTO usuarios (nombre, email, telefono, password, habilitado, email_verificado)
VALUES ('Demo Empleador', 'empleador@demo.com', '+549110000002',
        '$2b$10$aVTMva26US8qERaKF.gX1OJFsVqtzWO1ZAiDxs939BW3bC3UkTMnu', true, true);
INSERT INTO empleadores (usuario_id)
SELECT id FROM usuarios WHERE email = 'empleador@demo.com';

INSERT INTO usuarios (nombre, email, telefono, password, habilitado, email_verificado)
VALUES ('Demo Trabajador', 'trabajador@demo.com', '+549110000003',
        '$2b$10$aVTMva26US8qERaKF.gX1OJFsVqtzWO1ZAiDxs939BW3bC3UkTMnu', true, true);
INSERT INTO trabajadores (usuario_id, sector, biografia, certificados, fotografia, token_push)
SELECT id, 'Centro', 'Electricista matriculado con 10 años de experiencia en el barrio.',
        'Certificado electricista matriculado N 12345', NULL, NULL
FROM usuarios WHERE email = 'trabajador@demo.com';

-- Trabajador de otro barrio: sirve para el caso de foro geografico (TC-SEC-002).
INSERT INTO usuarios (nombre, email, telefono, password, habilitado, email_verificado)
VALUES ('Ana Gomez', 'ana@lado.com', '+549110000011',
        '$2b$10$aVTMva26US8qERaKF.gX1OJFsVqtzWO1ZAiDxs939BW3bC3UkTMnu', true, true);
INSERT INTO trabajadores (usuario_id, sector, biografia, certificados, fotografia, token_push)
SELECT id, 'Norte', 'Pintora del barrio Norte.', NULL, NULL, NULL
FROM usuarios WHERE email = 'ana@lado.com';

-- =====================================================================
-- Suscripciones (el candado PRO de la UI depende de estos datos)
-- =====================================================================
INSERT INTO suscripciones (usuario_id, nivel, fecha_inicio, fecha_fin, activa)
SELECT id, 'PRO', CURRENT_DATE, NULL, true FROM usuarios WHERE email = 'trabajador@demo.com';
INSERT INTO suscripciones (usuario_id, nivel, fecha_inicio, fecha_fin, activa)
SELECT id, 'BASICO', CURRENT_DATE, NULL, true FROM usuarios WHERE email = 'empleador@demo.com';

-- =====================================================================
-- Etiquetas
-- =====================================================================
INSERT INTO etiquetas (nombre) VALUES ('Plomeria');
INSERT INTO etiquetas (nombre) VALUES ('Electricidad');
INSERT INTO etiquetas (nombre) VALUES ('Pintura');
INSERT INTO etiquetas (nombre) VALUES ('Jardineria');
INSERT INTO etiquetas (nombre) VALUES ('Limpieza');

-- =====================================================================
-- Oportunidades
-- =====================================================================
-- Publicada y activa: es la que se usa en listado, detalle y postulacion.
INSERT INTO posts_base (titulo, contenido, fecha_creacion, autor_id)
SELECT 'Reparar persiana', 'Se necesita un técnico para reparar una persiana este sábado por la mañana.',
       CURRENT_TIMESTAMP, id FROM usuarios WHERE email = 'empleador@demo.com';
INSERT INTO oportunidades_laborales (post_id, ubicacion, modalidad, rango_salarial, activa, destacada)
SELECT id, 'Centro', 'PRESENCIAL', '80000-120000', true, true
FROM posts_base WHERE titulo = 'Reparar persiana';
INSERT INTO oportunidad_etiqueta (oportunidad_id, etiqueta_id)
SELECT ol.post_id, e.id FROM posts_base o
JOIN oportunidades_laborales ol ON ol.post_id = o.id
JOIN etiquetas e ON e.nombre = 'Electricidad'
WHERE o.titulo = 'Reparar persiana';

-- Activa de otra etiqueta: sirve para el caso de filtros (TC-COB-003).
INSERT INTO posts_base (titulo, contenido, fecha_creacion, autor_id)
SELECT 'Pintar living', 'Pintura de un living de 4x5 metros, incluye preparación de superficie.',
       CURRENT_TIMESTAMP, id FROM usuarios WHERE email = 'empleador@demo.com';
INSERT INTO oportunidades_laborales (post_id, ubicacion, modalidad, rango_salarial, activa, destacada)
SELECT id, 'Centro', 'PRESENCIAL', '150000-200000', true, false
FROM posts_base WHERE titulo = 'Pintar living';
INSERT INTO oportunidad_etiqueta (oportunidad_id, etiqueta_id)
SELECT ol.post_id, e.id FROM posts_base o
JOIN oportunidades_laborales ol ON ol.post_id = o.id
JOIN etiquetas e ON e.nombre = 'Pintura'
WHERE o.titulo = 'Pintar living';

-- Cerrada (activa = false): base del caso de postulacion a oportunidad cerrada (TC-NEG-002).
INSERT INTO posts_base (titulo, contenido, fecha_creacion, autor_id)
SELECT 'Cambiar cerrajeria', 'Cambio de cerradura de la puerta principal. Oportunidad ya cerrada.',
       CURRENT_TIMESTAMP, id FROM usuarios WHERE email = 'empleador@demo.com';
INSERT INTO oportunidades_laborales (post_id, ubicacion, modalidad, rango_salarial, activa, destacada)
SELECT id, 'Centro', 'PRESENCIAL', '50000-70000', false, false
FROM posts_base WHERE titulo = 'Cambiar cerrajeria';

-- =====================================================================
-- Foro
-- =====================================================================
INSERT INTO publicaciones_foro (titulo, contenido, fecha_creacion, autor_id)
SELECT 'Recomendacion de electricista', 'Conozco a un electricista del barrio que trabaja muy bien.',
       CURRENT_TIMESTAMP, id FROM usuarios WHERE email = 'trabajador@demo.com';
