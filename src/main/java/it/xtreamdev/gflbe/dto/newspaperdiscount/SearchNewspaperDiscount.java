package it.xtreamdev.gflbe.dto.newspaperdiscount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchNewspaperDiscount {

    private Integer customerId;
    private Integer newspaperId;

}
