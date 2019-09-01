package ru.itpark.gameslauncher.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.itpark.gameslauncher.domain.CompanyDomain;
import ru.itpark.gameslauncher.domain.UserDomain;
import ru.itpark.gameslauncher.domain.game.GameDomain;
import ru.itpark.gameslauncher.dto.game.*;
import ru.itpark.gameslauncher.dto.user.UserGameResponseDto;
import ru.itpark.gameslauncher.enums.GameGenre;
import ru.itpark.gameslauncher.enums.GameStatus;
import ru.itpark.gameslauncher.enums.RequestStatus;
import ru.itpark.gameslauncher.exception.CompanyNotFoundException;
import ru.itpark.gameslauncher.exception.GameNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class GameRepository {
    private final NamedParameterJdbcTemplate template;
    private final DeveloperRepository developerRepository;

    /**
     * Получение списка игр, у которых параметр approved = true.
     *
     * @return список игр
     */
    public List<GameCondensedResponseDto> getAllApproved() {
        return template.query(
                "SELECT id, name, coverage FROM games WHERE request_status = 1;",
                (rs, i) -> new GameCondensedResponseDto(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("coverage")
                ));
    }

    /**
     * Получение сущности с данными игры по id, у которой параметр approved = true.
     *
     * @param id id игры
     * @return сущность с данными игры
     */
    public Optional<GameResponseDto> findApprovedById(long id) {
        try {
            var game = template.queryForObject(
                    "SELECT g.id, g.name, g.release_date, g.content, g.coverage, c.name as company_name, g.game_status, g.genre, g.likes, g.dislikes " +
                            "FROM games g " +
                            "JOIN companies c ON g.company_id = c.id " +
                            "WHERE g.id = :id " +
                            "AND g.request_status = 1;",
                    Map.of("id", id),
                    (rs, i) -> new GameResponseDto(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getDate("release_date").toLocalDate(),
                            rs.getString("content"),
                            rs.getString("coverage"),
                            rs.getString("company_name"),
                            GameStatus.getStatusByIndex(rs.getInt("game_status")),
                            GameGenre.getGenreByIndex(rs.getInt("genre")),
                            rs.getInt("likes"),
                            rs.getInt("dislikes")
                    ));

            assert game != null;
            return Optional.of(game);
        } catch (EmptyResultDataAccessException e) {
            throw new GameNotFoundException("Game not found!");
        }
    }

    /**
     * Получение списка неподтвержденных игр, у которых параметры request_status = 0.
     *
     * @return список игр
     */
    public List<GameCondensedResponseDto> getAllNotApproved() {
        return template.query(
                "SELECT id, name, coverage FROM games WHERE request_status = 0;",
                (rs, i) -> new GameCondensedResponseDto(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("coverage")
                ));
    }

    /**
     * Получение списка возвращенных игр, у которых параметры request_status = 2.
     *
     * @return список игр
     */
    public List<GameCondensedResponseDto> getAllReturned() {
        return template.query(
                "SELECT id, name, coverage FROM games WHERE request_status = 2;",
                (rs, i) -> new GameCondensedResponseDto(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("coverage")
                ));
    }

    /**
     * Получение сущности с данными игры по id, у которой параметры request_status = 2.
     *
     * @param id id игры
     * @return сущность с данными игры
     */
    public Optional<NotApprovedGameResponseDto> findNotApprovedById(long id) {
        try {
            var game = template.queryForObject(
                    "SELECT g.id, g.name, g.release_date, g.content, g.coverage, c.name as company_name, g.game_status, g.genre, g.request_status " +
                            "FROM games g " +
                            "JOIN companies c ON g.company_id = c.id " +
                            "WHERE g.id = :id " +
                            "AND g.request_status = 0;",
                    Map.of("id", id),
                    (rs, i) -> new NotApprovedGameResponseDto(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getDate("release_date").toLocalDate(),
                            rs.getString("content"),
                            rs.getString("coverage"),
                            rs.getString("company_name"),
                            GameStatus.getStatusByIndex(rs.getInt("game_status")),
                            GameGenre.getGenreByIndex(rs.getInt("genre")),
                            RequestStatus.getStatusByIndex(rs.getInt("request_status"))));

            assert game != null;
            return Optional.of(game);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * Сохранение новой игры.
     *
     * @param domain данные, которые нужно сохранить
     * @return сущность игры
     */
    public Optional<GameDomain> createGame(GameDomain domain) {
        var keyHolder = new GeneratedKeyHolder();
        try {
            template.update(
                    "INSERT INTO games (name, release_date, content, coverage, company_id, game_status, genre, creator_id) VALUES (:name, :releaseDate, :content, :coverage, :companyId, :status, :genre, :creatorId);",
                    new MapSqlParameterSource(
                            Map.of("name", domain.getName(),
                                    "releaseDate", domain.getReleaseDate(),
                                    "content", domain.getContent(),
                                    "coverage", domain.getCoverage(),
                                    "companyId", domain.getCompanyId(),
                                    "game_status", domain.getGameStatus().getIndex(),
                                    "genre", domain.getGenre().getIndex(),
                                    "creatorId", domain.getCreatorId())),
                    keyHolder);

            var key = ((Number) Objects.requireNonNull(keyHolder.getKeys()).get("id")).longValue();
            domain.setId(key);
            return Optional.of(domain);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * Проверка наличия игры с искомым названием.
     *
     * @param name название, по которому нужно произвести поиск
     * @return true, если результат поиск больше нуля
     */
    public boolean checkExistsByName(final String name) {
        var gameCount = template.queryForObject(
                "SELECT count(id) FROM games WHERE name = :name;",
                Map.of("name", name),
                Integer.class);

        assert gameCount != null;
        return gameCount > 0;
    }

    /**
     * Подтверждения записи игры, изменение параметра request_status = 1.
     *
     * @param id id игры
     */
    public void approveGame(long id) {
        template.update(
                "UPDATE games SET request_status = 1 WHERE id = :id;",
                Map.of("id", id));
    }

    /**
     * Поиск не подтверждённой записи игры, которую вернули на доработку с комментарием.
     *
     * @param gameId id игры
     * @return сущность с данными игры
     */
    public Optional<ReturnedGameResponseDto> findReturnedGameWithCommentById(long gameId) {
        try {
            var game = template.queryForObject(
                    "SELECT g.id, g.name, g.release_date, g.content, g.coverage, c.name as company_name, g.game_status, g.genre, rgc.comment " +
                            "FROM games g " +
                            "JOIN returned_games_comments rgc ON g.id = rgc.game_id " +
                            "JOIN companies c ON g.company_id = c.id " +
                            "AND g.request_status = 2 " +
                            "AND rgc.game_id = :id;",
                    Map.of("id", gameId),
                    (rs, i) -> new ReturnedGameResponseDto(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getDate("release_date").toLocalDate(),
                            rs.getString("content"),
                            rs.getString("coverage"),
                            rs.getString("company_name"),
                            GameStatus.getStatusByIndex(rs.getInt("game_status")),
                            GameGenre.getGenreByIndex(rs.getInt("genre")),
                            rs.getString("comment")
                    ));

            return Optional.ofNullable(game);
        } catch (EmptyResultDataAccessException e) {
            throw new GameNotFoundException("Game not found!");
        }
    }

    /**
     * Возвращение игры разработчику на доработку, с добавлением комментария.
     *
     * @param id          id игры
     * @param companyName название компании разработчика
     * @param comment     комментарий к записи
     */
    public void returnGame(long id,
                           String companyName,
                           String comment) {
        var companyId = template.query(
                "SELECT id FROM companies WHERE name = :name AND request_status = 1;",
                Map.of("name", companyName),
                (rs, i) ->
                        rs.getLong("id"));

        template.update(
                "INSERT INTO returned_games_comments (game_id, company_id, comment) VALUES (:id, :companyId, :comment);",
                new MapSqlParameterSource(
                        Map.of("id", id,
                                "companyId", companyId,
                                "comment", comment)));

        template.update(
                "UPDATE games set request_status = 2 where id = :id;",
                Map.of("id", id));
    }

    /**
     * Поиск игр пользователя по id игры.
     *
     * @param id id игры
     * @return сущность с данными игры
     */
    public Optional<UserGameResponseDto> findUserGameByGameId(long id) {
        try {
            var game = template.queryForObject(
                    "SELECT g.id, g.name, g.release_date, g.content, g.coverage, c.name as company_name, g.game_status, g.genre, g.likes, g.dislikes, g.request_status = 1 " +
                            "FROM games g " +
                            "JOIN companies c ON g.company_id = c.id " +
                            "WHERE g.id = :id;",
                    Map.of("id", id),
                    (rs, i) -> new UserGameResponseDto(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getDate("release_date").toLocalDate(),
                            rs.getString("content"),
                            rs.getString("coverage"),
                            rs.getString("company_name"),
                            GameStatus.getStatusByIndex(rs.getInt("game_status")),
                            GameGenre.getGenreByIndex(rs.getInt("genre")),
                            rs.getInt("likes"),
                            rs.getInt("dislikes"),
                            RequestStatus.getStatusByIndex(rs.getInt("approved"))
                    ));

            return Optional.ofNullable(game);
        } catch (EmptyResultDataAccessException e) {
            throw new GameNotFoundException("Game not found!");
        }
    }

    /**
     * Поиск игры по id игры.
     *
     * @param id id игры
     * @return сущность игры с данными
     */
    public Optional<GameResponseDto> findById(long id) {
        try {
            var game = template.queryForObject(
                    "SELECT g.id, g.name, g.release_date, g.content, g.coverage, c.name as company_name, g.game_status, g.genre, g.likes, g.dislikes " +
                            "FROM games g " +
                            "JOIN companies c ON g.company_id = c.id " +
                            "WHERE g.id = :id;",
                    Map.of("id", id),
                    (rs, i) -> new GameResponseDto(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getDate("release_date").toLocalDate(),
                            rs.getString("content"),
                            rs.getString("coverage"),
                            rs.getString("company_name"),
                            GameStatus.getStatusByIndex(rs.getInt("game_status")),
                            GameGenre.getGenreByIndex(rs.getInt("genre")),
                            rs.getInt("likes"),
                            rs.getInt("dislikes")
                    ));

            assert game != null;
            return Optional.of(game);
        } catch (EmptyResultDataAccessException e) {
            throw new GameNotFoundException("Game not found!");
        }
    }


    /**
     * Поиск игр компании, в которой числится пользователь.
     *
     * @param domain сущность с данными пользователя
     * @return список игр пользователя
     */
    public List<GameCondensedResponseDto> findUserGames(UserDomain domain) {
        var company = developerRepository
                .getCompanyByUserId(domain.getId())
                .orElseThrow(() -> new CompanyNotFoundException("Not found company for this user!"));
        List<Long> ids = new ArrayList<>();
        for (CompanyDomain companyDomain : company) {
            ids.add(companyDomain.getId());
        }

        if (ids.size() == 0) {
            throw new GameNotFoundException("You dont't have games yet!");
        }

        String companiesId = ids.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        String sql = String.format("SELECT id, name, coverage FROM games WHERE company_id IN (%s);", companiesId);

        return template.query(
                sql,
                Map.of("companiesId", companiesId),
                (rs, i) -> new GameCondensedResponseDto(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("coverage")
                ));
    }

    /**
     * Поиск игр пользователя с помощью id пользователя.
     *
     * @param domain сущность с данными пользователя
     * @return список игр пользователя
     */
    public List<GameCondensedResponseDto> findCreatedGamesByUser(UserDomain domain) {
        return template.query(
                "SELECT g.id, g.name, g.coverage " +
                        "FROM games g " +
                        "JOIN companies c ON g.company_id = c.id " +
                        "JOIN users u ON u.id = c.creator_id " +
                        "WHERE u.id = :userId;",
                Map.of("userId", domain.getId()),
                (rs, i) -> new GameCondensedResponseDto(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("coverage")
                ));
    }

    /**
     * Обновление информации игры.
     *
     * @param dto данные, которые нужно перезаписать
     */
    public void editGame(GameEditRequestDto dto) {
        template.update(
                "UPDATE games SET name = :name, release_date = :releaseDate, content = :content, coverage = :coverage, game_status = :status, genre = :genre, request_status = 1 WHERE id = :id;",
                Map.of("name", dto.getName(),
                        "releaseDate", dto.getReleaseDate(),
                        "content", dto.getContent(),
                        "coverage", dto.getCoverage(),
                        "game_status", dto.getGameStatus().getIndex(),
                        "genre", dto.getGenre().getIndex(),
                        "id", dto.getId()));
    }
}
