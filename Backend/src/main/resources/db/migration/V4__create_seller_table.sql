CREATE TABLE public.sellers (
                                  id int4 NOT NULL,
                                  vat_number varchar(11) NOT NULL,
                                  name varchar(50) NOT NULL,
                                  surname varchar(50) NOT NULL,
                                  email varchar(50) NOT NULL,
                                  birth_date date NOT NULL,
                                  "password" varchar(60) NOT NULL,
                                  is_banned boolean NOT NULL DEFAULT false,
                                  CONSTRAINT sellers_email_key UNIQUE (email),
                                  CONSTRAINT sellers_id_check CHECK (((id >= 10000) AND (id <= 99999))),
                                  CONSTRAINT sellers_vatNumber_key UNIQUE (vat_number),
                                  CONSTRAINT sellers_pkey PRIMARY KEY (id)
);