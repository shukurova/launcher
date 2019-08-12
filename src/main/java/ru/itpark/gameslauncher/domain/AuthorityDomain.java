package ru.itpark.gameslauncher.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorityDomain {
    private long id;
    private long userId;
    private String authority;
}
