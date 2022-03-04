package it.xtreamdev.gflbe.model;

import it.xtreamdev.gflbe.dto.enumerations.NewspaperTopic;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

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

    @Column(name = "note")
    private String note;

    @Column(name = "ip")
    private String ip;

    @Column(name = "za")
    private Integer za;

    @ManyToMany
    @JoinTable(
            name = "newspaper_topics",
            joinColumns = @JoinColumn(name = "newspaper_id"),
            inverseJoinColumns = @JoinColumn(name = "topic_id"))
    Set<Topic> topics;

}
