CREATE TABLE public.admins (
                               id          INT          PRIMARY KEY,
                               name        VARCHAR(50)  NOT NULL,
                               surname     VARCHAR(50)  NOT NULL,
                               email       VARCHAR(50)  NOT NULL,
                               "password" VARCHAR(60) NOT NULL,
                               UNIQUE (email)
);