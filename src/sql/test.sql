--
-- PostgreSQL database dump
--

-- Dumped from database version 11.4
-- Dumped by pg_dump version 11.4

-- Started on 2021-02-22 01:58:36

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 2820 (class 0 OID 152162)
-- Dependencies: 197
-- Data for Name: tareas; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.tareas VALUES (41, 'hola mundo 2', '2021-02-22 01:23:25.703-04', true);
INSERT INTO public.tareas VALUES (43, 'holaa', '2021-02-22 01:27:54.78-04', true);
INSERT INTO public.tareas VALUES (39, 'holaa', '2021-02-22 01:23:17.513-04', true);


--
-- TOC entry 2830 (class 0 OID 0)
-- Dependencies: 198
-- Name: contact_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.contact_id_seq', 10, true);


--
-- TOC entry 2831 (class 0 OID 0)
-- Dependencies: 196
-- Name: tareas_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.tareas_id_seq', 43, true);


-- Completed on 2021-02-22 01:58:37

--
-- PostgreSQL database dump complete
--

