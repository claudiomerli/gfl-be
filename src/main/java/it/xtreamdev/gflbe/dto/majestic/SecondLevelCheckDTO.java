package it.xtreamdev.gflbe.dto.majestic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecondLevelCheckDTO {

    private String link;
    private Integer trustFlow;

}
