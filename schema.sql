CREATE TABLE users
(
    id                      SERIAL PRIMARY KEY,
    name                    TEXT NOT NULL,
    username                TEXT NOT NULL UNIQUE,
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
    -- убрать дефолтное значение
    authority TEXT    NOT NULL DEFAULT 'ROLE_USER' -- ROLE_ADMIN, ROLE_DEVELOPER, ROLE_APPROVER
);

-- TODO: спросить про корректность создания таблицы
-- Поменять тип даты
CREATE TABLE tokens
(
    token        TEXT    NOT NULL PRIMARY KEY,
    user_id      INTEGER NOT NULL REFERENCES users,
    created_date DATE DEFAULT CURRENT_DATE
);

CREATE TABLE games
(
    id         SERIAL PRIMARY KEY,
    name       TEXT    NOT NULL UNIQUE,
    content    TEXT    NOT NULL,
    coverage   TEXT    NOT NULL,
    company_id INTEGER NOT NULL REFERENCES companies,
    status     TEXT    NOT NULL DEFAULT 'TODO', -- 'IN_PROGRESS', 'BETA', 'RELEASE'
    genre      TEXT    NOT NULL,
    likes      INTEGER          DEFAULT 0,
    dislikes   INTEGER          DEFAULT 0
);

CREATE TABLE comments
(
    id       SERIAL PRIMARY KEY,
    user_id  INTEGER NOT NULL REFERENCES users,
    game_id  INTEGER NOT NULL REFERENCES games,
    content  TEXT    NOT NULL,
    date     DATE    DEFAULT CURRENT_DATE,
    likes    INTEGER DEFAULT 0,
    dislikes INTEGER DEFAULT 0
);

CREATE TABLE companies
(
    id            SERIAL PRIMARY KEY,
    name          TEXT NOT NULL UNIQUE,
    country       TEXT NOT NULL,
    content       TEXT NOT NULL,
    date_creation DATE NOT NULL
);

CREATE TABLE medias
(
    id      SERIAL PRIMARY KEY,
    game_id INTEGER NOT NULL REFERENCES games,
    path    TEXT    NOT NULL,
    type    TEXT    NOT NULL -- 'IMAGE', 'VIDEO'
);
