package it.xtreamdev.gflbe.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class ContentRules {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    private String title;
    private String linkUrl;
    private String linkText;
    private String body;
    private Integer maxCharacterBodyLength;

    @OneToOne(cascade = CascadeType.PERSIST)
    private Attachment attachment;

}
