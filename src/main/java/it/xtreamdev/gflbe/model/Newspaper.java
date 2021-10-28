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
@Table(name = "newspaper")
public class Newspaper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "purchased_content")
    private Integer purchasedContent;

    @Column(name = "cost_each")
    private Double costEach;

    @Column(name = "cost_sell")
    private Double costSell;

    @Column(name = "email")
    private String email;

    @Column(name = "regional_geolocalization")
    private String regionalGeolocalization;

    @Enumerated(EnumType.STRING)
    @Column(name = "topic")
    private NewspaperTopic topic;

}
