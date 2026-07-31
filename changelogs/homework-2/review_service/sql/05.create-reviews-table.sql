CREATE TABLE IF NOT EXISTS reviews (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    content     TEXT NOT NULL,
    brand_id    UUID NOT NULL REFERENCES brands(id) ON DELETE CASCADE,
    author_name VARCHAR(255),
    rating      SMALLINT CHECK (rating BETWEEN 1 AND 5),
    source      VARCHAR(255),
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    indexed_at  TIMESTAMP
);