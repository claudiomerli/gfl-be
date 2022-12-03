package it.xtreamdev.gflbe.dto.project;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SaveProjectCommissionDTO {

    private Integer newspaperId;
    private String period;
    private Integer year;
    private String anchor;
    private String url;
    private String title;
    private String notes;
    private String publicationUrl;

    private LocalDate publicationDate;

}
