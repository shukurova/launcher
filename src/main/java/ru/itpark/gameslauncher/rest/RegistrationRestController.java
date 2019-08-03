package ru.itpark.gameslauncher.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.itpark.gameslauncher.dto.AuthenticationTokenResponseDto;
import ru.itpark.gameslauncher.dto.RegistrationConfirmationRequestDto;
import ru.itpark.gameslauncher.dto.RegistrationRequestDto;
import ru.itpark.gameslauncher.dto.RegistrationTokenResponseDto;
import ru.itpark.gameslauncher.service.RegistrationService;

@RestController
@RequestMapping("/api/registration")
@RequiredArgsConstructor
public class RegistrationRestController {
    private final RegistrationService service;

    @PostMapping
    public RegistrationTokenResponseDto register(@RequestBody RegistrationRequestDto dto) {
        return service.register(dto);
    }

    @PostMapping("/confirm")
    public AuthenticationTokenResponseDto confirm(@RequestBody RegistrationConfirmationRequestDto dto) {
        return service.confirm(dto);
    }

    @PostMapping("/request-new-token")
    public RegistrationTokenResponseDto requestNewToken(@RequestBody RegistrationRequestDto dto) {
        return service.requestNewToken(dto);
    }
}
