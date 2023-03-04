package it.xtreamdev.gflbe.dto.purchasecontent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindContentPurchaseDTO {

    private Integer newspaperId;
    private String globalSearch;

}
