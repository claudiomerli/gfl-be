package it.xtreamdev.gflbe.dto.newspaper;

import it.xtreamdev.gflbe.dto.topic.TopicDTO;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewspaperDTO {

    private Integer id;
    private String name;
    private Integer leftContent;
    private Double costEach;
    private Double costSell;
    private String email;
    private String regionalGeolocalization;
    private String note;
    private Integer za;
    private String ip;
    private Set<TopicDTO> topics;
    private Boolean hidden;
    private Boolean sensitiveTopics;
    private Boolean nofollow;
    private Boolean warning;
    private String description;


}
