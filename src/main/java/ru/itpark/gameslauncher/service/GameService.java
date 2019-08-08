package ru.itpark.gameslauncher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itpark.gameslauncher.domain.UserDomain;
import ru.itpark.gameslauncher.domain.game.GameDomain;
import ru.itpark.gameslauncher.dto.*;
import ru.itpark.gameslauncher.exception.CompanyNotFoundException;
import ru.itpark.gameslauncher.exception.GameAlreadyExistsException;
import ru.itpark.gameslauncher.exception.GameNotFoundException;
import ru.itpark.gameslauncher.repository.CompanyRepository;
import ru.itpark.gameslauncher.repository.DeveloperRepository;
import ru.itpark.gameslauncher.repository.GameRepository;
import ru.itpark.gameslauncher.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;
    private final DeveloperRepository developerRepository;
    private final EmailService emailService;

    public List<GameResponseDto> getAll() {
        return gameRepository.getAll();
    }

    public List<GameResponseDto> getNotApproved() {
        return gameRepository.getNotApproved();
    }

    public Optional<GameDomain> findById(long id) {
        return gameRepository.findById(id);
    }

    public Optional<GameDomain> findNotApprovedById(long id) {
        return gameRepository.findNotApprovedById(id);
    }

    public Optional<GameDomain> create(GameRequestDto dto, UserDomain user) {
        if (gameRepository.existsByName(dto.getName())) {
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

        return gameRepository.create(game);
    }

    public void approve(long id) {
        gameRepository.approve(id);
    }

    public ReturnedGameResponseDto returnGame(long id, UserDomain user, ReturnedGameRequestDto dto) {
        var userEmail = user.getEmail();
        var game = gameRepository.findNotApprovedById(id)
                .orElseThrow(() -> new GameNotFoundException("Game not found!"));

//        emailService.sendSimpleMessage(
//                userEmail,
//                "Edit your record",
//                String.format("Please, check your game record. You need to edit your game.\n %s", comment));
        return gameRepository.returnGame(id, game.getCompanyId(), dto.getComment());
    }

    public Optional<GameDomain> edit(long id,
                                     GameRequestDto dto,
                                     UserDomain user) {
        if (gameRepository.existsByName(dto.getName())) {
            throw new GameAlreadyExistsException(
                    String.format("Game with this name %s already exists!", dto.getName()));
        }

        var company = developerRepository
                .getCompanyByUserId(user.getId())
                .orElseThrow(() -> new CompanyNotFoundException("Not found company for this user!"));
        var game = new GameEditRequestDto(
                id,
                dto.getName(),
                dto.getReleaseDate(),
                dto.getContent(),
                dto.getCoverage(),
                company.getId(),
                dto.getStatus(),
                dto.getGenre()
        );

        return gameRepository.edit(game);
    }
}
