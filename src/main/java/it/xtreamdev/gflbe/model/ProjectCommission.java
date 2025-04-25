package it.xtreamdev.gflbe.model;

import com.fasterxml.jackson.annotation.*;
import it.xtreamdev.gflbe.dto.project.ProjectListElementDTO;
import it.xtreamdev.gflbe.model.enumerations.ContentType;
import it.xtreamdev.gflbe.model.enumerations.ProjectCommissionStatus;
import lombok.*;
import org.hibernate.annotations.Formula;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
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

    @Enumerated
    private Month period;

    private Integer year;

    @Formula("(year * 100) + period")
    private Integer periodOrder;

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

    @ManyToOne
    @JsonIgnore
    private ContentPurchase contentPurchase;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ProjectCommissionStatus status = ProjectCommissionStatus.CREATED;

    @Formula(
            "CASE status " +
                    "WHEN 'CREATED' THEN 10 " +
                    "WHEN 'STARTED' THEN 20 " +
                    "WHEN 'ASSIGNED' THEN 30 " +
                    "WHEN 'STANDBY_EDITORIAL' THEN 40 " +
                    "WHEN 'WORKED' THEN 50 " +
                    "WHEN 'TO_PUBLISH' THEN 60 " +
                    "WHEN 'SENT_TO_NEWSPAPER' THEN 70 " +
                    "WHEN 'STANDBY_PUBLICATION' THEN 80 " +
                    "WHEN 'SENT_TO_ADMINISTRATION' THEN 90 " +
                    "WHEN 'PUBLISHED_INTERNAL_NETWORK' THEN 100 " +
                    "END"
    )
    private Integer statusOrder;

    @OneToOne(mappedBy = "projectCommission", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("contentId")
    private Content content;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Formula("(select c.assign_date from content c where c.project_commission_id = id)")
    private LocalDateTime contentAssignDate;

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

    @Column(name = "cost_sell")
    private Double costSell;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deliveryDate;

    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    @Lob
    private String publicationWorkNotes;

    @Lob
    private String contentWorkNotes;

    public ProjectListElementDTO.ProjectCommissionListElementDTO toListElement() {
        return ProjectListElementDTO.ProjectCommissionListElementDTO
                .builder()
                .status(status)
                .build();
    }
}
