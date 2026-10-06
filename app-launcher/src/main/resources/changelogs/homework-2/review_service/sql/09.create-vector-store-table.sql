CREATE EXTENSION IF NOT EXISTS vector;
CREATE TABLE IF NOT EXISTS vector_store (
    id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    review_id UUID NOT NULL UNIQUE REFERENCES reviews(id) ON DELETE CASCADE,
    content   TEXT NOT NULL,
    metadata  TEXT NOT NULL,
    embedding vector(1536)

);
