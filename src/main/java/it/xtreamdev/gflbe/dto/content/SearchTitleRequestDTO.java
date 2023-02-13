package it.xtreamdev.gflbe.dto.content;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SearchTitleRequestDTO {

    private String title;
    private Integer projectId;
    private Integer year;
    private String period;
    private Integer editorId;

}
