CREATE TABLE public.reviews (
                                   id int4 NOT NULL,
                                   title varchar(50) NOT NULL,
                                   description TEXT,
                                   rating int4 NOT NULL,
                                   created_at date DEFAULT CURRENT_DATE NOT NULL,
                                   id_user int4 NULL,
                                   id_post int4 NULL,
                                   CONSTRAINT reviews_id_check CHECK (((id >= 10000) AND (id <= 99999))),
                                   CONSTRAINT reviews_pkey PRIMARY KEY (id),
                                   CONSTRAINT reviews_valutazione_check CHECK (((rating >= 0) AND (rating <= 5))),
                                   CONSTRAINT reviews_id_post_fkey FOREIGN KEY (id_post) REFERENCES public.posts(id),
                                   CONSTRAINT reviews_id_user_fkey FOREIGN KEY (id_user) REFERENCES public.users(id)
);