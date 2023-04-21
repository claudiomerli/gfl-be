package it.xtreamdev.gflbe.dto.purchasecontent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindContentPurchaseDTO {

    private Integer newspaperId;
    private String globalSearch;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expirationFrom;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expirationTo;

}
