package ru.itpark.gameslauncher.repository.sql;

/**
 * Запросы в БД для манипуляции информацией по разработчикам.
 */
public interface DeveloperSqlQueries {

    String GET_COMPANY_BY_USER_ID = "SELECT id, name, country, content, creation_date FROM companies WHERE id = (SELECT company_id FROM developers WHERE user_id = :id);";

    String GET_COMPANY_ID_FROM_DEVELOPERS = "SELECT company_id FROM developers WHERE user_id = :id;";
}
