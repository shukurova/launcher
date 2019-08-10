package ru.itpark.gameslauncher.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Repository;
import ru.itpark.gameslauncher.domain.UserDomain;
import ru.itpark.gameslauncher.dto.game.GameCondensedResponseDto;
import ru.itpark.gameslauncher.dto.user.UserProfileResponseDto;
import ru.itpark.gameslauncher.exception.CompanyNotFoundException;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {
    private final NamedParameterJdbcTemplate template;
    private final DeveloperRepository developerRepository;

    /**
     * Расписание удаления неактивных пользователей,
     * у которых параметр enabled = false.
     */
    @Scheduled(fixedRate = 60 * 1000)
    public void dropDisabledUsersByTime() {
        template.update("DELETE FROM users WHERE enabled = false AND (SELECT extract(epoch FROM (SELECT CURRENT_TIMESTAMP::timestamp - created::timestamp)) / 60) >= 60;",
                new MapSqlParameterSource());
    }

    /**
     * Поиск пользователя по логину.
     *
     * @param username логин пользователя
     * @return сущность с данными пользователя
     */
    public Optional<UserDomain> findByUsername(final String username) {
        try {
            var user =
                    template.queryForObject("SELECT id, name, username, email, password, account_non_expired, account_non_locked, credentials_non_expired, enabled, created FROM users WHERE username = :username;",
                            Map.of("username", username),
                            (rs, i) -> new UserDomain(
                                    rs.getLong("id"),
                                    rs.getString("name"),
                                    rs.getString("username"),
                                    rs.getString("email"),
                                    rs.getString("password"),
                                    Collections.emptyList(),
                                    rs.getBoolean("account_non_expired"),
                                    rs.getBoolean("account_non_locked"),
                                    rs.getBoolean("credentials_non_expired"),
                                    rs.getBoolean("enabled"),
                                    rs.getTimestamp("created").toLocalDateTime()
                            )
                    );

            var authorities = template.query(
                    "SELECT authority FROM authorities WHERE user_id = :id",
                    Map.of("id", user.getId()),
                    (rs, i) -> new SimpleGrantedAuthority(rs.getString("authority"))
            );

            user.setAuthorities(authorities);

            return Optional.of(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * Создание пользователя.
     *
     * @param domain сущность с данными пользователя
     * @return готовую сущность пользователя
     */
    public long save(UserDomain domain) {
        var keyHolder = new GeneratedKeyHolder();
        template.update("INSERT INTO users (name, username, password, email) VALUES (:name, :username, :password, :email);",
                new MapSqlParameterSource(
                        Map.of("name", domain.getName(),
                                "username", domain.getUsername(),
                                "email", domain.getEmail(),
                                "password", domain.getPassword())),
                keyHolder);
        var key = keyHolder.getKeys().get("id");
        for (GrantedAuthority authority : domain.getAuthorities()) {
            template.update("INSERT INTO authorities (user_id, authority) VALUES (:id, :authority);",
                    Map.of(
                            "id", key,
                            "authority", authority.getAuthority()));
        }
        return ((Number) key).longValue();
    }

    /**
     * Проверка наличия логина среди уже созданных данных.
     *
     * @param username предполагаемый логин пользователя
     * @return true, если пользователь с таким логином уже есть
     */
    public boolean existsByUserName(final String username) {
        int userCount = template.queryForObject("SELECT count(id) FROM users WHERE username = :username;",
                Map.of("username", username),
                Integer.class);

        return userCount > 0;
    }

    /**
     * Поиск пользователя по id.
     *
     * @param id id пользователя
     * @return сущность пользователя
     */
    public Optional<UserDomain> findById(long id) {
        try {
            var user =
                    template.queryForObject(
                            "SELECT id, name, username, email, password, account_non_expired, account_non_locked, credentials_non_expired, enabled, created FROM users WHERE id = :id",
                            Map.of("id", id),
                            (rs, i) -> new UserDomain(
                                    rs.getLong("id"),
                                    rs.getString("name"),
                                    rs.getString("username"),
                                    rs.getString("email"),
                                    rs.getString("password"),
                                    Collections.emptyList(),
                                    rs.getBoolean("account_non_expired"),
                                    rs.getBoolean("account_non_locked"),
                                    rs.getBoolean("credentials_non_expired"),
                                    rs.getBoolean("enabled"),
                                    rs.getTimestamp("created").toLocalDateTime()
                            )
                    );

            var authorities = template.query(
                    "SELECT authority FROM authorities WHERE user_id = :id",
                    Map.of("id", id),
                    (rs, i) -> new SimpleGrantedAuthority(rs.getString("authority"))
            );

            assert user != null;
            user.setAuthorities(authorities);

            return Optional.of(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * Активация профиля пользователя по его id.
     *
     * @param id id пользователя
     */
    public void enableUser(long id) {
        template.update("UPDATE users SET enabled = true WHERE id = :id;",
                Map.of("id", id));
    }

    /**
     * Получение информации пользователя в личном кабинете.
     *
     * @param domain объект с данными пользователя
     * @return информация пользователя
     */
    public Optional<UserProfileResponseDto> getUserInformation(UserDomain domain) {
        try {
            var user =
                    template.queryForObject(
                            "SELECT id, name, username, email, created FROM users WHERE id = :id",
                            Map.of("id", domain.getId()),
                            (rs, i) -> new UserProfileResponseDto(
                                    rs.getLong("id"),
                                    rs.getString("name"),
                                    rs.getString("username"),
                                    rs.getString("email"),
                                    Collections.emptyList(),
                                    rs.getTimestamp("created").toLocalDateTime().toLocalDate(),
                                    Collections.emptyList()
                            ));

            assert user != null;

            var authorities = template.query(
                    "SELECT authority FROM authorities WHERE user_id = :id",
                    Map.of("id", domain.getId()),
                    (rs, i) -> new SimpleGrantedAuthority(rs.getString("authority"))
            );
            user.setAuthorities(authorities);

            var company = developerRepository
                    .getCompanyByUserId(user.getId())
                    .orElseThrow(() -> new CompanyNotFoundException("Not found company for this user!"));

            var games = template.query(
                    "SELECT g.id, g.name, c.name as company_name, g.coverage " +
                            "FROM games g " +
                            "JOIN companies c " +
                            "ON g.company_id = c.id " +
                            "WHERE company_id = :company_id",
                    Map.of("company_id", company.getId()),
                    (rs, i) -> new GameCondensedResponseDto(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("company_name"),
                            rs.getString("coverage")
                    ));
            user.setGames(games);

            return Optional.of(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * Проверка наличия прав пользователя для редактирования игры.
     *
     * @param gameId id игры
     * @param userId id пользователя
     * @return true, если id компании игры равно id компании пользователя
     */
    public boolean isGameDeveloper(long gameId, long userId) {
        var gameCompanyId = template.query(
                "SELECT company_id " +
                        "FROM games " +
                        "WHERE id = :id",
                Map.of("id", gameId),
                (rs, i) ->
                        rs.getLong("company_id"));

        var userCompanyId = template.query(
                "SELECT company_id " +
                        "FROM developers " +
                        "WHERE user_id = :id",
                Map.of("id", userId),
                (rs, i) ->
                        rs.getLong("company_id"));
        return gameCompanyId.equals(userCompanyId);
    }
}
