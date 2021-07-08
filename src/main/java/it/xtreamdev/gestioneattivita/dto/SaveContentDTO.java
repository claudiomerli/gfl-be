package it.xtreamdev.gestioneattivita.dto;

import it.xtreamdev.gestioneattivita.model.Content;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SaveContentDTO {

    private Content content;
    private Integer editorId;
    private Integer customerId;
    private Integer newspaperId;


}
