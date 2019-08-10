package ru.itpark.gameslauncher.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.itpark.gameslauncher.domain.UserDomain;
import ru.itpark.gameslauncher.domain.game.GameDomain;
import ru.itpark.gameslauncher.dto.game.*;
import ru.itpark.gameslauncher.service.GameService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/games")
public class GameRestController {
    private final GameService gameService;

    @GetMapping
    public List<GameCondensedResponseDto> getAll() {
        return gameService.getAll();
    }

    @GetMapping("/not-approved")
    @PreAuthorize("hasRole('ADMIN')")
    public List<GameCondensedResponseDto> getNotApproved() {
        return gameService.getNotApproved();
    }

    @GetMapping("/{id}")
    public Optional<GameResponseDto> findById(@PathVariable long id) {
        return gameService.findById(id);
    }

    @GetMapping("/not-approved/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Optional<NotApprovedGameResponseDto> findNotApprovedById(@PathVariable long id) {
        return gameService.findNotApprovedById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('DEVELOPER')")
    public Optional<GameDomain> create(@RequestBody GameRequestDto dto,
                                       @AuthenticationPrincipal UserDomain user) {
        return gameService.create(dto, user);
    }

    @PostMapping("/not-approved/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public void approve(@PathVariable long id) {
        gameService.approve(id);
    }

    @PostMapping("/not-approved/{id}/return")
    @PreAuthorize("hasRole('ADMIN')")
    public ReturnedGameResponseDto returnGame(@PathVariable long id,
                                              @AuthenticationPrincipal UserDomain user,
                                              @RequestBody ReturnedGameRequestDto dto) {
        return gameService.returnGame(id, user, dto);
    }

    @PostMapping("/{id}/edit")
    @PreAuthorize("@permissionService.isGameDeveloper(#id, #user.id)")
    public Optional<GameResponseDto> edit(@PathVariable long id,
                                          @RequestBody GameRequestDto dto,
                                          @AuthenticationPrincipal UserDomain user) {
        return gameService.edit(id, dto);
    }
}
