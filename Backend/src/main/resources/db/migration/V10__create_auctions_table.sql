CREATE TABLE auctions (
                          id            INT4        PRIMARY KEY,
                          id_post       INT4        NOT NULL UNIQUE REFERENCES posts(id) ON DELETE CASCADE,
                          starting_price DOUBLE PRECISION NOT NULL,
                          current_best   DOUBLE PRECISION NOT NULL,
                          end_date       TIMESTAMP   NOT NULL,
                          is_closed      BOOLEAN     NOT NULL DEFAULT FALSE,
                          winner_id      INT4        REFERENCES users(id) ON DELETE SET NULL
);