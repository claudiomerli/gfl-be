package it.xtreamdev.gflbe.dto;

import it.xtreamdev.gflbe.model.enumerations.ContentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

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
    private List<LinkDto> links;
    private LocalDate deliveryDate;
    private Integer score;
    private ContentStatus contentStatus;
    private String adminNotes;
    private Month monthUse;
    private Integer projectContentPreviewId;

}
