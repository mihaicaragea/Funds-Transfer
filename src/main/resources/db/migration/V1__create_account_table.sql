CREATE TABLE account (
    id BIGINT PRIMARY KEY,
    owner_id BIGINT NOT NULL,
    currency VARCHAR(10) NOT NULL,
    balance NUMERIC(20, 4) NOT NULL,
    version BIGINT
);