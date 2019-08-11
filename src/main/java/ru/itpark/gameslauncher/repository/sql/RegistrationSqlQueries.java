package ru.itpark.gameslauncher.repository.sql;

/**
 * Запросы в БД для манипуляции информацией по токенам регистрации.
 */
public interface RegistrationSqlQueries {

    String DROP_TOKENS_BY_TIME = "DELETE FROM registration_tokens WHERE (SELECT extract(epoch FROM (SELECT CURRENT_TIMESTAMP::timestamp - created::timestamp)) / 60) >= 30;";

    String SAVE_TOKEN = "INSERT INTO registration_tokens (id, user_id) VALUES (:id, :userId);";

    String FIND_BY_TOKEN = "SELECT id, user_id, created FROM registration_tokens WHERE id = :id;";

    String TOKENS_COUNT = "SELECT COUNT(id) FROM registration_tokens WHERE user_id = :userId;";
}