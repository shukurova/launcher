CREATE TABLE users
(
    id                      SERIAL PRIMARY KEY,
    name                    TEXT         NOT NULL,
    username                VARCHAR(255) NOT NULL UNIQUE,
    password                VARCHAR(20)  NOT NULL,
    authorities             TEXT[]       NOT NULL DEFAULT ARRAY[]::TEXT[],
    account_non_expired     BOOLEAN               DEFAULT TRUE,
    account_non_locked      BOOLEAN               DEFAULT TRUE,
    credentials_non_expired BOOLEAN               DEFAULT TRUE,
    enabled                 BOOLEAN               DEFAULT FALSE
);