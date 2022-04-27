package it.xtreamdev.gflbe.dto.newspaper;

import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchNewspaperDTO {

    private Integer newspaperId;
    private Integer topicId;
    private Double maxBudget;

    private String name;
    private Long zaFrom;
    private Long zaTo;
    private Long purchasedContentFrom;
    private Long purchasedContentTo;
    private Double costEachFrom;
    private Double costEachTo;
    private Double costSellFrom;
    private Double costSellTo;
    private String regionalGeolocalization;
    private List<String> topics;
    private String sortDirection;
    private String sortBy;

    private String orderBy;
    private String orderDirection;

    public void cleanSortParam(String sortBy, String sortDirection) {
        this.sortBy = !StringUtils.isEmpty(this.sortBy) ? this.sortBy : sortBy;
        this.sortDirection = !StringUtils.isEmpty(this.sortDirection) ? this.sortDirection : sortDirection;
    }

}
