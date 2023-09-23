package it.xtreamdev.gflbe.dto.genericorder;

import it.xtreamdev.gflbe.dto.common.MapEntry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SaveVideoOrderDTO {

    private Integer templateId;
    @Builder.Default
    private List<MapEntry> fields = new ArrayList<>();

}
