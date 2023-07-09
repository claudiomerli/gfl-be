package it.xtreamdev.gflbe.dto.mejestic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FullDomainSeoStatistic {

    private DomainSEOStatistics principalDomain;
    private DomainSEOStatistics competitor1;
    private DomainSEOStatistics competitor2;

}
