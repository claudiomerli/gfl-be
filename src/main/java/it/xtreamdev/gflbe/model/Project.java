package it.xtreamdev.gflbe.model;

import com.fasterxml.jackson.annotation.*;
import it.xtreamdev.gflbe.dto.project.ProjectListElementDTO;
import it.xtreamdev.gflbe.model.enumerations.ProjectStatus;
import lombok.*;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "project")
@EntityListeners({AuditingEntityListener.class})
@Where(clause = "deleted = false")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;

    @Column(name = "billing_description")
    private String billingDescription;

    @Column(name = "billing_amount")
    private Double billingAmount;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiration;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProjectStatusChange> projectStatusChanges = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ProjectStatus status = ProjectStatus.CREATED;

    @OneToOne(cascade = CascadeType.ALL)
    private ContentHint hint;

    @CreatedDate
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @LastModifiedDate
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    @OneToOne
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("domainId")
    private Domain domain;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProjectCommission> projectCommissions = new ArrayList<>();

    @Formula("domain_id is not null")
    private Boolean isDomainProject;

    @Formula("(select count(*) > 0 from project_commission pc where pc.status = 'STARTED' and pc.project_id = id)")
    private Boolean hasStartedCommission;

    @Formula("(select count(*) > 0 from project_commission pc where pc.status = 'ASSIGNED' and pc.project_id = id)")
    private Boolean hasAssignedCommission;

    @Formula("(select count(*) > 0 from project_commission pc where pc.status = 'WORKED' and pc.project_id = id)")
    private Boolean hasWorkedCommission;

    @Builder.Default
    private Boolean deleted = false;

    @ManyToMany
    private List<User> finalCustomers;

    public ProjectListElementDTO toListElement() {
        return ProjectListElementDTO
                .builder()
                .billingAmount(billingAmount)
                .billingDescription(billingDescription)
                .createdDate(createdDate)
                .customer(customer)
                .expiration(expiration)
                .id(id)
                .lastModifiedDate(lastModifiedDate)
                .hasStartedCommission(hasStartedCommission)
                .hasAssignedCommission(hasAssignedCommission)
                .hasWorkedCommission(hasWorkedCommission)
                .status(status)
                .name(name)
                .projectCommissions(projectCommissions
                        .stream()
                        .map(ProjectCommission::toListElement)
                        .collect(Collectors.toList()))
                .build();
    }

}
