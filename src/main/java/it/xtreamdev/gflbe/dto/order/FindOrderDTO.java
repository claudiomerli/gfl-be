package it.xtreamdev.gflbe.dto.order;

import it.xtreamdev.gflbe.model.enumerations.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FindOrderDTO {

    private String name;

    private Integer customerId;

    private String status;

    private String excludeOrderPack;

    @Builder.Default
    private List<Integer> newspaperIds = new ArrayList<>();

}
