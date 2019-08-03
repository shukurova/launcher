package ru.itpark.gameslauncher.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.itpark.gameslauncher.domain.CompanyDomain;

import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DeveloperRepository {
    private final NamedParameterJdbcTemplate template;

    public Optional<CompanyDomain> getCompanyByUserId(long id) {
        try {
            var company =
                    template.queryForObject("SELECT id, name, country, content, creation_date FROM companies WHERE id = (SELECT company_id FROM developers WHERE user_id = :id);",
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
