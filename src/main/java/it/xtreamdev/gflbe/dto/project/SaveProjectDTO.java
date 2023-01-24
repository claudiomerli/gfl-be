package it.xtreamdev.gflbe.dto.project;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SaveProjectDTO {
    private String name;
    private Integer customerId;

    private Double billingAmount;
    private String billingDescription;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiration;
    private String hintBody;
}
