package it.xtreamdev.gflbe.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GenerateOrderFromOrderPackDTO {

    private String name;
    private Integer idOrderPack;

}
