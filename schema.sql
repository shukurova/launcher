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

CREATE TABLE companies
(
    id            SERIAL PRIMARY KEY,
    name          TEXT NOT NULL UNIQUE,
    country       TEXT NOT NULL,
    content       TEXT NOT NULL,
    date_creation DATE NOT NULL
);

CREATE TABLE games
(
    id           SERIAL PRIMARY KEY,
    name         TEXT    NOT NULL UNIQUE,
    release_date DATE    NOT NULL,
    content      TEXT    NOT NULL,
    coverage     TEXT    NOT NULL,
    company_id   INTEGER NOT NULL REFERENCES companies,
    status       TEXT    NOT NULL DEFAULT 'TODO',
    genre        TEXT    NOT NULL,
    likes        INTEGER          DEFAULT 0,
    dislikes     INTEGER          DEFAULT 0
);

CREATE TABLE comments
(
    id       SERIAL PRIMARY KEY,
    user_id  INTEGER NOT NULL REFERENCES users,
    game_id  INTEGER NOT NULL REFERENCES games,
    content  TEXT    NOT NULL,
    date     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    likes    INTEGER   DEFAULT 0,
    dislikes INTEGER   DEFAULT 0
);

CREATE TABLE medias
(
    id      SERIAL PRIMARY KEY,
    game_id INTEGER NOT NULL REFERENCES games,
    path    TEXT    NOT NULL,
    type    TEXT    NOT NULL -- 'IMAGE', 'VIDEO'
);