CREATE TABLE roles (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    --created_at  TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT roles_name_format
        CHECK (name ~ '^ROLE_[A-Z_]+$')
);
