package ru.itpark.gameslauncher.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.itpark.gameslauncher.domain.TokenDomain;
import ru.itpark.gameslauncher.exception.InvalidTokenException;

import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TokenRepository {
    private final NamedParameterJdbcTemplate template;

    public Optional<TokenDomain> findById(final String token) {
        try {
            return Optional.of(
                    template.queryForObject("SELECT user_id FROM tokens WHERE token = :token;",
                            Map.of("token", token),
                            new BeanPropertyRowMapper<>(TokenDomain.class)));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void save(TokenDomain domain) {
        if (domain.getToken() == null) {
            throw new InvalidTokenException("Token can't be null");
        }

        template.update("INSERT INTO tokens (token, user_id) VALUES (:token, :userId);",
                Map.of("token", domain.getToken(),
                        "user_id", domain.getUser()));
    }
}
