package it.xtreamdev.gflbe.service;

import it.xtreamdev.gflbe.dto.chatgpt.ChatGPTContentResponse;
import it.xtreamdev.gflbe.dto.chatgpt.ChatGPTResponse;
import it.xtreamdev.gflbe.model.User;
import it.xtreamdev.gflbe.model.enumerations.RoleName;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.stream.Collectors;

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

        if(user.getRole() == RoleName.CUSTOMER){
            String domainToCheck = extractDomain(url);
            String domainUser = user.getCustomerInfo().getUrl();
            if(!domainToCheck.equals(domainUser)){
                throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
            }
        }
    }

    public List<String> getAnchorText(String url){
//        checkIsRequestCustomerDomain(url);
        return this.majesticSEOService.getAnchorTextByUrl(url);
    }

}
