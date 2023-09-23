package it.xtreamdev.gflbe.dto.genericorder;

import it.xtreamdev.gflbe.model.enumerations.GenericOrderLevel;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SaveSecondLevelOrderDTO {

    private String link;
    private GenericOrderLevel level;

}
