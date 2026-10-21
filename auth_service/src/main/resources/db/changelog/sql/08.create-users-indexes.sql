CREATE INDEX IF NOT EXISTS idx_users_password_hash ON users(password_hash);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);