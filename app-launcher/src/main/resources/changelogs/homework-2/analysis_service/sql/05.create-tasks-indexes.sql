CREATE INDEX IF NOT EXISTS idx_tasks_status     ON tasks(status);
--CREATE INDEX IF NOT EXISTS idx_tasks_created_by ON tasks(created_by);
CREATE INDEX IF NOT EXISTS idx_tasks_brand_id   ON tasks(brand_id);