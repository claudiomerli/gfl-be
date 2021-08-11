package it.xtreamdev.gflbe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveContentRulesDTO {
    private String title;
    private String linkUrl;
    private String linkText;
    private String body;
    private Integer maxCharacterBodyLength;
}
