package it.xtreamdev.gflbe.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
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

    @Formula("(select count(*) from project_commission pc where pc.newspaper_id = id and pc.status = 'SENT_TO_ADMINISTRATION')")
    private Integer soldContent;

    @Formula("purchased_content - (select count(*) from project_commission pc where pc.newspaper_id = id and pc.status = 'SENT_TO_ADMINISTRATION')")
    private Integer leftContent;

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

    @Column(name = "hidden")
    private Boolean hidden;

    @Column(name = "sensitive_topics")
    private Boolean sensitiveTopics;

    @OneToOne(mappedBy = "newspaper")
    @JsonIgnore
    private Domain domain;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "newspaper_topics",
            joinColumns = @JoinColumn(name = "newspaper_id"),
            inverseJoinColumns = @JoinColumn(name = "topic_id"))
    Set<Topic> topics;

    @OneToMany(mappedBy = "newspaper")
    @JsonIgnore
    Set<ProjectCommission> projectCommissions;

}
