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

    //TODO: нужно ли здесь создавать dto-слой?
    public void save(RegistrationTokenDomain token) {
        if (token.getToken() == null) {
            throw new InvalidTokenException();
        }

        template.update("INSERT INTO tokens (token, user_id) VALUES (:token, :user_id);",
                Map.of("token", token.getToken(),
                        "user_id", token.getUser().getId()));
    }
}
