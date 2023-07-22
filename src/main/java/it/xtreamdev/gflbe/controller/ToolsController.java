package it.xtreamdev.gflbe.controller;

import it.xtreamdev.gflbe.dto.chatgpt.ChatGPTContentResponse;
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

    @GetMapping("anchor-text")
    public List<String> anchorTexts(
            @RequestParam("url") String url
    ) {
        return this.toolsService.getAnchorText(url);
    }

}
