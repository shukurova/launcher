package ru.itpark.gameslauncher.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.itpark.gameslauncher.domain.UserDomain;

import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {
    private final NamedParameterJdbcTemplate template;

    public Optional<UserDomain> findByUsername(String username) {
        return template.query("SELECT id, name, username FROM users WHERE username = :username;",
                Map.of("username", username),
                new BeanPropertyRowMapper<>(UserDomain.class));
    }
}
