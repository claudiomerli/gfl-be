package it.xtreamdev.gestioneattivita.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import it.xtreamdev.gestioneattivita.model.Content;
import it.xtreamdev.gestioneattivita.model.Customer;
import it.xtreamdev.gestioneattivita.model.Newspaper;
import it.xtreamdev.gestioneattivita.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ResponseContentDTO {

    private List<Customer> customers;
    private List<User> editors;
    private List<Newspaper> newspapers;
    private Page<Content> contents;
    private Customer selectedCustomer;
    private Integer editorId;
    private Integer customerId;
    private Integer newspaperId;
    private SaveContentDTO saveContent;

}
