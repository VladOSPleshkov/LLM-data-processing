CREATE TABLE refresh_token_blacklist (
    jti           VARCHAR(255) PRIMARY KEY,
    user_id       UUID REFERENCES users(id) ON DELETE CASCADE,
    revoked_at    TIMESTAMP NOT NULL DEFAULT NOW(),
    expires_at    TIMESTAMP NOT NULL,
    revoke_reason VARCHAR(255)
);
