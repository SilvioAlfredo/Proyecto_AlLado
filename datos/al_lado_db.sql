--
-- PostgreSQL database dump
--

\restrict Cu12SKsuhGRKBmxg1317quTvdZKuBrdwD1v2lLoAQmxBTQRb6JhO1IwjbxSD2xi

-- Dumped from database version 18.4 (Debian 18.4-1.pgdg13+1)
-- Dumped by pg_dump version 18.4 (Debian 18.4-1.pgdg13+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

ALTER TABLE IF EXISTS ONLY public.posts_base DROP CONSTRAINT IF EXISTS fktgiwmg3hffedujq9vi7vqx0nx;
ALTER TABLE IF EXISTS ONLY public.preferencias_notificacion DROP CONSTRAINT IF EXISTS fksjxr7os10gi85yq6djugvbd3m;
ALTER TABLE IF EXISTS ONLY public.postulaciones DROP CONSTRAINT IF EXISTS fksiwsi8grqfjf95t3j8qs6d33b;
ALTER TABLE IF EXISTS ONLY public.notificaciones DROP CONSTRAINT IF EXISTS fkrxqukj4ow44h862qlbgl54ij2;
ALTER TABLE IF EXISTS ONLY public.suscripciones DROP CONSTRAINT IF EXISTS fkrrsvcjm7l6p0pr4l8ehygsmuy;
ALTER TABLE IF EXISTS ONLY public.favoritos DROP CONSTRAINT IF EXISTS fkrgubem4ylcunqw580crm6a7th;
ALTER TABLE IF EXISTS ONLY public.reportes DROP CONSTRAINT IF EXISTS fkr8kvv75wb3gp9ygdg840bvnrc;
ALTER TABLE IF EXISTS ONLY public.perfiles_empresariales DROP CONSTRAINT IF EXISTS fkpacihgpav01rxaiw6nckyd44n;
ALTER TABLE IF EXISTS ONLY public.oportunidades_laborales DROP CONSTRAINT IF EXISTS fkny76u9e5xk2i9opoce21oxe97;
ALTER TABLE IF EXISTS ONLY public.comunicados DROP CONSTRAINT IF EXISTS fkngbcdfgv2ofvnuugtimjcltwq;
ALTER TABLE IF EXISTS ONLY public.trabajador_etiqueta DROP CONSTRAINT IF EXISTS fkn8y8yq9ycgraks3nvm61e7pf8;
ALTER TABLE IF EXISTS ONLY public.reportes DROP CONSTRAINT IF EXISTS fkkmka1wx2y4enuhbioshbicb82;
ALTER TABLE IF EXISTS ONLY public.donaciones DROP CONSTRAINT IF EXISTS fkjpnhxmv4mgfkq83md41i1kf88;
ALTER TABLE IF EXISTS ONLY public.empleadores DROP CONSTRAINT IF EXISTS fkj5i1v9ytsmym06jynw6ox1rm7;
ALTER TABLE IF EXISTS ONLY public.favoritos DROP CONSTRAINT IF EXISTS fkhrlx2a6mmwkp5pp7n9ubusbfx;
ALTER TABLE IF EXISTS ONLY public.perfiles_empresariales DROP CONSTRAINT IF EXISTS fkgje4hjvwg9j0xsjot2oivuyht;
ALTER TABLE IF EXISTS ONLY public.oportunidad_etiqueta DROP CONSTRAINT IF EXISTS fkdgav7l9359qw9u011ejncypad;
ALTER TABLE IF EXISTS ONLY public.trabajadores DROP CONSTRAINT IF EXISTS fkbj2tq4b166h22cigjb8r3vm3t;
ALTER TABLE IF EXISTS ONLY public.publicaciones_foro DROP CONSTRAINT IF EXISTS fkbamkgapsbpkorbbasd4bn2yfk;
ALTER TABLE IF EXISTS ONLY public.publicidades DROP CONSTRAINT IF EXISTS fka8x9cv3r940wepehw1a2vlrhq;
ALTER TABLE IF EXISTS ONLY public.horarios_disponibilidad DROP CONSTRAINT IF EXISTS fk9ytw7sv5rnjsaesaam5lr6okp;
ALTER TABLE IF EXISTS ONLY public.valoraciones DROP CONSTRAINT IF EXISTS fk89ayqwx1u4ou4a56ahkskaq14;
ALTER TABLE IF EXISTS ONLY public.postulaciones DROP CONSTRAINT IF EXISTS fk7t4f17cf26qccbqjt2ek1y8g8;
ALTER TABLE IF EXISTS ONLY public.trabajador_etiqueta DROP CONSTRAINT IF EXISTS fk5v6h8gbf87xsgn5as08wca76y;
ALTER TABLE IF EXISTS ONLY public.valoraciones DROP CONSTRAINT IF EXISTS fk5hv10spy020bvo0l0bmme6m15;
ALTER TABLE IF EXISTS ONLY public.reportes DROP CONSTRAINT IF EXISTS fk4qkjc9dwu7ymw40l5l0gcggbu;
ALTER TABLE IF EXISTS ONLY public.oportunidad_etiqueta DROP CONSTRAINT IF EXISTS fk3wc9xkf7nm5epl7qdg79a4k7a;
ALTER TABLE IF EXISTS ONLY public.administradores DROP CONSTRAINT IF EXISTS fk39dd1lon1to4ofwlcxf73gahm;
ALTER TABLE IF EXISTS ONLY public.portafolio_items DROP CONSTRAINT IF EXISTS fk35eewvffonj773b17odpg8xnm;
ALTER TABLE IF EXISTS ONLY public.valoraciones DROP CONSTRAINT IF EXISTS valoraciones_pkey;
ALTER TABLE IF EXISTS ONLY public.usuarios DROP CONSTRAINT IF EXISTS usuarios_pkey;
ALTER TABLE IF EXISTS ONLY public.favoritos DROP CONSTRAINT IF EXISTS uke2waxy08h7tj51vpsl1swswv2;
ALTER TABLE IF EXISTS ONLY public.preferencias_notificacion DROP CONSTRAINT IF EXISTS uk_nlt2t2ifol5pi3lmj7t47irn7;
ALTER TABLE IF EXISTS ONLY public.usuarios DROP CONSTRAINT IF EXISTS uk_kfsp0s1tflm1cwlj8idhqsad0;
ALTER TABLE IF EXISTS ONLY public.tokens_revocados DROP CONSTRAINT IF EXISTS uk_j6fjrrusjw96gmy8t5ksrpyx6;
ALTER TABLE IF EXISTS ONLY public.perfiles_empresariales DROP CONSTRAINT IF EXISTS uk_i48w8sqh0klnllpkfvxod8uil;
ALTER TABLE IF EXISTS ONLY public.perfiles_empresariales DROP CONSTRAINT IF EXISTS uk_e66hvxecibak2ks4agck5e8id;
ALTER TABLE IF EXISTS ONLY public.suscripciones DROP CONSTRAINT IF EXISTS uk_dunjwecaq037n5har4gbsalye;
ALTER TABLE IF EXISTS ONLY public.etiquetas DROP CONSTRAINT IF EXISTS uk_7krxm7l1tk3hmrrqrtcdy8n77;
ALTER TABLE IF EXISTS ONLY public.trabajadores DROP CONSTRAINT IF EXISTS trabajadores_pkey;
ALTER TABLE IF EXISTS ONLY public.trabajador_etiqueta DROP CONSTRAINT IF EXISTS trabajador_etiqueta_pkey;
ALTER TABLE IF EXISTS ONLY public.tokens_revocados DROP CONSTRAINT IF EXISTS tokens_revocados_pkey;
ALTER TABLE IF EXISTS ONLY public.suscripciones DROP CONSTRAINT IF EXISTS suscripciones_pkey;
ALTER TABLE IF EXISTS ONLY public.reportes DROP CONSTRAINT IF EXISTS reportes_pkey;
ALTER TABLE IF EXISTS ONLY public.publicidades DROP CONSTRAINT IF EXISTS publicidades_pkey;
ALTER TABLE IF EXISTS ONLY public.publicaciones_foro DROP CONSTRAINT IF EXISTS publicaciones_foro_pkey;
ALTER TABLE IF EXISTS ONLY public.preferencias_notificacion DROP CONSTRAINT IF EXISTS preferencias_notificacion_pkey;
ALTER TABLE IF EXISTS ONLY public.postulaciones DROP CONSTRAINT IF EXISTS postulaciones_pkey;
ALTER TABLE IF EXISTS ONLY public.posts_base DROP CONSTRAINT IF EXISTS posts_base_pkey;
ALTER TABLE IF EXISTS ONLY public.portafolio_items DROP CONSTRAINT IF EXISTS portafolio_items_pkey;
ALTER TABLE IF EXISTS ONLY public.perfiles_empresariales DROP CONSTRAINT IF EXISTS perfiles_empresariales_pkey;
ALTER TABLE IF EXISTS ONLY public.oportunidades_laborales DROP CONSTRAINT IF EXISTS oportunidades_laborales_pkey;
ALTER TABLE IF EXISTS ONLY public.oportunidad_etiqueta DROP CONSTRAINT IF EXISTS oportunidad_etiqueta_pkey;
ALTER TABLE IF EXISTS ONLY public.notificaciones DROP CONSTRAINT IF EXISTS notificaciones_pkey;
ALTER TABLE IF EXISTS ONLY public.horarios_disponibilidad DROP CONSTRAINT IF EXISTS horarios_disponibilidad_pkey;
ALTER TABLE IF EXISTS ONLY public.favoritos DROP CONSTRAINT IF EXISTS favoritos_pkey;
ALTER TABLE IF EXISTS ONLY public.etiquetas DROP CONSTRAINT IF EXISTS etiquetas_pkey;
ALTER TABLE IF EXISTS ONLY public.empleadores DROP CONSTRAINT IF EXISTS empleadores_pkey;
ALTER TABLE IF EXISTS ONLY public.donaciones DROP CONSTRAINT IF EXISTS donaciones_pkey;
ALTER TABLE IF EXISTS ONLY public.comunicados DROP CONSTRAINT IF EXISTS comunicados_pkey;
ALTER TABLE IF EXISTS ONLY public.administradores DROP CONSTRAINT IF EXISTS administradores_pkey;
ALTER TABLE IF EXISTS public.valoraciones ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.usuarios ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.tokens_revocados ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.suscripciones ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.reportes ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.publicidades ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.publicaciones_foro ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.preferencias_notificacion ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.posts_base ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.portafolio_items ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.perfiles_empresariales ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.notificaciones ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.horarios_disponibilidad ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.favoritos ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.etiquetas ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.donaciones ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.comunicados ALTER COLUMN id DROP DEFAULT;
DROP SEQUENCE IF EXISTS public.valoraciones_id_seq;
DROP TABLE IF EXISTS public.valoraciones;
DROP SEQUENCE IF EXISTS public.usuarios_id_seq;
DROP TABLE IF EXISTS public.usuarios;
DROP TABLE IF EXISTS public.trabajadores;
DROP TABLE IF EXISTS public.trabajador_etiqueta;
DROP SEQUENCE IF EXISTS public.tokens_revocados_id_seq;
DROP TABLE IF EXISTS public.tokens_revocados;
DROP SEQUENCE IF EXISTS public.suscripciones_id_seq;
DROP TABLE IF EXISTS public.suscripciones;
DROP SEQUENCE IF EXISTS public.reportes_id_seq;
DROP TABLE IF EXISTS public.reportes;
DROP SEQUENCE IF EXISTS public.publicidades_id_seq;
DROP TABLE IF EXISTS public.publicidades;
DROP SEQUENCE IF EXISTS public.publicaciones_foro_id_seq;
DROP TABLE IF EXISTS public.publicaciones_foro;
DROP SEQUENCE IF EXISTS public.preferencias_notificacion_id_seq;
DROP TABLE IF EXISTS public.preferencias_notificacion;
DROP TABLE IF EXISTS public.postulaciones;
DROP SEQUENCE IF EXISTS public.posts_base_id_seq;
DROP TABLE IF EXISTS public.posts_base;
DROP SEQUENCE IF EXISTS public.portafolio_items_id_seq;
DROP TABLE IF EXISTS public.portafolio_items;
DROP SEQUENCE IF EXISTS public.perfiles_empresariales_id_seq;
DROP TABLE IF EXISTS public.perfiles_empresariales;
DROP TABLE IF EXISTS public.oportunidades_laborales;
DROP TABLE IF EXISTS public.oportunidad_etiqueta;
DROP SEQUENCE IF EXISTS public.notificaciones_id_seq;
DROP TABLE IF EXISTS public.notificaciones;
DROP SEQUENCE IF EXISTS public.horarios_disponibilidad_id_seq;
DROP TABLE IF EXISTS public.horarios_disponibilidad;
DROP SEQUENCE IF EXISTS public.favoritos_id_seq;
DROP TABLE IF EXISTS public.favoritos;
DROP SEQUENCE IF EXISTS public.etiquetas_id_seq;
DROP TABLE IF EXISTS public.etiquetas;
DROP TABLE IF EXISTS public.empleadores;
DROP SEQUENCE IF EXISTS public.donaciones_id_seq;
DROP TABLE IF EXISTS public.donaciones;
DROP SEQUENCE IF EXISTS public.comunicados_id_seq;
DROP TABLE IF EXISTS public.comunicados;
DROP TABLE IF EXISTS public.administradores;
SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: administradores; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.administradores (
    nivel_acceso character varying(50) NOT NULL,
    usuario_id bigint NOT NULL
);


ALTER TABLE public.administradores OWNER TO postgres;

--
-- Name: comunicados; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.comunicados (
    id bigint NOT NULL,
    alcance character varying(50),
    contenido text NOT NULL,
    fecha_emision timestamp(6) without time zone NOT NULL,
    titulo character varying(150) NOT NULL,
    administrador_id bigint NOT NULL
);


ALTER TABLE public.comunicados OWNER TO postgres;

--
-- Name: comunicados_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.comunicados_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.comunicados_id_seq OWNER TO postgres;

--
-- Name: comunicados_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.comunicados_id_seq OWNED BY public.comunicados.id;


--
-- Name: donaciones; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.donaciones (
    id bigint NOT NULL,
    asunto_transferencia character varying(100),
    banco character varying(50),
    estado character varying(20) NOT NULL,
    fecha_donacion timestamp(6) without time zone NOT NULL,
    mecanismo_pago character varying(50),
    moneda character varying(10) NOT NULL,
    monto numeric(10,2) NOT NULL,
    tipo_transferencia character varying(50),
    usuario_id bigint NOT NULL
);


ALTER TABLE public.donaciones OWNER TO postgres;

--
-- Name: donaciones_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.donaciones_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.donaciones_id_seq OWNER TO postgres;

--
-- Name: donaciones_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.donaciones_id_seq OWNED BY public.donaciones.id;


--
-- Name: empleadores; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.empleadores (
    usuario_id bigint NOT NULL
);


ALTER TABLE public.empleadores OWNER TO postgres;

--
-- Name: etiquetas; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.etiquetas (
    id bigint NOT NULL,
    nombre character varying(50) NOT NULL
);


ALTER TABLE public.etiquetas OWNER TO postgres;

--
-- Name: etiquetas_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.etiquetas_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.etiquetas_id_seq OWNER TO postgres;

--
-- Name: etiquetas_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.etiquetas_id_seq OWNED BY public.etiquetas.id;


--
-- Name: favoritos; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.favoritos (
    id bigint NOT NULL,
    fecha_guardado timestamp(6) without time zone NOT NULL,
    empleador_id bigint NOT NULL,
    trabajador_id bigint NOT NULL
);


ALTER TABLE public.favoritos OWNER TO postgres;

--
-- Name: favoritos_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.favoritos_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.favoritos_id_seq OWNER TO postgres;

--
-- Name: favoritos_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.favoritos_id_seq OWNED BY public.favoritos.id;


--
-- Name: horarios_disponibilidad; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.horarios_disponibilidad (
    id bigint NOT NULL,
    dia_semana character varying(20) NOT NULL,
    hora_fin time(6) without time zone NOT NULL,
    hora_inicio time(6) without time zone NOT NULL,
    trabajador_id bigint NOT NULL
);


ALTER TABLE public.horarios_disponibilidad OWNER TO postgres;

--
-- Name: horarios_disponibilidad_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.horarios_disponibilidad_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.horarios_disponibilidad_id_seq OWNER TO postgres;

--
-- Name: horarios_disponibilidad_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.horarios_disponibilidad_id_seq OWNED BY public.horarios_disponibilidad.id;


--
-- Name: notificaciones; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.notificaciones (
    id bigint NOT NULL,
    fecha_creacion timestamp(6) without time zone NOT NULL,
    leida boolean NOT NULL,
    mensaje text NOT NULL,
    referencia_id bigint,
    tipo character varying(30) NOT NULL,
    titulo character varying(150) NOT NULL,
    destinatario_id bigint NOT NULL
);


ALTER TABLE public.notificaciones OWNER TO postgres;

--
-- Name: notificaciones_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.notificaciones_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.notificaciones_id_seq OWNER TO postgres;

--
-- Name: notificaciones_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.notificaciones_id_seq OWNED BY public.notificaciones.id;


--
-- Name: oportunidad_etiqueta; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.oportunidad_etiqueta (
    oportunidad_id bigint NOT NULL,
    etiqueta_id bigint NOT NULL
);


ALTER TABLE public.oportunidad_etiqueta OWNER TO postgres;

--
-- Name: oportunidades_laborales; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.oportunidades_laborales (
    activa boolean NOT NULL,
    modalidad character varying(50),
    rango_salarial character varying(100),
    ubicacion character varying(100),
    post_id bigint NOT NULL,
    destacada boolean DEFAULT false NOT NULL
);


ALTER TABLE public.oportunidades_laborales OWNER TO postgres;

--
-- Name: perfiles_empresariales; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.perfiles_empresariales (
    id bigint NOT NULL,
    cuit character varying(20),
    descripcion text,
    nombre_empresa character varying(100) NOT NULL,
    sitio_web character varying(150),
    url_logo character varying(255),
    verificado boolean NOT NULL,
    empleador_id bigint NOT NULL,
    supervisado_por_id bigint
);


ALTER TABLE public.perfiles_empresariales OWNER TO postgres;

--
-- Name: perfiles_empresariales_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.perfiles_empresariales_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.perfiles_empresariales_id_seq OWNER TO postgres;

--
-- Name: perfiles_empresariales_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.perfiles_empresariales_id_seq OWNED BY public.perfiles_empresariales.id;


--
-- Name: portafolio_items; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.portafolio_items (
    id bigint NOT NULL,
    descripcion text,
    titulo character varying(120) NOT NULL,
    url_archivo character varying(255),
    trabajador_id bigint NOT NULL
);


ALTER TABLE public.portafolio_items OWNER TO postgres;

--
-- Name: portafolio_items_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.portafolio_items_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.portafolio_items_id_seq OWNER TO postgres;

--
-- Name: portafolio_items_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.portafolio_items_id_seq OWNED BY public.portafolio_items.id;


--
-- Name: posts_base; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.posts_base (
    id bigint NOT NULL,
    contenido text NOT NULL,
    fecha_creacion timestamp(6) without time zone NOT NULL,
    titulo character varying(150) NOT NULL,
    autor_id bigint NOT NULL
);


ALTER TABLE public.posts_base OWNER TO postgres;

--
-- Name: posts_base_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.posts_base_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.posts_base_id_seq OWNER TO postgres;

--
-- Name: posts_base_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.posts_base_id_seq OWNED BY public.posts_base.id;


--
-- Name: postulaciones; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.postulaciones (
    estado character varying(50) NOT NULL,
    remuneracion_pretendida double precision,
    post_id bigint NOT NULL,
    oportunidad_id bigint NOT NULL
);


ALTER TABLE public.postulaciones OWNER TO postgres;

--
-- Name: preferencias_notificacion; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.preferencias_notificacion (
    id bigint NOT NULL,
    alertas_nuevas_ofertas boolean NOT NULL,
    alertas_nuevas_postulaciones boolean NOT NULL,
    notificar_email boolean NOT NULL,
    notificar_push boolean NOT NULL,
    trabajador_id bigint NOT NULL
);


ALTER TABLE public.preferencias_notificacion OWNER TO postgres;

--
-- Name: preferencias_notificacion_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.preferencias_notificacion_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.preferencias_notificacion_id_seq OWNER TO postgres;

--
-- Name: preferencias_notificacion_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.preferencias_notificacion_id_seq OWNED BY public.preferencias_notificacion.id;


--
-- Name: publicaciones_foro; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.publicaciones_foro (
    id bigint NOT NULL,
    contenido text NOT NULL,
    fecha_creacion timestamp(6) without time zone NOT NULL,
    titulo character varying(150) NOT NULL,
    autor_id bigint NOT NULL
);


ALTER TABLE public.publicaciones_foro OWNER TO postgres;

--
-- Name: publicaciones_foro_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.publicaciones_foro_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.publicaciones_foro_id_seq OWNER TO postgres;

--
-- Name: publicaciones_foro_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.publicaciones_foro_id_seq OWNED BY public.publicaciones_foro.id;


--
-- Name: publicidades; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.publicidades (
    id bigint NOT NULL,
    activa boolean NOT NULL,
    contenido text,
    fecha_fin date NOT NULL,
    fecha_inicio date NOT NULL,
    titulo character varying(120) NOT NULL,
    url_destino character varying(255),
    url_imagen character varying(255),
    anunciante_id bigint NOT NULL
);


ALTER TABLE public.publicidades OWNER TO postgres;

--
-- Name: publicidades_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.publicidades_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.publicidades_id_seq OWNER TO postgres;

--
-- Name: publicidades_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.publicidades_id_seq OWNED BY public.publicidades.id;


--
-- Name: reportes; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.reportes (
    id bigint NOT NULL,
    descripcion text NOT NULL,
    estado character varying(30) NOT NULL,
    fecha_creacion timestamp(6) without time zone NOT NULL,
    motivo character varying(100) NOT NULL,
    moderador_id bigint,
    reportado_id bigint,
    reportante_id bigint NOT NULL
);


ALTER TABLE public.reportes OWNER TO postgres;

--
-- Name: reportes_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.reportes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.reportes_id_seq OWNER TO postgres;

--
-- Name: reportes_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.reportes_id_seq OWNED BY public.reportes.id;


--
-- Name: suscripciones; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.suscripciones (
    id bigint NOT NULL,
    activa boolean NOT NULL,
    fecha_fin date,
    fecha_inicio date NOT NULL,
    nivel character varying(50) NOT NULL,
    usuario_id bigint NOT NULL
);


ALTER TABLE public.suscripciones OWNER TO postgres;

--
-- Name: suscripciones_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.suscripciones_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.suscripciones_id_seq OWNER TO postgres;

--
-- Name: suscripciones_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.suscripciones_id_seq OWNED BY public.suscripciones.id;


--
-- Name: tokens_revocados; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tokens_revocados (
    id bigint NOT NULL,
    expira timestamp(6) without time zone NOT NULL,
    jti character varying(100) NOT NULL,
    sujeto character varying(150)
);


ALTER TABLE public.tokens_revocados OWNER TO postgres;

--
-- Name: tokens_revocados_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.tokens_revocados_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.tokens_revocados_id_seq OWNER TO postgres;

--
-- Name: tokens_revocados_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.tokens_revocados_id_seq OWNED BY public.tokens_revocados.id;


--
-- Name: trabajador_etiqueta; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.trabajador_etiqueta (
    trabajador_id bigint NOT NULL,
    etiqueta_id bigint NOT NULL
);


ALTER TABLE public.trabajador_etiqueta OWNER TO postgres;

--
-- Name: trabajadores; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.trabajadores (
    biografia text,
    certificados text,
    fotografia character varying(255),
    sector character varying(100),
    token_push character varying(255),
    usuario_id bigint NOT NULL
);


ALTER TABLE public.trabajadores OWNER TO postgres;

--
-- Name: usuarios; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.usuarios (
    id bigint NOT NULL,
    email character varying(150) NOT NULL,
    email_verificado boolean NOT NULL,
    habilitado boolean NOT NULL,
    nombre character varying(100) NOT NULL,
    password character varying(255) NOT NULL,
    password_reset_token character varying(100),
    password_reset_token_expiry timestamp(6) without time zone,
    telefono character varying(20) NOT NULL
);


ALTER TABLE public.usuarios OWNER TO postgres;

--
-- Name: usuarios_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.usuarios_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.usuarios_id_seq OWNER TO postgres;

--
-- Name: usuarios_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.usuarios_id_seq OWNED BY public.usuarios.id;


--
-- Name: valoraciones; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.valoraciones (
    id bigint NOT NULL,
    comentario text,
    fecha_creacion timestamp(6) without time zone NOT NULL,
    puntuacion integer NOT NULL,
    autor_id bigint NOT NULL,
    trabajador_id bigint NOT NULL,
    CONSTRAINT valoraciones_puntuacion_check CHECK (((puntuacion <= 5) AND (puntuacion >= 1)))
);


ALTER TABLE public.valoraciones OWNER TO postgres;

--
-- Name: valoraciones_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.valoraciones_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.valoraciones_id_seq OWNER TO postgres;

--
-- Name: valoraciones_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.valoraciones_id_seq OWNED BY public.valoraciones.id;


--
-- Name: comunicados id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.comunicados ALTER COLUMN id SET DEFAULT nextval('public.comunicados_id_seq'::regclass);


--
-- Name: donaciones id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.donaciones ALTER COLUMN id SET DEFAULT nextval('public.donaciones_id_seq'::regclass);


--
-- Name: etiquetas id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.etiquetas ALTER COLUMN id SET DEFAULT nextval('public.etiquetas_id_seq'::regclass);


--
-- Name: favoritos id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.favoritos ALTER COLUMN id SET DEFAULT nextval('public.favoritos_id_seq'::regclass);


--
-- Name: horarios_disponibilidad id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.horarios_disponibilidad ALTER COLUMN id SET DEFAULT nextval('public.horarios_disponibilidad_id_seq'::regclass);


--
-- Name: notificaciones id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.notificaciones ALTER COLUMN id SET DEFAULT nextval('public.notificaciones_id_seq'::regclass);


--
-- Name: perfiles_empresariales id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.perfiles_empresariales ALTER COLUMN id SET DEFAULT nextval('public.perfiles_empresariales_id_seq'::regclass);


--
-- Name: portafolio_items id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.portafolio_items ALTER COLUMN id SET DEFAULT nextval('public.portafolio_items_id_seq'::regclass);


--
-- Name: posts_base id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.posts_base ALTER COLUMN id SET DEFAULT nextval('public.posts_base_id_seq'::regclass);


--
-- Name: preferencias_notificacion id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.preferencias_notificacion ALTER COLUMN id SET DEFAULT nextval('public.preferencias_notificacion_id_seq'::regclass);


--
-- Name: publicaciones_foro id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.publicaciones_foro ALTER COLUMN id SET DEFAULT nextval('public.publicaciones_foro_id_seq'::regclass);


--
-- Name: publicidades id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.publicidades ALTER COLUMN id SET DEFAULT nextval('public.publicidades_id_seq'::regclass);


--
-- Name: reportes id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.reportes ALTER COLUMN id SET DEFAULT nextval('public.reportes_id_seq'::regclass);


--
-- Name: suscripciones id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.suscripciones ALTER COLUMN id SET DEFAULT nextval('public.suscripciones_id_seq'::regclass);


--
-- Name: tokens_revocados id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tokens_revocados ALTER COLUMN id SET DEFAULT nextval('public.tokens_revocados_id_seq'::regclass);


--
-- Name: usuarios id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usuarios ALTER COLUMN id SET DEFAULT nextval('public.usuarios_id_seq'::regclass);


--
-- Name: valoraciones id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.valoraciones ALTER COLUMN id SET DEFAULT nextval('public.valoraciones_id_seq'::regclass);


--
-- Data for Name: administradores; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.administradores (nivel_acceso, usuario_id) VALUES ('SUPER', 1);
INSERT INTO public.administradores (nivel_acceso, usuario_id) VALUES ('SUPERVISOR', 5);
INSERT INTO public.administradores (nivel_acceso, usuario_id) VALUES ('SUPER', 6);


--
-- Data for Name: comunicados; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.comunicados (id, alcance, contenido, fecha_emision, titulo, administrador_id) VALUES (1, 'GENERAL', 'Ventana domingo 02:00', '2026-10-09 20:26:06.061925', 'Mantenimiento', 1);
INSERT INTO public.comunicados (id, alcance, contenido, fecha_emision, titulo, administrador_id) VALUES (2, 'GENERAL', 'Ventana domingo 02:00', '2026-10-09 20:31:43.689381', 'Mantenimiento', 1);


--
-- Data for Name: donaciones; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.donaciones (id, asunto_transferencia, banco, estado, fecha_donacion, mecanismo_pago, moneda, monto, tipo_transferencia, usuario_id) VALUES (1, 'Aporte mensual', 'Nación', 'VERIFICADO', '2026-10-09 20:26:05.976763', 'TRANSFERENCIA', 'ARS', 5000.00, 'TED', 7);
INSERT INTO public.donaciones (id, asunto_transferencia, banco, estado, fecha_donacion, mecanismo_pago, moneda, monto, tipo_transferencia, usuario_id) VALUES (2, 'Aporte mensual', 'Nación', 'VERIFICADO', '2026-10-09 20:31:43.630641', 'TRANSFERENCIA', 'ARS', 5000.00, 'TED', 7);


--
-- Data for Name: empleadores; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.empleadores (usuario_id) VALUES (2);
INSERT INTO public.empleadores (usuario_id) VALUES (11);
INSERT INTO public.empleadores (usuario_id) VALUES (12);
INSERT INTO public.empleadores (usuario_id) VALUES (15);
INSERT INTO public.empleadores (usuario_id) VALUES (16);


--
-- Data for Name: etiquetas; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.etiquetas (id, nombre) VALUES (1, 'Plomería');
INSERT INTO public.etiquetas (id, nombre) VALUES (2, 'Electricidad');
INSERT INTO public.etiquetas (id, nombre) VALUES (3, 'Pintura');
INSERT INTO public.etiquetas (id, nombre) VALUES (4, 'Jardinería');
INSERT INTO public.etiquetas (id, nombre) VALUES (5, 'Limpieza');
INSERT INTO public.etiquetas (id, nombre) VALUES (6, 'Carpintería');
INSERT INTO public.etiquetas (id, nombre) VALUES (7, 'Albañilería');


--
-- Data for Name: favoritos; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.favoritos (id, fecha_guardado, empleador_id, trabajador_id) VALUES (1, '2026-10-07 01:08:29.436707', 2, 3);
INSERT INTO public.favoritos (id, fecha_guardado, empleador_id, trabajador_id) VALUES (2, '2026-10-09 20:26:06.018902', 11, 7);


--
-- Data for Name: horarios_disponibilidad; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.horarios_disponibilidad (id, dia_semana, hora_fin, hora_inicio, trabajador_id) VALUES (1, 'LUNES', '13:00:00', '09:00:00', 7);


--
-- Data for Name: notificaciones; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.notificaciones (id, fecha_creacion, leida, mensaje, referencia_id, tipo, titulo, destinatario_id) VALUES (1, '2026-10-09 20:26:05.650853', false, 'Ana Gómez se postuló a tu oferta "Reparación eléctrica".', 9, 'POSTULACION_RECIBIDA', 'Nueva postulación recibida', 11);
INSERT INTO public.notificaciones (id, fecha_creacion, leida, mensaje, referencia_id, tipo, titulo, destinatario_id) VALUES (2, '2026-10-09 20:26:05.707227', false, 'Bruno Díaz se postuló a tu oferta "Corte de pasto".', 10, 'POSTULACION_RECIBIDA', 'Nueva postulación recibida', 12);
INSERT INTO public.notificaciones (id, fecha_creacion, leida, mensaje, referencia_id, tipo, titulo, destinatario_id) VALUES (3, '2026-10-09 20:26:05.746931', false, 'Carla Ruiz se postuló a tu oferta "Reparación eléctrica".', 11, 'POSTULACION_RECIBIDA', 'Nueva postulación recibida', 11);
INSERT INTO public.notificaciones (id, fecha_creacion, leida, mensaje, referencia_id, tipo, titulo, destinatario_id) VALUES (4, '2026-10-09 20:26:05.786065', false, 'Diego Paz se postuló a tu oferta "Corte de pasto".', 12, 'POSTULACION_RECIBIDA', 'Nueva postulación recibida', 12);
INSERT INTO public.notificaciones (id, fecha_creacion, leida, mensaje, referencia_id, tipo, titulo, destinatario_id) VALUES (5, '2026-10-09 20:26:05.805808', false, 'Tu postulación fue seleccionada en la oferta "Reparación eléctrica".', 9, 'POSTULACION_ESTADO', 'Tu postulación fue seleccionada', 7);
INSERT INTO public.notificaciones (id, fecha_creacion, leida, mensaje, referencia_id, tipo, titulo, destinatario_id) VALUES (6, '2026-10-09 20:26:05.823344', false, 'Tu postulación fue rechazada en la oferta "Corte de pasto".', 10, 'POSTULACION_ESTADO', 'Tu postulación fue rechazada', 8);
INSERT INTO public.notificaciones (id, fecha_creacion, leida, mensaje, referencia_id, tipo, titulo, destinatario_id) VALUES (7, '2026-10-09 20:26:05.845042', false, 'Se retiró una postulación de tu oferta "Corte de pasto".', 12, 'POSTULACION_ESTADO', 'Un trabajador retiró su postulación', 12);
INSERT INTO public.notificaciones (id, fecha_creacion, leida, mensaje, referencia_id, tipo, titulo, destinatario_id) VALUES (8, '2026-10-09 20:31:43.473641', false, 'Ana Gómez se postuló a tu oferta "Reparación eléctrica".', 16, 'POSTULACION_RECIBIDA', 'Nueva postulación recibida', 11);
INSERT INTO public.notificaciones (id, fecha_creacion, leida, mensaje, referencia_id, tipo, titulo, destinatario_id) VALUES (9, '2026-10-09 20:31:43.489916', false, 'Bruno Díaz se postuló a tu oferta "Corte de pasto".', 17, 'POSTULACION_RECIBIDA', 'Nueva postulación recibida', 12);
INSERT INTO public.notificaciones (id, fecha_creacion, leida, mensaje, referencia_id, tipo, titulo, destinatario_id) VALUES (10, '2026-10-09 20:31:43.503703', false, 'Carla Ruiz se postuló a tu oferta "Reparación eléctrica".', 18, 'POSTULACION_RECIBIDA', 'Nueva postulación recibida', 11);
INSERT INTO public.notificaciones (id, fecha_creacion, leida, mensaje, referencia_id, tipo, titulo, destinatario_id) VALUES (11, '2026-10-09 20:31:43.516731', false, 'Diego Paz se postuló a tu oferta "Corte de pasto".', 19, 'POSTULACION_RECIBIDA', 'Nueva postulación recibida', 12);
INSERT INTO public.notificaciones (id, fecha_creacion, leida, mensaje, referencia_id, tipo, titulo, destinatario_id) VALUES (12, '2026-10-09 20:31:43.528741', false, 'Tu postulación fue seleccionada en la oferta "Reparación eléctrica".', 16, 'POSTULACION_ESTADO', 'Tu postulación fue seleccionada', 7);
INSERT INTO public.notificaciones (id, fecha_creacion, leida, mensaje, referencia_id, tipo, titulo, destinatario_id) VALUES (13, '2026-10-09 20:31:43.540595', false, 'Tu postulación fue rechazada en la oferta "Corte de pasto".', 17, 'POSTULACION_ESTADO', 'Tu postulación fue rechazada', 8);
INSERT INTO public.notificaciones (id, fecha_creacion, leida, mensaje, referencia_id, tipo, titulo, destinatario_id) VALUES (14, '2026-10-09 20:31:43.554609', false, 'Se retiró una postulación de tu oferta "Corte de pasto".', 19, 'POSTULACION_ESTADO', 'Un trabajador retiró su postulación', 12);


--
-- Data for Name: oportunidad_etiqueta; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.oportunidad_etiqueta (oportunidad_id, etiqueta_id) VALUES (1, 1);
INSERT INTO public.oportunidad_etiqueta (oportunidad_id, etiqueta_id) VALUES (5, 2);
INSERT INTO public.oportunidad_etiqueta (oportunidad_id, etiqueta_id) VALUES (6, 3);
INSERT INTO public.oportunidad_etiqueta (oportunidad_id, etiqueta_id) VALUES (7, 4);
INSERT INTO public.oportunidad_etiqueta (oportunidad_id, etiqueta_id) VALUES (13, 2);
INSERT INTO public.oportunidad_etiqueta (oportunidad_id, etiqueta_id) VALUES (14, 3);
INSERT INTO public.oportunidad_etiqueta (oportunidad_id, etiqueta_id) VALUES (15, 4);


--
-- Data for Name: oportunidades_laborales; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.oportunidades_laborales (activa, modalidad, rango_salarial, ubicacion, post_id, destacada) VALUES (true, 'PRESENCIAL', '50000-80000', 'Belgrano', 1, false);
INSERT INTO public.oportunidades_laborales (activa, modalidad, rango_salarial, ubicacion, post_id, destacada) VALUES (true, 'PRESENCIAL', '50000/60000', 'San Martín 147', 3, false);
INSERT INTO public.oportunidades_laborales (activa, modalidad, rango_salarial, ubicacion, post_id, destacada) VALUES (true, 'PRESENCIAL', '80000-120000', 'Centro', 5, true);
INSERT INTO public.oportunidades_laborales (activa, modalidad, rango_salarial, ubicacion, post_id, destacada) VALUES (true, 'PRESENCIAL', '150000-200000', 'Norte', 6, false);
INSERT INTO public.oportunidades_laborales (activa, modalidad, rango_salarial, ubicacion, post_id, destacada) VALUES (true, 'PRESENCIAL', '30000-50000', 'Sur', 7, false);
INSERT INTO public.oportunidades_laborales (activa, modalidad, rango_salarial, ubicacion, post_id, destacada) VALUES (true, 'REMOTO', '20000-30000', 'Remoto', 8, false);
INSERT INTO public.oportunidades_laborales (activa, modalidad, rango_salarial, ubicacion, post_id, destacada) VALUES (true, 'PRESENCIAL', '80000-120000', 'Centro', 13, true);
INSERT INTO public.oportunidades_laborales (activa, modalidad, rango_salarial, ubicacion, post_id, destacada) VALUES (true, 'PRESENCIAL', '150000-200000', 'Norte', 14, false);
INSERT INTO public.oportunidades_laborales (activa, modalidad, rango_salarial, ubicacion, post_id, destacada) VALUES (true, 'PRESENCIAL', '30000-50000', 'Sur', 15, false);


--
-- Data for Name: perfiles_empresariales; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.perfiles_empresariales (id, cuit, descripcion, nombre_empresa, sitio_web, url_logo, verificado, empleador_id, supervisado_por_id) VALUES (1, '30-12345678-9', NULL, 'Empresa Sur SRL', NULL, NULL, false, 11, NULL);


--
-- Data for Name: portafolio_items; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.portafolio_items (id, descripcion, titulo, url_archivo, trabajador_id) VALUES (1, 'Placard de roble', 'Mueble a medida', NULL, 9);
INSERT INTO public.portafolio_items (id, descripcion, titulo, url_archivo, trabajador_id) VALUES (2, 'Placard de roble', 'Mueble a medida', NULL, 9);


--
-- Data for Name: posts_base; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.posts_base (id, contenido, fecha_creacion, titulo, autor_id) VALUES (1, 'Se necesita plomero matriculado en Belgrano', '2026-10-05 18:12:19.12383', 'Arreglo de cañería', 2);
INSERT INTO public.posts_base (id, contenido, fecha_creacion, titulo, autor_id) VALUES (2, 'Soy plomero matriculado', '2026-10-05 18:12:29.045333', 'Me postulo', 3);
INSERT INTO public.posts_base (id, contenido, fecha_creacion, titulo, autor_id) VALUES (3, 'Necesito una persona que pueda hacer tramites de jubilación. ', '2026-10-07 01:07:50.565936', 'Tramite de jubilación', 2);
INSERT INTO public.posts_base (id, contenido, fecha_creacion, titulo, autor_id) VALUES (4, 'Me interesa, puedo pasar mañana a buscar la diguiente documentación. 
- DNI
- Recibos.
- Documentos asociados a aportes. 
', '2026-10-07 01:17:39.790726', 'Postulación a: Tramite de jubilación', 3);
INSERT INTO public.posts_base (id, contenido, fecha_creacion, titulo, autor_id) VALUES (5, 'Tablero y enchufes', '2026-10-09 20:26:05.551344', 'Reparación eléctrica', 11);
INSERT INTO public.posts_base (id, contenido, fecha_creacion, titulo, autor_id) VALUES (6, 'Dos ambientes', '2026-10-09 20:26:05.571581', 'Pintura departamento', 11);
INSERT INTO public.posts_base (id, contenido, fecha_creacion, titulo, autor_id) VALUES (7, 'Jardín amplio', '2026-10-09 20:26:05.592683', 'Corte de pasto', 12);
INSERT INTO public.posts_base (id, contenido, fecha_creacion, titulo, autor_id) VALUES (8, 'Limpieza de virus', '2026-10-09 20:26:05.611404', 'Soporte remoto PC', 12);
INSERT INTO public.posts_base (id, contenido, fecha_creacion, titulo, autor_id) VALUES (9, 'Me interesa', '2026-10-09 20:26:05.638888', 'Postulación a Reparación eléctrica', 7);
INSERT INTO public.posts_base (id, contenido, fecha_creacion, titulo, autor_id) VALUES (10, 'Me interesa', '2026-10-09 20:26:05.700243', 'Postulación a Corte de pasto', 8);
INSERT INTO public.posts_base (id, contenido, fecha_creacion, titulo, autor_id) VALUES (11, 'Me interesa', '2026-10-09 20:26:05.741905', 'Postulación a Reparación eléctrica', 9);
INSERT INTO public.posts_base (id, contenido, fecha_creacion, titulo, autor_id) VALUES (12, 'Me interesa', '2026-10-09 20:26:05.78228', 'Postulación a Corte de pasto', 10);
INSERT INTO public.posts_base (id, contenido, fecha_creacion, titulo, autor_id) VALUES (13, 'Tablero y enchufes', '2026-10-09 20:29:57.784786', 'Reparación eléctrica', 11);
INSERT INTO public.posts_base (id, contenido, fecha_creacion, titulo, autor_id) VALUES (14, 'Dos ambientes', '2026-10-09 20:29:57.804654', 'Pintura departamento', 11);
INSERT INTO public.posts_base (id, contenido, fecha_creacion, titulo, autor_id) VALUES (15, 'Jardín amplio', '2026-10-09 20:29:57.824953', 'Corte de pasto', 12);
INSERT INTO public.posts_base (id, contenido, fecha_creacion, titulo, autor_id) VALUES (16, 'Me interesa', '2026-10-09 20:31:43.467806', 'Postulación a Reparación eléctrica', 7);
INSERT INTO public.posts_base (id, contenido, fecha_creacion, titulo, autor_id) VALUES (17, 'Me interesa', '2026-10-09 20:31:43.487724', 'Postulación a Corte de pasto', 8);
INSERT INTO public.posts_base (id, contenido, fecha_creacion, titulo, autor_id) VALUES (18, 'Me interesa', '2026-10-09 20:31:43.501271', 'Postulación a Reparación eléctrica', 9);
INSERT INTO public.posts_base (id, contenido, fecha_creacion, titulo, autor_id) VALUES (19, 'Me interesa', '2026-10-09 20:31:43.51477', 'Postulación a Corte de pasto', 10);


--
-- Data for Name: postulaciones; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.postulaciones (estado, remuneracion_pretendida, post_id, oportunidad_id) VALUES ('COMPLETADA', 60000, 2, 1);
INSERT INTO public.postulaciones (estado, remuneracion_pretendida, post_id, oportunidad_id) VALUES ('PENDIENTE', 50000, 4, 3);
INSERT INTO public.postulaciones (estado, remuneracion_pretendida, post_id, oportunidad_id) VALUES ('PENDIENTE', NULL, 11, 5);
INSERT INTO public.postulaciones (estado, remuneracion_pretendida, post_id, oportunidad_id) VALUES ('SELECCIONADA', NULL, 9, 5);
INSERT INTO public.postulaciones (estado, remuneracion_pretendida, post_id, oportunidad_id) VALUES ('RECHAZADA', NULL, 10, 7);
INSERT INTO public.postulaciones (estado, remuneracion_pretendida, post_id, oportunidad_id) VALUES ('RETIRADA', NULL, 12, 7);
INSERT INTO public.postulaciones (estado, remuneracion_pretendida, post_id, oportunidad_id) VALUES ('PENDIENTE', NULL, 18, 13);
INSERT INTO public.postulaciones (estado, remuneracion_pretendida, post_id, oportunidad_id) VALUES ('SELECCIONADA', NULL, 16, 13);
INSERT INTO public.postulaciones (estado, remuneracion_pretendida, post_id, oportunidad_id) VALUES ('RECHAZADA', NULL, 17, 15);
INSERT INTO public.postulaciones (estado, remuneracion_pretendida, post_id, oportunidad_id) VALUES ('RETIRADA', NULL, 19, 15);


--
-- Data for Name: preferencias_notificacion; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: publicaciones_foro; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.publicaciones_foro (id, contenido, fecha_creacion, titulo, autor_id) VALUES (1, 'Usen cinta de enmascarar', '2026-10-09 20:26:06.038641', 'Consejos de pintura', 7);
INSERT INTO public.publicaciones_foro (id, contenido, fecha_creacion, titulo, autor_id) VALUES (2, 'Usen cinta de enmascarar', '2026-10-09 20:31:43.67382', 'Consejos de pintura', 7);


--
-- Data for Name: publicidades; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.publicidades (id, activa, contenido, fecha_fin, fecha_inicio, titulo, url_destino, url_imagen, anunciante_id) VALUES (1, true, 'Descuento', '2026-12-31', '2026-10-01', 'Promo Sure', NULL, NULL, 11);
INSERT INTO public.publicidades (id, activa, contenido, fecha_fin, fecha_inicio, titulo, url_destino, url_imagen, anunciante_id) VALUES (2, true, 'Descuento', '2026-12-31', '2026-10-01', 'Promo Sure', NULL, NULL, 11);


--
-- Data for Name: reportes; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.reportes (id, descripcion, estado, fecha_creacion, motivo, moderador_id, reportado_id, reportante_id) VALUES (1, 'Publicación dudosa', 'RESUELTO', '2026-10-09 20:26:05.936388', 'Spam', 1, NULL, 7);
INSERT INTO public.reportes (id, descripcion, estado, fecha_creacion, motivo, moderador_id, reportado_id, reportante_id) VALUES (2, 'Publicación dudosa', 'RESUELTO', '2026-10-09 20:31:43.593693', 'Spam', 1, NULL, 7);


--
-- Data for Name: suscripciones; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.suscripciones (id, activa, fecha_fin, fecha_inicio, nivel, usuario_id) VALUES (1, true, NULL, '2026-10-07', 'PRO', 2);
INSERT INTO public.suscripciones (id, activa, fecha_fin, fecha_inicio, nivel, usuario_id) VALUES (2, true, NULL, '2026-10-01', 'PRO', 7);
INSERT INTO public.suscripciones (id, activa, fecha_fin, fecha_inicio, nivel, usuario_id) VALUES (3, true, NULL, '2026-10-01', 'PRO', 9);
INSERT INTO public.suscripciones (id, activa, fecha_fin, fecha_inicio, nivel, usuario_id) VALUES (4, true, NULL, '2026-10-01', 'PRO', 11);
INSERT INTO public.suscripciones (id, activa, fecha_fin, fecha_inicio, nivel, usuario_id) VALUES (5, true, NULL, '2026-10-01', 'PRO', 13);
INSERT INTO public.suscripciones (id, activa, fecha_fin, fecha_inicio, nivel, usuario_id) VALUES (6, true, NULL, '2026-10-01', 'PRO', 15);


--
-- Data for Name: tokens_revocados; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.tokens_revocados (id, expira, jti, sujeto) VALUES (5, '2026-10-10 20:07:53', 'b0989b90-dea6-48e6-9cbe-e064fbac0b0e', 'adminnuevo@allado.com');
INSERT INTO public.tokens_revocados (id, expira, jti, sujeto) VALUES (6, '2026-10-10 20:21:29', '2586ccdb-443d-440a-b507-432459958cd8', 'adminnuevo@allado.com');
INSERT INTO public.tokens_revocados (id, expira, jti, sujeto) VALUES (7, '2026-10-10 20:33:04', '8f293159-e56f-4bca-adde-2668941ef14c', 'ana@lado.com');
INSERT INTO public.tokens_revocados (id, expira, jti, sujeto) VALUES (8, '2026-10-10 20:34:20', '0c405d2b-0883-49db-a94f-c143a0872f27', 'tra2@lado.com');


--
-- Data for Name: trabajador_etiqueta; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.trabajador_etiqueta (trabajador_id, etiqueta_id) VALUES (13, 1);
INSERT INTO public.trabajador_etiqueta (trabajador_id, etiqueta_id) VALUES (14, 2);
INSERT INTO public.trabajador_etiqueta (trabajador_id, etiqueta_id) VALUES (7, 2);
INSERT INTO public.trabajador_etiqueta (trabajador_id, etiqueta_id) VALUES (7, 3);
INSERT INTO public.trabajador_etiqueta (trabajador_id, etiqueta_id) VALUES (8, 4);
INSERT INTO public.trabajador_etiqueta (trabajador_id, etiqueta_id) VALUES (9, 5);
INSERT INTO public.trabajador_etiqueta (trabajador_id, etiqueta_id) VALUES (9, 6);
INSERT INTO public.trabajador_etiqueta (trabajador_id, etiqueta_id) VALUES (10, 7);


--
-- Data for Name: trabajadores; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.trabajadores (biografia, certificados, fotografia, sector, token_push, usuario_id) VALUES ('Tengo amplia experiencia en tramites legales asociados al ambito laboral. 
', 'Abogado laboral', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTBVp14Xw1koQ_y07fZFYvANLzZH8vMO7eksqQGizCRuNulxPnppg6pGYPT&s=10', NULL, NULL, 3);
INSERT INTO public.trabajadores (biografia, certificados, fotografia, sector, token_push, usuario_id) VALUES (NULL, NULL, NULL, 'Centro', NULL, 7);
INSERT INTO public.trabajadores (biografia, certificados, fotografia, sector, token_push, usuario_id) VALUES (NULL, NULL, NULL, 'Sur', NULL, 8);
INSERT INTO public.trabajadores (biografia, certificados, fotografia, sector, token_push, usuario_id) VALUES (NULL, NULL, NULL, 'Norte', NULL, 9);
INSERT INTO public.trabajadores (biografia, certificados, fotografia, sector, token_push, usuario_id) VALUES (NULL, NULL, NULL, 'Oeste', NULL, 10);
INSERT INTO public.trabajadores (biografia, certificados, fotografia, sector, token_push, usuario_id) VALUES (NULL, NULL, NULL, 'Centro', NULL, 13);
INSERT INTO public.trabajadores (biografia, certificados, fotografia, sector, token_push, usuario_id) VALUES (NULL, NULL, NULL, 'Sur', NULL, 14);


--
-- Data for Name: usuarios; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.usuarios (id, email, email_verificado, habilitado, nombre, password, password_reset_token, password_reset_token_expiry, telefono) VALUES (1, 'admin@allado.com', true, true, 'Admin', '$2a$10$iN.pYfMIeIq0VeIx/UlxAugC1Yx26.qcKzkXcuuME.D3bvLo9iPO2', NULL, NULL, '+549110000001');
INSERT INTO public.usuarios (id, email, email_verificado, habilitado, nombre, password, password_reset_token, password_reset_token_expiry, telefono) VALUES (2, 'empleador@demo.com', false, true, 'Demo Empleador', '$2a$10$HU16HEfw9enmRd3uGI5bgute8bi1QRuSAzHueJ6DqCVzOkaKwTR8m', NULL, NULL, '+549110000002');
INSERT INTO public.usuarios (id, email, email_verificado, habilitado, nombre, password, password_reset_token, password_reset_token_expiry, telefono) VALUES (3, 'trabajador@demo.com', false, true, 'Demo Trabajador', '$2a$10$r7b6EO4ej5ytP055t0Jsx.hZEbk/ITt7PO3ou.eJp3r3l6CnsiHgC', NULL, NULL, '+549110000003');
INSERT INTO public.usuarios (id, email, email_verificado, habilitado, nombre, password, password_reset_token, password_reset_token_expiry, telefono) VALUES (5, 'adminnuevo@allado.com', false, true, 'Alfredo', '$2a$10$50iVm9lmmbfq4f4ojzL73.SkcEZcfuruLkDVOZUVB4eWMRPv8jrHS', NULL, NULL, '3571224455');
INSERT INTO public.usuarios (id, email, email_verificado, habilitado, nombre, password, password_reset_token, password_reset_token_expiry, telefono) VALUES (6, 'admin@lado.com', true, true, 'Admin', '$2a$10$LHx31XwbxzgEqWE0xtiOkOryBM5F7qTJYPZ5irrTkpApnW4P92H46', NULL, NULL, '+549110000001');
INSERT INTO public.usuarios (id, email, email_verificado, habilitado, nombre, password, password_reset_token, password_reset_token_expiry, telefono) VALUES (7, 'ana@lado.com', false, true, 'Ana Gómez', '$2a$10$c5S5f7/.6o7Y/NSdKWOoj.1FV/vMlUgw0AOl6r1kYmYvJXfdYDzxO', NULL, NULL, '+549110000011');
INSERT INTO public.usuarios (id, email, email_verificado, habilitado, nombre, password, password_reset_token, password_reset_token_expiry, telefono) VALUES (8, 'bruno@lado.com', false, true, 'Bruno Díaz', '$2a$10$w34bn8YANGImtWRMRHFqfObiE2hRptJz7eaymXi8HRrU/TEwryJGC', NULL, NULL, '+549110000012');
INSERT INTO public.usuarios (id, email, email_verificado, habilitado, nombre, password, password_reset_token, password_reset_token_expiry, telefono) VALUES (9, 'carla@lado.com', false, true, 'Carla Ruiz', '$2a$10$RvjyRnnm5gf2nECP5sd.fO11Tlgsi8JRD8Gdd3rmya./1K8ntandm', NULL, NULL, '+549110000013');
INSERT INTO public.usuarios (id, email, email_verificado, habilitado, nombre, password, password_reset_token, password_reset_token_expiry, telefono) VALUES (10, 'diego@lado.com', false, true, 'Diego Paz', '$2a$10$HxbwpRFTjDo27xChUNvUiOQDTO/kKSSzOcl0hNfWNPtrE3kcicspq', NULL, NULL, '+549110000014');
INSERT INTO public.usuarios (id, email, email_verificado, habilitado, nombre, password, password_reset_token, password_reset_token_expiry, telefono) VALUES (11, 'sure@lado.com', false, true, 'Empresa Sur', '$2a$10$wEleCgHrTu5moEHwGM.Zsu9jtkeL.k6dL8zIMCICFICzmiprIM2Ya', NULL, NULL, '+549110000021');
INSERT INTO public.usuarios (id, email, email_verificado, habilitado, nombre, password, password_reset_token, password_reset_token_expiry, telefono) VALUES (12, 'hogares@lado.com', false, true, 'Hogares SA', '$2a$10$r48KCCp5LXvJmjRNTZa1aOxCuAm.3DRGllUzshXB873pf9enwTk3a', NULL, NULL, '+549110000022');
INSERT INTO public.usuarios (id, email, email_verificado, habilitado, nombre, password, password_reset_token, password_reset_token_expiry, telefono) VALUES (13, 'tra@lado.com', false, true, 'Tra Uno', '$2a$10$G/RI587j5a330TUg6MpzvegCgRLFn/vzf4JMxfly9JNW9LSJUtLrW', NULL, NULL, '+549110000003');
INSERT INTO public.usuarios (id, email, email_verificado, habilitado, nombre, password, password_reset_token, password_reset_token_expiry, telefono) VALUES (14, 'tra2@lado.com', false, true, 'Tra Dos', '$2a$10$6Z2EuTCyidN.FzdrpA6YyeRxJmtYD5tMhbdliYsitEL5eBWEMKmOa', NULL, NULL, '+549110000005');
INSERT INTO public.usuarios (id, email, email_verificado, habilitado, nombre, password, password_reset_token, password_reset_token_expiry, telefono) VALUES (15, 'emp@lado.com', false, true, 'Emp Uno', '$2a$10$HtE8IhAI7MZMc4DPZiDyC.ts6A2qpYjZG7BF6AV3gfkf97v4EuYs6', NULL, NULL, '+549110000002');
INSERT INTO public.usuarios (id, email, email_verificado, habilitado, nombre, password, password_reset_token, password_reset_token_expiry, telefono) VALUES (16, 'emp2@lado.com', false, true, 'Emp Dos', '$2a$10$Es3tc0GgecMvdEUnC0/xiO0IT1g3Ins5PWfT6k4OOV.v.RpJHRDYG', NULL, NULL, '+549110000004');


--
-- Data for Name: valoraciones; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.valoraciones (id, comentario, fecha_creacion, puntuacion, autor_id, trabajador_id) VALUES (1, 'Buen trabajo', '2026-10-09 20:26:05.894554', 5, 11, 7);
INSERT INTO public.valoraciones (id, comentario, fecha_creacion, puntuacion, autor_id, trabajador_id) VALUES (2, 'Buen trabajo', '2026-10-09 20:26:05.917307', 4, 11, 8);


--
-- Name: comunicados_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.comunicados_id_seq', 2, true);


--
-- Name: donaciones_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.donaciones_id_seq', 2, true);


--
-- Name: etiquetas_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.etiquetas_id_seq', 7, true);


--
-- Name: favoritos_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.favoritos_id_seq', 2, true);


--
-- Name: horarios_disponibilidad_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.horarios_disponibilidad_id_seq', 1, true);


--
-- Name: notificaciones_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.notificaciones_id_seq', 14, true);


--
-- Name: perfiles_empresariales_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.perfiles_empresariales_id_seq', 1, true);


--
-- Name: portafolio_items_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.portafolio_items_id_seq', 2, true);


--
-- Name: posts_base_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.posts_base_id_seq', 19, true);


--
-- Name: preferencias_notificacion_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.preferencias_notificacion_id_seq', 1, false);


--
-- Name: publicaciones_foro_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.publicaciones_foro_id_seq', 2, true);


--
-- Name: publicidades_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.publicidades_id_seq', 2, true);


--
-- Name: reportes_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.reportes_id_seq', 2, true);


--
-- Name: suscripciones_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.suscripciones_id_seq', 6, true);


--
-- Name: tokens_revocados_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.tokens_revocados_id_seq', 8, true);


--
-- Name: usuarios_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.usuarios_id_seq', 16, true);


--
-- Name: valoraciones_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.valoraciones_id_seq', 2, true);


--
-- Name: administradores administradores_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.administradores
    ADD CONSTRAINT administradores_pkey PRIMARY KEY (usuario_id);


--
-- Name: comunicados comunicados_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.comunicados
    ADD CONSTRAINT comunicados_pkey PRIMARY KEY (id);


--
-- Name: donaciones donaciones_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.donaciones
    ADD CONSTRAINT donaciones_pkey PRIMARY KEY (id);


--
-- Name: empleadores empleadores_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.empleadores
    ADD CONSTRAINT empleadores_pkey PRIMARY KEY (usuario_id);


--
-- Name: etiquetas etiquetas_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.etiquetas
    ADD CONSTRAINT etiquetas_pkey PRIMARY KEY (id);


--
-- Name: favoritos favoritos_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.favoritos
    ADD CONSTRAINT favoritos_pkey PRIMARY KEY (id);


--
-- Name: horarios_disponibilidad horarios_disponibilidad_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.horarios_disponibilidad
    ADD CONSTRAINT horarios_disponibilidad_pkey PRIMARY KEY (id);


--
-- Name: notificaciones notificaciones_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.notificaciones
    ADD CONSTRAINT notificaciones_pkey PRIMARY KEY (id);


--
-- Name: oportunidad_etiqueta oportunidad_etiqueta_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.oportunidad_etiqueta
    ADD CONSTRAINT oportunidad_etiqueta_pkey PRIMARY KEY (oportunidad_id, etiqueta_id);


--
-- Name: oportunidades_laborales oportunidades_laborales_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.oportunidades_laborales
    ADD CONSTRAINT oportunidades_laborales_pkey PRIMARY KEY (post_id);


--
-- Name: perfiles_empresariales perfiles_empresariales_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.perfiles_empresariales
    ADD CONSTRAINT perfiles_empresariales_pkey PRIMARY KEY (id);


--
-- Name: portafolio_items portafolio_items_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.portafolio_items
    ADD CONSTRAINT portafolio_items_pkey PRIMARY KEY (id);


--
-- Name: posts_base posts_base_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.posts_base
    ADD CONSTRAINT posts_base_pkey PRIMARY KEY (id);


--
-- Name: postulaciones postulaciones_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.postulaciones
    ADD CONSTRAINT postulaciones_pkey PRIMARY KEY (post_id);


--
-- Name: preferencias_notificacion preferencias_notificacion_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.preferencias_notificacion
    ADD CONSTRAINT preferencias_notificacion_pkey PRIMARY KEY (id);


--
-- Name: publicaciones_foro publicaciones_foro_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.publicaciones_foro
    ADD CONSTRAINT publicaciones_foro_pkey PRIMARY KEY (id);


--
-- Name: publicidades publicidades_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.publicidades
    ADD CONSTRAINT publicidades_pkey PRIMARY KEY (id);


--
-- Name: reportes reportes_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.reportes
    ADD CONSTRAINT reportes_pkey PRIMARY KEY (id);


--
-- Name: suscripciones suscripciones_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.suscripciones
    ADD CONSTRAINT suscripciones_pkey PRIMARY KEY (id);


--
-- Name: tokens_revocados tokens_revocados_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tokens_revocados
    ADD CONSTRAINT tokens_revocados_pkey PRIMARY KEY (id);


--
-- Name: trabajador_etiqueta trabajador_etiqueta_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trabajador_etiqueta
    ADD CONSTRAINT trabajador_etiqueta_pkey PRIMARY KEY (trabajador_id, etiqueta_id);


--
-- Name: trabajadores trabajadores_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trabajadores
    ADD CONSTRAINT trabajadores_pkey PRIMARY KEY (usuario_id);


--
-- Name: etiquetas uk_7krxm7l1tk3hmrrqrtcdy8n77; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.etiquetas
    ADD CONSTRAINT uk_7krxm7l1tk3hmrrqrtcdy8n77 UNIQUE (nombre);


--
-- Name: suscripciones uk_dunjwecaq037n5har4gbsalye; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.suscripciones
    ADD CONSTRAINT uk_dunjwecaq037n5har4gbsalye UNIQUE (usuario_id);


--
-- Name: perfiles_empresariales uk_e66hvxecibak2ks4agck5e8id; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.perfiles_empresariales
    ADD CONSTRAINT uk_e66hvxecibak2ks4agck5e8id UNIQUE (empleador_id);


--
-- Name: perfiles_empresariales uk_i48w8sqh0klnllpkfvxod8uil; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.perfiles_empresariales
    ADD CONSTRAINT uk_i48w8sqh0klnllpkfvxod8uil UNIQUE (cuit);


--
-- Name: tokens_revocados uk_j6fjrrusjw96gmy8t5ksrpyx6; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tokens_revocados
    ADD CONSTRAINT uk_j6fjrrusjw96gmy8t5ksrpyx6 UNIQUE (jti);


--
-- Name: usuarios uk_kfsp0s1tflm1cwlj8idhqsad0; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usuarios
    ADD CONSTRAINT uk_kfsp0s1tflm1cwlj8idhqsad0 UNIQUE (email);


--
-- Name: preferencias_notificacion uk_nlt2t2ifol5pi3lmj7t47irn7; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.preferencias_notificacion
    ADD CONSTRAINT uk_nlt2t2ifol5pi3lmj7t47irn7 UNIQUE (trabajador_id);


--
-- Name: favoritos uke2waxy08h7tj51vpsl1swswv2; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.favoritos
    ADD CONSTRAINT uke2waxy08h7tj51vpsl1swswv2 UNIQUE (empleador_id, trabajador_id);


--
-- Name: usuarios usuarios_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usuarios
    ADD CONSTRAINT usuarios_pkey PRIMARY KEY (id);


--
-- Name: valoraciones valoraciones_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.valoraciones
    ADD CONSTRAINT valoraciones_pkey PRIMARY KEY (id);


--
-- Name: portafolio_items fk35eewvffonj773b17odpg8xnm; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.portafolio_items
    ADD CONSTRAINT fk35eewvffonj773b17odpg8xnm FOREIGN KEY (trabajador_id) REFERENCES public.trabajadores(usuario_id);


--
-- Name: administradores fk39dd1lon1to4ofwlcxf73gahm; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.administradores
    ADD CONSTRAINT fk39dd1lon1to4ofwlcxf73gahm FOREIGN KEY (usuario_id) REFERENCES public.usuarios(id);


--
-- Name: oportunidad_etiqueta fk3wc9xkf7nm5epl7qdg79a4k7a; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.oportunidad_etiqueta
    ADD CONSTRAINT fk3wc9xkf7nm5epl7qdg79a4k7a FOREIGN KEY (oportunidad_id) REFERENCES public.oportunidades_laborales(post_id);


--
-- Name: reportes fk4qkjc9dwu7ymw40l5l0gcggbu; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.reportes
    ADD CONSTRAINT fk4qkjc9dwu7ymw40l5l0gcggbu FOREIGN KEY (moderador_id) REFERENCES public.administradores(usuario_id);


--
-- Name: valoraciones fk5hv10spy020bvo0l0bmme6m15; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.valoraciones
    ADD CONSTRAINT fk5hv10spy020bvo0l0bmme6m15 FOREIGN KEY (trabajador_id) REFERENCES public.trabajadores(usuario_id);


--
-- Name: trabajador_etiqueta fk5v6h8gbf87xsgn5as08wca76y; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trabajador_etiqueta
    ADD CONSTRAINT fk5v6h8gbf87xsgn5as08wca76y FOREIGN KEY (trabajador_id) REFERENCES public.trabajadores(usuario_id);


--
-- Name: postulaciones fk7t4f17cf26qccbqjt2ek1y8g8; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.postulaciones
    ADD CONSTRAINT fk7t4f17cf26qccbqjt2ek1y8g8 FOREIGN KEY (oportunidad_id) REFERENCES public.oportunidades_laborales(post_id);


--
-- Name: valoraciones fk89ayqwx1u4ou4a56ahkskaq14; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.valoraciones
    ADD CONSTRAINT fk89ayqwx1u4ou4a56ahkskaq14 FOREIGN KEY (autor_id) REFERENCES public.empleadores(usuario_id);


--
-- Name: horarios_disponibilidad fk9ytw7sv5rnjsaesaam5lr6okp; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.horarios_disponibilidad
    ADD CONSTRAINT fk9ytw7sv5rnjsaesaam5lr6okp FOREIGN KEY (trabajador_id) REFERENCES public.trabajadores(usuario_id);


--
-- Name: publicidades fka8x9cv3r940wepehw1a2vlrhq; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.publicidades
    ADD CONSTRAINT fka8x9cv3r940wepehw1a2vlrhq FOREIGN KEY (anunciante_id) REFERENCES public.empleadores(usuario_id);


--
-- Name: publicaciones_foro fkbamkgapsbpkorbbasd4bn2yfk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.publicaciones_foro
    ADD CONSTRAINT fkbamkgapsbpkorbbasd4bn2yfk FOREIGN KEY (autor_id) REFERENCES public.usuarios(id);


--
-- Name: trabajadores fkbj2tq4b166h22cigjb8r3vm3t; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trabajadores
    ADD CONSTRAINT fkbj2tq4b166h22cigjb8r3vm3t FOREIGN KEY (usuario_id) REFERENCES public.usuarios(id);


--
-- Name: oportunidad_etiqueta fkdgav7l9359qw9u011ejncypad; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.oportunidad_etiqueta
    ADD CONSTRAINT fkdgav7l9359qw9u011ejncypad FOREIGN KEY (etiqueta_id) REFERENCES public.etiquetas(id);


--
-- Name: perfiles_empresariales fkgje4hjvwg9j0xsjot2oivuyht; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.perfiles_empresariales
    ADD CONSTRAINT fkgje4hjvwg9j0xsjot2oivuyht FOREIGN KEY (empleador_id) REFERENCES public.empleadores(usuario_id);


--
-- Name: favoritos fkhrlx2a6mmwkp5pp7n9ubusbfx; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.favoritos
    ADD CONSTRAINT fkhrlx2a6mmwkp5pp7n9ubusbfx FOREIGN KEY (empleador_id) REFERENCES public.empleadores(usuario_id);


--
-- Name: empleadores fkj5i1v9ytsmym06jynw6ox1rm7; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.empleadores
    ADD CONSTRAINT fkj5i1v9ytsmym06jynw6ox1rm7 FOREIGN KEY (usuario_id) REFERENCES public.usuarios(id);


--
-- Name: donaciones fkjpnhxmv4mgfkq83md41i1kf88; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.donaciones
    ADD CONSTRAINT fkjpnhxmv4mgfkq83md41i1kf88 FOREIGN KEY (usuario_id) REFERENCES public.usuarios(id);


--
-- Name: reportes fkkmka1wx2y4enuhbioshbicb82; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.reportes
    ADD CONSTRAINT fkkmka1wx2y4enuhbioshbicb82 FOREIGN KEY (reportante_id) REFERENCES public.usuarios(id);


--
-- Name: trabajador_etiqueta fkn8y8yq9ycgraks3nvm61e7pf8; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trabajador_etiqueta
    ADD CONSTRAINT fkn8y8yq9ycgraks3nvm61e7pf8 FOREIGN KEY (etiqueta_id) REFERENCES public.etiquetas(id);


--
-- Name: comunicados fkngbcdfgv2ofvnuugtimjcltwq; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.comunicados
    ADD CONSTRAINT fkngbcdfgv2ofvnuugtimjcltwq FOREIGN KEY (administrador_id) REFERENCES public.administradores(usuario_id);


--
-- Name: oportunidades_laborales fkny76u9e5xk2i9opoce21oxe97; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.oportunidades_laborales
    ADD CONSTRAINT fkny76u9e5xk2i9opoce21oxe97 FOREIGN KEY (post_id) REFERENCES public.posts_base(id);


--
-- Name: perfiles_empresariales fkpacihgpav01rxaiw6nckyd44n; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.perfiles_empresariales
    ADD CONSTRAINT fkpacihgpav01rxaiw6nckyd44n FOREIGN KEY (supervisado_por_id) REFERENCES public.administradores(usuario_id);


--
-- Name: reportes fkr8kvv75wb3gp9ygdg840bvnrc; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.reportes
    ADD CONSTRAINT fkr8kvv75wb3gp9ygdg840bvnrc FOREIGN KEY (reportado_id) REFERENCES public.usuarios(id);


--
-- Name: favoritos fkrgubem4ylcunqw580crm6a7th; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.favoritos
    ADD CONSTRAINT fkrgubem4ylcunqw580crm6a7th FOREIGN KEY (trabajador_id) REFERENCES public.trabajadores(usuario_id);


--
-- Name: suscripciones fkrrsvcjm7l6p0pr4l8ehygsmuy; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.suscripciones
    ADD CONSTRAINT fkrrsvcjm7l6p0pr4l8ehygsmuy FOREIGN KEY (usuario_id) REFERENCES public.usuarios(id);


--
-- Name: notificaciones fkrxqukj4ow44h862qlbgl54ij2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.notificaciones
    ADD CONSTRAINT fkrxqukj4ow44h862qlbgl54ij2 FOREIGN KEY (destinatario_id) REFERENCES public.usuarios(id);


--
-- Name: postulaciones fksiwsi8grqfjf95t3j8qs6d33b; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.postulaciones
    ADD CONSTRAINT fksiwsi8grqfjf95t3j8qs6d33b FOREIGN KEY (post_id) REFERENCES public.posts_base(id);


--
-- Name: preferencias_notificacion fksjxr7os10gi85yq6djugvbd3m; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.preferencias_notificacion
    ADD CONSTRAINT fksjxr7os10gi85yq6djugvbd3m FOREIGN KEY (trabajador_id) REFERENCES public.trabajadores(usuario_id);


--
-- Name: posts_base fktgiwmg3hffedujq9vi7vqx0nx; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.posts_base
    ADD CONSTRAINT fktgiwmg3hffedujq9vi7vqx0nx FOREIGN KEY (autor_id) REFERENCES public.usuarios(id);


--
-- PostgreSQL database dump complete
--

\unrestrict Cu12SKsuhGRKBmxg1317quTvdZKuBrdwD1v2lLoAQmxBTQRb6JhO1IwjbxSD2xi

