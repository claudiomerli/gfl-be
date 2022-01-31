package it.xtreamdev.gflbe.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "content_rules")
public class ContentRules {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id")
    private Integer id;

    @Column(name = "title")
    private String title;

    @Column(name = "link_url")
    private String linkUrl;

    @Column(name = "link_text")
    private String linkText;

    @Column(name = "body")
    private String body;

    @Column(name = "max_character_body_length")
    private Integer maxCharacterBodyLength;

    @OneToOne(cascade = CascadeType.PERSIST)
    private Attachment attachment;

    @OneToMany(mappedBy = "contentRules", cascade = CascadeType.ALL)
    private List<ContentLink> links;

}
