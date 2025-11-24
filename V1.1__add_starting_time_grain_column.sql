--
-- PostgreSQL database dump
--

\restrict HkbynVTMQdGhjLgFxmGWfLMSOTNcOr6KaIjm63Po0oEI8FVXq7BhsGaSSIj5w0v

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
-- Name: scheduled_job; Type: TABLE; Schema: public; Owner: lp_user
--

CREATE TABLE public.scheduled_job (
    scheduled_job_uuid uuid NOT NULL,
    end_time timestamp(6) without time zone NOT NULL,
    start_time timestamp(6) without time zone NOT NULL,
    job_uuid uuid NOT NULL,
    machine_uuid uuid NOT NULL,
    starting_time_grain integer NOT NULL,
    schedule_uuid uuid
);


ALTER TABLE public.scheduled_job OWNER TO lp_user;

--
-- Name: scheduled_job scheduled_job_pkey; Type: CONSTRAINT; Schema: public; Owner: lp_user
--

ALTER TABLE ONLY public.scheduled_job
    ADD CONSTRAINT scheduled_job_pkey PRIMARY KEY (scheduled_job_uuid);


--
-- Name: scheduled_job fk2gw2sq8tfvx8g5u29tojt6buf; Type: FK CONSTRAINT; Schema: public; Owner: lp_user
--

ALTER TABLE ONLY public.scheduled_job
    ADD CONSTRAINT fk2gw2sq8tfvx8g5u29tojt6buf FOREIGN KEY (machine_uuid) REFERENCES public.machine(machine_uuid);


--
-- Name: scheduled_job fk_scheduled_job_schedule; Type: FK CONSTRAINT; Schema: public; Owner: lp_user
--

ALTER TABLE ONLY public.scheduled_job
    ADD CONSTRAINT fk_scheduled_job_schedule FOREIGN KEY (schedule_uuid) REFERENCES public.schedule(schedule_uuid);


--
-- Name: scheduled_job fktg2i8rwld0yc9jn0s4kixiawk; Type: FK CONSTRAINT; Schema: public; Owner: lp_user
--

ALTER TABLE ONLY public.scheduled_job
    ADD CONSTRAINT fktg2i8rwld0yc9jn0s4kixiawk FOREIGN KEY (job_uuid) REFERENCES public.job(job_uuid);


--
-- Name: TABLE scheduled_job; Type: ACL; Schema: public; Owner: lp_user
--



--
-- PostgreSQL database dump complete
--

\unrestrict HkbynVTMQdGhjLgFxmGWfLMSOTNcOr6KaIjm63Po0oEI8FVXq7BhsGaSSIj5w0v

