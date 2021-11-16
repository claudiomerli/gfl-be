package it.xtreamdev.gflbe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PageDTO<T> {
    private Set<T> content;
    private PageableDTO pageable;
    private long totalElements;


}
