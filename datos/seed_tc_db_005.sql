-- =============================================================================
-- Seed mínimo para TC-DB-005 (HU-08 CA-01) — borrado de una oferta con
-- postulaciones asociadas.
-- -----------------------------------------------------------------------------
-- Requiere el esquema ya creado (ver datos/schema_al_lado_db.sql).
--
-- Precondiciones que deja armadas:
--   · empleador id=2 (empleador@demo.com) dueño de la oferta
--   · oferta id=1 (post_base 1) activa, con 2 postulaciones PENDIENTE
--   · trabajadores id=3 y id=4 como autores de esas postulaciones
--
-- Credenciales de las tres cuentas: Clave123!
-- Se usa un hash generado para este seed porque los del volcado
-- datos/al_lado_db.sql NO corresponden a Clave123! (verificado con bcrypt).
--
-- El seed es idempotente: borra solo sus propias filas y las vuelve a insertar.
-- Está pensado para una base dedicada de pruebas; si se corre sobre la base de
-- desarrollo completa, elimina únicamente los IDs que maneja.
--
-- Oráculo del caso (según Testing.md): 204, activa=false, COUNT(postulaciones)=2
-- y la postulación pasa a CANCELADA.
--
-- Observación: la implementación actual de OportunidadLaboralServiceImpl.eliminar
-- hace un DELETE físico y responde BusinessRuleException ("No se puede eliminar
-- la oferta porque tiene postulaciones asociadas") cuando la FK lo impide; además
-- el enum de estados no tiene CANCELADA (usa RETIRADA). El caso, tal como está
-- escrito, no puede pasar contra el código actual: es un defecto del requerimiento
-- y se reporta, no se adapta el oráculo.
-- =============================================================================

BEGIN;

-- Limpieza de las filas propias, de hijas a padres.
DELETE FROM public.postulaciones WHERE oportunidad_id = 1;
DELETE FROM public.oportunidades_laborales WHERE post_id = 1;
DELETE FROM public.posts_base WHERE id IN (1, 2, 3);
DELETE FROM public.trabajadores WHERE usuario_id IN (3, 4);
DELETE FROM public.empleadores WHERE usuario_id = 2;
DELETE FROM public.usuarios WHERE id IN (2, 3, 4);

-- Usuarios. password = Clave123! (bcrypt, costo 10).
INSERT INTO public.usuarios
    (id, email, email_verificado, habilitado, nombre, password, telefono)
VALUES
    (2, 'empleador@demo.com', true, true, 'Demo Empleador',
     '$2a$10$QBYr0IGztoIZJhX8ozvXXukFzwK0EXGhw1/2.ooSFTurBo3eKGAVe', '+549110000002'),
    (3, 'trabajador@demo.com', true, true, 'Demo Trabajador',
     '$2a$10$QBYr0IGztoIZJhX8ozvXXukFzwK0EXGhw1/2.ooSFTurBo3eKGAVe', '+549110000003'),
    (4, 'trabajador2@demo.com', true, true, 'Demo Trabajador 2',
     '$2a$10$QBYr0IGztoIZJhX8ozvXXukFzwK0EXGhw1/2.ooSFTurBo3eKGAVe', '+549110000004');

-- Roles.
INSERT INTO public.empleadores (usuario_id) VALUES (2);

INSERT INTO public.trabajadores (usuario_id, sector) VALUES
    (3, 'Centro'),
    (4, 'Sur');

-- posts_base: la oferta (id 1) y el contenido de cada postulación (ids 2 y 3).
INSERT INTO public.posts_base (id, titulo, contenido, fecha_creacion, autor_id) VALUES
    (1, 'Arreglo de cañería', 'Se necesita plomero matriculado en Belgrano',
        '2026-10-01 10:00:00', 2),
    (2, 'Me postulo', 'Soy plomero matriculado, tengo disponibilidad inmediata',
        '2026-10-02 10:00:00', 3),
    (3, 'Me postulo', 'Tengo experiencia en arreglos de cañería',
        '2026-10-02 11:00:00', 4);

-- Oferta activa sobre el post 1.
INSERT INTO public.oportunidades_laborales
    (post_id, activa, modalidad, rango_salarial, ubicacion, destacada)
VALUES
    (1, true, 'PRESENCIAL', '50000-80000', 'Belgrano', false);

-- Las dos postulaciones que el caso cuenta.
INSERT INTO public.postulaciones
    (post_id, oportunidad_id, estado, remuneracion_pretendida)
VALUES
    (2, 1, 'PENDIENTE', 50000),
    (3, 1, 'PENDIENTE', 45000);

-- Secuencias alineadas para que la aplicación pueda seguir insertando.
SELECT setval('public.usuarios_id_seq', (SELECT MAX(id) FROM public.usuarios), true);
SELECT setval('public.posts_base_id_seq', (SELECT MAX(id) FROM public.posts_base), true);

COMMIT;

-- Verificación de precondiciones: debe devolver active=true y 2 postulaciones.
SELECT o.post_id,
       o.activa,
       (SELECT COUNT(*) FROM public.postulaciones p WHERE p.oportunidad_id = o.post_id) AS postulaciones
FROM public.oportunidades_laborales o
WHERE o.post_id = 1;
