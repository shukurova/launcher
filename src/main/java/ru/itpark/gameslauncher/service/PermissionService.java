package ru.itpark.gameslauncher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itpark.gameslauncher.repository.UserRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class PermissionService {
    private final UserRepository userRepository;

    public boolean isGameDeveloper(long gameId, long userId) {
        return userRepository.isGameDeveloper(gameId, userId);
    }

    //TODO: реализовать проверку, что пользователь разработчик той команды, информацию которой он хочет отредактировать
    public boolean isCompanyDeveloper(long companyId, long userId) {
        return false;
    }
}
