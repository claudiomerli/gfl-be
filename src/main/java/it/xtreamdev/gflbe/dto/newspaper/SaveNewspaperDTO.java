package it.xtreamdev.gflbe.dto.newspaper;

import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveNewspaperDTO {

    private String name;
    private Double costEach;
    private Double costSell;
    private String email;
    private String regionalGeolocalization;
    private List<Integer> topics;
    private String note;
    private Integer za;
    private String ip;
    private Boolean hidden;
    private Boolean sensitiveTopics;
    private Boolean warning;
}
