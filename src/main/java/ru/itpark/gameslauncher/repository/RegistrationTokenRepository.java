package ru.itpark.gameslauncher.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import ru.itpark.gameslauncher.domain.RegistrationTokenDomain;
import ru.itpark.gameslauncher.dto.registration.RegistrationConfirmationRequestDto;
import ru.itpark.gameslauncher.exception.TokenException;
import ru.itpark.gameslauncher.repository.sql.RegistrationSqlQueries;

import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RegistrationTokenRepository implements RegistrationSqlQueries {
    private final NamedParameterJdbcTemplate template;

    /**
     * Расписание для удаления токенов регистрации,
     * если регистрация не была совершена вовремя.
     */
    @Scheduled(fixedRate = 60 * 1000)
    public void dropTokensByTime() {
        template.update(
                DROP_TOKENS_BY_TIME,
                new MapSqlParameterSource());
    }

    /**
     * Сохранение токена регистрации.
     *
     * @param domain сущность с данными токена регистрации
     */
    public void save(RegistrationTokenDomain domain) {
        if (domain.getToken() == null) {
            throw new TokenException("Token invalid");
        }
        template.update(
                SAVE_TOKEN,
                Map.of("id", domain.getToken(),
                        "userId", domain.getUserId()));
    }

    /**
     * Поиск сущности токена с данными.
     *
     * @param dto сущность с данными токена для подтверждения регистрации
     * @return сущности токена с данными
     */
    public Optional<RegistrationTokenDomain> findByToken(RegistrationConfirmationRequestDto dto) {
        try {
            var domain = template.queryForObject(
                    FIND_BY_TOKEN,
                    Map.of("id", dto.getToken()),
                    (rs, i) -> new RegistrationTokenDomain(
                            rs.getString("id"),
                            rs.getLong("user_id"),
                            rs.getTimestamp("created").toLocalDateTime())
            );
            return Optional.ofNullable(domain);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * Получение кол-ва токенов регистрации пользователя.
     *
     * @param userId id пользователя
     * @return кол-во токенов регистрации
     */
    public int countTokenByUserId(final long userId) {
        var tokenCount = template.queryForObject(
                TOKENS_COUNT,
                Map.of("userId", userId),
                Integer.class);
        assert tokenCount != null;
        return tokenCount;
    }
}
