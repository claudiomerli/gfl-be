package it.xtreamdev.gflbe.dto.content;


import it.xtreamdev.gflbe.model.Project;
import it.xtreamdev.gflbe.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class TitleResponseDTO {

    private String title;
    private Project project;
    private String period;
    private Integer year;
    private User editor;

}
