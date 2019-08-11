package ru.itpark.gameslauncher.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.itpark.gameslauncher.domain.CompanyDomain;
import ru.itpark.gameslauncher.dto.company.*;
import ru.itpark.gameslauncher.dto.game.GameCondensedResponseDto;
import ru.itpark.gameslauncher.exception.CompanyNotFoundException;
import ru.itpark.gameslauncher.repository.sql.CompanySqlQueries;

import java.util.*;

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

    public List<CompanyCondensedResponseDto> getAllNotApproved() {
        return template.query(
                GET_ALL_NOT_APPROVED_COMPANIES,
                (rs, i) -> new CompanyCondensedResponseDto(
                        rs.getLong("id"),
                        rs.getString("name")
                ));
    }

    public Optional<NotApprovedCompanyResponseDto> findNotApprovedById(long id) {
        try {
            var company = template.queryForObject(
                    GET_NOT_APPROVED_COMPANY_BY_ID,
                    Map.of("id", id),
                    (rs, i) -> new NotApprovedCompanyResponseDto(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("country"),
                            rs.getString("content"),
                            rs.getDate("creation_date").toLocalDate()
                    ));

            assert company != null;
            return Optional.of(company);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<CompanyCondensedResponseDto> getAllReturned() {
        return template.query(
                GET_ALL_RETURNED_COMPANIES,
                (rs, i) -> new CompanyCondensedResponseDto(
                        rs.getLong("id"),
                        rs.getString("name")
                ));
    }

    public Optional<NotApprovedCompanyResponseDto> findReturnedById(long id) {
        try {
            var company = template.queryForObject(
                    GET_RETURNED_COMPANY_BY_ID,
                    Map.of("id", id),
                    (rs, i) -> new NotApprovedCompanyResponseDto(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("country"),
                            rs.getString("content"),
                            rs.getDate("creation_date").toLocalDate()
                    ));

            assert company != null;
            return Optional.of(company);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    //TODO: привязывать ID созданной компании к ID разраба в таблице 'developers'
    public Optional<CompanyDomain> createCompany(CompanyDomain domain) {
        var keyHolder = new GeneratedKeyHolder();
        try {
            template.update(
                    CREATE_COMPANY,
                    new MapSqlParameterSource(
                            Map.of("name", domain.getName(),
                                    "country", domain.getCountry(),
                                    "content", domain.getContent(),
                                    "creation_date", domain.getCreationDate())),
                    keyHolder);
            var key = ((Number) Objects.requireNonNull(keyHolder.getKeys()).get("id")).longValue();
            domain.setId(key);
            return Optional.of(domain);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public boolean checkExistsByName(String name) {
        var companyCount = template.queryForObject(
                CHECK_COMPANY_EXISTS_BY_NAME,
                Map.of("name", name),
                Integer.class);

        assert companyCount != null;
        return companyCount > 0;
    }

    //TODO: если заявку на создание компании апрувят, то менять роль USER на DEVELOPER
    public void approveCompany(long id) {
        template.update(
                APPROVE_COMPANY,
                Map.of("id", id));
    }

    public ReturnedCompanyResponseDto findNotApprovedCompanyWithCommentById(long id) {
        return null;
    }

    public void returnCompany(long id, String name, String comment) {

    }

    public Optional<CompanyResponseDto> editCompany(long id, CompanyRequestDto dto) {
        return null;
    }
}