CREATE TABLE users
(
    id                      SERIAL PRIMARY KEY,
    name                    TEXT NOT NULL,
    username                TEXT NOT NULL UNIQUE,
    email                   TEXT NOT NULL UNIQUE,
    password                TEXT NOT NULL,
    account_non_expired     BOOLEAN DEFAULT TRUE,
    account_non_locked      BOOLEAN DEFAULT TRUE,
    credentials_non_expired BOOLEAN DEFAULT TRUE,
    enabled                 BOOLEAN DEFAULT FALSE
);

CREATE TABLE authorities
(
    id        SERIAL PRIMARY KEY,
    user_id   INTEGER NOT NULL REFERENCES users,
    authority TEXT    NOT NULL
);

CREATE TABLE auth_tokens
(
    id      TEXT PRIMARY KEY NOT NULL,
    user_id INTEGER          NOT NULL REFERENCES users
);

CREATE TABLE reg_tokens
(
    id      TEXT PRIMARY KEY NOT NULL,
    user_id INTEGER REFERENCES users,
    created TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP
);