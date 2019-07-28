package ru.itpark.gameslauncher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itpark.gameslauncher.domain.UserDomain;
import ru.itpark.gameslauncher.domain.game.GameDomain;
import ru.itpark.gameslauncher.dto.GameRequestDto;
import ru.itpark.gameslauncher.dto.GameResponseDto;
import ru.itpark.gameslauncher.exception.GameAlreadyExistsException;
import ru.itpark.gameslauncher.exception.GameNotFoundException;
import ru.itpark.gameslauncher.repository.GameRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;

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
            throw new GameAlreadyExistsException(String.format("Game with this name %s already exists!", dto.getName()));
        }

        var game = new GameDomain(
                0L,
                dto.getName(),
                dto.getReleaseDate(),
                dto.getContent(),
                dto.getCoverage(),
                user.getId(),
                dto.getStatus(),
                dto.getGenre(),
                0,
                0,
                false
        );

        return gameRepository.create(game);
    }

    public void approve(long id) {
        gameRepository.approve(id);
    }
}
