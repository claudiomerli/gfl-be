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
    private String zaFrom;
    private String zaTo;
    private String purchasedContentFrom;
    private String purchasedContentTo;
    private String costEachFrom;
    private String costEachTo;
    private String costSellFrom;
    private String costSellTo;
    private String regionalGeolocalization;
    private List<String> topics;



}
