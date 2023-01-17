package it.xtreamdev.gflbe.model;

import com.fasterxml.jackson.annotation.*;
import it.xtreamdev.gflbe.model.enumerations.ProjectCommissionStatus;
import lombok.*;
import org.hibernate.annotations.Formula;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "project_commission")
@EntityListeners({AuditingEntityListener.class})
public class ProjectCommission {

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

    @Formula("(select p.name from project p  inner join project_commission pc on pc.project_id = p.id where pc.id = id)")
    private String projectName;

    private String period;

    private Integer year;

    private String anchor;

    private Boolean isAnchorBold;

    private Boolean isAnchorItalic;

    private String url;

    private String title;

    @Lob
    private String notes;


    @Column(name = "publication_url")
    private String publicationUrl;

    @Column(name = "publication_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate publicationDate;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ProjectCommissionStatus status = ProjectCommissionStatus.CREATED;

    @OneToOne(mappedBy = "projectCommission", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("contentId")
    private Content content;

    @OneToMany(mappedBy = "projectCommission", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProjectStatusChange> projectStatusChanges = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime lastModifiedDate;

}
