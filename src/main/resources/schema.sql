-- CREATE TABLE USERS IF NOT EXIST
CREATE TABLE IF NOT EXISTS public.users
(
    id character varying(50) COLLATE pg_catalog."default" NOT NULL,
    email character varying(100) COLLATE pg_catalog."default" NOT NULL,
    first_nm character varying(50) COLLATE pg_catalog."default" NOT NULL,
    last_nm character varying(50) COLLATE pg_catalog."default" NOT NULL,
    password character varying(25) COLLATE pg_catalog."default" NOT NULL,
    balance numeric,
    CONSTRAINT users_pkey PRIMARY KEY (id)
)

-- CREATE TABLE USER_PIC IF NOT EXIST
CREATE TABLE IF NOT EXISTS public.user_pic
(
    id character varying(50) COLLATE pg_catalog."default" NOT NULL,
    image_nm character varying(100) COLLATE pg_catalog."default" NOT NULL,
    image_dir character varying(200) COLLATE pg_catalog."default" NOT NULL,
    image_size numeric NOT NULL,
    user_id character varying COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT user_pic_pkey PRIMARY KEY (id)
)

-- CREATE TABLE TRANSACTION IF NOT EXIST
CREATE TABLE IF NOT EXISTS public.transaction
(
    id character varying(50) COLLATE pg_catalog."default" NOT NULL,
    email character varying(50) COLLATE pg_catalog."default" NOT NULL,
    invoice_no character varying(50) COLLATE pg_catalog."default" NOT NULL,
    service_nm character varying(100) COLLATE pg_catalog."default" NOT NULL,
    trx_type character varying(50) COLLATE pg_catalog."default" NOT NULL,
    total_amt numeric NOT NULL,
    created_dt timestamp with time zone NOT NULL,
    service_cd character varying(30) COLLATE pg_catalog."default",
    description character varying(50) COLLATE pg_catalog."default",
    CONSTRAINT trx_type_const PRIMARY KEY (id)
)