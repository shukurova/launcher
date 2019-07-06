package ru.itpark.gameslauncher.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
//TODO: сохранение ID пользователя, досаточно ли?
// Если всего пользователя, то как записывать в БД?
public class TokenDomain {
    private String token;
    private UserDomain user;
}
