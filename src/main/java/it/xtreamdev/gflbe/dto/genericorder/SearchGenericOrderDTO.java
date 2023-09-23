package it.xtreamdev.gflbe.dto.genericorder;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
public class SearchGenericOrderDTO {

    private Integer customerId;
    private String orderType;
    private String orderStatus;
    private String orderLevel;


}
