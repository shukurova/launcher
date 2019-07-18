package ru.itpark.gameslauncher.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.itpark.gameslauncher.domain.AuthTokenDomain;

import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AuthTokenRepository {
    private final NamedParameterJdbcTemplate template;

    public Optional<AuthTokenDomain> findById(String token) {
        try {
            return Optional.of(
                    template.queryForObject(
                            "SELECT id, user_id FROM auth_tokens WHERE id = :id",
                            Map.of("id", token),
                            (rs, i) -> new AuthTokenDomain(
                                    rs.getString("id"),
                                    rs.getLong("user_id")
                            )
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
