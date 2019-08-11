package ru.itpark.gameslauncher.dto.company;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itpark.gameslauncher.dto.game.GameCondensedResponseDto;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyResponseDto {
    private long id;
    private String name;
    private String country;
    private String content;
    private LocalDate creationDate;
    private List<GameCondensedResponseDto> games;
}
