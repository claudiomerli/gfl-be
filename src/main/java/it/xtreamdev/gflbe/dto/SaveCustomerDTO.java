package it.xtreamdev.gflbe.dto;

import it.xtreamdev.gflbe.model.ContentRules;
import it.xtreamdev.gflbe.model.Customer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveCustomerDTO {

    private Customer customer;
    private ContentRules contentRules;

}
