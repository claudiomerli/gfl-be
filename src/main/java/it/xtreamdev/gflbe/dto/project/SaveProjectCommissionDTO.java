package it.xtreamdev.gflbe.dto.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SaveProjectCommissionDTO {

    private Integer newspaperId;
    private String period;
    private String anchor;
    private String url;
    private String title;
    private String notes;
    private String publicationUrl;

}
