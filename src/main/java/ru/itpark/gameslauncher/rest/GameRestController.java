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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/games")
public class GameRestController {
    private final GameService gameService;

    @GetMapping
    public List<GameCondensedResponseDto> getAllApproved() {
        return gameService.getAllApproved();
    }

    @GetMapping("/{id}")
    public GameResponseDto findApprovedById(@PathVariable long id) {
        return gameService.findApprovedById(id);
    }

    @GetMapping("/not-approved")
    @PreAuthorize("hasRole('ADMIN')")
    public List<GameCondensedResponseDto> getAllNotApproved() {
        return gameService.getAllNotApproved();
    }

    @GetMapping("/not-approved/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public NotApprovedGameResponseDto findNotApprovedById(@PathVariable long id) {
        return gameService.findNotApprovedById(id);
    }

    @GetMapping("/returned")
    @PreAuthorize("hasRole('ADMIN')")
    public List<GameCondensedResponseDto> getAllReturned() {
        return gameService.getAllReturned();
    }

    @GetMapping("/returned/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public NotApprovedGameResponseDto findReturnedById(@PathVariable long id) {
        return gameService.findReturnedById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('DEVELOPER')")
    public GameDomain createGame(@RequestBody GameRequestDto dto,
                                 @AuthenticationPrincipal UserDomain user) {
        return gameService.createGame(dto, user);
    }

    @PostMapping("/not-approved/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public void approve(@PathVariable long id) {
        gameService.approveGame(id);
    }

    @PostMapping("/not-approved/{id}/return")
    @PreAuthorize("hasRole('ADMIN')")
    public ReturnedGameResponseDto returnGame(@PathVariable long id,
                                              @RequestBody ReturnedGameRequestDto dto) {
        return gameService.returnGame(id, dto);
    }

    @PostMapping("/{id}/edit")
    @PreAuthorize("@permissionService.isGameDeveloper(#id, #user.id)")
    public GameResponseDto createUpdateRequest(@PathVariable long id,
                                               @RequestBody GameRequestDto dto) {
        return gameService.createUpdateRequest(id, dto);
    }
}
