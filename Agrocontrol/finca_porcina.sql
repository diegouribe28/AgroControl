--
-- PostgreSQL database dump
--

\restrict E6qfDiiHQRioIOREseRTUKbSynGqGsatqwXvkarngKXsC0TOjMCK14Z4jWXQDT6

-- Dumped from database version 18.4
-- Dumped by pg_dump version 18.4

-- Started on 2026-11-26 03:00:45

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
-- TOC entry 222 (class 1259 OID 40980)
-- Name: animal; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.animal (
    id_animal integer NOT NULL,
    codigo character varying(30) NOT NULL,
    sexo character varying(10) NOT NULL,
    raza character varying(60),
    fecha_nacimiento date,
    etapa character varying(20) NOT NULL,
    estado character varying(20) DEFAULT 'ACTIVO'::character varying NOT NULL,
    fecha_registro timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    id_usuario_registro integer NOT NULL,
    CONSTRAINT animal_estado_check CHECK (((estado)::text = ANY ((ARRAY['ACTIVO'::character varying, 'INACTIVO'::character varying, 'VENDIDO'::character varying, 'FALLECIDO'::character varying, 'RETIRADO'::character varying])::text[]))),
    CONSTRAINT animal_etapa_check CHECK (((etapa)::text = ANY ((ARRAY['CRECIMIENTO'::character varying, 'ENGORDE'::character varying])::text[]))),
    CONSTRAINT animal_sexo_check CHECK (((sexo)::text = ANY ((ARRAY['MACHO'::character varying, 'HEMBRA'::character varying])::text[]))),
    CONSTRAINT chk_fecha_nacimiento CHECK (((fecha_nacimiento IS NULL) OR (fecha_nacimiento <= CURRENT_DATE)))
);


--
-- TOC entry 221 (class 1259 OID 40979)
-- Name: animal_id_animal_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.animal ALTER COLUMN id_animal ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.animal_id_animal_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 228 (class 1259 OID 41058)
-- Name: historial_etapa; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.historial_etapa (
    id_historial integer NOT NULL,
    id_animal integer NOT NULL,
    id_usuario integer NOT NULL,
    etapa_anterior character varying(20),
    etapa_nueva character varying(20) NOT NULL,
    fecha_cambio timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    observaciones text,
    CONSTRAINT chk_etapa_anterior CHECK (((etapa_anterior IS NULL) OR ((etapa_anterior)::text = ANY ((ARRAY['CRECIMIENTO'::character varying, 'ENGORDE'::character varying])::text[])))),
    CONSTRAINT historial_etapa_etapa_nueva_check CHECK (((etapa_nueva)::text = ANY ((ARRAY['CRECIMIENTO'::character varying, 'ENGORDE'::character varying])::text[])))
);


--
-- TOC entry 227 (class 1259 OID 41057)
-- Name: historial_etapa_id_historial_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.historial_etapa ALTER COLUMN id_historial ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.historial_etapa_id_historial_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 232 (class 1259 OID 41109)
-- Name: registro_alimentacion; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.registro_alimentacion (
    id_registro integer NOT NULL,
    id_animal integer NOT NULL,
    id_usuario integer NOT NULL,
    fecha timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    tipo_alimento character varying(80) NOT NULL,
    cantidad_kg numeric(8,3) NOT NULL,
    observaciones text,
    CONSTRAINT registro_alimentacion_cantidad_kg_check CHECK ((cantidad_kg > (0)::numeric))
);


--
-- TOC entry 231 (class 1259 OID 41108)
-- Name: registro_alimentacion_id_registro_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.registro_alimentacion ALTER COLUMN id_registro ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.registro_alimentacion_id_registro_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 224 (class 1259 OID 41006)
-- Name: registro_peso; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.registro_peso (
    id_registro integer NOT NULL,
    id_animal integer NOT NULL,
    id_usuario integer NOT NULL,
    fecha date DEFAULT CURRENT_DATE NOT NULL,
    peso numeric(7,2) NOT NULL,
    observaciones text,
    CONSTRAINT registro_peso_peso_check CHECK ((peso > (0)::numeric))
);


--
-- TOC entry 223 (class 1259 OID 41005)
-- Name: registro_peso_id_registro_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.registro_peso ALTER COLUMN id_registro ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.registro_peso_id_registro_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 230 (class 1259 OID 41084)
-- Name: registro_sanitario; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.registro_sanitario (
    id_registro integer NOT NULL,
    id_animal integer NOT NULL,
    id_usuario integer NOT NULL,
    fecha date DEFAULT CURRENT_DATE NOT NULL,
    tipo character varying(60) NOT NULL,
    descripcion text NOT NULL
);


--
-- TOC entry 229 (class 1259 OID 41083)
-- Name: registro_sanitario_id_registro_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.registro_sanitario ALTER COLUMN id_registro ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.registro_sanitario_id_registro_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 226 (class 1259 OID 41031)
-- Name: reproduccion; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.reproduccion (
    id_reproduccion integer NOT NULL,
    id_cerda integer NOT NULL,
    id_usuario integer NOT NULL,
    fecha_servicio date NOT NULL,
    fecha_probable_parto date,
    fecha_parto_real date,
    resultado character varying(30) DEFAULT 'PENDIENTE'::character varying NOT NULL,
    observaciones text,
    CONSTRAINT chk_fecha_parto CHECK (((fecha_parto_real IS NULL) OR (fecha_parto_real >= fecha_servicio))),
    CONSTRAINT chk_fecha_probable CHECK (((fecha_probable_parto IS NULL) OR (fecha_probable_parto >= fecha_servicio))),
    CONSTRAINT reproduccion_resultado_check CHECK (((resultado)::text = ANY ((ARRAY['PENDIENTE'::character varying, 'PARTO'::character varying, 'FALSA_GESTACION'::character varying, 'NO_GESTANTE'::character varying, 'PERDIDA_GESTACIONAL'::character varying])::text[])))
);


--
-- TOC entry 225 (class 1259 OID 41030)
-- Name: reproduccion_id_reproduccion_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.reproduccion ALTER COLUMN id_reproduccion ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.reproduccion_id_reproduccion_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 220 (class 1259 OID 40962)
-- Name: usuario; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.usuario (
    id_usuario integer NOT NULL,
    nombre character varying(100) NOT NULL,
    nombre_usuario character varying(50) NOT NULL,
    clave_hash text NOT NULL,
    rol character varying(20) NOT NULL,
    activo boolean DEFAULT true NOT NULL,
    CONSTRAINT usuario_rol_check CHECK (((rol)::text = ANY ((ARRAY['ADMINISTRADOR'::character varying, 'EMPLEADO'::character varying])::text[])))
);


--
-- TOC entry 219 (class 1259 OID 40961)
-- Name: usuario_id_usuario_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.usuario ALTER COLUMN id_usuario ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.usuario_id_usuario_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 5090 (class 0 OID 40980)
-- Dependencies: 222
-- Data for Name: animal; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.animal (id_animal, codigo, sexo, raza, fecha_nacimiento, etapa, estado, fecha_registro, id_usuario_registro) FROM stdin;
7	CER-005	HEMBRA	ddd	2025-11-16	ENGORDE	ACTIVO	2026-10-19 16:37:54.315747	1
8	CER-0006	MACHO	DUROC	2026-11-24	ENGORDE	ACTIVO	2026-11-25 22:40:22.389321	1
4	CER-004	MACHO	HAMPSHIRE	2025-01-11	CRECIMIENTO	ACTIVO	2026-10-19 14:39:39.349939	1
5	CER-003	MACHO	poland	2025-01-11	CRECIMIENTO	ACTIVO	2026-10-19 14:40:02.865928	1
9	CER-001	MACHO	DUROC	2026-10-11	CRECIMIENTO	ACTIVO	2026-11-26 00:07:21.947176	1
10	CER-PRUEBA-001	MACHO	duroc	2026-09-15	CRECIMIENTO	ACTIVO	2026-11-26 01:43:26.780288	1
\.


--
-- TOC entry 5096 (class 0 OID 41058)
-- Dependencies: 228
-- Data for Name: historial_etapa; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.historial_etapa (id_historial, id_animal, id_usuario, etapa_anterior, etapa_nueva, fecha_cambio, observaciones) FROM stdin;
\.


--
-- TOC entry 5100 (class 0 OID 41109)
-- Dependencies: 232
-- Data for Name: registro_alimentacion; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.registro_alimentacion (id_registro, id_animal, id_usuario, fecha, tipo_alimento, cantidad_kg, observaciones) FROM stdin;
1	10	1	2026-11-26 02:04:52	concentrado	10.000	\N
2	4	1	2026-11-26 02:05:02	restos	10.000	\N
3	8	1	2026-11-26 02:05:12	pasto	1.000	\N
\.


--
-- TOC entry 5092 (class 0 OID 41006)
-- Dependencies: 224
-- Data for Name: registro_peso; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.registro_peso (id_registro, id_animal, id_usuario, fecha, peso, observaciones) FROM stdin;
1	4	1	2026-11-25	111.00	super biien.
2	4	1	2026-11-25	111.01	\N
\.


--
-- TOC entry 5098 (class 0 OID 41084)
-- Dependencies: 230
-- Data for Name: registro_sanitario; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.registro_sanitario (id_registro, id_animal, id_usuario, fecha, tipo, descripcion) FROM stdin;
1	9	1	2026-11-15	DESPARASITACIÓN	prueba de desparasitacion
2	9	1	2026-11-20	REVISIÓN VETERINARIA	prueba de revision  general
3	9	1	2026-11-25	VACUNACIÓN	pruueba de vacunacion
\.


--
-- TOC entry 5094 (class 0 OID 41031)
-- Dependencies: 226
-- Data for Name: reproduccion; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.reproduccion (id_reproduccion, id_cerda, id_usuario, fecha_servicio, fecha_probable_parto, fecha_parto_real, resultado, observaciones) FROM stdin;
1	7	1	2026-11-26	2027-03-20	2026-11-26	PARTO	\N
\.


--
-- TOC entry 5088 (class 0 OID 40962)
-- Dependencies: 220
-- Data for Name: usuario; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.usuario (id_usuario, nombre, nombre_usuario, clave_hash, rol, activo) FROM stdin;
1	adm	diego	pbkdf2_sha256$600000$uM/osx1Tl9B1+BtdYqk7+w==$izuSm68LFpQGrxLAOw2/NEEZZzT1kgiBe7DMaLPZcPc=	ADMINISTRADOR	t
2	luis ernesto estevez porras	luis	pbkdf2_sha256$600000$SH0n4si2y6c/IXflYI3ZCA==$pEQgh0xvIdaox/AD6zpzrxYo80tFrNZRaT1rtj39vO8=	EMPLEADO	t
\.


--
-- TOC entry 5106 (class 0 OID 0)
-- Dependencies: 221
-- Name: animal_id_animal_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.animal_id_animal_seq', 10, true);


--
-- TOC entry 5107 (class 0 OID 0)
-- Dependencies: 227
-- Name: historial_etapa_id_historial_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.historial_etapa_id_historial_seq', 1, false);


--
-- TOC entry 5108 (class 0 OID 0)
-- Dependencies: 231
-- Name: registro_alimentacion_id_registro_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.registro_alimentacion_id_registro_seq', 3, true);


--
-- TOC entry 5109 (class 0 OID 0)
-- Dependencies: 223
-- Name: registro_peso_id_registro_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.registro_peso_id_registro_seq', 2, true);


--
-- TOC entry 5110 (class 0 OID 0)
-- Dependencies: 229
-- Name: registro_sanitario_id_registro_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.registro_sanitario_id_registro_seq', 3, true);


--
-- TOC entry 5111 (class 0 OID 0)
-- Dependencies: 225
-- Name: reproduccion_id_reproduccion_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.reproduccion_id_reproduccion_seq', 1, true);


--
-- TOC entry 5112 (class 0 OID 0)
-- Dependencies: 219
-- Name: usuario_id_usuario_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.usuario_id_usuario_seq', 2, true);


--
-- TOC entry 4911 (class 2606 OID 40999)
-- Name: animal animal_codigo_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.animal
    ADD CONSTRAINT animal_codigo_key UNIQUE (codigo);


--
-- TOC entry 4913 (class 2606 OID 40997)
-- Name: animal animal_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.animal
    ADD CONSTRAINT animal_pkey PRIMARY KEY (id_animal);


--
-- TOC entry 4921 (class 2606 OID 41072)
-- Name: historial_etapa historial_etapa_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.historial_etapa
    ADD CONSTRAINT historial_etapa_pkey PRIMARY KEY (id_historial);


--
-- TOC entry 4928 (class 2606 OID 41123)
-- Name: registro_alimentacion registro_alimentacion_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.registro_alimentacion
    ADD CONSTRAINT registro_alimentacion_pkey PRIMARY KEY (id_registro);


--
-- TOC entry 4916 (class 2606 OID 41019)
-- Name: registro_peso registro_peso_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.registro_peso
    ADD CONSTRAINT registro_peso_pkey PRIMARY KEY (id_registro);


--
-- TOC entry 4925 (class 2606 OID 41097)
-- Name: registro_sanitario registro_sanitario_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.registro_sanitario
    ADD CONSTRAINT registro_sanitario_pkey PRIMARY KEY (id_registro);


--
-- TOC entry 4919 (class 2606 OID 41046)
-- Name: reproduccion reproduccion_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.reproduccion
    ADD CONSTRAINT reproduccion_pkey PRIMARY KEY (id_reproduccion);


--
-- TOC entry 4907 (class 2606 OID 40978)
-- Name: usuario usuario_nombre_usuario_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.usuario
    ADD CONSTRAINT usuario_nombre_usuario_key UNIQUE (nombre_usuario);


--
-- TOC entry 4909 (class 2606 OID 40976)
-- Name: usuario usuario_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.usuario
    ADD CONSTRAINT usuario_pkey PRIMARY KEY (id_usuario);


--
-- TOC entry 4926 (class 1259 OID 41137)
-- Name: idx_alimentacion_animal_fecha; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_alimentacion_animal_fecha ON public.registro_alimentacion USING btree (id_animal, fecha);


--
-- TOC entry 4922 (class 1259 OID 41138)
-- Name: idx_historial_etapa_animal_fecha; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_historial_etapa_animal_fecha ON public.historial_etapa USING btree (id_animal, fecha_cambio);


--
-- TOC entry 4914 (class 1259 OID 41134)
-- Name: idx_peso_animal_fecha; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_peso_animal_fecha ON public.registro_peso USING btree (id_animal, fecha);


--
-- TOC entry 4917 (class 1259 OID 41135)
-- Name: idx_reproduccion_cerda_fecha; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_reproduccion_cerda_fecha ON public.reproduccion USING btree (id_cerda, fecha_servicio);


--
-- TOC entry 4923 (class 1259 OID 41136)
-- Name: idx_sanitario_animal_fecha; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_sanitario_animal_fecha ON public.registro_sanitario USING btree (id_animal, fecha);


--
-- TOC entry 4938 (class 2606 OID 41124)
-- Name: registro_alimentacion fk_alimentacion_animal; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.registro_alimentacion
    ADD CONSTRAINT fk_alimentacion_animal FOREIGN KEY (id_animal) REFERENCES public.animal(id_animal) ON DELETE RESTRICT;


--
-- TOC entry 4939 (class 2606 OID 41129)
-- Name: registro_alimentacion fk_alimentacion_usuario; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.registro_alimentacion
    ADD CONSTRAINT fk_alimentacion_usuario FOREIGN KEY (id_usuario) REFERENCES public.usuario(id_usuario) ON DELETE RESTRICT;


--
-- TOC entry 4929 (class 2606 OID 41000)
-- Name: animal fk_animal_usuario; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.animal
    ADD CONSTRAINT fk_animal_usuario FOREIGN KEY (id_usuario_registro) REFERENCES public.usuario(id_usuario) ON DELETE RESTRICT;


--
-- TOC entry 4934 (class 2606 OID 41073)
-- Name: historial_etapa fk_historial_animal; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.historial_etapa
    ADD CONSTRAINT fk_historial_animal FOREIGN KEY (id_animal) REFERENCES public.animal(id_animal) ON DELETE RESTRICT;


--
-- TOC entry 4935 (class 2606 OID 41078)
-- Name: historial_etapa fk_historial_usuario; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.historial_etapa
    ADD CONSTRAINT fk_historial_usuario FOREIGN KEY (id_usuario) REFERENCES public.usuario(id_usuario) ON DELETE RESTRICT;


--
-- TOC entry 4930 (class 2606 OID 41020)
-- Name: registro_peso fk_peso_animal; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.registro_peso
    ADD CONSTRAINT fk_peso_animal FOREIGN KEY (id_animal) REFERENCES public.animal(id_animal) ON DELETE RESTRICT;


--
-- TOC entry 4931 (class 2606 OID 41025)
-- Name: registro_peso fk_peso_usuario; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.registro_peso
    ADD CONSTRAINT fk_peso_usuario FOREIGN KEY (id_usuario) REFERENCES public.usuario(id_usuario) ON DELETE RESTRICT;


--
-- TOC entry 4932 (class 2606 OID 41047)
-- Name: reproduccion fk_reproduccion_cerda; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.reproduccion
    ADD CONSTRAINT fk_reproduccion_cerda FOREIGN KEY (id_cerda) REFERENCES public.animal(id_animal) ON DELETE RESTRICT;


--
-- TOC entry 4933 (class 2606 OID 41052)
-- Name: reproduccion fk_reproduccion_usuario; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.reproduccion
    ADD CONSTRAINT fk_reproduccion_usuario FOREIGN KEY (id_usuario) REFERENCES public.usuario(id_usuario) ON DELETE RESTRICT;


--
-- TOC entry 4936 (class 2606 OID 41098)
-- Name: registro_sanitario fk_sanitario_animal; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.registro_sanitario
    ADD CONSTRAINT fk_sanitario_animal FOREIGN KEY (id_animal) REFERENCES public.animal(id_animal) ON DELETE RESTRICT;


--
-- TOC entry 4937 (class 2606 OID 41103)
-- Name: registro_sanitario fk_sanitario_usuario; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.registro_sanitario
    ADD CONSTRAINT fk_sanitario_usuario FOREIGN KEY (id_usuario) REFERENCES public.usuario(id_usuario) ON DELETE RESTRICT;


-- Completed on 2026-11-26 03:00:45

--
-- PostgreSQL database dump complete
--

\unrestrict E6qfDiiHQRioIOREseRTUKbSynGqGsatqwXvkarngKXsC0TOjMCK14Z4jWXQDT6

