CREATE TABLE refresh_token_blacklist (
    id            UUID PRIMARY KEY,
    user_id       UUID REFERENCES users(id) ON DELETE CASCADE,
    token         VARCHAR(255),
    revoked_at    TIMESTAMP NOT NULL DEFAULT NOW(),
    expires_at    TIMESTAMP NOT NULL,
    revoke_reason VARCHAR(255)
);
