package ru.itpark.gameslauncher.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationTokenDomain {
    private String token;
    //TODO: что записывать в базу? Достаточно ли id пользователя?
    private UserDomain user;
    private LocalDateTime created;
}
