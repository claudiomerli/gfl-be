package it.xtreamdev.gflbe.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import it.xtreamdev.gflbe.model.enumerations.ProjectCommissionStatus;
import it.xtreamdev.gflbe.model.enumerations.ProjectStatus;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "project_status_change")
@EntityListeners({AuditingEntityListener.class})
public class ProjectStatusChange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Enumerated(EnumType.STRING)
    private ProjectStatus projectStatus;

    @Enumerated(EnumType.STRING)
    private ProjectCommissionStatus projectCommissionStatus;

    @CreatedDate
    @Column(name = "created_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdDate;

    @ManyToOne
    @JoinColumn(name = "project_id")
    @JsonIgnore
    private Project project;

    @ManyToOne
    @JoinColumn(name = "project_commission_id")
    @JsonIgnore
    private ProjectCommission projectCommission;

}
