package ru.itpark.gameslauncher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itpark.gameslauncher.domain.RegistrationTokenDomain;
import ru.itpark.gameslauncher.domain.UserDomain;
import ru.itpark.gameslauncher.dto.RegistrationRequestDto;
import ru.itpark.gameslauncher.exception.UsernameAlreadyExistsException;
import ru.itpark.gameslauncher.repository.RegistrationTokenRepository;
import ru.itpark.gameslauncher.repository.TokenRepository;
import ru.itpark.gameslauncher.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class RegistrationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;

    public void register(RegistrationRequestDto dto) {
        if (userRepository.existsByUserName(dto.getUsername())) {
            throw new UsernameAlreadyExistsException(dto.getUsername());
        }

        var userOptional = userRepository.findByUsername(dto.getUsername());
        if (userOptional.isEmpty()) {
            var user = new UserDomain(
                    0L,
                    dto.getName(),
                    dto.getUsername(),
                    passwordEncoder.encode(dto.getPassword()),
                    List.of(new SimpleGrantedAuthority("ROLE_USER")),
                    true,
                    true,
                    true,
                    false

            );

            var token = UUID.randomUUID().toString();
            var tokenDomain = new RegistrationTokenDomain(
                    token,
                    user.getId(),
                    LocalDateTime.now());

            tokenRepository.save(tokenDomain);

            userRepository.save(user);

            //TODO: link for email
            return;
        }
    }
}
