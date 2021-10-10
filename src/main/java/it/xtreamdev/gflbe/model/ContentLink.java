package it.xtreamdev.gflbe.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "content_link")
public class ContentLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id")
    private Integer id;

    @Column(name = "link_url")
    private String linkUrl;

    @Column(name = "link_text")
    private String linkText;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "content_id")
    private Content content;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "content_rule_id")
    private ContentRules contentRules;

}
