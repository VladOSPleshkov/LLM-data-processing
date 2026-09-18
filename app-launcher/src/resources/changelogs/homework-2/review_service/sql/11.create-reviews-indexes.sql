
CREATE INDEX IF NOT EXISTS idx_reviews_brand_id   ON reviews(brand_id);
CREATE INDEX IF NOT EXISTS idx_reviews_created_at ON reviews(created_at DESC);