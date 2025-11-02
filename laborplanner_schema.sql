--
-- PostgreSQL database dump
--

\restrict wXNg3uIBppc0ieI0HQvHanMN3VOgaCELu9BfYp9IR14yMzSOLuR1rM3oyYmiPnn

-- Dumped from database version 14.19 (Homebrew)
-- Dumped by pg_dump version 14.19 (Homebrew)

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

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: account_type; Type: TABLE; Schema: public; Owner: lp_prod_user
--

CREATE TABLE public.account_type (
    account_type_uuid uuid NOT NULL,
    name character varying(255) NOT NULL
);


ALTER TABLE public.account_type OWNER TO lp_prod_user;

--
-- Name: app_user; Type: TABLE; Schema: public; Owner: lp_prod_user
--

CREATE TABLE public.app_user (
    user_uuid uuid NOT NULL,
    email character varying(255) NOT NULL,
    last_name character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    account_type_uuid uuid NOT NULL
);


ALTER TABLE public.app_user OWNER TO lp_prod_user;

--
-- Name: job; Type: TABLE; Schema: public; Owner: lp_prod_user
--

CREATE TABLE public.job (
    job_uuid uuid NOT NULL,
    deadline timestamp(6) without time zone,
    description character varying(255),
    duration numeric(21,0) NOT NULL,
    name character varying(255) NOT NULL,
    required_machine_type_uuid uuid,
    job_template_uuid uuid
);


ALTER TABLE public.job OWNER TO lp_prod_user;

--
-- Name: job_dependencies; Type: TABLE; Schema: public; Owner: lp_prod_user
--

CREATE TABLE public.job_dependencies (
    job_uuid uuid NOT NULL,
    dependency_uuid uuid NOT NULL
);


ALTER TABLE public.job_dependencies OWNER TO lp_prod_user;

--
-- Name: job_template; Type: TABLE; Schema: public; Owner: lp_prod_user
--

CREATE TABLE public.job_template (
    job_template_uuid uuid NOT NULL,
    description character varying(255),
    duration numeric(21,0) NOT NULL,
    name character varying(255) NOT NULL,
    created_by_user_uuid uuid NOT NULL,
    required_machine_type_uuid uuid NOT NULL
);


ALTER TABLE public.job_template OWNER TO lp_prod_user;

--
-- Name: machine; Type: TABLE; Schema: public; Owner: lp_prod_user
--

CREATE TABLE public.machine (
    machine_uuid uuid NOT NULL,
    description character varying(255),
    name character varying(255) NOT NULL,
    machine_status_uuid uuid NOT NULL,
    machine_type_uuid uuid NOT NULL
);


ALTER TABLE public.machine OWNER TO lp_prod_user;

--
-- Name: machine_status; Type: TABLE; Schema: public; Owner: lp_prod_user
--

CREATE TABLE public.machine_status (
    machine_status_uuid uuid NOT NULL,
    name character varying(255) NOT NULL
);


ALTER TABLE public.machine_status OWNER TO lp_prod_user;

--
-- Name: machine_type; Type: TABLE; Schema: public; Owner: lp_prod_user
--

CREATE TABLE public.machine_type (
    machine_type_uuid uuid NOT NULL,
    name character varying(255) NOT NULL
);


ALTER TABLE public.machine_type OWNER TO lp_prod_user;

--
-- Name: schedule; Type: TABLE; Schema: public; Owner: lp_prod_user
--

CREATE TABLE public.schedule (
    schedule_uuid uuid NOT NULL,
    last_modified_date timestamp(6) without time zone NOT NULL,
    week_start_date timestamp(6) without time zone NOT NULL,
    created_by_user_uuid uuid NOT NULL
);


ALTER TABLE public.schedule OWNER TO lp_prod_user;

--
-- Name: scheduled_job; Type: TABLE; Schema: public; Owner: lp_prod_user
--

CREATE TABLE public.scheduled_job (
    scheduled_job_uuid uuid NOT NULL,
    end_time timestamp(6) without time zone NOT NULL,
    start_time timestamp(6) without time zone NOT NULL,
    job_uuid uuid NOT NULL,
    machine_uuid uuid NOT NULL,
    schedule_uuid uuid NOT NULL
);


ALTER TABLE public.scheduled_job OWNER TO lp_prod_user;

--
-- Name: account_type account_type_pkey; Type: CONSTRAINT; Schema: public; Owner: lp_prod_user
--

ALTER TABLE ONLY public.account_type
    ADD CONSTRAINT account_type_pkey PRIMARY KEY (account_type_uuid);


--
-- Name: app_user app_user_pkey; Type: CONSTRAINT; Schema: public; Owner: lp_prod_user
--

ALTER TABLE ONLY public.app_user
    ADD CONSTRAINT app_user_pkey PRIMARY KEY (user_uuid);


--
-- Name: job job_pkey; Type: CONSTRAINT; Schema: public; Owner: lp_prod_user
--

ALTER TABLE ONLY public.job
    ADD CONSTRAINT job_pkey PRIMARY KEY (job_uuid);


--
-- Name: job_template job_template_pkey; Type: CONSTRAINT; Schema: public; Owner: lp_prod_user
--

ALTER TABLE ONLY public.job_template
    ADD CONSTRAINT job_template_pkey PRIMARY KEY (job_template_uuid);


--
-- Name: machine machine_pkey; Type: CONSTRAINT; Schema: public; Owner: lp_prod_user
--

ALTER TABLE ONLY public.machine
    ADD CONSTRAINT machine_pkey PRIMARY KEY (machine_uuid);


--
-- Name: machine_status machine_status_pkey; Type: CONSTRAINT; Schema: public; Owner: lp_prod_user
--

ALTER TABLE ONLY public.machine_status
    ADD CONSTRAINT machine_status_pkey PRIMARY KEY (machine_status_uuid);


--
-- Name: machine_type machine_type_pkey; Type: CONSTRAINT; Schema: public; Owner: lp_prod_user
--

ALTER TABLE ONLY public.machine_type
    ADD CONSTRAINT machine_type_pkey PRIMARY KEY (machine_type_uuid);


--
-- Name: schedule schedule_pkey; Type: CONSTRAINT; Schema: public; Owner: lp_prod_user
--

ALTER TABLE ONLY public.schedule
    ADD CONSTRAINT schedule_pkey PRIMARY KEY (schedule_uuid);


--
-- Name: scheduled_job scheduled_job_pkey; Type: CONSTRAINT; Schema: public; Owner: lp_prod_user
--

ALTER TABLE ONLY public.scheduled_job
    ADD CONSTRAINT scheduled_job_pkey PRIMARY KEY (scheduled_job_uuid);


--
-- Name: app_user uk1j9d9a06i600gd43uu3km82jw; Type: CONSTRAINT; Schema: public; Owner: lp_prod_user
--

ALTER TABLE ONLY public.app_user
    ADD CONSTRAINT uk1j9d9a06i600gd43uu3km82jw UNIQUE (email);


--
-- Name: machine_type ukgibpxlfdrkuwm6mjmd0j0yb3v; Type: CONSTRAINT; Schema: public; Owner: lp_prod_user
--

ALTER TABLE ONLY public.machine_type
    ADD CONSTRAINT ukgibpxlfdrkuwm6mjmd0j0yb3v UNIQUE (name);


--
-- Name: account_type ukh6j4wuyxw7bvqjnjf7cx4pisc; Type: CONSTRAINT; Schema: public; Owner: lp_prod_user
--

ALTER TABLE ONLY public.account_type
    ADD CONSTRAINT ukh6j4wuyxw7bvqjnjf7cx4pisc UNIQUE (name);


--
-- Name: machine_status ukjolov500tr73km0mq5lp9a6ma; Type: CONSTRAINT; Schema: public; Owner: lp_prod_user
--

ALTER TABLE ONLY public.machine_status
    ADD CONSTRAINT ukjolov500tr73km0mq5lp9a6ma UNIQUE (name);


--
-- Name: scheduled_job fk2gw2sq8tfvx8g5u29tojt6buf; Type: FK CONSTRAINT; Schema: public; Owner: lp_prod_user
--

ALTER TABLE ONLY public.scheduled_job
    ADD CONSTRAINT fk2gw2sq8tfvx8g5u29tojt6buf FOREIGN KEY (machine_uuid) REFERENCES public.machine(machine_uuid);


--
-- Name: job_dependencies fk66vdtal6qsxlw35hx5ye00spr; Type: FK CONSTRAINT; Schema: public; Owner: lp_prod_user
--

ALTER TABLE ONLY public.job_dependencies
    ADD CONSTRAINT fk66vdtal6qsxlw35hx5ye00spr FOREIGN KEY (dependency_uuid) REFERENCES public.job(job_uuid);


--
-- Name: app_user fk9i52kqgi6gflp0oga2xhdw3n9; Type: FK CONSTRAINT; Schema: public; Owner: lp_prod_user
--

ALTER TABLE ONLY public.app_user
    ADD CONSTRAINT fk9i52kqgi6gflp0oga2xhdw3n9 FOREIGN KEY (account_type_uuid) REFERENCES public.account_type(account_type_uuid);


--
-- Name: job fkb4l4mom6e24g6esxep5ak9tfw; Type: FK CONSTRAINT; Schema: public; Owner: lp_prod_user
--

ALTER TABLE ONLY public.job
    ADD CONSTRAINT fkb4l4mom6e24g6esxep5ak9tfw FOREIGN KEY (required_machine_type_uuid) REFERENCES public.machine_type(machine_type_uuid);


--
-- Name: job_dependencies fkfdpkhjig1uona6ooil1lgdoov; Type: FK CONSTRAINT; Schema: public; Owner: lp_prod_user
--

ALTER TABLE ONLY public.job_dependencies
    ADD CONSTRAINT fkfdpkhjig1uona6ooil1lgdoov FOREIGN KEY (job_uuid) REFERENCES public.job(job_uuid);


--
-- Name: job_template fkh0tk3gkqc7wu1vcegv6qdc1or; Type: FK CONSTRAINT; Schema: public; Owner: lp_prod_user
--

ALTER TABLE ONLY public.job_template
    ADD CONSTRAINT fkh0tk3gkqc7wu1vcegv6qdc1or FOREIGN KEY (created_by_user_uuid) REFERENCES public.app_user(user_uuid);


--
-- Name: machine fkjgxkrg5dwx67hkc0pwy4kujt5; Type: FK CONSTRAINT; Schema: public; Owner: lp_prod_user
--

ALTER TABLE ONLY public.machine
    ADD CONSTRAINT fkjgxkrg5dwx67hkc0pwy4kujt5 FOREIGN KEY (machine_type_uuid) REFERENCES public.machine_type(machine_type_uuid);


--
-- Name: job fkkf23oeps4lh78hu4kwi9cspa7; Type: FK CONSTRAINT; Schema: public; Owner: lp_prod_user
--

ALTER TABLE ONLY public.job
    ADD CONSTRAINT fkkf23oeps4lh78hu4kwi9cspa7 FOREIGN KEY (job_template_uuid) REFERENCES public.job_template(job_template_uuid);


--
-- Name: schedule fkm7e8iti15jfq3n7kaff9ljsms; Type: FK CONSTRAINT; Schema: public; Owner: lp_prod_user
--

ALTER TABLE ONLY public.schedule
    ADD CONSTRAINT fkm7e8iti15jfq3n7kaff9ljsms FOREIGN KEY (created_by_user_uuid) REFERENCES public.app_user(user_uuid);


--
-- Name: job_template fkom9ssg1mjv2u4uwoskyf977di; Type: FK CONSTRAINT; Schema: public; Owner: lp_prod_user
--

ALTER TABLE ONLY public.job_template
    ADD CONSTRAINT fkom9ssg1mjv2u4uwoskyf977di FOREIGN KEY (required_machine_type_uuid) REFERENCES public.machine_type(machine_type_uuid);


--
-- Name: machine fkro4crjpajutw1t3r61ysjicmr; Type: FK CONSTRAINT; Schema: public; Owner: lp_prod_user
--

ALTER TABLE ONLY public.machine
    ADD CONSTRAINT fkro4crjpajutw1t3r61ysjicmr FOREIGN KEY (machine_status_uuid) REFERENCES public.machine_status(machine_status_uuid);


--
-- Name: scheduled_job fks0ft3mwwpefkbciv1kaihjuu7; Type: FK CONSTRAINT; Schema: public; Owner: lp_prod_user
--

ALTER TABLE ONLY public.scheduled_job
    ADD CONSTRAINT fks0ft3mwwpefkbciv1kaihjuu7 FOREIGN KEY (schedule_uuid) REFERENCES public.schedule(schedule_uuid);


--
-- Name: scheduled_job fktg2i8rwld0yc9jn0s4kixiawk; Type: FK CONSTRAINT; Schema: public; Owner: lp_prod_user
--

ALTER TABLE ONLY public.scheduled_job
    ADD CONSTRAINT fktg2i8rwld0yc9jn0s4kixiawk FOREIGN KEY (job_uuid) REFERENCES public.job(job_uuid);


--
-- Name: TABLE account_type; Type: ACL; Schema: public; Owner: lp_prod_user
--

GRANT SELECT ON TABLE public.account_type TO dev_lp_user;


--
-- Name: TABLE app_user; Type: ACL; Schema: public; Owner: lp_prod_user
--

GRANT SELECT ON TABLE public.app_user TO dev_lp_user;


--
-- Name: TABLE job; Type: ACL; Schema: public; Owner: lp_prod_user
--

GRANT SELECT ON TABLE public.job TO dev_lp_user;


--
-- Name: TABLE job_dependencies; Type: ACL; Schema: public; Owner: lp_prod_user
--

GRANT SELECT ON TABLE public.job_dependencies TO dev_lp_user;


--
-- Name: TABLE job_template; Type: ACL; Schema: public; Owner: lp_prod_user
--

GRANT SELECT ON TABLE public.job_template TO dev_lp_user;


--
-- Name: TABLE machine; Type: ACL; Schema: public; Owner: lp_prod_user
--

GRANT SELECT ON TABLE public.machine TO dev_lp_user;


--
-- Name: TABLE machine_status; Type: ACL; Schema: public; Owner: lp_prod_user
--

GRANT SELECT ON TABLE public.machine_status TO dev_lp_user;


--
-- Name: TABLE machine_type; Type: ACL; Schema: public; Owner: lp_prod_user
--

GRANT SELECT ON TABLE public.machine_type TO dev_lp_user;


--
-- Name: TABLE schedule; Type: ACL; Schema: public; Owner: lp_prod_user
--

GRANT SELECT ON TABLE public.schedule TO dev_lp_user;


--
-- Name: TABLE scheduled_job; Type: ACL; Schema: public; Owner: lp_prod_user
--

GRANT SELECT ON TABLE public.scheduled_job TO dev_lp_user;


--
-- PostgreSQL database dump complete
--

\unrestrict wXNg3uIBppc0ieI0HQvHanMN3VOgaCELu9BfYp9IR14yMzSOLuR1rM3oyYmiPnn

