package it.xtreamdev.gflbe.dto;

import it.xtreamdev.gflbe.model.enumerations.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SearchProjectDTO {

    private String globalSearch;
    private ProjectStatus statusSearch;

}
