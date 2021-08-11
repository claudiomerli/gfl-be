package it.xtreamdev.gflbe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveCustomerRestDTO {
    private String name;
    private Integer contentRulesId;
}
