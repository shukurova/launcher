package ru.itpark.gameslauncher.repository.sql;

/**
 * Запросы в БД для манипуляции информацией по токенам авторизации.
 */
public interface AuthenticationSqlQueries {

    String FIND_BY_TOKEN = "SELECT id, user_id FROM authentication_tokens WHERE id = :id;";

    String SAVE_TOKEN = "INSERT INTO authentication_tokens (id, user_id) VALUES (:id, :userId);";
}
