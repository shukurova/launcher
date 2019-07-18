package ru.itpark.gameslauncher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itpark.gameslauncher.exception.TokenException;
import ru.itpark.gameslauncher.repository.TokenRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class TokenService implements AuthenticationManager {
    private final TokenRepository tokenRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        var token = (String) authentication.getPrincipal();

        if (token == null) {
            throw new TokenException("Token can't be null");
        }

        var tokenDomain = tokenRepository.findById(token).orElseThrow(() -> new TokenException("Token invalid"));
        var userDomain = tokenDomain.getUser();

        return new UsernamePasswordAuthenticationToken(
                userDomain,
                userDomain.getPassword(),
                userDomain.getAuthorities()
        );

    }
}
