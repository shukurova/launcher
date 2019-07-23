package ru.itpark.gameslauncher.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.itpark.gameslauncher.domain.game.GameDomain;
import ru.itpark.gameslauncher.domain.game.GameGenre;
import ru.itpark.gameslauncher.domain.game.GameStatus;
import ru.itpark.gameslauncher.dto.GameResponseDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GameRepository {
    private final NamedParameterJdbcTemplate template;

    public List<GameResponseDto> getAll() {
        return template.query("SELECT id, name, company_id, coverage FROM games",
                new BeanPropertyRowMapper<>(GameResponseDto.class));
    }

    public Optional<GameResponseDto> findById(long id) {
        try {
            var game = template.queryForObject(
                    "SELECT id, name, release_date, content, coverage, company_id, status, genre, likes, dislikes FROM games WHERE id = :id",
                    Map.of("id", id),
                    (rs, i) -> new GameDomain(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getTimestamp("release_date").toLocalDateTime(),
                            rs.getString("content"),
                            rs.getString("coverage"),
                            rs.getLong("company_id"),
                            GameStatus.valueOf(rs.getString("status")),
                            GameGenre.valueOf(rs.getString("genre")),
                            rs.getInt("likes"),
                            rs.getInt("dislikes")
                            ));

            return Optional.of(game);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
