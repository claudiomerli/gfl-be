package it.xtreamdev.gflbe.dto.chatgpt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatGPTResponse {
    private String id;
    private String object;
    private Integer created;
    private String model;
    private ChatGPTResponseUsage usage;
    private ArrayList<ChatGPTResponseChoice> choices;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    private static class ChatGPTResponseUsage {
        private int prompt_tokens;
        private int completion_tokens;
        private int total_tokens;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    private static class ChatGPTResponseChoice {
        private ChatGPTResponseMessage message;
        private String finish_reason;
        private int index;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    private static class ChatGPTResponseMessage {
        private String role;
        private String content;
    }

}
