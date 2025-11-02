package it.xtreamdev.gflbe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommissionDashboardSearchRequest {

    private Integer projectId;
    private Integer newspaperId;
    private Integer customerId;
    private LocalDate deliveryDateFrom;
    private LocalDate deliveryDateTo;
    @Builder.Default
    private Boolean includeArchived = false;

}
