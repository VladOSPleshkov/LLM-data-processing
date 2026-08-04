
CREATE INDEX IF NOT EXISTS idx_token_usage_recorded_at
    ON token_usage(recorded_at DESC);