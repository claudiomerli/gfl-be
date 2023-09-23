package it.xtreamdev.gflbe.dto.videotemplate;

import it.xtreamdev.gflbe.dto.common.MapEntry;
import it.xtreamdev.gflbe.model.enumerations.VideoTemplateType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveVideoTemplate {

    private String name;
    private String url;
    private VideoTemplateType type;
    @Builder.Default
    private List<MapEntry> fields = new ArrayList<>();

}
