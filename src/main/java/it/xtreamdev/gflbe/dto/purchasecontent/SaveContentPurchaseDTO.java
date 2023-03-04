package it.xtreamdev.gflbe.dto.purchasecontent;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContentPurchaseSaveDTO {


    private Double amount;
    private Integer contentNumber;
    private List<Integer> newspaperId;

}
