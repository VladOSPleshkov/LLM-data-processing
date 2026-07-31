CREATE TABLE IF NOT EXISTS review_categories (
    review_id   UUID NOT NULL REFERENCES reviews(id)    ON DELETE CASCADE,
    category_id UUID NOT NULL REFERENCES categories(id) ON DELETE CASCADE,
    PRIMARY KEY (review_id, category_id)
);