package ru.itpark.gameslauncher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itpark.gameslauncher.exception.TokenException;
import ru.itpark.gameslauncher.repository.AuthenticationTokenRepository;
import ru.itpark.gameslauncher.repository.UserRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class TokenAuthenticationManager implements AuthenticationManager {
    private final AccountStatusUserDetailsChecker checker = new AccountStatusUserDetailsChecker();
    private final AuthenticationTokenRepository authenticationTokenRepository;
    private final UserRepository userRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        var token = (String) authentication.getPrincipal();

        var tokenDomain = authenticationTokenRepository.findByToken(token).orElseThrow(() -> new TokenException("Invalid token"));
        var userDomain = userRepository.findById(tokenDomain.getUserId()).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        checker.check(userDomain);

        return new UsernamePasswordAuthenticationToken(
                userDomain,
                userDomain.getPassword(),
                userDomain.getAuthorities()
        );
    }
}