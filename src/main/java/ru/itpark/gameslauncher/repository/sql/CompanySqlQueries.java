package ru.itpark.gameslauncher.repository.sql;

/**
 * Запросы в БД для манипуляции информацией по токенам авторизации.
 */
public interface CompanySqlQueries {

    String GET_ALL_APPROVED_COMPANIES = "SELECT id, name, country, content, creation_date FROM companies WHERE approved = true;";

    String GET_APPROVED_COMPANY_BY_ID = "SELECT id, name FROM companies WHERE id = :id AND approved = true;";

    String CONDENSED_GAME_INFO_FOR_COMPANY = "SELECT g.id, g.name, g.coverage FROM games g JOIN companies c ON g.company_id = c.id WHERE c.id = :id;";

    String GET_ALL_NOT_APPROVED_COMPANIES = "SELECT id, name FROM companies WHERE approved = false AND returned = false;";

    String GET_NOT_APPROVED_COMPANY_BY_ID = "SELECT id, name, country, content, creation_date FROM companies WHERE approved = false AND returned = false;";

    String GET_ALL_RETURNED_COMPANIES = "SELECT id, name FROM companies WHERE approved = false AND returned = true;";

    String GET_RETURNED_COMPANY_BY_ID = "SELECT id, name, country, content, creation_date FROM companies WHERE id = :id AND approved = false AND returned = true;";

    String CREATE_COMPANY = "INSERT INTO companies (name, country, content, creation_date) VALUES (:name, :country, :content, :creation_date);";

    String CHECK_COMPANY_EXISTS_BY_NAME = "SELECT count(id) FROM companies WHERE name = :name;";

    String APPROVE_COMPANY = "UPDATE companies SET approved = true, returned = false WHERE id = :id;";
}