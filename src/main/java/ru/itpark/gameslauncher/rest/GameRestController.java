package ru.itpark.gameslauncher.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.itpark.gameslauncher.domain.UserDomain;
import ru.itpark.gameslauncher.domain.game.GameDomain;
import ru.itpark.gameslauncher.dto.GameRequestDto;
import ru.itpark.gameslauncher.dto.GameResponseDto;
import ru.itpark.gameslauncher.exception.GameNotFoundException;
import ru.itpark.gameslauncher.service.GameService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/games")
public class GameRestController {
    private final GameService gameService;

    @GetMapping
    public List<GameResponseDto> getAll() {
        return gameService.getAll();
    }

    @GetMapping("/not-approved")
    @PreAuthorize("hasRole('ADMIN')")
    public List<GameResponseDto> getNotApproved() {
        return gameService.getNotApproved();
    }

    @GetMapping("/{id}")
    public Optional<GameDomain> findById(@PathVariable long id) {
        return gameService.findById(id);
    }

    @GetMapping("/not-approved/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Optional<GameDomain> findNotApprovedById(@PathVariable long id) {
        return gameService.findNotApprovedById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('DEVELOPER')")
    public Optional<GameDomain> create(@RequestBody GameRequestDto dto, @AuthenticationPrincipal UserDomain user) {
        return gameService.create(dto, user);
    }

    @PostMapping("/not-approved/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public void approve(@PathVariable long id) {
        gameService.approve(id);
    }

    //TODO: возвращение записи игры разработчику
}
