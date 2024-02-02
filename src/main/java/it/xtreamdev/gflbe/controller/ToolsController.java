package it.xtreamdev.gflbe.controller;

import it.xtreamdev.gflbe.dto.chatgpt.ChatGPTContentResponse;
import it.xtreamdev.gflbe.dto.majestic.SecondLevelCheckDTO;
import it.xtreamdev.gflbe.service.ToolsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("api/tools")
@RestController
public class ToolsController {

    @Autowired
    private ToolsService toolsService;

    @GetMapping("keywords")
    public ChatGPTContentResponse keywords(
            @RequestParam("word") String word
    ) {
        return this.toolsService.generateKeywords(word);
    }

    @GetMapping("keywords-for-url")
    public ChatGPTContentResponse keywordsForUrl(
            @RequestParam("url") String url
    ) {
        return this.toolsService.generateKeywordsForUrl(url);
    }

    @GetMapping("generate-anchor-text")
    public List<String> anchorTexts(
            @RequestParam("url") String url
    ) {
        return this.toolsService.getAnchorText(url);
    }

    @GetMapping("second-level")
    public List<SecondLevelCheckDTO> secondLevel(
            @RequestParam("url") String url
    ) {
        return this.toolsService.getSecondLevel(url);
    }

    @GetMapping("ref-domains")
    public List<Object> getRefDomain(
            @RequestParam("url") String url
    ) {
        return this.toolsService.getRefDomainResponse(url);
    }

    @GetMapping("anchor-text")
    public List<Object> getAnchorText(
            @RequestParam("url") String url
    ) {
        return this.toolsService.getAnchorTextResponse(url);
    }

    @GetMapping("backlinks")
    public List<Object> getBacklinks(
            @RequestParam("url") String url
    ) {
        return this.toolsService.getBackLinksResponse(url);
    }

    @GetMapping("export-ref-domains")
    public byte[] getRefDomainExport(
            @RequestParam("url") String url
    ) {
        return this.toolsService.getRefDomainResponseExport(url);
    }

    @GetMapping("export-anchor-text")
    public byte[] getAnchorTextExport(
            @RequestParam("url") String url
    ) {
        return this.toolsService.getAnchorTextResponseExport(url);
    }

    @GetMapping("export-backlinks")
    public byte[] getBacklinksExport(
            @RequestParam("url") String url
    ) {
        return this.toolsService.getBackLinksResponseExport(url);
    }

}
