package it.xtreamdev.gflbe.service;

import it.xtreamdev.gflbe.dto.chatgpt.ChatGPTRequest;
import it.xtreamdev.gflbe.dto.chatgpt.ChatGPTResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ChatGPTService {


    @Value("${CHATGPT_API_KEY:}")
    private String CHAT_GPT_APIKEY;

    @Autowired
    private RestTemplate restTemplate;

    public ChatGPTResponse doChatGPTRequest(ChatGPTRequest chatGPTRequest) {
        RequestEntity<ChatGPTRequest> request = RequestEntity.post("https://api.openai.com/v1/chat/completions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + CHAT_GPT_APIKEY)
                .body(chatGPTRequest);

        ResponseEntity<ChatGPTResponse> chatGPTResponseResponseEntity = this.restTemplate.exchange(request, ChatGPTResponse.class);
        return chatGPTResponseResponseEntity.getBody();
    }

    public ChatGPTResponse doChatGPTRequest(String content) {
        RequestEntity<ChatGPTRequest> request = RequestEntity.post("https://api.openai.com/v1/chat/completions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + CHAT_GPT_APIKEY)
                .body(ChatGPTRequest.builder()
                        .messages(
                                List.of(ChatGPTRequest.ChatGPTRequestMessage
                                        .builder()
                                        .content(content)
                                        .build()))
                        .build());

        ResponseEntity<ChatGPTResponse> chatGPTResponseResponseEntity = this.restTemplate.exchange(request, ChatGPTResponse.class);
        return chatGPTResponseResponseEntity.getBody();
    }



}
