package ru.itpark.gameslauncher.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
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

    public Optional<UserDomain> findByUsername(final String username) {
        try {
            return Optional.of(
                    template.queryForObject("SELECT id, name, username FROM users WHERE username = :username;",
                            Map.of("username", username),
                            new BeanPropertyRowMapper<>(UserDomain.class)));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void save(UserDomain domain) {
        template.update("INSERT INTO users (name, username, password) VALUES (:name, :username, :password);",
                Map.of("name", domain.getName(),
                        "username", domain.getUsername(),
                        "password", domain.getPassword()));
        template.update("INSERT INTO authorities (user_id, authority) VALUES (:userId, :authority);",
                Map.of("userId", domain.getId(),
                        "authority", domain.getAuthorities()));
    }

    public boolean existsByUserName(final String username) {
        int userCount = template.queryForObject("SELECT count(id) FROM users WHERE username = :username;",
                Map.of("username", username),
                Integer.class);

        return userCount > 0;
    }
}
