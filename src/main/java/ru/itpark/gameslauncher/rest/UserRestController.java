package ru.itpark.gameslauncher.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.itpark.gameslauncher.domain.UserDomain;
import ru.itpark.gameslauncher.dto.game.GameCondensedResponseDto;
import ru.itpark.gameslauncher.dto.game.GameRequestDto;
import ru.itpark.gameslauncher.dto.game.GameResponseDto;
import ru.itpark.gameslauncher.dto.user.UserGameResponseDto;
import ru.itpark.gameslauncher.dto.user.UserProfileResponseDto;
import ru.itpark.gameslauncher.service.GameService;
import ru.itpark.gameslauncher.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class UserRestController {
    private final UserService userService;
    private final GameService gameService;

    @GetMapping
    public UserProfileResponseDto profile(@AuthenticationPrincipal UserDomain domain) {
        return userService.getUserInformation(domain);
    }

    @GetMapping("/games")
    public List<GameCondensedResponseDto> findUserGames(@AuthenticationPrincipal UserDomain domain) {
        return userService.findUserGames(domain);
    }

    @GetMapping("/games/created-by-me")
    public List<GameCondensedResponseDto> findNotApprovedUserGames(@AuthenticationPrincipal UserDomain domain) {
        return userService.findNotApprovedUserGames(domain);
    }

    @GetMapping("/games/{id}")
    public UserGameResponseDto findUserGameById(@PathVariable long id) {
        return userService.findUserGameById(id);
    }

    @PostMapping("/games/{id}/edit")
    @PreAuthorize("@permissionService.isGameDeveloper(#id, #user.id)")
    public GameResponseDto createUpdateRequest(@PathVariable long id,
                                               @RequestBody GameRequestDto dto,
                                               @AuthenticationPrincipal UserDomain user) {
        return gameService.createUpdateRequest(id, dto);
    }
}
