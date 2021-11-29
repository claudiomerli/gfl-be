package it.xtreamdev.gflbe.model;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

import javax.persistence.*;
import java.time.Month;

@Entity
@Table(name = "project_content_preview")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectContentPreview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "newspaper_id")
    private Newspaper newspaper;

    @ManyToOne
    @JoinColumn(name = "project_id")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("projectId")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "content_id")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("contentId")
    private Content content;

    @Enumerated(EnumType.STRING)
    @Column(name = "month_use")
    private Month monthUse;

    @Column(name = "link_url")
    private String linkUrl;

    @Column(name = "link_text")
    private String linkText;

    @Column(name = "title")
    private String title;

    @Column(name = "customer_notes")
    private String customerNotes;
}
