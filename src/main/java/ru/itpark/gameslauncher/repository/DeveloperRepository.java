package ru.itpark.gameslauncher.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.itpark.gameslauncher.domain.CompanyDomain;
import ru.itpark.gameslauncher.repository.sql.DeveloperSqlQueries;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DeveloperRepository implements DeveloperSqlQueries {
    private final NamedParameterJdbcTemplate template;

    /**
     * Получение данных компании по id пользователя.
     *
     * @param id id пользователя
     * @return данные компании
     */
    public Optional<CompanyDomain> getCompanyByUserId(long id) {
        try {
            var company =
                    template.queryForObject(
                            GET_COMPANY_BY_USER_ID,
                            Map.of("id", id),
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
     * @param id id компании
     * @return список адресов почты
     */
    public Optional<List<String>> getDevelopersEmailsByCompanyId(long id) {
        try {
            var emails = template.query(
                    GET_DEVELOPERS_EMAILS,
                    Map.of("id", id),
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
                            GET_USER_ID_BY_COMPANY_ID,
                            Map.of("id", companyId),
                            (rs, i) -> rs.getLong("user_id"));

            return Optional.ofNullable(userId);
        } catch (
                EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
