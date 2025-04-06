package it.xtreamdev.gflbe.dto.project;

import it.xtreamdev.gflbe.model.enumerations.CurrentlyMonthCustomerMonitorStatus;
import it.xtreamdev.gflbe.model.enumerations.CustomerMonitorStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveCustomerMonitorDTO {

    private Integer id;
    private Integer customerId;
    private Integer projectId;
    private CustomerMonitorStatus status;
    private CurrentlyMonthCustomerMonitorStatus currentlyMonthStatus;
    private String lastWork;

}
