CREATE TABLE public.users (
                               id int4 NOT NULL,
                               name varchar(50) NOT NULL,
                               surname varchar(50) NOT NULL,
                               email varchar(50) NOT NULL,
                               birthDate date,
                               "password" varchar(60),
                               auth_provider VARCHAR(10) NOT NULL DEFAULT 'LOCAL',
                               is_banned BOOLEAN NOT NULL DEFAULT FALSE,
                               CONSTRAINT users_email_key UNIQUE (email),
                               CONSTRAINT users_id_check CHECK (((id >= 10000) AND (id <= 99999))),
                               CONSTRAINT users_pkey PRIMARY KEY (id)
);