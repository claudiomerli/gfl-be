package it.xtreamdev.gflbe.dto.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SaveProjectCommissionMassiveDTO {

    private List<Integer> newspaperIds;
    private String period;
    private Integer year;
    private String anchor;
    private Boolean isAnchorBold;
    private Boolean isAnchorItalic;
    private String url;
    private String title;
    private String notes;
    private String publicationUrl;

    private LocalDate publicationDate;

}
