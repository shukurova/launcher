package ru.itpark.gameslauncher.domain.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itpark.gameslauncher.enums.GameGenre;
import ru.itpark.gameslauncher.enums.GameStatus;
import ru.itpark.gameslauncher.enums.RequestStatus;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameDomain {
    private long id;
    private String name;
    private LocalDate releaseDate;
    private String content;
    private String coverage;
    private long companyId;
    private GameStatus gameStatus;
    private GameGenre genre;
    private int likes;
    private int dislikes;
    private long creatorId;
    private RequestStatus requestStatus;
}
