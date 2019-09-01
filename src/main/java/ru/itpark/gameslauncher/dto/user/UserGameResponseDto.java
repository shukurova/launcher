package ru.itpark.gameslauncher.dto.user;

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
public class UserGameResponseDto {
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
    private RequestStatus requestStatus;
}
