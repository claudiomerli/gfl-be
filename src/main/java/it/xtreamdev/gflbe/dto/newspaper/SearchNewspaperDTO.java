package it.xtreamdev.gflbe.dto.newspaper;

import lombok.*;

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

    private String orderBy;
    private String orderDirection;

}
