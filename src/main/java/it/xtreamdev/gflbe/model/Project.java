package it.xtreamdev.gflbe.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import it.xtreamdev.gflbe.dto.project.ProjectListElementDTO;
import it.xtreamdev.gflbe.model.enumerations.ProjectStatus;
import lombok.*;
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
@Table(name = "project")
@EntityListeners({AuditingEntityListener.class})
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

    private LocalDate expiration;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProjectStatusChange> projectStatusChanges = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ProjectStatus status = ProjectStatus.CREATED;

    @CreatedDate
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @LastModifiedDate
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectCommission> projectCommissions;

    public ProjectListElementDTO toListElement(){
        return ProjectListElementDTO
                .builder()
                .billingAmount(billingAmount)
                .billingDescription(billingDescription)
                .createdDate(createdDate)
                .customer(customer)
                .expiration(expiration)
                .id(id)
                .lastModifiedDate(lastModifiedDate)
                .status(status)
                .name(name)
                .build();
    }

}
