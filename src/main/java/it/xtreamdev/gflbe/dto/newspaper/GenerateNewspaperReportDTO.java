package it.xtreamdev.gflbe.dto.newspaper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenerateNewspaperReportDTO {

    private Integer id;
    private Double costSell;

}
