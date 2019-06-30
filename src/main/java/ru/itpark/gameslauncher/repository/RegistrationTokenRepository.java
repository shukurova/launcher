package ru.itpark.gameslauncher.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.itpark.gameslauncher.domain.RegistrationTokenDomain;
import ru.itpark.gameslauncher.exception.InvalidTokenException;

import java.util.Map;

@Repository
@RequiredArgsConstructor
public class RegistrationTokenRepository {
    private final NamedParameterJdbcTemplate template;

    public void save(RegistrationTokenDomain domain) {
        if (domain.getToken() == null) {
            throw new InvalidTokenException();
        }

        //TODO: как прописывать id пользака с dto
        template.update("INSERT INTO tokens (token, user_id) VALUES (:token, :userId);",
                Map.of("token", domain.getToken(),
                        "user_id", domain.getUserId()));
    }
}
