package ru.itpark.gameslauncher.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.itpark.gameslauncher.domain.game.GameDomain;
import ru.itpark.gameslauncher.domain.game.GameGenre;
import ru.itpark.gameslauncher.domain.game.GameStatus;
import ru.itpark.gameslauncher.dto.GameResponseDto;
import ru.itpark.gameslauncher.dto.ReturnedGameResponseDto;
import ru.itpark.gameslauncher.exception.GameNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GameRepository {
    private final NamedParameterJdbcTemplate template;

    public List<GameResponseDto> getAll() {
        return template.query("SELECT id, name, company_id, coverage FROM games WHERE approved = true",
                new BeanPropertyRowMapper<>(GameResponseDto.class));
    }

    public Optional<GameDomain> findById(long id) {
        try {
            var game = template.queryForObject(
                    "SELECT id, name, release_date, content, coverage, company_id, status, genre, likes, dislikes, approved, returned FROM games WHERE id = :id AND approved = true",
                    Map.of("id", id),
                    (rs, i) -> new GameDomain(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getDate("release_date").toLocalDate(),
                            rs.getString("content"),
                            rs.getString("coverage"),
                            rs.getLong("company_id"),
                            GameStatus.getStatusByIndex(rs.getInt("status")),
                            GameGenre.getGenreByIndex(rs.getInt("genre")),
                            rs.getInt("likes"),
                            rs.getInt("dislikes"),
                            rs.getBoolean("approved"),
                            rs.getBoolean("returned")
                    ));

            return Optional.of(game);
        } catch (EmptyResultDataAccessException e) {
            throw new GameNotFoundException("Game not found!");
        }
    }

    public List<GameResponseDto> getNotApproved() {
        return template.query("SELECT id, name, company_id, coverage FROM games WHERE approved = false",
                new BeanPropertyRowMapper<>(GameResponseDto.class));
    }

    public Optional<GameDomain> findNotApprovedById(long id) {
        try {
            var game = template.queryForObject(
                    "SELECT id, name, release_date, content, coverage, company_id, status, genre, likes, dislikes, approved, returned FROM games WHERE id = :id AND approved = false",
                    Map.of("id", id),
                    (rs, i) -> new GameDomain(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getDate("release_date").toLocalDate(),
                            rs.getString("content"),
                            rs.getString("coverage"),
                            rs.getLong("company_id"),
                            GameStatus.getStatusByIndex(rs.getInt("status")),
                            GameGenre.getGenreByIndex(rs.getInt("genre")),
                            rs.getInt("likes"),
                            rs.getInt("dislikes"),
                            rs.getBoolean("approved"),
                            rs.getBoolean("returned")
                    ));

            return Optional.of(game);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<GameDomain> create(GameDomain domain) {
        var keyHolder = new GeneratedKeyHolder();
        try {
            template.update("INSERT INTO games (name, release_date, content, coverage, company_id, status, genre) VALUES (:name, :releaseDate, :content, :coverage, :companyId, :status, :genre);",
                    new MapSqlParameterSource(
                            Map.of("name", domain.getName(),
                                    "releaseDate", domain.getReleaseDate(),
                                    "content", domain.getContent(),
                                    "coverage", domain.getCoverage(),
                                    "companyId", domain.getCompanyId(),
                                    "status", domain.getStatus().getIndex(),
                                    "genre", domain.getGenre().getIndex())),
                    keyHolder);
            var key = ((Number) keyHolder.getKeys().get("id")).longValue();
            domain.setId(key);
            return Optional.of(domain);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public boolean existsByName(final String name) {
        int userCount =
                template.queryForObject("SELECT count(id) FROM games WHERE name = :name;",
                        Map.of("name", name),
                        Integer.class);

        return userCount > 0;
    }

    public void approve(long id) {
        template.update("UPDATE games SET approved = true WHERE id = :id;",
                Map.of("id", id));
    }

    private ReturnedGameResponseDto findNotApprovedGameWithCommentById(long gameId) {
        return template.queryForObject("SELECT g.id, g.name, g.release_date, g.content, g.coverage, g.company_id, g.status, g.genre, rgc.comment FROM games g JOIN returned_games_comments rgc WHERE g.id = :id AND rgc.game_id = :id",
                Map.of("id", gameId),
                (rs, i) -> new ReturnedGameResponseDto(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getDate("release_date").toLocalDate(),
                        rs.getString("content"),
                        rs.getString("coverage"),
                        rs.getLong("company_id"),
                        GameStatus.getStatusByIndex(rs.getInt("status")),
                        GameGenre.getGenreByIndex(rs.getInt("genre")),
                        rs.getString("comment")
                ));
    }

    //TODO: почему comment == null
    public ReturnedGameResponseDto returnGame(long id, long companyId, String comment) {
        template.update("INSERT INTO returned_games_comments (game_id, company_id, comment) VALUES (:id, :companyId, :comment)",
                new MapSqlParameterSource(
                        Map.of("id", id,
                                "companyId", companyId,
                                "comment", comment)));
        return findNotApprovedGameWithCommentById(id);
    }
}
