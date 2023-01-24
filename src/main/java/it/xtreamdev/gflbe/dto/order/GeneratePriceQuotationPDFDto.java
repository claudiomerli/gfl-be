package it.xtreamdev.gflbe.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GeneratePriceQuotationPDFDto {

    private List<PriceQuotationRow> rows;
    private OtherInformation otherInformation;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class OtherInformation {
        private String activity;
        private String customerName;
        private String header;
        private String signature;
        private Boolean tax;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class PriceQuotationRow {
        private Integer id;
        private Double costEach;
        private Double costSell;
        private Double earn;
        private Double expense;
        private String nameNewspaper;
        private Double numberOfEditors;
        private Double revenue;
    }
}


