package it.xtreamdev.gflbe.dto.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SearchProjectDTO {

    private String globalSearch;

    private String status;

    private List<String> projectCommissionStatus;

    private Integer commissionYear;

    private String commissionPeriod;

    @Builder.Default
    private List<Integer> newspapers = new ArrayList<>();

}
