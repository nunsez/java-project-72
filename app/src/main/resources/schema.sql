DROP TABLE IF EXISTS url_checks;

DROP TABLE IF EXISTS urls;

CREATE TABLE urls (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    inserted_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE url_checks (
    id BIGSERIAL PRIMARY KEY,
    status_code INT,
    title VARCHAR(255),
    h1 VARCHAR(255),
    description TEXT,
    url_id BIGINT REFERENCES urls (id),
    inserted_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_inserted_a
ON url_checks (inserted_at);
