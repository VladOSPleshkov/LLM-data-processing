CREATE TABLE IF NOT EXISTS tasks (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    brand_id      UUID NOT NULL,
    brand_name    VARCHAR(255) NOT NULL,
    --created_by    UUID NOT NULL,
    query         TEXT NOT NULL,
    status        VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    result        TEXT,
    error_message TEXT,
    top_k         SMALLINT NOT NULL DEFAULT 10,
    created_at    TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP NOT NULL DEFAULT NOW(),
    completed_at  TIMESTAMP,
    token_used    Integer,

    CONSTRAINT tasks_status_check CHECK (
        status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED')
    )
);

CREATE INDEX IF NOT EXISTS idx_tasks_status     ON tasks(status);
--CREATE INDEX IF NOT EXISTS idx_tasks_created_by ON tasks(created_by);
CREATE INDEX IF NOT EXISTS idx_tasks_brand_id   ON tasks(brand_id);