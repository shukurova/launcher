package ru.itpark.gameslauncher.repository.sql;

/**
 * Запросы в БД для манипуляции информацией по токенам авторизации.
 */
public interface CompanySqlQueries {

    String GET_ALL_APPROVED_COMPANIES = "SELECT id, name, country, content, creation_date FROM companies WHERE approved = true;";

    String GET_APPROVED_COMPANY_BY_ID = "SELECT id, name FROM companies WHERE id = :id AND approved = true;";

    String CONDENSED_GAME_INFO_FOR_COMPANY = "SELECT g.id, g.name, g.coverage FROM games g JOIN companies c ON g.company_id = c.id WHERE c.id = :id;";
}