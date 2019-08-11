package ru.itpark.gameslauncher.repository.sql;

/**
 * Запросы в БД для манипуляции информацией по играм.
 */
public interface GameSqlQueries {

    String GET_ALL_GAMES =
            "SELECT id, name, coverage FROM games  WHERE approved = true;";

    String GET_APPROVED_BY_ID =
            "SELECT g.id, g.name, g.release_date, g.content, g.coverage, c.name as company_name, g.status, g.genre, g.likes, g.dislikes FROM games g JOIN companies c ON g.company_id = c.id WHERE g.id = :id AND g.approved = true;";

    String GET_ALL_NOT_APPROVED =
            "SELECT id, name, coverage FROM games WHERE approved = false AND returned = false;";

    String GET_ALL_RETURNED =
            "SELECT id, name, coverage FROM games WHERE approved = false AND returned = true;";

    String GET_NOT_APPROVED_BY_ID =
            "SELECT g.id, g.name, g.release_date, g.content, g.coverage, c.name as company_name, g.status, g.genre FROM games g JOIN companies c ON g.company_id = c.id WHERE g.id = :id AND g.approved = false AND g.returned = false;";

    String GET_RETURNED_BY_ID =
            "SELECT g.id, g.name, g.release_date, g.content, g.coverage, c.name as company_name, g.status, g.genre FROM games g JOIN companies c ON g.company_id = c.id WHERE g.id = :id AND g.approved = false AND g.returned = true;";

    String CREATE_GAME =
            "INSERT INTO games (name, release_date, content, coverage, company_id, status, genre) VALUES (:name, :releaseDate, :content, :coverage, :companyId, :status, :genre);";

    String CHECK_EXISTS_BY_NAME = "SELECT count(id) FROM games WHERE name = :name;";

    String APPROVE_GAME = "UPDATE games SET approved = true, returned = false WHERE id = :id;";

    String FIND_NOT_APPROVED_GAME_WITH_COMMENT =
            "SELECT g.id, g.name, g.release_date, g.content, g.coverage, c.name as company_name, g.status, g.genre, rgc.comment FROM games g JOIN returned_games_comments rgc ON g.id = rgc.game_id JOIN companies c ON g.company_id = c.id AND g.returned = true AND rgc.game_id = :id;";

    String SAVE_COMMENT_WITH_RETURNED_GAME = "INSERT INTO returned_games_comments (game_id, company_id, comment) VALUES (:id, :companyId, :comment);";

    String CHANGE_GAME_STATUS_TO_RETURNED = "UPDATE games set returned = true where id = :id;";

    String EDIT_GAME = "UPDATE games SET name = :name, release_date = :releaseDate, content = :content, coverage = :coverage, status = :status, genre = :genre, returned = false WHERE id = :id;";

    String FIND_USER_GAME_BY_GAME_ID =
            "SELECT g.id, g.name, g.release_date, g.content, g.coverage, c.name as company_name, g.status, g.genre, g.likes, g.dislikes, g.approved, g.returned FROM games g JOIN companies c ON g.company_id = c.id WHERE g.id = :id;";

    String FIND_GAME_BY_GAME_ID =
            "SELECT g.id, g.name, g.release_date, g.content, g.coverage, c.name as company_name, g.status, g.genre, g.likes, g.dislikes FROM games g JOIN companies c ON g.company_id = c.id WHERE g.id = :id;";

    String FIND_GAME_BY_USER_ID = "SELECT id, name, coverage FROM games WHERE company_id = :companyId;";

    String GET_COMPANY_ID_FROM_GAMES = "SELECT company_id FROM games WHERE id = :id;";
}
