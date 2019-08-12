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
import ru.itpark.gameslauncher.repository.sql.DeveloperSqlQueries;
import ru.itpark.gameslauncher.repository.sql.GameSqlQueries;
import ru.itpark.gameslauncher.repository.sql.UserSqlQueries;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository implements UserSqlQueries, DeveloperSqlQueries, GameSqlQueries {
    private final NamedParameterJdbcTemplate template;

    /**
     * Расписание удаления неактивных пользователей,
     * у которых параметр enabled = false.
     */
    @Scheduled(fixedRate = 60 * 1000)
    public void dropDisabledUsersByTime() {
        template.update(
                DROP_USERS_BY_TIME,
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
                            FIND_BY_USERNAME,
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
                    FIND_AUTHORITY,
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
                SAVE_USER,
                new MapSqlParameterSource(
                        Map.of("name", domain.getName(),
                                "username", domain.getUsername(),
                                "email", domain.getEmail(),
                                "password", domain.getPassword())),
                keyHolder);
        var key = Objects.requireNonNull(keyHolder.getKeys()).get("id");
        for (GrantedAuthority authority : domain.getAuthorities()) {
            template.update(
                    SAVE_AUTHORITY,
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
                USERNAME_COUNT,
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
                            FIND_USER_BY_ID,
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
                    FIND_AUTHORITY,
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
                ENABLE_USER,
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
                            USER_INFORMATION,
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
                    FIND_AUTHORITY,
                    Map.of("id", domain.getId()),
                    (rs, i) -> new SimpleGrantedAuthority(rs.getString("authority"))
            );
            user.setAuthorities(authorities);

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
    public boolean isGameDeveloper(long gameId,
                                   long userId) {
        var gameCompanyId = template.query(
                GET_COMPANY_ID_FROM_GAMES,
                Map.of("id", gameId),
                (rs, i) ->
                        rs.getLong("company_id"));

        var userCompanyId = template.query(
                GET_COMPANY_ID_FROM_DEVELOPERS,
                Map.of("id", userId),
                (rs, i) ->
                        rs.getLong("company_id"));
        return gameCompanyId.equals(userCompanyId);
    }

    public void addRoleToUsers(AuthorityDomain domain) {
        template.update(
                ADD_ROLE,
                Map.of("user_id", domain.getId(),
                        "role", domain.getAuthority()));
    }
}
