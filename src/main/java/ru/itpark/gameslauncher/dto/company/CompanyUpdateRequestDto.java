package ru.itpark.gameslauncher.dto.company;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itpark.gameslauncher.enums.GameGenre;
import ru.itpark.gameslauncher.enums.GameStatus;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyUpdateRequestDto {
    private long companyId;
    private String name;
    private LocalDate releaseDate;
    private String content;
    private String coverage;
    private GameStatus gameStatus;
    private GameGenre genre;
}
