package ru.itpark.gameslauncher.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.itpark.gameslauncher.domain.CompanyDomain;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DeveloperRepository {
    private final NamedParameterJdbcTemplate template;

    /**
     * Получение данных компании по id пользователя.
     *
     * @param userId id пользователя
     * @return данные компании
     */
    public Optional<CompanyDomain> getCompanyByUserId(long userId) {
        try {
            var company =
                    template.queryForObject(
                            "SELECT id, name, country, content, creation_date, approved, returned FROM companies WHERE id = (SELECT company_id FROM developers WHERE user_id = :userId);",
                            Map.of("userId", userId),
                            (rs, i) -> new CompanyDomain(
                                    rs.getLong("id"),
                                    rs.getString("name"),
                                    rs.getString("country"),
                                    rs.getString("content"),
                                    rs.getDate("creation_date").toLocalDate(),
                                    rs.getBoolean("approved"),
                                    rs.getBoolean("returned")
                            )
                    );
            return Optional.ofNullable(company);
        } catch (
                EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * Получение адресов почты команды разработчиков по id компании.
     *
     * @param companyId id компании
     * @return список адресов почты
     */
    public Optional<List<String>> getDevelopersEmailsByCompanyId(long companyId) {
        try {
            var emails = template.query(
                    "SELECT DISTINCT u.email FROM users u JOIN developers d ON u.id = d.user_id JOIN games g ON g.company_id = d.company_id WHERE g.company_id = :companyId;",
                    Map.of("companyId", companyId),
                    (rs, i) ->
                            rs.getString("email"));

            return Optional.ofNullable(emails);
        } catch (
                EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * Получение списка разработчиков по id компании.
     *
     * @param companyId id компании
     * @return список id пользователей
     */
    public Optional<List<Long>> getUsersIdByCompanyId(long companyId) {
        try {
            var userId =
                    template.query(
                            "SELECT user_id FROM developers WHERE company_id = :companyId;",
                            Map.of("companyId", companyId),
                            (rs, i) -> rs.getLong("user_id"));

            return Optional.ofNullable(userId);
        } catch (
                EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * Создание записи о новом разработчике.
     *
     * @param userId    id пользователя
     * @param companyId id компании
     */
    public void createDeveloper(long userId,
                                long companyId) {
        template.update(
                "INSERT INTO developers (user_id, company_id) VALUES (:userId, :companyId);",
                Map.of("userId", userId,
                        "companyId", companyId));
    }
}
