package ru.itpark.gameslauncher.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.itpark.gameslauncher.domain.CompanyDomain;
import ru.itpark.gameslauncher.repository.sql.DeveloperSqlQueries;

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
                                    rs.getDate("creation_date").toLocalDate()
                            )
                    );
            return Optional.ofNullable(company);
        } catch (
                EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
