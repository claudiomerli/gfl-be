package it.xtreamdev.gflbe.dto.majestic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RefDomainResponseDTO {

    private int Position;
    private String Domain;
    private int RefDomains;
    private int AlexaRank;
    private int Matches;
    private int MatchedLinks;
    private int ExtBackLinks;
    private int IndexedURLs;
    private int CrawledURLs;
    private LocalDate FirstCrawled;
    private LocalDate LastSuccessfulCrawl;
    private String IP;
    private String SubNet;
    private String CountryCode;
    private String TLD;
    private int CitationFlow;
    private int TrustFlow;
    private String Title;
    private int OutDomainsExternal;
    private int OutLinksExternal;
    private int OutLinksInternal;
    private int OutLinksPages;
    private String Language;
    private String LanguageDesc;
    private String LanguageConfidence;
    private String LanguagePageRatios;
    private int LanguageTotalPages;
    private int LinkSaturation;

}
