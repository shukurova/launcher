package ru.itpark.gameslauncher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itpark.gameslauncher.domain.game.GameDomain;
import ru.itpark.gameslauncher.dto.GameResponseDto;
import ru.itpark.gameslauncher.repository.GameRepository;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;

    public List<GameResponseDto> getAll() {
        return gameRepository.getAll();
    }

    public GameResponseDto findById(long id) {
        return gameRepository.findById(id);
    }
}
