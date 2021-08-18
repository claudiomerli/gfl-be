package it.xtreamdev.gflbe.dto.newspaper;

import it.xtreamdev.gflbe.dto.enumerations.NewspaperTopic;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveNewspaperDTO {

    private String name;

    private Integer purchasedContent;

    private Double costEach;

    private String email;

    private String regionalGeolocalization;

    private NewspaperTopic topic;

}
