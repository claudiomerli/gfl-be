package it.xtreamdev.gflbe.dto.project;

import it.xtreamdev.gflbe.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerMonitorListDTO {

    private User customer;
    private Integer monitors;

}
