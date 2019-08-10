package ru.itpark.gameslauncher.dto.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itpark.gameslauncher.domain.game.GameGenre;
import ru.itpark.gameslauncher.domain.game.GameStatus;

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
    private GameStatus status;
    private GameGenre genre;
    private int likes;
    private int dislikes;
}