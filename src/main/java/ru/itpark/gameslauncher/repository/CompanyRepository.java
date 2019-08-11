package ru.itpark.gameslauncher.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.itpark.gameslauncher.dto.company.CompanyCondensedResponseDto;
import ru.itpark.gameslauncher.dto.company.CompanyResponseDto;
import ru.itpark.gameslauncher.dto.game.GameCondensedResponseDto;
import ru.itpark.gameslauncher.exception.CompanyNotFoundException;
import ru.itpark.gameslauncher.repository.sql.CompanySqlQueries;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CompanyRepository implements CompanySqlQueries {
    private final NamedParameterJdbcTemplate template;

    /**
     * Получение списка подтверждённых компаний с параметром approved = true.
     *
     * @return список компаний
     */
    public List<CompanyCondensedResponseDto> getAllApproved() {
        return template.query(
                GET_ALL_APPROVED_COMPANIES,
                (rs, i) -> new CompanyCondensedResponseDto(
                        rs.getLong("id"),
                        rs.getString("name")
                ));
    }

    /**
     * Полчение компании по её id с параметром approved = true.
     *
     * @param id id компании
     * @return сущность с данными компании
     */
    public Optional<CompanyResponseDto> findApprovedById(long id) {
        try {
            var company = template.queryForObject(
                    GET_APPROVED_COMPANY_BY_ID,
                    Map.of("id", id),
                    (rs, i) -> new CompanyResponseDto(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("country"),
                            rs.getString("content"),
                            rs.getDate("creation_date").toLocalDate(),
                            Collections.emptyList()
                    ));

            var games = template.query(
                    CONDENSED_GAME_INFO_FOR_COMPANY,
                    Map.of("id", id),
                    (rs, i) -> new GameCondensedResponseDto(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("coverage")
                    ));

            assert company != null;
            company.setGames(games);

            return Optional.of(company);
        } catch (EmptyResultDataAccessException e) {
            throw new CompanyNotFoundException("Company not found!");
        }
    }
}