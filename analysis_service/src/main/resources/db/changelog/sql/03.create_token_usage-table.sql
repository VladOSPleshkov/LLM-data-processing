CREATE TABLE IF NOT EXISTS token_usage (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    task_id         UUID NOT NULL UNIQUE REFERENCES tasks(id) ON DELETE CASCADE,
    prompt_tokens   INTEGER NOT NULL,
    response_tokens INTEGER NOT NULL,
    model_name      VARCHAR(100),
    recorded_at     TIMESTAMP NOT NULL DEFAULT NOW()
);
