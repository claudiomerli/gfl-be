package it.xtreamdev.gflbe.dto.newspaper;

import it.xtreamdev.gflbe.dto.topic.TopicDTO;
import it.xtreamdev.gflbe.model.Project;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewspaperCustomerFilterPopulationDTO {

    private List<LazyProject> projects;
    private Double maxNewspaperCost;
    private Double minNewspaperCost;
    private List<TopicDTO> topics;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LazyProject {
        private Integer id;
        private String name;

    }

}
