package ru.itpark.gameslauncher.domain.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameDomain {
    private long id;
    private String name;
    private LocalDateTime releaseDate;
    private String content;
    private String coverage;
    private long companyId;
    private GameStatus status;
    private GameGenre genre;
    private int likes;
    private int dislikes;
}
