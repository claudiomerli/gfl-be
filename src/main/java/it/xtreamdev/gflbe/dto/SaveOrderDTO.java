package it.xtreamdev.gflbe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveOrderDTO {

    private String name;
    private String note;
    private List<SaveOrderElementDTO> elements;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SaveOrderElementDTO {
        private Integer newspaperId;
        private Integer contentNumber;
    }
}
