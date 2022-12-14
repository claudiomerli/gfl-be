package it.xtreamdev.gflbe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MaxMinRangeNewspaperAttributesDTO {

    private Integer minZA;
    private Integer maxZA;

    private Integer minPurchasedContent;
    private Integer maxPurchasedContent;

    private Integer minLeftContent;
    private Integer maxLeftContent;

    private Double minCostEach;
    private Double maxCostEach;

    private Double minCostSell;
    private Double maxCostSell;
}
