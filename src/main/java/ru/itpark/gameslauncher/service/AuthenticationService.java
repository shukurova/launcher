package ru.itpark.gameslauncher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itpark.gameslauncher.domain.TokenDomain;
import ru.itpark.gameslauncher.dto.AuthenticationTokenRequestDto;
import ru.itpark.gameslauncher.dto.AuthenticationTokenResponseDto;
import ru.itpark.gameslauncher.repository.TokenRepository;
import ru.itpark.gameslauncher.repository.UserRepository;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationTokenResponseDto authenticate(AuthenticationTokenRequestDto dto) {
        var userEntity = userRepository.findByUsername(dto.getUsername()).orElseThrow(() -> new UsernameNotFoundException(dto.getUsername()));

        if (!passwordEncoder.matches(dto.getPassword(), userEntity.getPassword())) {
            throw new BadCredentialsException(dto.getUsername());
        }

        var token = UUID.randomUUID().toString();
        var tokenDomain = new TokenDomain(token, userEntity);
        tokenRepository.save(tokenDomain);

        return new AuthenticationTokenResponseDto(token);
    }
}
