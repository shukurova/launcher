package ru.itpark.gameslauncher.domain.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itpark.gameslauncher.enums.GameGenre;
import ru.itpark.gameslauncher.enums.GameStatus;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateGameDomain {
    private long id;
    private long gameId;
    private String name;
    private LocalDate releaseDate;
    private String content;
    private String coverage;
    private long companyId;
    private GameStatus status;
    private GameGenre genre;
    private boolean approved;
    private boolean returned;
    private long creatorId;
}