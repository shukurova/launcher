package ru.itpark.gameslauncher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itpark.gameslauncher.domain.RegistrationTokenDomain;
import ru.itpark.gameslauncher.domain.UserDomain;
import ru.itpark.gameslauncher.dto.RegistrationRequestDto;
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

    public void register(RegistrationRequestDto dto) {
        var userOptional = userRepository.findByUsername(dto.getUsername());
        if (userOptional.isEmpty()) {
            var user = new UserDomain(
                    0L,
                    dto.getName(),
                    dto.getUsername(),
                    passwordEncoder.encode(dto.getPassword()),
                    //TODO: спросить про надобность данного поля
                    List.of(new SimpleGrantedAuthority("ROLE_USER")),
                    true,
                    true,
                    true,
                    false

            );

            var token = new RegistrationTokenDomain(
                    UUID.randomUUID().toString(),
                    userOptional.get(),
                    LocalDateTime.now()
            );

            registrationTokenRepository.save(token);
        }
    }
}
