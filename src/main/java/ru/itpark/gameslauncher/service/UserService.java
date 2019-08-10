package ru.itpark.gameslauncher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itpark.gameslauncher.domain.UserDomain;
import ru.itpark.gameslauncher.dto.user.UserGameResponseDto;
import ru.itpark.gameslauncher.dto.user.UserProfileResponseDto;
import ru.itpark.gameslauncher.repository.GameRepository;
import ru.itpark.gameslauncher.repository.UserRepository;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final GameRepository gameRepository;

    public Optional<UserProfileResponseDto> getUserInformation(UserDomain domain) {
        return userRepository.getUserInformation(domain);
    }

    public Optional<UserGameResponseDto> findUserGameById(long id) {
        return gameRepository.findUserGameById(id);
    }
}
