package ru.itpark.gameslauncher.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.itpark.gameslauncher.domain.CompanyDomain;
import ru.itpark.gameslauncher.domain.UserDomain;
import ru.itpark.gameslauncher.dto.company.*;
import ru.itpark.gameslauncher.dto.game.GameCondensedResponseDto;
import ru.itpark.gameslauncher.dto.user.UserCompanyResponseDto;
import ru.itpark.gameslauncher.enums.RequestStatus;
import ru.itpark.gameslauncher.exception.CompanyNotFoundException;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class CompanyRepository {
    private final NamedParameterJdbcTemplate template;
    /**
     * Получение списка подтверждённых компаний с параметром approved = true.
     *
     * @return список компаний
     */
    public List<CompanyCondensedResponseDto> getAllApproved() {
        return template.query(
                "SELECT id, name, country, content, creation_date FROM companies WHERE request_status = 1;",
                (rs, i) -> new CompanyCondensedResponseDto(
                        rs.getLong("id"),
                        rs.getString("name")
                ));
    }

    /**
     * Получение компании по её id с параметром approved = true.
     *
     * @param companyId id компании
     * @return сущность с данными компании
     */
    public Optional<CompanyResponseDto> findApprovedById(long companyId) {
        try {
            var company = template.queryForObject(
                    "SELECT id, name, country, content, creation_date FROM companies WHERE id = :id AND request_status = 1;",
                    Map.of("id", companyId),
                    (rs, i) -> new CompanyResponseDto(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("country"),
                            rs.getString("content"),
                            rs.getDate("creation_date").toLocalDate(),
                            Collections.emptyList()
                    ));

            var games = template.query(
                    "SELECT g.id, g.name, g.coverage FROM games g JOIN companies c ON g.company_id = c.id WHERE c.id = :id;",
                    Map.of("id", companyId),
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
                "SELECT id, name FROM companies WHERE request_status = 0;",
                (rs, i) -> new CompanyCondensedResponseDto(
                        rs.getLong("id"),
                        rs.getString("name")
                ));
    }

    public Optional<NotApprovedCompanyResponseDto> findNotApprovedById(long companyId) {
        try {
            var company = template.queryForObject(
                    "SELECT id, name, country, content, creation_date FROM companies WHERE request_status = 0;",
                    Map.of("id", companyId),
                    (rs, i) -> new NotApprovedCompanyResponseDto(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("country"),
                            rs.getString("content"),
                            rs.getDate("creation_date").toLocalDate()
                    ));

            return Optional.ofNullable(company);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<CompanyCondensedResponseDto> getAllReturned() {
        return template.query(
                "SELECT id, name FROM companies WHERE request_status = 2;",
                (rs, i) -> new CompanyCondensedResponseDto(
                        rs.getLong("id"),
                        rs.getString("name")
                ));
    }

    public Optional<NotApprovedCompanyResponseDto> findReturnedById(long companyId) {
        try {
            var company = template.queryForObject(
                    "SELECT id, name, country, content, creation_date FROM companies WHERE id = :id AND request_status = 2;",
                    Map.of("id", companyId),
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

    public Optional<CompanyDomain> createCompany(CompanyDomain domain) {
        var keyHolder = new GeneratedKeyHolder();
        try {
            template.update(
                    "INSERT INTO companies (name, country, content, creation_date, creator_id) VALUES (:name, :country, :content, :creationDate, :creatorId);",
                    new MapSqlParameterSource(
                            Map.of("name", domain.getName(),
                                    "country", domain.getCountry(),
                                    "content", domain.getContent(),
                                    "creationDate", domain.getCreationDate(),
                                    "creatorId", domain.getCreatorId())),
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
                "SELECT count(id) FROM companies WHERE name = :name;",
                Map.of("name", name),
                Integer.class);

        assert companyCount != null;
        return companyCount > 0;
    }

    public void approveCompany(long companyId) {
        template.update(
                "UPDATE companies SET request_status = 1 WHERE id = :id;",
                Map.of("id", companyId));
    }

    public Optional<ReturnedCompanyResponseDto> findReturnedCompanyWithCommentById(long companyId) {
        try {
            var company = template.queryForObject(
                    "SELECT c.id, c.name, c.country, c.content, c.creation_date, rcc.comment " +
                            "FROM companies c " +
                            "JOIN return_company_comments rcc ON c.id = rcc.company_id " +
                            "AND c.request_status = 2 " +
                            "AND c.id = :id;",
                    Map.of("id", companyId),
                    (rs, i) -> new ReturnedCompanyResponseDto(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("country"),
                            rs.getString("content"),
                            rs.getDate("creation_date").toLocalDate(),
                            rs.getString("comment")
                    ));

            return Optional.ofNullable(company);
        } catch (EmptyResultDataAccessException e) {
            throw new CompanyNotFoundException("Company not found!");
        }
    }

    public void returnCompany(long id,
                              String comment) {
        template.update(
                "INSERT INTO return_company_comments (company_id, comment) VALUES (:companyId, :comment);",
                new MapSqlParameterSource(
                        Map.of("companyId", id,
                                "comment", comment)));

        template.update(
                "UPDATE companies set request_status = 2 where id = :id;",
                Map.of("id", id));
    }

    public void editCompany(long companyId,
                            CompanyRequestDto dto) {
        template.update(
                "UPDATE companies SET name = :name, country = :country, content = :content, creation_date = :creationDate, request_status = 1 WHERE id = :id;",
                Map.of("name", dto.getName(),
                        "country", dto.getCountry(),
                        "content", dto.getContent(),
                        "creation_date", dto.getCreationDate(),
                        "id", companyId));
    }

    public long getCompanyIdByCompanyName(String companyName) {
        var id = template.queryForObject(
                "SELECT id FROM companies WHERE name = :name;",
                Map.of("name", companyName),
                (rs, i) -> rs.getLong("id"));
        assert id != null;
        return id;
    }

    public List<CompanyCondensedResponseDto> getCompanyByUserId(long userId) {
        return template.query(
                "SELECT c.id, c.name FROM companies c JOIN developers d ON c.id = d.company_id WHERE d.user_id = :userId;",
                Map.of("userId", userId),
                (rs, i) -> new CompanyCondensedResponseDto(
                        rs.getLong("id"),
                        rs.getString("name")));
    }

    public List<CompanyCondensedResponseDto> findCreatedCompaniesByUser(UserDomain domain) {
        return template.query(
                "SELECT id, name " +
                        "FROM companies " +
                        "WHERE creator_id = :userId;",
                Map.of("userId", domain.getId()),
                (rs, i) -> new CompanyCondensedResponseDto(
                        rs.getLong("id"),
                        rs.getString("name")
                ));
    }

    //TODO: добавить коммент, если игра не заапрувлена и вернута админом
    public Optional<UserCompanyResponseDto> findUserCompanyByCompanyId(long companyId) {
        try {
            var company = template.queryForObject(
                    "SELECT id, name, country, content, creation_date, request_status FROM companies WHERE id = :id;",
                    Map.of("id", companyId),
                    (rs, i) -> new UserCompanyResponseDto(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("country"),
                            rs.getString("content"),
                            rs.getDate("creation_date").toLocalDate(),
                            RequestStatus.getStatusByIndex(rs.getInt("approved"))
                    ));

            return Optional.ofNullable(company);
        } catch (EmptyResultDataAccessException e) {
            throw new CompanyNotFoundException("Company not found!");
        }
    }
}