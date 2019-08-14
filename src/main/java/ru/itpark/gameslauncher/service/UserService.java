package ru.itpark.gameslauncher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itpark.gameslauncher.domain.UserDomain;
import ru.itpark.gameslauncher.dto.game.GameCondensedResponseDto;
import ru.itpark.gameslauncher.dto.user.UserGameResponseDto;
import ru.itpark.gameslauncher.dto.user.UserProfileResponseDto;
import ru.itpark.gameslauncher.exception.GameNotFoundException;
import ru.itpark.gameslauncher.repository.GameRepository;
import ru.itpark.gameslauncher.repository.UserRepository;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final GameRepository gameRepository;

    public UserProfileResponseDto getUserInformation(UserDomain domain) {
        return userRepository.getUserInformation(domain);
    }

    public UserGameResponseDto findUserGameById(long id) {
        return gameRepository.findUserGameByGameId(id)
                .orElseThrow(() -> new GameNotFoundException("Users games not found!"));
    }

    public List<GameCondensedResponseDto> findUserGames(UserDomain domain) {
        return gameRepository.findUserGames(domain);
    }

    public List<GameCondensedResponseDto> findNotApprovedUserGames(UserDomain domain) {
        return gameRepository.findNotApprovedUserGames(domain);
    }
}
