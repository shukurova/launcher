CREATE TABLE users
(
    id                      SERIAL PRIMARY KEY,
    name                    TEXT      NOT NULL,
    username                TEXT      NOT NULL UNIQUE,
    email                   TEXT      NOT NULL UNIQUE,
    password                TEXT      NOT NULL,
    account_non_expired     BOOLEAN            DEFAULT TRUE,
    account_non_locked      BOOLEAN            DEFAULT TRUE,
    credentials_non_expired BOOLEAN            DEFAULT TRUE,
    enabled                 BOOLEAN            DEFAULT FALSE,
    created                 TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE authorities
(
    id        SERIAL PRIMARY KEY,
    user_id   INTEGER NOT NULL REFERENCES users,
    authority TEXT    NOT NULL
);

CREATE TABLE authentication_tokens
(
    id      TEXT PRIMARY KEY NOT NULL,
    user_id INTEGER          NOT NULL REFERENCES users
);

CREATE TABLE registration_tokens
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
    creation_date DATE NOT NULL,
    approved     BOOlEAN NOT NULL DEFAULT FALSE,
    returned     BOOlEAN NOT NULL DEFAULT FALSE,
    creator_id INTEGER NOT NULL REFERENCES users
);

CREATE TABLE games
(
    id           SERIAL PRIMARY KEY,
    name         TEXT    NOT NULL UNIQUE,
    release_date DATE    NOT NULL,
    content      TEXT    NOT NULL,
    coverage     TEXT    NOT NULL,
    company_id   INTEGER NOT NULL REFERENCES companies,
    status       INTEGER NOT NULL DEFAULT 0,
    genre        INTEGER NOT NULL,
    likes        INTEGER          DEFAULT 0,
    dislikes     INTEGER          DEFAULT 0,
    approved     BOOlEAN NOT NULL DEFAULT FALSE,
    returned     BOOlEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE developers
(
    id         SERIAL PRIMARY KEY,
    user_id    INTEGER NOT NULL REFERENCES users,
    company_id INTEGER NOT NULL REFERENCES companies
);

CREATE TABLE return_game_comments
(
    id         SERIAL PRIMARY KEY,
    game_id    INTEGER   NOT NULL REFERENCES games,
    company_id INTEGER   NOT NULL REFERENCES companies,
    comment    TEXT      NOT NULL,
    created    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE return_company_comments
(
    id         SERIAL PRIMARY KEY,
    company_id INTEGER   NOT NULL REFERENCES companies,
    comment    TEXT      NOT NULL,
    created    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
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

INSERT INTO companies (name, country, content, creation_date)
VALUES ('Young Horses Game', 'USA',
        'As Young Horses we strive to push the boundaries of game design in order to create experiences that players have not seen before. ',
        '2011-03-15'),
       ('Basilisk Games', 'USA',
        'Currently specializing in role-playing games, the company released its first title Eschalon: Book 1 in 2007. The company consists of one full-time employee, Thomas Riegsecker, as well as several contract employees.',
        '2017-01-03'),
       ('Zero Games', 'USA',
        'Independant Video Games / Mobile Application / Innovative Technologies creators since 2013. Unity & Unreal Engine development specialists',
        '2013-09-08'),
       ('Capybara Games', 'Canada',
        'The company is most known for developing 2011s Superbrothers: Sword & Sworcery EP and 2014s Super Time Force.',
        '2003-10-01'),
       ('Daedalic Entertainment', 'Germany',
        'They are best known for developing point-and-click adventure games. Daedalic operates two subsidiary studios: Daedalic Entertainment Studio West (in Düsseldorf; since July 2014) and Daedalic Entertainment Bavaria',
        '2018-02-12'),
       ('Digital Happiness', 'Indonesia',
        'The founder of the company and main producer of their most well-known game, DreadOut, is Rachmad Imron.',
        '2013-04-02'),
       ('Games Distillery', 'Slovakia',
        'C It was founded by members of defunct company 10tacle Studios Slovakia that worked on a cancelled game Elveon',
        '2008-09-03'),
       ('Jagex', 'England',
        'It is best known for RuneScape and Old School RuneScape, collectively known as the worlds largest free-to-play massively multiplayer online role-playing games.',
        '2016-08-08'),
       ('Mojang', 'Sweden',
        'Mojang is best known for creating Minecraft (released in 2011), the best-selling video game of all time.',
        '2016-07-01'),
       ('Squad ', 'Mexico',
        'The company is best known for their debut title, the spaceflight simulation game Kerbal Space Program.',
        '2014-09-12');

INSERT INTO games (name, release_date, content, coverage, company_id, status, genre)
VALUES ('Redcon', '2019-09-01',
        'The player controls a fort(later progressing to "Fortress"-type fortifications) armed with cannons. The players task is to destroy an enemys fort/fortress without being defeated. Weapons and facilities can be upgraded and configured between battles.',
        'empty-coverage.png', 1, 'BETA', 'STRATEGY'),
       ('The Void', '2020-03-09',
        'The player takes on the role of a lost soul that accidentally lingers in a place called "the Void" before meeting its absolute death.',
        'empty-coverage.png', 2, 'IN_PROGRESS', 'ADVENTURE'),
       ('Ace of Spades', '2021-08-15',
        'This version of the game played as a 16-versus-16 team-based first-person shooter with a capture the flag game mode, in which players were to obtain the opposing teams intelligence briefcase and return it to the own teams base.',
        'empty-coverage.png', 3, 'TODO', 'ACTION'),
       ('Aurion: Legacy of the Kori-Odan', '2022-06-21',
        'Aurion is a single-player[1] action role-playing game, in which the player controls main character Enzo Kori-Odan. The gameworld is a two dimensional setting, which the player can explore.',
        'empty-coverage.png', 4, 'IN_PROGRESS', 'RPG'),
       ('Cobalt ', '2019-07-01',
        'Players play as the main character, known as Cobalt. Some key mechanics of the game include bullet time, rolling (to deflect bullets) and punching which can deal damage and knock back explosives, all of which a player can combo together to produce an advanced level of play.',
        'empty-coverage.png', 5, 'IN_RELEASE', 'ACTION'),
       ('Ori and the Blind Forest', '2020-05-03',
        'Ori and the Blind Forest is a 2D platform game. The player controls Ori, a white guardian spirit of indeterminate gender, and Sein, who is the light and eyes of the Spirit Tree. Ori can jump, climb, and use other abilities to navigate.',
        'empty-coverage.png', 6, 'IN_PROGRESS', 'ADVENTURE'),
       ('Gorogoa', '2019-04-01',
        'Players can manipulate each image, such as zooming in or out, and discover interesting clues that can be used to find a solution for each scene. The game is depicted in a beautiful, hand-drawn art style and uses its puzzles to tell a story about a boy’s fascination with a divine creature.',
        'empty-coverage.png', 7, 'IN_RELEASE', 'PUZZLE'),
       ('Limbo 2', '2022-08-26',
        'The player controls the boy throughout the game. As is typical of most two-dimensional platform games, the boy can run left or right, jump, climb onto short ledges or up and down ladders and ropes, and push or pull objects. Limbo is presented through dark, greyscale graphics and with minimalist ambient sounds, creating an eerie, haunting environment',
        'empty-coverage.png', 8, 'TODO', 'PUZZLE'),
       ('Imperial Motel', '2019-12-15',
        'Our hero just wants to find his wallet and catch a bus out of this place. Help the hapless youth as he wanders across realities, making. . . "friends". . . along the way. ',
        'empty-coverage.png', 9, 'BETA', 'INTERACTIVE_FICTION'),
       ('Ashen', '2021-11-15',
        'Gameplay has you exploring caves, forests, and ancient ruins in search of stronger equipment as character progression in Ashen is tied to gear, not stats.',
        'empty-coverage.png', 10, 'IN_PROGRESS', 'RPG');