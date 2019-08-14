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
import ru.itpark.gameslauncher.domain.AuthorityDomain;
import ru.itpark.gameslauncher.domain.UserDomain;
import ru.itpark.gameslauncher.dto.user.UserProfileResponseDto;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {
    private final NamedParameterJdbcTemplate template;

    /**
     * Расписание удаления неактивных пользователей,
     * у которых параметр enabled = false.
     */
    @Scheduled(fixedRate = 60 * 1000)
    public void dropDisabledUsersByTime() {
        template.update(
                "DELETE FROM users WHERE enabled = false AND (SELECT extract(epoch FROM (SELECT CURRENT_TIMESTAMP::timestamp - created::timestamp)) / 60) >= 60;",
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
                    template.queryForObject(
                            "SELECT id, name, username, email, password, account_non_expired, account_non_locked, credentials_non_expired, enabled, created " +
                                    "FROM users " +
                                    "WHERE username = :username;",
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
                    "SELECT authority FROM authorities WHERE user_id = :id;",
                    Map.of("id", Objects.requireNonNull(user).getId()),
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
        template.update(
                "INSERT INTO users (name, username, password, email) VALUES (:name, :username, :password, :email);",
                new MapSqlParameterSource(
                        Map.of("name", domain.getName(),
                                "username", domain.getUsername(),
                                "email", domain.getEmail(),
                                "password", domain.getPassword())),
                keyHolder);
        var key = Objects.requireNonNull(keyHolder.getKeys()).get("id");
        for (GrantedAuthority authority : domain.getAuthorities()) {
            template.update(
                    "INSERT INTO authorities (user_id, authority) VALUES (:id, :authority);",
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
        var userCount = template.queryForObject(
                "SELECT count(id) FROM users WHERE username = :username;",
                Map.of("username", username),
                Integer.class);

        assert userCount != null;
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
                            "SELECT id, name, username, email, password, account_non_expired, account_non_locked, credentials_non_expired, enabled, created " +
                                    "FROM users " +
                                    "WHERE id = :id;",
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
                    "SELECT authority FROM authorities WHERE user_id = :id;",
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
        template.update(
                "UPDATE users SET enabled = true WHERE id = :id;",
                Map.of("id", id));
    }

    /**
     * Получение информации пользователя в личном кабинете.
     *
     * @param domain объект с данными пользователя
     * @return информация пользователя
     */
    public UserProfileResponseDto getUserInformation(UserDomain domain) {
        var user =
                template.queryForObject(
                        "SELECT id, name, username, email, created " +
                                "FROM users " +
                                "WHERE id = :id;",
                        Map.of("id", domain.getId()),
                        (rs, i) -> new UserProfileResponseDto(
                                rs.getLong("id"),
                                rs.getString("name"),
                                rs.getString("username"),
                                rs.getString("email"),
                                Collections.emptyList(),
                                rs.getTimestamp("created").toLocalDateTime().toLocalDate()
                        ));

        assert user != null;

        var authorities = template.query(
                "SELECT authority FROM authorities WHERE user_id = :id;",
                Map.of("id", domain.getId()),
                (rs, i) -> new SimpleGrantedAuthority(rs.getString("authority"))
        );
        user.setAuthorities(authorities);
        return user;
    }

    /**
     * Проверка наличия прав пользователя для редактирования игры.
     *
     * @param gameId id игры
     * @param userId id пользователя
     * @return true, если id компании игры равно id компании пользователя
     */
    public boolean isGameDeveloper(long gameId,
                                   long userId) {
        var gameCompanyId = template.query(
                "SELECT company_id FROM games WHERE id = :gameId;",
                Map.of("gameId", gameId),
                (rs, i) ->
                        rs.getLong("company_id"));

        var userCompanyId = template.query(
                "SELECT company_id FROM developers WHERE user_id = :userId;",
                Map.of("userId", userId),
                (rs, i) ->
                        rs.getLong("company_id"));
        return gameCompanyId.equals(userCompanyId);
    }

    /**
     * Добавление роли пользователю.
     *
     * @param domain сущность с данными ролт
     */
    public void addRoleToUsers(AuthorityDomain domain) {
        template.update(
                "INSERT INTO authorities (user_id, authority) VALUES (:userId, :authority)",
                Map.of("user_id", domain.getId(),
                        "role", domain.getAuthority()));
    }
}
