package ru.itpark.gameslauncher.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itpark.gameslauncher.enums.RequestStatus;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCompanyResponseDto {
    private long id;
    private String name;
    private String country;
    private String content;
    private LocalDate creationDate;
    private RequestStatus requestStatus;
}
