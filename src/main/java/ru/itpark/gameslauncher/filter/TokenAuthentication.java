package ru.itpark.gameslauncher.filter;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;

@Getter
public class TokenAuthentication extends AbstractAuthenticationToken {
  private final Object principal;
  private final Object credentials;

  public TokenAuthentication(Object principal, Object credentials) {
    super(null);
    this.principal = principal;
    this.credentials = credentials;
  }
}
