package ru.itpark.gameslauncher.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Repository;
import ru.itpark.gameslauncher.domain.UserDomain;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {
    private final NamedParameterJdbcTemplate template;

    public Optional<UserDomain> findByUsername(final String username) {
        try {
            var user =
                    template.queryForObject("SELECT id, name, username, email, password, account_non_expired, account_non_locked, credentials_non_expired, enabled FROM users WHERE username = :username;",
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
                                    rs.getBoolean("enabled")
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

    public boolean existsByUserName(final String username) {
        int userCount = template.queryForObject("SELECT count(id) FROM users WHERE username = :username;",
                Map.of("username", username),
                Integer.class);

        return userCount > 0;
    }

    public Optional<UserDomain> findById(long id) {
        try {
            var user =
                    template.queryForObject(
                            "SELECT id, name, username, email, password, account_non_expired, account_non_locked, credentials_non_expired, enabled FROM users WHERE id = :id",
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
                                    rs.getBoolean("enabled")
                            )
                    );

            var authorities = template.query(
                    "SELECT authority FROM authorities WHERE user_id = :id",
                    Map.of("id", id),
                    (rs, i) -> new SimpleGrantedAuthority(rs.getString("authority"))
            );

            user.setAuthorities(authorities);

            return Optional.of(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    //TODO: удаление пользователей, если через какой-то промежуток времени их аккаунты остаются enabled == false
}
