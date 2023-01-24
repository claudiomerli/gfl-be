package it.xtreamdev.gflbe.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveOrderPackDTO {

    private String name;
    private String description;
    private List<SaveOrderPackElementDTO> elements;
    private Double price;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SaveOrderPackElementDTO {
        private Integer newspaperId;
        private Integer contentNumber;
    }
}
