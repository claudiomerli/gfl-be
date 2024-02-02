package it.xtreamdev.gflbe.service;

import it.xtreamdev.gflbe.dto.chatgpt.ChatGPTContentResponse;
import it.xtreamdev.gflbe.dto.chatgpt.ChatGPTResponse;
import it.xtreamdev.gflbe.dto.majestic.SecondLevelCheckDTO;
import it.xtreamdev.gflbe.model.User;
import it.xtreamdev.gflbe.model.enumerations.RoleName;
import it.xtreamdev.gflbe.util.ExcelUtils;
import lombok.SneakyThrows;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.xlsx4j.sml.SheetData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static it.xtreamdev.gflbe.util.NetUtils.extractDomain;

@Service
public class ToolsService {


    @Autowired
    private ChatGPTService chatGPTService;
    @Autowired
    private MajesticSEOService majesticSEOService;

    @Autowired
    private UserService userService;

    private final String chatGPTRequestKeyword = "Trova parole chiave correlate a \"%s\" da usare per la link building e dammi qualche consiglio";
    private final String chatGPTRequestAnchorTexts = "Suggeriscimi parole chiave per linkare questa pagina: \"%s\" e dammi qualche consiglio";

    public ChatGPTContentResponse generateKeywords(String word) {
        ChatGPTResponse chatGPTResponse = this.chatGPTService.doChatGPTRequest(String.format(chatGPTRequestKeyword, word));
        return ChatGPTContentResponse.builder().content(chatGPTResponse.getChoices().get(0).getMessage().getContent()).build();
    }

    public ChatGPTContentResponse generateKeywordsForUrl(String url) {
//        checkIsRequestCustomerDomain(url);
        ChatGPTResponse chatGPTResponse = this.chatGPTService.doChatGPTRequest(String.format(chatGPTRequestAnchorTexts, url));
        return ChatGPTContentResponse.builder().content(chatGPTResponse.getChoices().get(0).getMessage().getContent()).build();
    }

    private void checkIsRequestCustomerDomain(String url) {
        User user = userService.userInfo();

        if (user.getRole() == RoleName.CUSTOMER) {
            String domainToCheck = extractDomain(url);
            String domainUser = user.getCustomerInfo().getUrl();
            if (!domainToCheck.equals(domainUser)) {
                throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
            }
        }
    }

    public List<String> getAnchorText(String url) {
//        checkIsRequestCustomerDomain(url);
        return this.majesticSEOService.getAnchorTextByUrl(url);
    }

    public List<Object> getRefDomainResponse(String domain) {
        return this.majesticSEOService.getRefDomainsByDomain(domain);
    }

    public List<Object> getAnchorTextResponse(String domain) {
        return this.majesticSEOService.getAnchorTextByDomain(domain);
    }

    public List<Object> getBackLinksResponse(String domain) {
        return this.majesticSEOService.getBacklinksByDomain(domain);
    }

    @SneakyThrows
    public byte[] getRefDomainResponseExport(String domain) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            List<Object> refDomainsByDomain = this.majesticSEOService.getRefDomainsByDomain(domain);
            SpreadsheetMLPackage spreadsheet = ExcelUtils.createSpreadsheet();
            SheetData sheetData = ExcelUtils.addSheet(spreadsheet, domain);
            ExcelUtils.addRow(sheetData, "Posizione", "Dominio di riferimento", "Link Totali", "Link Esterni");
            refDomainsByDomain.forEach(o -> {
                Map<String, Object> objectMap = (Map<String, Object>) o;
                ExcelUtils.addRow(sheetData,
                        objectMap.get("Position"),
                        objectMap.get("Domain"),
                        objectMap.get("MatchedLinks"),
                        objectMap.get("ExtBackLinks")
                );
            });

            spreadsheet.save(baos);
            return baos.toByteArray();
        }
    }

    @SneakyThrows
    public byte[] getAnchorTextResponseExport(String domain) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            List<Object> anchorTextByDomain = this.majesticSEOService.getAnchorTextByDomain(domain);
            SpreadsheetMLPackage spreadsheet = ExcelUtils.createSpreadsheet();
            SheetData sheetData = ExcelUtils.addSheet(spreadsheet, domain);
            ExcelUtils.addRow(sheetData, "Ancora", "Dominio di riferimento", "Link Totali", "Link cancellati", "Link no-follow");
            anchorTextByDomain.forEach(o -> {
                Map<String, Object> objectMap = (Map<String, Object>) o;
                ExcelUtils.addRow(sheetData,
                        objectMap.get("AnchorText"),
                        objectMap.get("RefDomains"),
                        objectMap.get("TotalLinks"),
                        objectMap.get("DeletedLinks"),
                        objectMap.get("NoFollowLinks")
                );
            });

            spreadsheet.save(baos);
            return baos.toByteArray();
        }
    }

    @SneakyThrows
    public byte[] getBackLinksResponseExport(String domain) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            List<Object> backlinks = this.majesticSEOService.getBacklinksByDomain(domain);
            SpreadsheetMLPackage spreadsheet = ExcelUtils.createSpreadsheet();
            SheetData sheetData = ExcelUtils.addSheet(spreadsheet, domain);
            ExcelUtils.addRow(sheetData, "Ancora", "Titolo", "Url", "Url Trust Flow", "Url Citation Flow", "Internal Outbound Links", "External Outbound Links", "External domain");
            backlinks.forEach(o -> {
                Map<String, Object> objectMap = (Map<String, Object>) o;
                ExcelUtils.addRow(sheetData,
                        objectMap.get("AnchorText"),
                        objectMap.get("SourceTitle"),
                        objectMap.get("SourceURL"),
                        objectMap.get("SourceTrustFlow"),
                        objectMap.get("SourceCitationFlow"),
                        objectMap.get("SourceOutLinksInternal"),
                        objectMap.get("SourceOutLinksExternal"),
                        objectMap.get("SourceOutDomainsExternal")
                );
            });

            spreadsheet.save(baos);
            return baos.toByteArray();
        }
    }

    public List<SecondLevelCheckDTO> getSecondLevel(String url) {
        JSONObject backlinksForUrl = this.majesticSEOService.getBacklinksForUrl(url);
        List<SecondLevelCheckDTO> secondLevelCheckDTOS = new ArrayList<>();
        if (backlinksForUrl.opt("DataTables") != null) {
            backlinksForUrl.getJSONObject("DataTables").getJSONObject("BackLinks").getJSONArray("Data")
                    .forEach(o -> {
                        JSONObject backlink = (JSONObject) o;
                        SecondLevelCheckDTO secondLevelCheckDTO = new SecondLevelCheckDTO();
                        secondLevelCheckDTO.setLink(backlink.getString("SourceURL"));
                        secondLevelCheckDTO.setTrustFlow(backlink.getNumber("SourceTrustFlow").intValue());
                        secondLevelCheckDTOS.add(secondLevelCheckDTO);
                    });
        }
        return secondLevelCheckDTOS;
    }
}
