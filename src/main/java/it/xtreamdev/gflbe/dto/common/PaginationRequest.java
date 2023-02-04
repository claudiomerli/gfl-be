package it.xtreamdev.gflbe.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestParam;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginationRequest {

    @Builder.Default
    private Integer page = 0;

    @Builder.Default
    private Integer pageSize = 10;

    @Builder.Default
    private String sortBy = "id";

    @Builder.Default
    private String sortDirection = "ASC";

    public PageRequest getPageRequest() {
        return PageRequest.of(page, pageSize, Sort.Direction.fromString(sortDirection), sortBy);
    }

}
