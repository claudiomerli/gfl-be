package it.xtreamdev.gflbe.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import it.xtreamdev.gflbe.model.entitylisteners.ContentEntityListener;
import it.xtreamdev.gflbe.model.enumerations.ContentProjectStatus;
import it.xtreamdev.gflbe.model.enumerations.ContentStatus;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "content")
@Entity
@EntityListeners({AuditingEntityListener.class, ContentEntityListener.class})
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id")
    private Integer id;

    @Column(name = "title")
    private String title;

    @Lob
    @Column(name = "body")
    private String body;

    @Column(name = "export_file_name")
    private String exportFileName;

    @Column(name = "score")
    private Integer score;

    @Column(name = "customer_token")
    private String customerToken;

    @Column(name = "customer_notes")
    private String customerNotes;

    @Column(name = "admin_notes")
    private String adminNotes;

    @Enumerated(EnumType.STRING)
    @Column(name = "month_use")
    private Month monthUse;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "delivery_date")
    private LocalDate deliveryDate;

    @CreatedDate
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "created_date")
    private LocalDate createdDate;

    @LastModifiedDate
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "last_modified_date")
    private LocalDate lastModifiedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_status")
    private ContentStatus contentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_project_status")
    private ContentProjectStatus projectStatus;

    @ManyToOne
    @JoinColumn(name = "editor_id")
    private User editor;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "newspaper_id")
    private Newspaper newspaper;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name = "content_rules_id")
    private ContentRules contentRules;

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL)
    private List<ContentLink> links;

    @Transient
    private RuleSatisfation ruleSatisfation;

}
