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
@Table(name = "topic")
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id")
    private Integer id;

    @Column(name = "topic_name")
    private String name;

    @ManyToMany(mappedBy = "topics")
    Set<Newspaper> newspapers;
}
