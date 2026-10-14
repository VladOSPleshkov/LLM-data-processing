CREATE TABLE analysis_reports (
  task_id       UUID NOT NULL,
  brand_id      UUID NOT NULL,
  status        VARCHAR(50) NOT NULL,
  result        TEXT,
  error_message TEXT,
  completed_at  TIMESTAMP WITHOUT TIME ZONE,
  received_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  acknowledged  BOOLEAN NOT NULL DEFAULT FALSE,

  CONSTRAINT pk_analysis_reports PRIMARY KEY (task_id),

  CONSTRAINT fk_analysis_reports_on_brand FOREIGN KEY (brand_id)
  REFERENCES brands (id) ON DELETE CASCADE
);

CREATE INDEX idx_analysis_reports_brand_id ON analysis_reports(brand_id);