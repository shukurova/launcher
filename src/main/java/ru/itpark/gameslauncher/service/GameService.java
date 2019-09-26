package ru.itpark.gameslauncher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itpark.gameslauncher.domain.CompanyDomain;
import ru.itpark.gameslauncher.domain.UserDomain;
import ru.itpark.gameslauncher.domain.game.GameDomain;
import ru.itpark.gameslauncher.dto.company.CompanyRequestDto;
import ru.itpark.gameslauncher.dto.game.*;
import ru.itpark.gameslauncher.enums.RequestStatus;
import ru.itpark.gameslauncher.exception.*;
import ru.itpark.gameslauncher.repository.CompanyRepository;
import ru.itpark.gameslauncher.repository.DeveloperRepository;
import ru.itpark.gameslauncher.repository.GameRepository;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;
    private final DeveloperRepository developerRepository;
    private final CompanyRepository companyRepository;
    private final EmailService emailService;

    public List<GameCondensedResponseDto> getAllApproved() {
        return gameRepository.getAllApproved();
    }

    public GameResponseDto findApprovedById(long id) {
        return gameRepository.findApprovedById(id)
                .orElseThrow(() -> new GameNotFoundException("Game not found!"));
    }

    public List<GameCondensedResponseDto> getAllNotApproved() {
        return gameRepository.getAllNotApproved();
    }

    public NotApprovedGameResponseDto findNotApprovedById(long id) {
        return gameRepository.findNotApprovedById(id)
                .orElseThrow(() -> new GameNotFoundException("Game not found!"));
    }

    public List<GameCondensedResponseDto> getAllReturned() {
        return gameRepository.getAllReturned();
    }

    public GameDomain createGame(GameRequestDto dto,
                                 UserDomain user) {
        if (gameRepository.checkExistsByName(dto.getName())) {
            throw new GameAlreadyExistsException(
                    String.format("Game with this name %s already exists!", dto.getName()));
        }

        var companies = developerRepository
                .getCompanyByUserId(user.getId())
                .orElseThrow(() -> new CompanyNotFoundException("Not found company for this user!"));

        for (CompanyDomain company : companies) {
            if (!(company.getId() == dto.getCompanyId())) {
                throw new CompanyNotFoundException("You are not developer in this company!");
            }
        }

        var game = new GameDomain(
                0L,
                dto.getName(),
                dto.getReleaseDate(),
                dto.getContent(),
                dto.getCoverage(),
                dto.getCompanyId(),
                dto.getGameStatus(),
                dto.getGenre(),
                0,
                0,
                user.getId(),
                RequestStatus.PENDING
        );

        return gameRepository.createGame(game)
                .orElseThrow(() -> new CreateException("Failed create the record! Please, try later."));
    }

    public void approveGame(long id) {
        gameRepository.approveGame(id);
    }

    public ReturnedGameResponseDto returnGame(long id,
                                              ReturnedGameRequestDto dto) {
        var game = gameRepository.findNotApprovedById(id)
                .orElseThrow(() -> new GameNotFoundException("Game not found!"));

        var companyId = companyRepository.getCompanyIdByCompanyName(game.getCompanyName());
        List<String> emails = developerRepository
                .getDevelopersEmailsByCompanyId(companyId)
                .orElseThrow(() -> new EmailNotFoundException("Email not found!"));

//        emailService.sendSimpleMessage(
//                emails,
//                "Edit your record",
//                String.format("Please, check your game record. You need to edit your game.\n %s", comment));
        gameRepository.returnGame(id, game.getCompanyName(), dto.getComment());

        return gameRepository.findReturnedGameWithCommentById(id)
                .orElseThrow(() -> new GameNotFoundException("Users games not found!"));
    }

    public GameResponseDto createUpdateRequest(long id,
                                               GameRequestDto dto) {
        String gameName = gameRepository.getGameNameById(id);
        if (!gameName.equals(dto.getName()) | gameRepository.checkExistsByName(dto.getName())) {
            throw new GameAlreadyExistsException(
                    String.format("Game with this name %s already exists!", dto.getName()));
        }

        var game = new GameEditRequestDto(
                0,
                id,
                dto.getName(),
                dto.getReleaseDate(),
                dto.getContent(),
                dto.getCoverage(),
                dto.getGameStatus(),
                dto.getGenre()
        );

        //TODO: создание заявки на редактирование

        return gameRepository.findById(game.getId())
                .orElseThrow(() -> new GameNotFoundException("Game not found!"));
    }

    public ReturnedGameResponseDto findReturnedGameWithCommentById(long id) {
        return gameRepository.findReturnedGameWithCommentById(id)
                .orElseThrow(() -> new GameNotFoundException("Users games not found!"));
    }

    public void approveEditGame(long id, GameEditRequestDto dto) {
        gameRepository.editGame(id, dto);
    }
}
