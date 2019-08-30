package ru.itpark.gameslauncher.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.itpark.gameslauncher.domain.UserDomain;
import ru.itpark.gameslauncher.dto.company.CompanyCondensedResponseDto;
import ru.itpark.gameslauncher.dto.company.CompanyUpdateRequestDto;
import ru.itpark.gameslauncher.dto.game.GameCondensedResponseDto;
import ru.itpark.gameslauncher.dto.game.GameUpdateRequestDto;
import ru.itpark.gameslauncher.dto.user.UserCompanyResponseDto;
import ru.itpark.gameslauncher.dto.user.UserGameResponseDto;
import ru.itpark.gameslauncher.dto.user.UserProfileResponseDto;
import ru.itpark.gameslauncher.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class UserRestController {
    private final UserService userService;

    @GetMapping
    public UserProfileResponseDto profile(@AuthenticationPrincipal UserDomain domain) {
        return userService.getUserInformation(domain);
    }

    @GetMapping("/games")
    @PreAuthorize("hasRole('DEVELOPER')")
    public List<GameCondensedResponseDto> findUserGames(@AuthenticationPrincipal UserDomain domain) {
        return userService.findUserGames(domain);
    }

    @GetMapping("/games/created-by-me")
    @PreAuthorize("hasRole('DEVELOPER')")
    public List<GameCondensedResponseDto> findCreatedGamesByUser(@AuthenticationPrincipal UserDomain domain) {
        return userService.findCreatedGamesByUser(domain);
    }

    @GetMapping("/games/{id}")
    @PreAuthorize("hasRole('DEVELOPER')")
    public UserGameResponseDto findUserGameById(@PathVariable long id) {
        return userService.findUserGameById(id);
    }

    //TODO: реализация создания заявки
    @PostMapping("/games/{id}/edit")
    @PreAuthorize("@permissionService.isGameDeveloper(#id, #user.id)")
    public void createGameUpdateRequest(@PathVariable long id,
                                        @RequestBody GameUpdateRequestDto dto,
                                        @AuthenticationPrincipal UserDomain user) {
        userService.createGameUpdateRequest(id, dto);
    }

    @GetMapping("/companies")
    public List<CompanyCondensedResponseDto> findUserCompanies(@AuthenticationPrincipal UserDomain domain) {
        return userService.findUserCompanies(domain);
    }

    @GetMapping("/companies/created-by-me")
    public List<CompanyCondensedResponseDto> findCreatedCompaniesByUser(@AuthenticationPrincipal UserDomain domain) {
        return userService.findCreatedCompaniesByUser(domain);
    }

    @GetMapping("/companies/{id}")
    public UserCompanyResponseDto findUserCompanyById(@PathVariable long id) {
        return userService.findUserCompanyById(id);
    }

    //TODO: реализация создания заявки
    @PostMapping("/companies/{id}/edit")
    @PreAuthorize("@permissionService.isCompanyDeveloper(#id, #user.id)")
    public void createCompanyUpdateRequest(@PathVariable long id,
                                           @RequestBody CompanyUpdateRequestDto dto,
                                           @AuthenticationPrincipal UserDomain user) {
        userService.createCompanyUpdateRequest(id, dto);
    }
}
