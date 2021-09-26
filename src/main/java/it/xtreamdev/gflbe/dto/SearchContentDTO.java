package it.xtreamdev.gflbe.dto;

import it.xtreamdev.gflbe.model.enumerations.ContentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.Month;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SearchContentDTO {

    private String globalSearch;
    private Integer customerId;
    private Integer editorId;
    private Integer newspaperId;
    private ContentStatus status;
    private Month monthUse;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdDateFrom;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdDateTo;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate deliveryDateFrom;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate deliveryDateTo;

}
