package ru.itpark.gameslauncher.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.itpark.gameslauncher.dto.AuthenticationTokenResponseDto;
import ru.itpark.gameslauncher.dto.RegistrationConfirmationRequestDto;
import ru.itpark.gameslauncher.dto.RegistrationRequestDto;
import ru.itpark.gameslauncher.service.RegistrationService;

@RestController
@RequestMapping("/api/registration")
@RequiredArgsConstructor
public class RegistrationRestController {
    private final RegistrationService service;

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void register(@RequestBody RegistrationRequestDto dto) {
        service.register(dto);
    }

    @PostMapping("/confirm")
    public AuthenticationTokenResponseDto confirm(@RequestBody RegistrationConfirmationRequestDto dto) {
        return service.confirm(dto);
    }
}
