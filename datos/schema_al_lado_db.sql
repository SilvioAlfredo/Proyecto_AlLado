-- =============================================================================
-- Esquema de AlLado (PostgreSQL) — solo estructura, sin datos.
-- -----------------------------------------------------------------------------
-- Generado con:
--   pg_dump -h localhost -U postgres -d al_lado_db --schema-only --no-owner --no-privileges
--
-- Se toma de la base de desarrollo que Hibernate mantiene con ddl-auto=update,
-- porque `datos/al_lado_db.sql` es una foto del 09/10/2026 y quedó desactualizada:
-- le faltan columnas que las entidades ya declaran (por ejemplo latitud,
-- longitud, dia_semana_requerido, hora_inicio_requerida y hora_fin_requerida en
-- oportunidades_laborales; email_verification_token en usuarios).
--
-- Uso para el ciclo de pruebas:
--   createdb al_lado_tc
--   psql -d al_lado_tc -f datos/schema_al_lado_db.sql
--   psql -d al_lado_tc -f datos/seed_tc_db_005.sql
-- =============================================================================

--
-- PostgreSQL database dump
--

\restrict rVeXzHrYhQrj4FWLWYeZUaPwWEIbTUwVlWoqo7pqmzX66Kk8mNvnh4ve15OLHll

-- Dumped from database version 18.6 (Ubuntu 18.6-3build2)
-- Dumped by pg_dump version 18.6 (Ubuntu 18.6-3build2)

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

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: administradores; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.administradores (
    nivel_acceso character varying(50) NOT NULL,
    usuario_id bigint NOT NULL
);


--
-- Name: comunicados; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.comunicados (
    id bigint NOT NULL,
    alcance character varying(50),
    contenido text NOT NULL,
    fecha_emision timestamp(6) without time zone NOT NULL,
    titulo character varying(150) NOT NULL,
    administrador_id bigint NOT NULL
);


--
-- Name: comunicados_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.comunicados_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: comunicados_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.comunicados_id_seq OWNED BY public.comunicados.id;


--
-- Name: donaciones; Type: TABLE; Schema: public; Owner: -
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


--
-- Name: donaciones_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.donaciones_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: donaciones_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.donaciones_id_seq OWNED BY public.donaciones.id;


--
-- Name: empleadores; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.empleadores (
    usuario_id bigint NOT NULL
);


--
-- Name: etiquetas; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.etiquetas (
    id bigint NOT NULL,
    nombre character varying(50) NOT NULL
);


--
-- Name: etiquetas_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.etiquetas_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: etiquetas_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.etiquetas_id_seq OWNED BY public.etiquetas.id;


--
-- Name: favoritos; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.favoritos (
    id bigint NOT NULL,
    fecha_guardado timestamp(6) without time zone NOT NULL,
    empleador_id bigint NOT NULL,
    trabajador_id bigint NOT NULL
);


--
-- Name: favoritos_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.favoritos_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: favoritos_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.favoritos_id_seq OWNED BY public.favoritos.id;


--
-- Name: horarios_disponibilidad; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.horarios_disponibilidad (
    id bigint NOT NULL,
    dia_semana character varying(20) NOT NULL,
    hora_fin time(6) without time zone NOT NULL,
    hora_inicio time(6) without time zone NOT NULL,
    trabajador_id bigint NOT NULL
);


--
-- Name: horarios_disponibilidad_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.horarios_disponibilidad_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: horarios_disponibilidad_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.horarios_disponibilidad_id_seq OWNED BY public.horarios_disponibilidad.id;


--
-- Name: notificaciones; Type: TABLE; Schema: public; Owner: -
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


--
-- Name: notificaciones_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.notificaciones_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: notificaciones_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.notificaciones_id_seq OWNED BY public.notificaciones.id;


--
-- Name: oportunidad_etiqueta; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.oportunidad_etiqueta (
    oportunidad_id bigint NOT NULL,
    etiqueta_id bigint NOT NULL
);


--
-- Name: oportunidades_laborales; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.oportunidades_laborales (
    activa boolean NOT NULL,
    modalidad character varying(50),
    rango_salarial character varying(100),
    ubicacion character varying(100),
    post_id bigint NOT NULL,
    destacada boolean DEFAULT false NOT NULL,
    dia_semana_requerido character varying(20),
    hora_fin_requerida time(6) without time zone,
    hora_inicio_requerida time(6) without time zone,
    latitud double precision,
    longitud double precision
);


--
-- Name: perfiles_empresariales; Type: TABLE; Schema: public; Owner: -
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


--
-- Name: perfiles_empresariales_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.perfiles_empresariales_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: perfiles_empresariales_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.perfiles_empresariales_id_seq OWNED BY public.perfiles_empresariales.id;


--
-- Name: portafolio_items; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.portafolio_items (
    id bigint NOT NULL,
    descripcion text,
    titulo character varying(120) NOT NULL,
    url_archivo character varying(255),
    trabajador_id bigint NOT NULL
);


--
-- Name: portafolio_items_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.portafolio_items_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: portafolio_items_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.portafolio_items_id_seq OWNED BY public.portafolio_items.id;


--
-- Name: posts_base; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.posts_base (
    id bigint NOT NULL,
    contenido text NOT NULL,
    fecha_creacion timestamp(6) without time zone NOT NULL,
    titulo character varying(150) NOT NULL,
    autor_id bigint NOT NULL
);


--
-- Name: posts_base_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.posts_base_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: posts_base_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.posts_base_id_seq OWNED BY public.posts_base.id;


--
-- Name: postulaciones; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.postulaciones (
    estado character varying(50) NOT NULL,
    remuneracion_pretendida double precision,
    post_id bigint NOT NULL,
    oportunidad_id bigint NOT NULL
);


--
-- Name: preferencias_notificacion; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.preferencias_notificacion (
    id bigint NOT NULL,
    alertas_nuevas_ofertas boolean NOT NULL,
    alertas_nuevas_postulaciones boolean NOT NULL,
    notificar_email boolean NOT NULL,
    notificar_push boolean NOT NULL,
    trabajador_id bigint NOT NULL
);


--
-- Name: preferencias_notificacion_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.preferencias_notificacion_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: preferencias_notificacion_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.preferencias_notificacion_id_seq OWNED BY public.preferencias_notificacion.id;


--
-- Name: publicaciones_foro; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.publicaciones_foro (
    id bigint NOT NULL,
    contenido text NOT NULL,
    fecha_creacion timestamp(6) without time zone NOT NULL,
    titulo character varying(150) NOT NULL,
    autor_id bigint NOT NULL,
    sector character varying(100)
);


--
-- Name: publicaciones_foro_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.publicaciones_foro_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: publicaciones_foro_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.publicaciones_foro_id_seq OWNED BY public.publicaciones_foro.id;


--
-- Name: publicidades; Type: TABLE; Schema: public; Owner: -
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


--
-- Name: publicidades_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.publicidades_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: publicidades_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.publicidades_id_seq OWNED BY public.publicidades.id;


--
-- Name: reportes; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.reportes (
    id bigint NOT NULL,
    descripcion text NOT NULL,
    estado character varying(30) NOT NULL,
    fecha_creacion timestamp(6) without time zone NOT NULL,
    motivo character varying(100) NOT NULL,
    moderador_id bigint,
    reportado_id bigint,
    reportante_id bigint NOT NULL,
    sector character varying(100)
);


--
-- Name: reportes_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.reportes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: reportes_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.reportes_id_seq OWNED BY public.reportes.id;


--
-- Name: suscripciones; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.suscripciones (
    id bigint NOT NULL,
    activa boolean NOT NULL,
    fecha_fin date,
    fecha_inicio date NOT NULL,
    nivel character varying(50) NOT NULL,
    usuario_id bigint NOT NULL
);


--
-- Name: suscripciones_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.suscripciones_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: suscripciones_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.suscripciones_id_seq OWNED BY public.suscripciones.id;


--
-- Name: tokens_revocados; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tokens_revocados (
    id bigint NOT NULL,
    expira timestamp(6) without time zone NOT NULL,
    jti character varying(100) NOT NULL,
    sujeto character varying(150)
);


--
-- Name: tokens_revocados_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.tokens_revocados_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: tokens_revocados_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.tokens_revocados_id_seq OWNED BY public.tokens_revocados.id;


--
-- Name: trabajador_etiqueta; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.trabajador_etiqueta (
    trabajador_id bigint NOT NULL,
    etiqueta_id bigint NOT NULL
);


--
-- Name: trabajadores; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.trabajadores (
    biografia text,
    certificados text,
    fotografia character varying(255),
    sector character varying(100),
    token_push character varying(255),
    usuario_id bigint NOT NULL,
    latitud double precision,
    longitud double precision
);


--
-- Name: usuarios; Type: TABLE; Schema: public; Owner: -
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
    telefono character varying(20) NOT NULL,
    email_verification_token character varying(100),
    email_verification_token_expiry timestamp(6) without time zone
);


--
-- Name: usuarios_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.usuarios_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: usuarios_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.usuarios_id_seq OWNED BY public.usuarios.id;


--
-- Name: valoraciones; Type: TABLE; Schema: public; Owner: -
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


--
-- Name: valoraciones_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.valoraciones_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: valoraciones_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.valoraciones_id_seq OWNED BY public.valoraciones.id;


--
-- Name: comunicados id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.comunicados ALTER COLUMN id SET DEFAULT nextval('public.comunicados_id_seq'::regclass);


--
-- Name: donaciones id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.donaciones ALTER COLUMN id SET DEFAULT nextval('public.donaciones_id_seq'::regclass);


--
-- Name: etiquetas id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.etiquetas ALTER COLUMN id SET DEFAULT nextval('public.etiquetas_id_seq'::regclass);


--
-- Name: favoritos id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.favoritos ALTER COLUMN id SET DEFAULT nextval('public.favoritos_id_seq'::regclass);


--
-- Name: horarios_disponibilidad id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.horarios_disponibilidad ALTER COLUMN id SET DEFAULT nextval('public.horarios_disponibilidad_id_seq'::regclass);


--
-- Name: notificaciones id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.notificaciones ALTER COLUMN id SET DEFAULT nextval('public.notificaciones_id_seq'::regclass);


--
-- Name: perfiles_empresariales id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.perfiles_empresariales ALTER COLUMN id SET DEFAULT nextval('public.perfiles_empresariales_id_seq'::regclass);


--
-- Name: portafolio_items id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.portafolio_items ALTER COLUMN id SET DEFAULT nextval('public.portafolio_items_id_seq'::regclass);


--
-- Name: posts_base id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.posts_base ALTER COLUMN id SET DEFAULT nextval('public.posts_base_id_seq'::regclass);


--
-- Name: preferencias_notificacion id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.preferencias_notificacion ALTER COLUMN id SET DEFAULT nextval('public.preferencias_notificacion_id_seq'::regclass);


--
-- Name: publicaciones_foro id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.publicaciones_foro ALTER COLUMN id SET DEFAULT nextval('public.publicaciones_foro_id_seq'::regclass);


--
-- Name: publicidades id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.publicidades ALTER COLUMN id SET DEFAULT nextval('public.publicidades_id_seq'::regclass);


--
-- Name: reportes id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.reportes ALTER COLUMN id SET DEFAULT nextval('public.reportes_id_seq'::regclass);


--
-- Name: suscripciones id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.suscripciones ALTER COLUMN id SET DEFAULT nextval('public.suscripciones_id_seq'::regclass);


--
-- Name: tokens_revocados id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tokens_revocados ALTER COLUMN id SET DEFAULT nextval('public.tokens_revocados_id_seq'::regclass);


--
-- Name: usuarios id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.usuarios ALTER COLUMN id SET DEFAULT nextval('public.usuarios_id_seq'::regclass);


--
-- Name: valoraciones id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.valoraciones ALTER COLUMN id SET DEFAULT nextval('public.valoraciones_id_seq'::regclass);


--
-- Name: administradores administradores_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.administradores
    ADD CONSTRAINT administradores_pkey PRIMARY KEY (usuario_id);


--
-- Name: comunicados comunicados_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.comunicados
    ADD CONSTRAINT comunicados_pkey PRIMARY KEY (id);


--
-- Name: donaciones donaciones_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.donaciones
    ADD CONSTRAINT donaciones_pkey PRIMARY KEY (id);


--
-- Name: empleadores empleadores_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.empleadores
    ADD CONSTRAINT empleadores_pkey PRIMARY KEY (usuario_id);


--
-- Name: etiquetas etiquetas_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.etiquetas
    ADD CONSTRAINT etiquetas_pkey PRIMARY KEY (id);


--
-- Name: favoritos favoritos_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.favoritos
    ADD CONSTRAINT favoritos_pkey PRIMARY KEY (id);


--
-- Name: horarios_disponibilidad horarios_disponibilidad_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.horarios_disponibilidad
    ADD CONSTRAINT horarios_disponibilidad_pkey PRIMARY KEY (id);


--
-- Name: notificaciones notificaciones_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.notificaciones
    ADD CONSTRAINT notificaciones_pkey PRIMARY KEY (id);


--
-- Name: oportunidad_etiqueta oportunidad_etiqueta_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.oportunidad_etiqueta
    ADD CONSTRAINT oportunidad_etiqueta_pkey PRIMARY KEY (oportunidad_id, etiqueta_id);


--
-- Name: oportunidades_laborales oportunidades_laborales_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.oportunidades_laborales
    ADD CONSTRAINT oportunidades_laborales_pkey PRIMARY KEY (post_id);


--
-- Name: perfiles_empresariales perfiles_empresariales_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.perfiles_empresariales
    ADD CONSTRAINT perfiles_empresariales_pkey PRIMARY KEY (id);


--
-- Name: portafolio_items portafolio_items_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.portafolio_items
    ADD CONSTRAINT portafolio_items_pkey PRIMARY KEY (id);


--
-- Name: posts_base posts_base_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.posts_base
    ADD CONSTRAINT posts_base_pkey PRIMARY KEY (id);


--
-- Name: postulaciones postulaciones_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.postulaciones
    ADD CONSTRAINT postulaciones_pkey PRIMARY KEY (post_id);


--
-- Name: preferencias_notificacion preferencias_notificacion_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.preferencias_notificacion
    ADD CONSTRAINT preferencias_notificacion_pkey PRIMARY KEY (id);


--
-- Name: publicaciones_foro publicaciones_foro_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.publicaciones_foro
    ADD CONSTRAINT publicaciones_foro_pkey PRIMARY KEY (id);


--
-- Name: publicidades publicidades_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.publicidades
    ADD CONSTRAINT publicidades_pkey PRIMARY KEY (id);


--
-- Name: reportes reportes_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.reportes
    ADD CONSTRAINT reportes_pkey PRIMARY KEY (id);


--
-- Name: suscripciones suscripciones_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.suscripciones
    ADD CONSTRAINT suscripciones_pkey PRIMARY KEY (id);


--
-- Name: tokens_revocados tokens_revocados_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tokens_revocados
    ADD CONSTRAINT tokens_revocados_pkey PRIMARY KEY (id);


--
-- Name: trabajador_etiqueta trabajador_etiqueta_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.trabajador_etiqueta
    ADD CONSTRAINT trabajador_etiqueta_pkey PRIMARY KEY (trabajador_id, etiqueta_id);


--
-- Name: trabajadores trabajadores_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.trabajadores
    ADD CONSTRAINT trabajadores_pkey PRIMARY KEY (usuario_id);


--
-- Name: etiquetas uk_7krxm7l1tk3hmrrqrtcdy8n77; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.etiquetas
    ADD CONSTRAINT uk_7krxm7l1tk3hmrrqrtcdy8n77 UNIQUE (nombre);


--
-- Name: suscripciones uk_dunjwecaq037n5har4gbsalye; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.suscripciones
    ADD CONSTRAINT uk_dunjwecaq037n5har4gbsalye UNIQUE (usuario_id);


--
-- Name: perfiles_empresariales uk_e66hvxecibak2ks4agck5e8id; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.perfiles_empresariales
    ADD CONSTRAINT uk_e66hvxecibak2ks4agck5e8id UNIQUE (empleador_id);


--
-- Name: perfiles_empresariales uk_i48w8sqh0klnllpkfvxod8uil; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.perfiles_empresariales
    ADD CONSTRAINT uk_i48w8sqh0klnllpkfvxod8uil UNIQUE (cuit);


--
-- Name: tokens_revocados uk_j6fjrrusjw96gmy8t5ksrpyx6; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tokens_revocados
    ADD CONSTRAINT uk_j6fjrrusjw96gmy8t5ksrpyx6 UNIQUE (jti);


--
-- Name: usuarios uk_kfsp0s1tflm1cwlj8idhqsad0; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.usuarios
    ADD CONSTRAINT uk_kfsp0s1tflm1cwlj8idhqsad0 UNIQUE (email);


--
-- Name: preferencias_notificacion uk_nlt2t2ifol5pi3lmj7t47irn7; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.preferencias_notificacion
    ADD CONSTRAINT uk_nlt2t2ifol5pi3lmj7t47irn7 UNIQUE (trabajador_id);


--
-- Name: favoritos uke2waxy08h7tj51vpsl1swswv2; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.favoritos
    ADD CONSTRAINT uke2waxy08h7tj51vpsl1swswv2 UNIQUE (empleador_id, trabajador_id);


--
-- Name: usuarios usuarios_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.usuarios
    ADD CONSTRAINT usuarios_pkey PRIMARY KEY (id);


--
-- Name: valoraciones valoraciones_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.valoraciones
    ADD CONSTRAINT valoraciones_pkey PRIMARY KEY (id);


--
-- Name: portafolio_items fk35eewvffonj773b17odpg8xnm; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.portafolio_items
    ADD CONSTRAINT fk35eewvffonj773b17odpg8xnm FOREIGN KEY (trabajador_id) REFERENCES public.trabajadores(usuario_id);


--
-- Name: administradores fk39dd1lon1to4ofwlcxf73gahm; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.administradores
    ADD CONSTRAINT fk39dd1lon1to4ofwlcxf73gahm FOREIGN KEY (usuario_id) REFERENCES public.usuarios(id);


--
-- Name: oportunidad_etiqueta fk3wc9xkf7nm5epl7qdg79a4k7a; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.oportunidad_etiqueta
    ADD CONSTRAINT fk3wc9xkf7nm5epl7qdg79a4k7a FOREIGN KEY (oportunidad_id) REFERENCES public.oportunidades_laborales(post_id);


--
-- Name: reportes fk4qkjc9dwu7ymw40l5l0gcggbu; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.reportes
    ADD CONSTRAINT fk4qkjc9dwu7ymw40l5l0gcggbu FOREIGN KEY (moderador_id) REFERENCES public.administradores(usuario_id);


--
-- Name: valoraciones fk5hv10spy020bvo0l0bmme6m15; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.valoraciones
    ADD CONSTRAINT fk5hv10spy020bvo0l0bmme6m15 FOREIGN KEY (trabajador_id) REFERENCES public.trabajadores(usuario_id);


--
-- Name: trabajador_etiqueta fk5v6h8gbf87xsgn5as08wca76y; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.trabajador_etiqueta
    ADD CONSTRAINT fk5v6h8gbf87xsgn5as08wca76y FOREIGN KEY (trabajador_id) REFERENCES public.trabajadores(usuario_id);


--
-- Name: postulaciones fk7t4f17cf26qccbqjt2ek1y8g8; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.postulaciones
    ADD CONSTRAINT fk7t4f17cf26qccbqjt2ek1y8g8 FOREIGN KEY (oportunidad_id) REFERENCES public.oportunidades_laborales(post_id);


--
-- Name: valoraciones fk89ayqwx1u4ou4a56ahkskaq14; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.valoraciones
    ADD CONSTRAINT fk89ayqwx1u4ou4a56ahkskaq14 FOREIGN KEY (autor_id) REFERENCES public.empleadores(usuario_id);


--
-- Name: horarios_disponibilidad fk9ytw7sv5rnjsaesaam5lr6okp; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.horarios_disponibilidad
    ADD CONSTRAINT fk9ytw7sv5rnjsaesaam5lr6okp FOREIGN KEY (trabajador_id) REFERENCES public.trabajadores(usuario_id);


--
-- Name: publicidades fka8x9cv3r940wepehw1a2vlrhq; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.publicidades
    ADD CONSTRAINT fka8x9cv3r940wepehw1a2vlrhq FOREIGN KEY (anunciante_id) REFERENCES public.empleadores(usuario_id);


--
-- Name: publicaciones_foro fkbamkgapsbpkorbbasd4bn2yfk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.publicaciones_foro
    ADD CONSTRAINT fkbamkgapsbpkorbbasd4bn2yfk FOREIGN KEY (autor_id) REFERENCES public.usuarios(id);


--
-- Name: trabajadores fkbj2tq4b166h22cigjb8r3vm3t; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.trabajadores
    ADD CONSTRAINT fkbj2tq4b166h22cigjb8r3vm3t FOREIGN KEY (usuario_id) REFERENCES public.usuarios(id);


--
-- Name: oportunidad_etiqueta fkdgav7l9359qw9u011ejncypad; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.oportunidad_etiqueta
    ADD CONSTRAINT fkdgav7l9359qw9u011ejncypad FOREIGN KEY (etiqueta_id) REFERENCES public.etiquetas(id);


--
-- Name: perfiles_empresariales fkgje4hjvwg9j0xsjot2oivuyht; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.perfiles_empresariales
    ADD CONSTRAINT fkgje4hjvwg9j0xsjot2oivuyht FOREIGN KEY (empleador_id) REFERENCES public.empleadores(usuario_id);


--
-- Name: favoritos fkhrlx2a6mmwkp5pp7n9ubusbfx; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.favoritos
    ADD CONSTRAINT fkhrlx2a6mmwkp5pp7n9ubusbfx FOREIGN KEY (empleador_id) REFERENCES public.empleadores(usuario_id);


--
-- Name: empleadores fkj5i1v9ytsmym06jynw6ox1rm7; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.empleadores
    ADD CONSTRAINT fkj5i1v9ytsmym06jynw6ox1rm7 FOREIGN KEY (usuario_id) REFERENCES public.usuarios(id);


--
-- Name: donaciones fkjpnhxmv4mgfkq83md41i1kf88; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.donaciones
    ADD CONSTRAINT fkjpnhxmv4mgfkq83md41i1kf88 FOREIGN KEY (usuario_id) REFERENCES public.usuarios(id);


--
-- Name: reportes fkkmka1wx2y4enuhbioshbicb82; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.reportes
    ADD CONSTRAINT fkkmka1wx2y4enuhbioshbicb82 FOREIGN KEY (reportante_id) REFERENCES public.usuarios(id);


--
-- Name: trabajador_etiqueta fkn8y8yq9ycgraks3nvm61e7pf8; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.trabajador_etiqueta
    ADD CONSTRAINT fkn8y8yq9ycgraks3nvm61e7pf8 FOREIGN KEY (etiqueta_id) REFERENCES public.etiquetas(id);


--
-- Name: comunicados fkngbcdfgv2ofvnuugtimjcltwq; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.comunicados
    ADD CONSTRAINT fkngbcdfgv2ofvnuugtimjcltwq FOREIGN KEY (administrador_id) REFERENCES public.administradores(usuario_id);


--
-- Name: oportunidades_laborales fkny76u9e5xk2i9opoce21oxe97; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.oportunidades_laborales
    ADD CONSTRAINT fkny76u9e5xk2i9opoce21oxe97 FOREIGN KEY (post_id) REFERENCES public.posts_base(id);


--
-- Name: perfiles_empresariales fkpacihgpav01rxaiw6nckyd44n; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.perfiles_empresariales
    ADD CONSTRAINT fkpacihgpav01rxaiw6nckyd44n FOREIGN KEY (supervisado_por_id) REFERENCES public.administradores(usuario_id);


--
-- Name: reportes fkr8kvv75wb3gp9ygdg840bvnrc; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.reportes
    ADD CONSTRAINT fkr8kvv75wb3gp9ygdg840bvnrc FOREIGN KEY (reportado_id) REFERENCES public.usuarios(id);


--
-- Name: favoritos fkrgubem4ylcunqw580crm6a7th; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.favoritos
    ADD CONSTRAINT fkrgubem4ylcunqw580crm6a7th FOREIGN KEY (trabajador_id) REFERENCES public.trabajadores(usuario_id);


--
-- Name: suscripciones fkrrsvcjm7l6p0pr4l8ehygsmuy; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.suscripciones
    ADD CONSTRAINT fkrrsvcjm7l6p0pr4l8ehygsmuy FOREIGN KEY (usuario_id) REFERENCES public.usuarios(id);


--
-- Name: notificaciones fkrxqukj4ow44h862qlbgl54ij2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.notificaciones
    ADD CONSTRAINT fkrxqukj4ow44h862qlbgl54ij2 FOREIGN KEY (destinatario_id) REFERENCES public.usuarios(id);


--
-- Name: postulaciones fksiwsi8grqfjf95t3j8qs6d33b; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.postulaciones
    ADD CONSTRAINT fksiwsi8grqfjf95t3j8qs6d33b FOREIGN KEY (post_id) REFERENCES public.posts_base(id);


--
-- Name: preferencias_notificacion fksjxr7os10gi85yq6djugvbd3m; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.preferencias_notificacion
    ADD CONSTRAINT fksjxr7os10gi85yq6djugvbd3m FOREIGN KEY (trabajador_id) REFERENCES public.trabajadores(usuario_id);


--
-- Name: posts_base fktgiwmg3hffedujq9vi7vqx0nx; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.posts_base
    ADD CONSTRAINT fktgiwmg3hffedujq9vi7vqx0nx FOREIGN KEY (autor_id) REFERENCES public.usuarios(id);


--
-- PostgreSQL database dump complete
--

\unrestrict rVeXzHrYhQrj4FWLWYeZUaPwWEIbTUwVlWoqo7pqmzX66Kk8mNvnh4ve15OLHll

