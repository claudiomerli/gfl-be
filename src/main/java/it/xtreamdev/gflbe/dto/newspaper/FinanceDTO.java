package it.xtreamdev.gflbe.dto.newspaper;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FinanceDTO {

    private Double purchasesValue;

    private Double salesValue;

    private Double amountRemaining;

}
