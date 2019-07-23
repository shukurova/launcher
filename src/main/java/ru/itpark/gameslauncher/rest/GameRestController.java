package ru.itpark.gameslauncher.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.itpark.gameslauncher.dto.GameResponseDto;
import ru.itpark.gameslauncher.service.GameService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/games")
public class GameRestController {
    private final GameService gameService;

    @GetMapping
    public List<GameResponseDto> getAll() {
        return gameService.getAll();
    }

    @GetMapping("/{id}")
    public GameResponseDto findById(@PathVariable long id) {
        return gameService.findById(id);
    }
}
