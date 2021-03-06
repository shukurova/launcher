package ru.itpark.gameslauncher.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itpark.gameslauncher.enums.RequestStatus;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDomain {
    private long id;
    private String name;
    private String country;
    private String content;
    private LocalDate creationDate;
    private long creatorId;
    private RequestStatus requestStatus;
}
