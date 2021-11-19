package it.xtreamdev.gflbe.dto.newspaper;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchNewspaperDTO {

    private Integer newspaperId;
    private Integer topicId;
    private Double maxBudget;
}
