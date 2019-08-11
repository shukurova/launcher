package ru.itpark.gameslauncher.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDate;
import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponseDto {
    private long id;
    private String name;
    private String username;
    private String email;
    private Collection<? extends GrantedAuthority> authorities;
    private LocalDate created;
}
