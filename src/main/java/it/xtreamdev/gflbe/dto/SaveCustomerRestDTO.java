package it.xtreamdev.gflbe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveCustomerRestDTO {
    private String name;

    private String username;
    private String password;
    private String email;
    private String mobile;

    private SaveContentRulesDTO contentRules;
}
