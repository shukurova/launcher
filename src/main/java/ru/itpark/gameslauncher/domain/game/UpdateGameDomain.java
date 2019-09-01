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
    private long creatorId;
    private RequestStatus requestStatus;
}