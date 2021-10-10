package it.xtreamdev.gflbe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveContentRulesDTO {
    private String title;
    private String linkUrl;
    private String linkText;
    private String body;
    private Integer maxCharacterBodyLength;
    private List<LinkDto> links;

    private String attachmentBase64;
    private String attachmentContentType;
    private String attachmentFileName;
}
