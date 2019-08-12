package ru.itpark.gameslauncher.repository.sql;

/**
 * Запросы в БД для манипуляции информацией по разработчикам.
 */
public interface DeveloperSqlQueries {

    String GET_COMPANY_BY_USER_ID = "SELECT id, name, country, content, creation_date, approved, returned FROM companies WHERE id = (SELECT company_id FROM developers WHERE user_id = :id);";

    String GET_COMPANY_ID_FROM_DEVELOPERS = "SELECT company_id FROM developers WHERE user_id = :id;";

    String GET_DEVELOPERS_EMAILS = "SELECT DISTINCT u.email FROM users u JOIN developers d ON u.id = d.user_id JOIN games g ON g.company_id = d.company_id WHERE g.company_id = :id;";

    String GET_USER_ID_BY_COMPANY_ID = "SELECT user_id FROM developers WHERE company_id = :id;";
}
