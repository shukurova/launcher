package ru.itpark.gameslauncher.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itpark.gameslauncher.domain.game.GameGenre;
import ru.itpark.gameslauncher.domain.game.GameStatus;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturnedGameResponseDto {
    private long id;
    private String name;
    private LocalDate releaseDate;
    private String content;
    private String coverage;
    private long companyId;
    private GameStatus status;
    private GameGenre genre;
    private String comment;
}
