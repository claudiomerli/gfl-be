package it.xtreamdev.gflbe.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "content_hint_template")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentHintTemplate {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String body;

}
