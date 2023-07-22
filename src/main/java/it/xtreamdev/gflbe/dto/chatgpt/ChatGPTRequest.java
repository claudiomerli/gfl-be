package it.xtreamdev.gflbe.dto.chatgpt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatGPTRequest {

    @Builder.Default
    private String model = "gpt-3.5-turbo";
    @Builder.Default
    private Double temperature = 1.0;
    private List<ChatGPTRequestMessage> messages;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ChatGPTRequestMessage{
        @Builder.Default
        private String role = "user";
        private String content;
    }

}
