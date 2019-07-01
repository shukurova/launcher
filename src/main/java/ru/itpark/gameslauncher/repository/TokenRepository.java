package ru.itpark.gameslauncher.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.itpark.gameslauncher.domain.RegistrationTokenDomain;
import ru.itpark.gameslauncher.domain.UserDomain;
import ru.itpark.gameslauncher.exception.TokenException;

import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TokenRepository {
    private final NamedParameterJdbcTemplate template;

    public void save(RegistrationTokenDomain domain) {
        if (domain.getToken() == null) {
            throw new TokenException("Token invalid");
        }
        template.update("INSERT INTO tokens (token, user_id) VALUES (:token, :userId);",
                Map.of("token", domain.getToken(),
                        "user_id", domain.getUserId()));
    }

    //TODO: доделать запрос
    public Optional<UserDomain> findById(final String token) {
        try {
            return Optional.of(
                    template.query("SELECT u.id, u.name, u.username, FROM tokens WHERE token = :token",
                            Map.of("token", token),
                            new BeanPropertyRowMapper<>(UserDomain.class)));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
