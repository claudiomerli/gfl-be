package it.xtreamdev.gflbe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PageableDTO {

    private int pageNumber;
    private int pageSize;
    private String sortBy;
    private String sortDirection;

}
