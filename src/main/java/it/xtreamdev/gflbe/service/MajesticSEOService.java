package it.xtreamdev.gflbe.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.xtreamdev.gflbe.dto.majestic.DomainSEOStatistics;
import it.xtreamdev.gflbe.dto.majestic.FullDomainSeoStatistic;
import it.xtreamdev.gflbe.dto.majestic.LinkCheckDTO;
import it.xtreamdev.gflbe.model.User;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static it.xtreamdev.gflbe.util.FormatUtils.percentInstanceFormatter;


@Service
@Slf4j
public class MajesticSEOService {

    @Autowired
    private MajesticSEOService selfInstance;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MajesticSEOService majesticSEOService;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private Environment env;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    private static String API_KEY = "F9E9157021931F2AC3AFA598731BB06C";
    private static List<String> REF_DOMAIN_TO_CHECK = List.of("adnkronos.com", "notizie.it", "tiscali.it", "milanofinanza.it");


    public List<String> getAnchorTextByUrl(String url) {
        JSONObject jsonObjectAnchorTextResponse = new JSONObject(this.restTemplate.getForObject("https://api.majestic.com/api/json?app_api_key={majestic_api_key}&cmd=GetAnchorText&item={domain}&Count=1000&datasource=fresh", String.class, API_KEY, url));
        JSONArray jsonArray = jsonObjectAnchorTextResponse
                .getJSONObject("DataTables")
                .getJSONObject("AnchorText")
                .getJSONArray("Data");

        List<String> anchorTexts = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            anchorTexts.add(jsonArray.getJSONObject(i).getString("AnchorText"));
        }

        return anchorTexts.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
    }

    public FullDomainSeoStatistic getStatisticsByDomain(String domain, String competitor1, String competitor2, Boolean disableCache) {
        try {
            if (disableCache) {
                Optional.ofNullable(this.cacheManager.getCache("analysis")).ifPresent(cache -> {
                    cache.evictIfPresent(domain);
                    cache.evictIfPresent(competitor1);
                    cache.evictIfPresent(competitor2);
                });
            }

            DomainSEOStatistics domainSeoReportForDomain = selfInstance.getDomainSeoReportForDomain(domain);
            DomainSEOStatistics competitor1SeoReportForDomain = selfInstance.getDomainSeoReportForDomain(competitor1);
            DomainSEOStatistics competitor2SeoReportForDomain = selfInstance.getDomainSeoReportForDomain(competitor2);

            calculateAnalysis(domainSeoReportForDomain, competitor1SeoReportForDomain, competitor2SeoReportForDomain);

            return FullDomainSeoStatistic.builder()
                    .principalDomain(domainSeoReportForDomain)
                    .competitor1(competitor1SeoReportForDomain)
                    .competitor2(competitor2SeoReportForDomain)
                    .build();
        } catch (Exception e) {
            log.error("Error", e);
            throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Cacheable(cacheNames = "analysis", key = "#p0", condition = "#p0 != null ")
    public JSONObject getBacklinksForUrl(String url) {
        return new JSONObject(this.restTemplate.getForEntity("https://api.majestic.com/api/json?app_api_key={majestic_api_key}&cmd=GetBackLinkData&item={item}&Count=50000&datasource=fresh", String.class, API_KEY, url).getBody());
    }

    @Cacheable(cacheNames = "analysis", key = "#p0", condition = "#p0 != null ")
    public DomainSEOStatistics getDomainSeoReportForDomain(String domain) {
        log.info("Searching stats for {}", domain);
        if (StringUtils.isBlank(domain)) {
            return null;
        }

        DomainSEOStatistics domainSEOStatistics = DomainSEOStatistics.builder().domain(domain).build();
        //LINK TOTALI
        try {
            domainSEOStatistics.setLinks(
                    new JSONObject(this.restTemplate.getForObject("https://api.majestic.com/api/json?app_api_key={majestic_api_key}&cmd=GetBackLinkData&item={domain}&Count=0&datasource=fresh", String.class, API_KEY, domain))
                            .getJSONObject("DataTables")
                            .getJSONObject("BackLinks")
                            .getJSONObject("Headers")
                            .getInt("AvailableLines"));
        } catch (Exception e) {
            domainSEOStatistics.setLinks(0);
        }


        //LINK NO FOLLOW
        try {
            domainSEOStatistics.setNoFollow(
                    new JSONObject(this.restTemplate.getForObject("https://api.majestic.com/api/json?app_api_key={majestic_api_key}&cmd=GetBackLinkData&item={domain}&Count=0&datasource=fresh&Filters=NoFollow(\"1\")&ConsumeResourcesForAdditionalProcessing=1&FilteringDepth=50000", String.class, API_KEY, domain))
                            .getJSONObject("DataTables")
                            .getJSONObject("Statistics")
                            .getJSONArray("Data")
                            .getJSONObject(0)
                            .getInt("Matches")
            );
        } catch (Exception e) {
            domainSEOStatistics.setNoFollow(0);
        }
        domainSEOStatistics.setNoFollowRatio(domainSEOStatistics.getLinks() == 0 ? 0 : (double) domainSEOStatistics.getNoFollow() / (double) domainSEOStatistics.getLinks());

        //LINK DO FOLLOW
        try {

            domainSEOStatistics.setDoFollow(
                    new JSONObject(this.restTemplate.getForObject("https://api.majestic.com/api/json?app_api_key={majestic_api_key}&cmd=GetBackLinkData&item={domain}&Count=0&datasource=fresh&Filters=NoFollow(\"0\")&ConsumeResourcesForAdditionalProcessing=1&FilteringDepth=50000", String.class, API_KEY, domain))
                            .getJSONObject("DataTables")
                            .getJSONObject("Statistics")
                            .getJSONArray("Data")
                            .getJSONObject(0)
                            .getInt("Matches")
            );
        } catch (Exception e) {
            domainSEOStatistics.setDoFollow(0);
        }
        domainSEOStatistics.setDoFollowRatio(domainSEOStatistics.getLinks() == 0 ? 0 : (double) domainSEOStatistics.getDoFollow() / (double) domainSEOStatistics.getLinks());

        //REF DOMAIN
        JSONObject refDomainResponse = new JSONObject(this.restTemplate.getForObject("https://api.majestic.com/api/json?app_api_key={majestic_api_key}&cmd=GetRefDomains&item0={domain}&Count=100000&datasource=fresh", String.class, API_KEY, domain));
        domainSEOStatistics.setRefDomains(
                refDomainResponse
                        .getJSONObject("DataTables")
                        .getJSONObject("Results")
                        .getJSONObject("Headers")
                        .getInt("TotalMatchedDomains")
        );
        JSONArray refDomainData = refDomainResponse
                .getJSONObject("DataTables")
                .getJSONObject("Results")
                .getJSONArray("Data");
        List<String> totalDomain = new ArrayList<>();
        for (int i = 0; i < refDomainData.length(); i++) {
            totalDomain.add(refDomainData.getJSONObject(i).getString("Domain"));
        }
        domainSEOStatistics.setMissingDomains(REF_DOMAIN_TO_CHECK.stream().filter(s -> !totalDomain.contains(s)).collect(Collectors.toList()));

        //ANCHOR TEXT
        JSONObject jsonObjectAnchorTextResponse = new JSONObject(this.restTemplate.getForObject("https://api.majestic.com/api/json?app_api_key={majestic_api_key}&cmd=GetAnchorText&item={domain}&Count=1000&datasource=fresh", String.class, API_KEY, domain));
        domainSEOStatistics.setAnchorTexts(
                jsonObjectAnchorTextResponse
                        .getJSONObject("DataTables")
                        .getJSONObject("AnchorText")
                        .getJSONObject("Headers")
                        .getInt("TotalResults")
        );

        JSONArray jsonArrayAnchorBrandData = jsonObjectAnchorTextResponse
                .getJSONObject("DataTables")
                .getJSONObject("AnchorText")
                .getJSONArray("Data");
        int totalAnchorBrand = 0;
        for (int i = 0; i < jsonArrayAnchorBrandData.length(); i++) {
            JSONObject jsonObject = jsonArrayAnchorBrandData.getJSONObject(i);
            String anchorText = jsonObject.getString("AnchorText");
            if (StringUtils.isNotBlank(anchorText) && anchorText.trim().toLowerCase().contains(domain.trim().toLowerCase()) || domain.trim().toLowerCase().contains(anchorText.trim().toLowerCase())) {
                totalAnchorBrand++;
            }
        }
        domainSEOStatistics.setAnchorBrandRatio((double) totalAnchorBrand / (double) domainSEOStatistics.getAnchorTexts());


        //REF PAGES
        domainSEOStatistics.setPages(
                new JSONObject(this.restTemplate.getForObject("https://api.majestic.com/api/json?app_api_key={majestic_api_key}&cmd=GetTopPages&Query={domain}&Count=0&datasource=fresh", String.class, API_KEY, domain))
                        .getJSONObject("DataTables")
                        .getJSONObject("Matches")
                        .getJSONObject("Headers")
                        .getInt("TotalMatches")
        );

        //TRUST FLOW && CITATION FLOW
        JSONObject domainIndexInfo = new JSONObject(this.restTemplate.getForObject("https://api.majestic.com/api/json?app_api_key={majestic_api_key}&cmd=GetIndexItemInfo&items=1&item0={domain}&datasource=fresh", String.class, API_KEY, domain))
                .getJSONObject("DataTables")
                .getJSONObject("Results")
                .getJSONArray("Data")
                .getJSONObject(0);

        domainSEOStatistics.setTrustFlow(domainIndexInfo.getInt("TrustFlow"));
        domainSEOStatistics.setCitationFlow(domainIndexInfo.getInt("CitationFlow"));
        domainSEOStatistics.setMetric(((double) domainSEOStatistics.getCitationFlow() + (double) domainSEOStatistics.getTrustFlow()) / 2);

        return domainSEOStatistics;
    }

    public void calculateAnalysis(DomainSEOStatistics domainSEOStatistics, DomainSEOStatistics competitor1SeoReportForDomain, DomainSEOStatistics competitor2SeoReportForDomain) {
        //ANALISI

        if (domainSEOStatistics.getNoFollowRatio() / domainSEOStatistics.getDoFollowRatio() > 0) {
            domainSEOStatistics.setAnalysisLinksRatio(String.format(env.getProperty("tilinkotool.hintPositiveLinksRatio"), percentInstanceFormatter.format(domainSEOStatistics.getDoFollowRatio()), percentInstanceFormatter.format(domainSEOStatistics.getNoFollowRatio())));
        } else {
            domainSEOStatistics.setAnalysisLinksRatio(String.format(env.getProperty("tilinkotool.hintNegativeLinksRatio"), percentInstanceFormatter.format(domainSEOStatistics.getDoFollowRatio()), percentInstanceFormatter.format(domainSEOStatistics.getNoFollowRatio())));
        }

        if (domainSEOStatistics.getAnchorBrandRatio() < 0.3) {
            domainSEOStatistics.setAnalysisTextBrandRatio(String.format(env.getProperty("tilinkotool.hintNegativeTextBrandRatio"), percentInstanceFormatter.format(domainSEOStatistics.getAnchorBrandRatio())));
        } else if (domainSEOStatistics.getAnchorBrandRatio() >= 0.5) {
            domainSEOStatistics.setAnalysisTextBrandRatio(String.format(env.getProperty("tilinkotool.hintPositiveTextBrandRatio"), percentInstanceFormatter.format(domainSEOStatistics.getAnchorBrandRatio())));
        }

        if (domainSEOStatistics.getLinks() < 100) {
            domainSEOStatistics.setAnalysisLowLinks(env.getProperty("tilinkotool.hintLowLinks"));
        }

        if (domainSEOStatistics.getRefDomains() < 100) {
            domainSEOStatistics.setAnalysisLowRefDomains(env.getProperty("tilinkotool.hintLowRefDomains"));
        }
        if (domainSEOStatistics.getRefDomains() >= 100) {
            domainSEOStatistics.setAnalysisHighRefDomains(env.getProperty("tilinkotool.hintHighRefDomains"));
        }
        if ((competitor1SeoReportForDomain != null && domainSEOStatistics.getRefDomains() < competitor1SeoReportForDomain.getRefDomains()) ||
                (competitor2SeoReportForDomain != null && domainSEOStatistics.getRefDomains() < competitor2SeoReportForDomain.getRefDomains())) {
            domainSEOStatistics.setAnalysisMediumRefDomains(env.getProperty("tilinkotool.hintMediumRefDomain"));
        }
        if (Stream.of(domainSEOStatistics, competitor1SeoReportForDomain, competitor2SeoReportForDomain)
                .filter(Objects::nonNull)
                .mapToInt(DomainSEOStatistics::getRefDomains)
                .max().getAsInt() == domainSEOStatistics.getRefDomains()
        ) {
            domainSEOStatistics.setAnalysisOptimalRefDomains(env.getProperty("tilinkotool.hintOptimalRefDomain"));
        }


        if (domainSEOStatistics.getMissingDomains().size() > 0) {
            domainSEOStatistics.setAnalysisMissingDomains(String.format(
                    env.getProperty("tilinkotool.hintMissingDomains"), domainSEOStatistics.getMissingDomains().stream().collect(Collectors.joining(", "))));
        }

        if (competitor1SeoReportForDomain != null || competitor2SeoReportForDomain != null) {
            if ((competitor1SeoReportForDomain == null || domainSEOStatistics.getRefDomains() > competitor1SeoReportForDomain.getRefDomains()) && (competitor2SeoReportForDomain == null || domainSEOStatistics.getRefDomains() > competitor2SeoReportForDomain.getRefDomains())) {
                domainSEOStatistics.setAnalysisCompetitorComparison(env.getProperty("tilinkotool.hintGoodComparisonWithCompetitor"));
            } else if ((competitor1SeoReportForDomain == null || domainSEOStatistics.getRefDomains() <= competitor1SeoReportForDomain.getRefDomains()) && (competitor2SeoReportForDomain == null || domainSEOStatistics.getRefDomains() <= competitor2SeoReportForDomain.getRefDomains())) {
                domainSEOStatistics.setAnalysisCompetitorComparison(env.getProperty("tilinkotool.hintBadComparisonWithCompetitor"));
            } else {
                domainSEOStatistics.setAnalysisCompetitorComparison(String.format(env.getProperty("tilinkotool.hintMediumComparisonWithCompetitor"),
                        domainSEOStatistics.getRefDomains() < competitor1SeoReportForDomain.getRefDomains() ? competitor1SeoReportForDomain.getDomain() : competitor2SeoReportForDomain.getDomain(),
                        domainSEOStatistics.getRefDomains() > competitor1SeoReportForDomain.getRefDomains() ? competitor1SeoReportForDomain.getDomain() : competitor2SeoReportForDomain.getDomain())
                );
            }
        }
    }

    public FullDomainSeoStatistic getCustomerStatistics() {
        User user = this.userService.userInfo();
        return this.majesticSEOService.getStatisticsByDomain(
                user.getCustomerInfo().getPrincipalDomain(),
                user.getCustomerInfo().getCompetitor1(),
                user.getCustomerInfo().getCompetitor2(),
                false);
    }

    @Cacheable(cacheNames = "analysis")
    public LinkCheckDTO startAnalysisForLink(String publicationUrl, String url, String anchor) {
        boolean isOnline;
        boolean isInIndex;
        boolean containsUrl = false;
        boolean containsCorrectAnchorText = false;
        boolean isFollow = false;
        log.info("Checking publication url {} with url {} with anchor {}", publicationUrl, url, anchor);

        try {
            this.restTemplate.exchange(publicationUrl, HttpMethod.GET, RequestEntity.EMPTY, Void.class);
            isOnline = true;
        } catch (Exception e) {
            isOnline = false;
        }
        log.info("Checked online");
        JSONObject backlinksForUrl = this.majesticSEOService.getBacklinksForUrl(url);
        log.info("Got majestic info");
        isInIndex = backlinksForUrl.get("Code").equals("OK") && !backlinksForUrl.getJSONObject("DataTables").getJSONObject("BackLinks").getJSONArray("Data").isEmpty();

        if (isInIndex) {
            for (int i = 0; i < backlinksForUrl.getJSONObject("DataTables").getJSONObject("BackLinks").getJSONArray("Data").length(); i++) {
                JSONObject jsonObject = backlinksForUrl.getJSONObject("DataTables").getJSONObject("BackLinks").getJSONArray("Data").getJSONObject(i);
                containsUrl = jsonObject.getString("SourceURL").equals(publicationUrl);
                containsCorrectAnchorText = containsUrl && jsonObject.getString("AnchorText").equals(anchor);
                isFollow = containsUrl && jsonObject.getNumber("FlagNoFollow").equals(0);
                if (containsUrl) {
                    break;
                }
            }
        }

        return LinkCheckDTO.builder()
                .publicationUrl(publicationUrl)
                .url(url)
                .anchor(anchor)
                .isOnline(isOnline)
                .isInIndex(isInIndex)
                .containsUrl(containsUrl)
                .containsCorrectAnchorText(containsCorrectAnchorText)
                .isFollow(isFollow)
                .build();
    }

    @SneakyThrows
    @Cacheable(cacheNames = "analysis")
    public List<Object> getRefDomainsByDomain(String domain){
        JSONObject refDomainResponse = new JSONObject(this.restTemplate.getForObject(
                "https://api.majestic.com/api/json?app_api_key={majestic_api_key}&cmd=GetRefDomains&item0={domain}&Count=30000&datasource=fresh&OrderBy1=11&OrderDir1=1&OrderBy2=1&OrderDir2=0", String.class, API_KEY, domain));
        JSONArray jsonArray = refDomainResponse.getJSONObject("DataTables").getJSONObject("Results").getJSONArray("Data");
        return jsonArray.toList();
    }

    @SneakyThrows
    @Cacheable(cacheNames = "analysis")
    public List<Object> getAnchorTextByDomain(String domain){
        JSONObject refDomainResponse = new JSONObject(this.restTemplate.getForObject(
                "https://api.majestic.com/api/json?app_api_key={majestic_api_key}&cmd=GetAnchorText&item={domain}&Count=30000&datasource=fresh", String.class, API_KEY, domain));
        JSONArray jsonArray = refDomainResponse.getJSONObject("DataTables").getJSONObject("AnchorText").getJSONArray("Data");
        return jsonArray.toList();
    }
}
