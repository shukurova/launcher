package ru.itpark.gameslauncher.dto.company;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotApprovedCompanyResponseDto {
    private long id;
    private String name;
    private String country;
    private String content;
    private LocalDate creationDate;
}
