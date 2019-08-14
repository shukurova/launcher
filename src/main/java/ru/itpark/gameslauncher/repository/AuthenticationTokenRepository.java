package ru.itpark.gameslauncher.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.itpark.gameslauncher.domain.AuthenticationTokenDomain;
import ru.itpark.gameslauncher.exception.TokenException;

import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AuthenticationTokenRepository {
    private final NamedParameterJdbcTemplate template;

    /**
     * Поиск сущности токена авторизации пользователя по токену.
     *
     * @param token токен пользователя
     * @return сущность токена
     */
    public Optional<AuthenticationTokenDomain> findByToken(String token) {
        try {
            var user = template.queryForObject(
                    "SELECT id, user_id FROM authentication_tokens WHERE id = :id;",
                    Map.of("id", token),
                    (rs, i) -> new AuthenticationTokenDomain(
                            rs.getString("id"),
                            rs.getLong("user_id")
                    )
            );
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * Сохранение токена авторизации.
     *
     * @param domain сущность токена с данными
     */
    public void save(AuthenticationTokenDomain domain) {
        if (domain.getToken() == null) {
            throw new TokenException("Invalid token!");
        }
        template.update(
                "INSERT INTO authentication_tokens (id, user_id) VALUES (:id, :userId);",
                Map.of("id", domain.getToken(),
                        "userId", domain.getUserId()));
    }
}