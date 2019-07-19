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
    private final RegistrationTokenRepository registrationTokenRepository;
    private final EmailService emailService;

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
                    dto.getEmail(),
                    passwordEncoder.encode(dto.getPassword()),
                    List.of(new SimpleGrantedAuthority("ROLE_USER")),
                    true,
                    true,
                    true,
                    false

            );

            long userId = userRepository.save(user);

            var token = UUID.randomUUID().toString();
            var tokenDomain = new RegistrationTokenDomain(
                    token,
                    userId,
                    LocalDateTime.now());

            registrationTokenRepository.save(tokenDomain);

            emailService.sendSimpleMessage(dto.getEmail(), "Please, confirm your registration", token);
        } else {

        }
    }
}
