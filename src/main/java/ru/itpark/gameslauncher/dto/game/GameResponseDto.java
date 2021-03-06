package ru.itpark.gameslauncher.dto.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itpark.gameslauncher.enums.GameGenre;
import ru.itpark.gameslauncher.enums.GameStatus;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameResponseDto {
    private long id;
    private String name;
    private LocalDate releaseDate;
    private String content;
    private String coverage;
    private String companyName;
    private GameStatus gameStatus;
    private GameGenre genre;
    private int likes;
    private int dislikes;
}
