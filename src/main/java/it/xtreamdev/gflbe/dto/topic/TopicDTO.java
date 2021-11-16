package it.xtreamdev.gflbe.dto.topic;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopicDTO {

    private Integer id;
    private String name;
    private boolean selected; // Server al FE

}
