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

    //Escludo quelli dei pacchetti
    @Formula("(select IFNULL((select sum(oe.content_number) from order_element oe inner join booking o on oe.order_id = o.id where o.order_pack_price is null and oe.newspaper_id = id and o.status = 'CONFIRMED'), 0))")
    private Integer soldContent;

    @Formula("purchased_content - (select IFNULL((select sum(oe.content_number) from order_element oe inner join booking o on oe.order_id = o.id where oe.newspaper_id = id and o.status = 'CONFIRMED'), 0))")
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
