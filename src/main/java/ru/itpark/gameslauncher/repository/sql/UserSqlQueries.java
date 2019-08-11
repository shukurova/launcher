package ru.itpark.gameslauncher.repository.sql;

/**
 * Запросы в БД для манипуляции информацией по пользователям.
 */
public interface UserSqlQueries {

    String DROP_USERS_BY_TIME = "DELETE FROM users WHERE enabled = false AND (SELECT extract(epoch FROM (SELECT CURRENT_TIMESTAMP::timestamp - created::timestamp)) / 60) >= 60;";

    String FIND_BY_USERNAME = "SELECT id, name, username, email, password, account_non_expired, account_non_locked, credentials_non_expired, enabled, created FROM users WHERE username = :username;";

    String FIND_AUTHORITY = "SELECT authority FROM authorities WHERE user_id = :id;";

    String SAVE_USER = "INSERT INTO users (name, username, password, email) VALUES (:name, :username, :password, :email);";

    String SAVE_AUTHORITY = "INSERT INTO authorities (user_id, authority) VALUES (:id, :authority);";

    String USERNAME_COUNT = "SELECT count(id) FROM users WHERE username = :username;";

    String FIND_USER_BY_ID = "SELECT id, name, username, email, password, account_non_expired, account_non_locked, credentials_non_expired, enabled, created FROM users WHERE id = :id;";

    String ENABLE_USER = "UPDATE users SET enabled = true WHERE id = :id;";

    String USER_INFORMATION = "SELECT id, name, username, email, created FROM users WHERE id = :id;";
}