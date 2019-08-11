package ru.itpark.gameslauncher.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.itpark.gameslauncher.domain.AuthenticationTokenDomain;
import ru.itpark.gameslauncher.exception.TokenException;
import ru.itpark.gameslauncher.repository.sql.AuthenticationSqlQueries;

import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AuthenticationTokenRepository implements AuthenticationSqlQueries {
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
                    FIND_BY_TOKEN,
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
        template.update(SAVE_TOKEN,
                Map.of("id", domain.getToken(),
                        "userId", domain.getUserId()));
    }
}