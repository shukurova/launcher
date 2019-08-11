package ru.itpark.gameslauncher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itpark.gameslauncher.domain.UserDomain;
import ru.itpark.gameslauncher.domain.game.GameDomain;
import ru.itpark.gameslauncher.dto.game.*;
import ru.itpark.gameslauncher.exception.CompanyNotFoundException;
import ru.itpark.gameslauncher.exception.GameAlreadyExistsException;
import ru.itpark.gameslauncher.exception.GameNotFoundException;
import ru.itpark.gameslauncher.repository.DeveloperRepository;
import ru.itpark.gameslauncher.repository.GameRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;
    private final DeveloperRepository developerRepository;
    private final EmailService emailService;

    public List<GameCondensedResponseDto> getAllApproved() {
        return gameRepository.getAllApproved();
    }

    public Optional<GameResponseDto> findApprovedById(long id) {
        return gameRepository.findApprovedById(id);
    }

    public List<GameCondensedResponseDto> getAllNotApproved() {
        return gameRepository.getAllNotApproved();
    }

    public Optional<NotApprovedGameResponseDto> findNotApprovedById(long id) {
        return gameRepository.findNotApprovedById(id);
    }

    public List<GameCondensedResponseDto> getAllReturned() {
        return gameRepository.getAllReturned();
    }

    public Optional<NotApprovedGameResponseDto> findReturnedById(long id) {
        return gameRepository.findReturnedById(id);
    }

    public Optional<GameDomain> createGame(GameRequestDto dto, UserDomain user) {
        if (gameRepository.checkExistsByName(dto.getName())) {
            throw new GameAlreadyExistsException(
                    String.format("Game with this name %s already exists!", dto.getName()));
        }

        var company = developerRepository
                .getCompanyByUserId(user.getId())
                .orElseThrow(() -> new CompanyNotFoundException("Not found company for this user!"));

        var game = new GameDomain(
                0L,
                dto.getName(),
                dto.getReleaseDate(),
                dto.getContent(),
                dto.getCoverage(),
                company.getId(),
                dto.getStatus(),
                dto.getGenre(),
                0,
                0,
                false,
                false
        );

        return gameRepository.createGame(game);
    }

    public void approveGame(long id) {
        gameRepository.approveGame(id);
    }

    public ReturnedGameResponseDto returnGame(long id, UserDomain user, ReturnedGameRequestDto dto) {
        var userEmail = user.getEmail();
        var game = gameRepository.findNotApprovedById(id)
                .orElseThrow(() -> new GameNotFoundException("Game not found!"));

//        emailService.sendSimpleMessage(
//                userEmail,
//                "Edit your record",
//                String.format("Please, check your game record. You need to edit your game.\n %s", comment));
        gameRepository.returnGame(id, game.getCompanyName(), dto.getComment());

        return gameRepository.findNotApprovedGameWithCommentById(id);
    }

    public Optional<GameResponseDto> editGame(long id,
                                              GameRequestDto dto) {
        if (gameRepository.checkExistsByName(dto.getName())) {
            throw new GameAlreadyExistsException(
                    String.format("Game with this name %s already exists!", dto.getName()));
        }

        var game = new GameEditRequestDto(
                id,
                dto.getName(),
                dto.getReleaseDate(),
                dto.getContent(),
                dto.getCoverage(),
                dto.getStatus(),
                dto.getGenre()
        );

        gameRepository.editGame(game);

        return gameRepository.findById(game.getId());
    }
}
