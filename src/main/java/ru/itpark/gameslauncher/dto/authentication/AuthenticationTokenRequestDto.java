package ru.itpark.gameslauncher.dto.authentication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationTokenRequestDto {
  private String username;
  private String password;
}
