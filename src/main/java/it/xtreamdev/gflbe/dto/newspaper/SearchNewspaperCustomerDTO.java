package it.xtreamdev.gflbe.dto.newspaper;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchNewspaperCustomerDTO {

    private String globalSearch;
    private Integer projectId;
    private Integer maxCost;
    private String typology;
    private Integer topicId;


}
