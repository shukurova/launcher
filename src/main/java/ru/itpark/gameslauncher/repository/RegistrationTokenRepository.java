package ru.itpark.gameslauncher.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.itpark.gameslauncher.domain.RegistrationTokenDomain;
import ru.itpark.gameslauncher.dto.RegistrationConfirmationRequestDto;
import ru.itpark.gameslauncher.exception.TokenException;

import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RegistrationTokenRepository {
    private final NamedParameterJdbcTemplate template;

    public void save(RegistrationTokenDomain domain) {
        if (domain.getToken() == null) {
            throw new TokenException("Token invalid");
        }
        template.update("INSERT INTO reg_tokens (id, user_id) VALUES (:id, :userId);",
                Map.of("id", domain.getToken(),
                        "userId", domain.getUserId()));
    }

    public final Optional<RegistrationTokenDomain> findByToken(RegistrationConfirmationRequestDto dto) {
        if (dto.getToken() == null) {
            throw new TokenException("Token invalid");
        }
        try {
            var user =
                    template.queryForObject("SELECT id, user_id, created FROM reg_tokens WHERE id = :id;",
                            Map.of("id", dto.getToken()),
                            (rs, i) -> new RegistrationTokenDomain(
                                    rs.getString("id"),
                                    rs.getLong("user_id"),
                                    rs.getTimestamp("created").toLocalDateTime())

                    );
            return Optional.of(user);
        } catch (
                EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public final int countTokenByUserId(final long userId) {
        return template.queryForObject("SELECT COUNT(id) FROM reg_tokens WHERE user_id = :userId;",
                Map.of("userId", userId),
                Integer.class);
    }
}
