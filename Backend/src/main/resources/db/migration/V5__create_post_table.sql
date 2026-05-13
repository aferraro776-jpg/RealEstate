CREATE TABLE public.posts (
                              id int4 NOT NULL,
                              title varchar(50) NOT NULL,
                              description varchar(200) NOT NULL,
                              previous_price numeric(10, 2) NULL,
                              current_price numeric(10, 2) NOT NULL,
                              created_at date DEFAULT CURRENT_DATE NOT NULL,
                              id_seller int4 NULL,
                              id_real_estate int4 NULL,
                              transaction_type varchar(20) NOT NULL DEFAULT 'vendita',
                              is_auction boolean NOT NULL DEFAULT FALSE,
                              auction_end_time TIMESTAMP NULL,
                              CONSTRAINT posts_id_check CHECK ((id >= 10000) AND (id <= 99999)),
                              CONSTRAINT posts_pkey PRIMARY KEY (id),
                              CONSTRAINT posts_idRealEstate_fkey FOREIGN KEY (id_real_estate) REFERENCES public.real_estate(id),
                              CONSTRAINT posts_idSeller_fkey FOREIGN KEY (id_seller) REFERENCES public.sellers(id)
);