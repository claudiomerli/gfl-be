package it.xtreamdev.gflbe.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveCustomerCompetitorsDTO {

    private String competitor1;

    private String competitor2;

}
