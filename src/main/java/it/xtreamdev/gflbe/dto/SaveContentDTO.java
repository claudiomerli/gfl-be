package it.xtreamdev.gflbe.dto;

import it.xtreamdev.gflbe.model.Content;
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
    private Integer projectId;
    private Integer newspaperId;


}
