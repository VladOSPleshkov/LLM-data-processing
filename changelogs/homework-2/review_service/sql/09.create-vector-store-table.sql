CREATE EXTENSION IF NOT EXISTS vector;
CREATE TABLE IF NOT EXISTS vector_store (
    id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    content   TEXT NOT NULL,
    metadata  JSONB,
    embedding vector(1536)
);
