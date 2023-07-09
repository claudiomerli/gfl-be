package it.xtreamdev.gflbe.dto.news;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindNewsDTO {

    private String globalSearch;
    private Integer exclude;

}
