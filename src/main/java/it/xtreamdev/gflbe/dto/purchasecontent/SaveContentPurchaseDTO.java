package it.xtreamdev.gflbe.dto.purchasecontent;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SaveContentPurchaseDTO {


    private Double amount;
    private Integer contentNumber;
    private String note;
    private List<Integer> newspapers;

}
