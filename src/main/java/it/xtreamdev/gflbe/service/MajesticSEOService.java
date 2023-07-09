package it.xtreamdev.gflbe.service;

import it.xtreamdev.gflbe.dto.mejestic.DomainSEOStatistics;
import it.xtreamdev.gflbe.dto.mejestic.FullDomainSeoStatistic;
import it.xtreamdev.gflbe.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static it.xtreamdev.gflbe.util.FormatUtils.percentInstanceFormatter;


@Service
@Slf4j
@CacheConfig(cacheNames = {"analysis"})
public class MajesticSEOService {

    @Autowired
    private MajesticSEOService selfInstance;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MajesticSEOService majesticSEOService;

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    Environment env;

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

        return anchorTexts;
    }

    public FullDomainSeoStatistic getStatisticsByDomain(String domain, String competitor1, String competitor2, Boolean disableCache) {
        try {
            if (disableCache) {
                Optional.ofNullable(this.cacheManager.getCache(domain)).ifPresent(Cache::clear);
                Optional.ofNullable(this.cacheManager.getCache(competitor1)).ifPresent(Cache::clear);
                Optional.ofNullable(this.cacheManager.getCache(competitor2)).ifPresent(Cache::clear);
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

    @Cacheable(key = "#p0", condition = "#p0 != null ")
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

    public void sendStatisticsByDomainAndEmail(String domain, String competitor1, String competitor2, String email) {
        try {
            FullDomainSeoStatistic statisticsByDomain = this.majesticSEOService.getStatisticsByDomain(domain, competitor1, competitor2, false);
            DomainSEOStatistics principalDomainStats = statisticsByDomain.getPrincipalDomain();
            DomainSEOStatistics competitor1DomainStats = statisticsByDomain.getCompetitor1();
            DomainSEOStatistics competitor2DomainStats = statisticsByDomain.getCompetitor2();

            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setTo(email);
            helper.setFrom("tools@tilinkotool.it");
            helper.setSubject("Statistiche SEO per dominio: " + domain);
            helper.setText(
                    String.format("<h1>Statistiche SEO dominio: %s</h1>", domain) +
                            "<h1>Report</h1>" +
                            "<ul>" +
                            String.format("<li>Link totali: %s</li>", principalDomainStats.getLinks()) +
                            String.format("<li>Link DoFollow: %s</li>", principalDomainStats.getDoFollow()) +
                            String.format("<li>Link NoFollow: %s</li>", principalDomainStats.getNoFollow()) +
                            String.format("<li>Referring Domain: %s</li>", principalDomainStats.getRefDomains()) +
                            String.format("<li>Anchor Text: %s</li>", principalDomainStats.getAnchorTexts()) +
                            String.format("<li>Numero pagine che ricevono link: %s</li>", principalDomainStats.getPages()) +
                            String.format("<li>Rapporto link NoFollow/DoFollow: %s/%s</li>", percentInstanceFormatter.format(principalDomainStats.getNoFollowRatio()), percentInstanceFormatter.format(principalDomainStats.getDoFollowRatio())) +
                            String.format("<li>Anchor Brand: %s</li>", percentInstanceFormatter.format(principalDomainStats.getAnchorBrandRatio())) +
                            String.format("<li>Trust Flow: %s</li>", percentInstanceFormatter.format(principalDomainStats.getTrustFlow())) +
                            String.format("<li>Citation Flow: %s</li>", percentInstanceFormatter.format(principalDomainStats.getCitationFlow())) +
                            String.format("<li>Metrica: %s</li>", percentInstanceFormatter.format(principalDomainStats.getMetric())) +
                            "</ul>" +
                            "<h1>Analisi Competitor</h1>" +
                            "<table style=\"border: 1px solid black\">" +
                            "<thead><tr><td>DOMINIO</td><td>LINK TOTALI</td><td>LINK DOFOLLOW</td><td>LINK NOFOLLOW</td><td>REFERRING DOMAIN</td><td>ANCHOR BRAND</td></tr></thead><tbody>" +
                            String.format("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>", principalDomainStats.getDomain(), principalDomainStats.getLinks(), principalDomainStats.getDoFollow(), principalDomainStats.getNoFollow(), principalDomainStats.getRefDomains(), principalDomainStats.getAnchorBrandRatio()) +
                            (competitor1 != null ? String.format("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>", competitor1DomainStats.getDomain(), competitor1DomainStats.getLinks(), competitor1DomainStats.getDoFollow(), competitor1DomainStats.getNoFollow(), competitor1DomainStats.getRefDomains(), competitor1DomainStats.getAnchorBrandRatio()) : "") +
                            (competitor2 != null ? String.format("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>", competitor2DomainStats.getDomain(), competitor2DomainStats.getLinks(), competitor2DomainStats.getDoFollow(), competitor2DomainStats.getNoFollow(), competitor2DomainStats.getRefDomains(), competitor2DomainStats.getAnchorBrandRatio()) : "") +
                            "</tbody></table>" +
                            "<h1>Analisi</h1>" +
                            "<ul>" +
                            (StringUtils.isNotBlank(principalDomainStats.getAnalysisLinksRatio()) ? String.format("<li>%s</li>", principalDomainStats.getAnalysisLinksRatio()) : "") +
                            (StringUtils.isNotBlank(principalDomainStats.getAnalysisTextBrandRatio()) ? String.format("<li>%s</li>", principalDomainStats.getAnalysisTextBrandRatio()) : "") +
                            (StringUtils.isNotBlank(principalDomainStats.getAnalysisLowLinks()) ? String.format("<li>%s</li>", principalDomainStats.getAnalysisLowLinks()) : "") +
                            (StringUtils.isNotBlank(principalDomainStats.getAnalysisLowRefDomains()) ? String.format("<li>%s</li>", principalDomainStats.getAnalysisLowRefDomains()) : "") +
                            (StringUtils.isNotBlank(principalDomainStats.getAnalysisMissingDomains()) ? String.format("<li>%s</li>", principalDomainStats.getAnalysisMissingDomains()) : "") +
                            (StringUtils.isNotBlank(principalDomainStats.getAnalysisCompetitorComparison()) ? String.format("<li>%s</li>", principalDomainStats.getAnalysisCompetitorComparison()) : "") +
                            "</ul>", true);

            log.info("Sending email...");
            emailSender.send(message);
        } catch (MessagingException e) {
            log.error("Error sending email", e);
            throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public FullDomainSeoStatistic getCustomerStatistics() {
        User user = this.userService.userInfo();
        return this.majesticSEOService.getStatisticsByDomain(user.getCustomerInfo().getUrl(),
                user.getCustomerInfo().getCompetitor1(),
                user.getCustomerInfo().getCompetitor2(),
                false);
    }
}
