CREATE TABLE bids (
                      id          INT4             PRIMARY KEY,
                      id_auction  INT4             NOT NULL REFERENCES auctions(id) ON DELETE CASCADE,
                      id_user     INT4             NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                      amount      DOUBLE PRECISION NOT NULL,
                      placed_at   TIMESTAMP        NOT NULL DEFAULT NOW()
);