package it.xtreamdev.gflbe.dto;

import it.xtreamdev.gflbe.model.Content;
import it.xtreamdev.gflbe.model.enumerations.ContentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SaveContentDTO {

    private SaveContentRulesDTO contentRules;
    private Integer editorId;
    private Integer projectId;
    private Integer customerId;
    private Integer newspaperId;
    private String title;
    private String body;
    private String linkUrl;
    private String linkText;
    private LocalDate deliveryDate;
    private Integer score;
    private ContentStatus contentStatus;
    private String adminNotes;

}
