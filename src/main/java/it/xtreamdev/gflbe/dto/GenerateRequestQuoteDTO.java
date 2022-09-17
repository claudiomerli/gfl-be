package it.xtreamdev.gflbe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GenerateRequestQuoteDTO {
    private String header;
    private String signature;
    private List<PriceReplacementDTO> priceReplacements;
    private Integer orderId;
    private Integer requestQuoteId;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class PriceReplacementDTO {
        private Integer newspaperId;
        private Double priceReplacement;
    }
}