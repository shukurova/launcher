package ru.itpark.gameslauncher.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameResponseDto {
    private long id;
    private String name;
    private long companyId;
    private String coverage;

    //TODO: подумать над dto для перехода в конкретную игру
}
