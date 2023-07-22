package it.xtreamdev.gflbe.dto.mejestic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DomainSEOStatistics {

    private String domain;
    private Integer links;
    private Integer noFollow;
    private Double noFollowRatio;
    private Integer doFollow;
    private Double doFollowRatio;
    private Integer refDomains;
    private List<String> missingDomains;
    private Integer anchorTexts;
    private Integer pages;
    private Double anchorBrandRatio;
    private Integer trustFlow;
    private Integer citationFlow;
    private Double metric;

    private String analysisLinksRatio;
    private String analysisTextBrandRatio;
    private String analysisLowLinks;

    private String analysisLowRefDomains;
    private String analysisHighRefDomains;
    private String analysisMediumRefDomains;
    private String analysisMinimumRefDomains;

    private String analysisMissingDomains;
    private String analysisCompetitorComparison;

}
