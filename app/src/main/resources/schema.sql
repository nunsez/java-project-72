DROP TABLE IF EXISTS urls;

CREATE TABLE urls (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    inserted_at TIMESTAMP
);