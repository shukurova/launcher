package ru.itpark.gameslauncher.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.itpark.gameslauncher.domain.UserDomain;
import ru.itpark.gameslauncher.domain.game.GameDomain;
import ru.itpark.gameslauncher.domain.game.GameGenre;
import ru.itpark.gameslauncher.domain.game.GameStatus;
import ru.itpark.gameslauncher.dto.game.*;
import ru.itpark.gameslauncher.dto.user.UserGameResponseDto;
import ru.itpark.gameslauncher.exception.CompanyNotFoundException;
import ru.itpark.gameslauncher.exception.GameNotFoundException;
import ru.itpark.gameslauncher.repository.sql.CompanySqlQueries;
import ru.itpark.gameslauncher.repository.sql.GameSqlQueries;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GameRepository implements GameSqlQueries, CompanySqlQueries {
    private final NamedParameterJdbcTemplate template;
    private final DeveloperRepository developerRepository;

    /**
     * Получение списка игр, у которых параметр approved = true.
     *
     * @return список игр
     */
    public List<GameCondensedResponseDto> getAllApproved() {
        return template.query(
                GET_ALL_GAMES,
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
                    GET_APPROVED_BY_ID,
                    Map.of("id", id),
                    (rs, i) -> new GameResponseDto(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getDate("release_date").toLocalDate(),
                            rs.getString("content"),
                            rs.getString("coverage"),
                            rs.getString("company_name"),
                            GameStatus.getStatusByIndex(rs.getInt("status")),
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
     * Получение списка неподтвержденных игр, у которых параметры approved = false и returned = true.
     *
     * @return список игр
     */
    public List<GameCondensedResponseDto> getAllNotApproved() {
        return template.query(
                GET_ALL_NOT_APPROVED_GAMES,
                (rs, i) -> new GameCondensedResponseDto(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("coverage")
                ));
    }

    /**
     * Получение списка возвращенных игр, у которых параметры approved = false и returned = true.
     *
     * @return список игр
     */
    public List<GameCondensedResponseDto> getAllReturned() {
        return template.query(
                GET_ALL_RETURNED_GAMES,
                (rs, i) -> new GameCondensedResponseDto(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("coverage")
                ));
    }

    /**
     * Получение сущности с данными игры по id, у которой параметры approved = false и returned = false.
     *
     * @param id id игры
     * @return сущность с данными игры
     */
    public Optional<NotApprovedGameResponseDto> findNotApprovedById(long id) {
        try {
            var game = template.queryForObject(
                    GET_NOT_APPROVED_GAME_BY_ID,
                    Map.of("id", id),
                    (rs, i) -> new NotApprovedGameResponseDto(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getDate("release_date").toLocalDate(),
                            rs.getString("content"),
                            rs.getString("coverage"),
                            rs.getString("company_name"),
                            GameStatus.getStatusByIndex(rs.getInt("status")),
                            GameGenre.getGenreByIndex(rs.getInt("genre"))
                    ));

            assert game != null;
            return Optional.of(game);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * Получение сущности с данными игры по id, у которой параметры approved = false и returned = true.
     *
     * @param id id игры
     * @return сущность с данными игры
     */
    public Optional<NotApprovedGameResponseDto> findReturnedById(long id) {
        try {
            var game = template.queryForObject(
                    GET_RETURNED_GAME_BY_ID,
                    Map.of("id", id),
                    (rs, i) -> new NotApprovedGameResponseDto(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getDate("release_date").toLocalDate(),
                            rs.getString("content"),
                            rs.getString("coverage"),
                            rs.getString("company_name"),
                            GameStatus.getStatusByIndex(rs.getInt("status")),
                            GameGenre.getGenreByIndex(rs.getInt("genre"))
                    ));

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
                    CREATE_GAME,
                    new MapSqlParameterSource(
                            Map.of("name", domain.getName(),
                                    "releaseDate", domain.getReleaseDate(),
                                    "content", domain.getContent(),
                                    "coverage", domain.getCoverage(),
                                    "companyId", domain.getCompanyId(),
                                    "status", domain.getStatus().getIndex(),
                                    "genre", domain.getGenre().getIndex())),
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
                CHECK_GAME_EXISTS_BY_NAME,
                Map.of("name", name),
                Integer.class);

        assert gameCount != null;
        return gameCount > 0;
    }

    /**
     * Подтверждения записи игры, изменение параметров 'approved' на true и 'returned' на false.
     *
     * @param id id игры
     */
    public void approveGame(long id) {
        template.update(
                APPROVE_GAME,
                Map.of("id", id));
    }

    /**
     * Поиск не подтверждённой записи игры, которую вернули на доработку с комментарием.
     *
     * @param gameId id игры
     * @return сущность с данными игры
     */
    public ReturnedGameResponseDto findNotApprovedGameWithCommentById(long gameId) {
        return template.queryForObject(
                FIND_NOT_APPROVED_GAME_WITH_COMMENT,
                Map.of("id", gameId),
                (rs, i) -> new ReturnedGameResponseDto(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getDate("release_date").toLocalDate(),
                        rs.getString("content"),
                        rs.getString("coverage"),
                        rs.getString("company_name"),
                        GameStatus.getStatusByIndex(rs.getInt("status")),
                        GameGenre.getGenreByIndex(rs.getInt("genre")),
                        rs.getString("comment")
                ));
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
                GET_APPROVED_COMPANY_BY_ID,
                Map.of("name", companyName),
                (rs, i) ->
                        rs.getLong("id"));

        template.update(SAVE_COMMENT_WITH_RETURNED_GAME,
                new MapSqlParameterSource(
                        Map.of("id", id,
                                "companyId", companyId,
                                "comment", comment)));

        template.update(CHANGE_GAME_STATUS_TO_RETURNED,
                Map.of("id", id));
    }

    /**
     * Обновление информации игры.
     *
     * @param dto данные, которые нужно перезаписать
     */
    public void editGame(GameEditRequestDto dto) {
        template.update(EDIT_GAME,
                Map.of("name", dto.getName(),
                        "releaseDate", dto.getReleaseDate(),
                        "content", dto.getContent(),
                        "coverage", dto.getCoverage(),
                        "status", dto.getStatus().getIndex(),
                        "genre", dto.getGenre().getIndex(),
                        "id", dto.getId()));
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
                    FIND_USER_GAME_BY_GAME_ID,
                    Map.of("id", id),
                    (rs, i) -> new UserGameResponseDto(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getDate("release_date").toLocalDate(),
                            rs.getString("content"),
                            rs.getString("coverage"),
                            rs.getString("company_name"),
                            GameStatus.getStatusByIndex(rs.getInt("status")),
                            GameGenre.getGenreByIndex(rs.getInt("genre")),
                            rs.getInt("likes"),
                            rs.getInt("dislikes"),
                            rs.getBoolean("approved"),
                            rs.getBoolean("returned")
                    ));

            assert game != null;
            return Optional.of(game);
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
                    FIND_GAME_BY_GAME_ID,
                    Map.of("id", id),
                    (rs, i) -> new GameResponseDto(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getDate("release_date").toLocalDate(),
                            rs.getString("content"),
                            rs.getString("coverage"),
                            rs.getString("company_name"),
                            GameStatus.getStatusByIndex(rs.getInt("status")),
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
     * Поиск игр пользователя с помощью id пользователя.
     *
     * @param domain сущность с данными пользователя
     * @return список игр пользователя
     */
    public Optional<List<GameCondensedResponseDto>> findUserGames(UserDomain domain) {
        try {
            var company = developerRepository
                    .getCompanyByUserId(domain.getId())
                    .orElseThrow(() -> new CompanyNotFoundException("Not found company for this user!"));

            var games = template.query(
                    FIND_GAME_BY_USER_ID,
                    Map.of("companyId", company.getId()),
                    (rs, i) -> new GameCondensedResponseDto(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("coverage")
                    ));

            return Optional.of(games);
        } catch (EmptyResultDataAccessException e) {
            throw new GameNotFoundException("Game not found!");
        }
    }
}
