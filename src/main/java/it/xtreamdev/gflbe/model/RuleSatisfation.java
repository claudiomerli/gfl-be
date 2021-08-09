package it.xtreamdev.gflbe.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RuleSatisfation {

    private Boolean characterSatisfied;
    private String characterError;

    private Boolean expirationDateOneDaySatisfied;
    private String expirationDateOneDayError;

    private Boolean expirationDatePlusThenOneDaySatisfied;
    private String expirationDatePlusThenOneDayError;

    private Boolean titleSatisfied;
    private String titleError;

    private Boolean linkTextSatisfied;
    private String linkTextError;

    private Boolean linkUrlSatisfied;
    private String linkUrlError;

}
