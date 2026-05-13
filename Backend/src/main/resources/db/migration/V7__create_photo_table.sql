CREATE TABLE public.photos (
                               id          INT           PRIMARY KEY,
                               url         VARCHAR(255)  NOT NULL,
                               post_id      INT           NOT NULL,
                               UNIQUE(url),
                               FOREIGN KEY (post_id) REFERENCES public.posts(id)
);