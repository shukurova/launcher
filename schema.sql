CREATE TABLE users
(
    id                      SERIAL PRIMARY KEY,
    name                    TEXT NOT NULL,
    username                TEXT NOT NULL UNIQUE,
    password                TEXT NOT NULL,
    authority               INTEGER NOT NULL REFERENCES authorities(role_id),
    account_non_expired     BOOLEAN       DEFAULT TRUE,
    account_non_locked      BOOLEAN       DEFAULT TRUE,
    credentials_non_expired BOOLEAN       DEFAULT TRUE,
    enabled                 BOOLEAN       DEFAULT FALSE
);

CREATE TABLE authorities (
    id SERIAL,
    role_id INTEGER NOT NULL PRIMARY KEY,
    role TEXT NOT NULL DEFAULT 'ROLE_USER'
);