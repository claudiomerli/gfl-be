package it.xtreamdev.gflbe.dto.newspaperdiscount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveNewspaperDiscount {

    private Integer discountPercentage;
    private Integer customerId;
    private Integer newspaperId;
    private Boolean allNewspaper;

}
