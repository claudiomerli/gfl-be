package it.xtreamdev.gflbe.dto.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SaveProjectLinkDTO {

    private Integer newspaperId;
    private String period;
    private Integer year;
    private String anchor;
    private String url;
    private String publicationUrl;

}
