package it.xtreamdev.gflbe.model;

import it.xtreamdev.gflbe.dto.enumerations.NewspaperTopic;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Newspaper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    private String name;

    private Integer purchasedContent;

    private Double costEach;

    private String email;

    private String regionalGeolocalization;

    @Enumerated(EnumType.STRING)
    private NewspaperTopic topic;

}
