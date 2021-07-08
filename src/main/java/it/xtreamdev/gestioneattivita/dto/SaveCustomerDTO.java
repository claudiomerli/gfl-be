package it.xtreamdev.gestioneattivita.dto;

import it.xtreamdev.gestioneattivita.model.ContentRules;
import it.xtreamdev.gestioneattivita.model.Customer;
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
