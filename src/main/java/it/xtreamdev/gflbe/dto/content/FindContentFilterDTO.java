package it.xtreamdev.gflbe.dto.content;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class FindContentFilterDTO {

    private String contentStatus;
    private String projectId;
    private String newspaperId;

}
