package it.xtreamdev.gflbe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FindOrderPackDTO {

    private String globalSearch;

    @Builder.Default
    private List<Integer> newspaperIds = new ArrayList<>();

}
