package it.xtreamdev.gflbe.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import it.xtreamdev.gflbe.model.entitylisteners.ContentEntityListener;
import it.xtreamdev.gflbe.model.enumerations.ContentStatus;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.Month;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners({AuditingEntityListener.class, ContentEntityListener.class})
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    private String title;
    private String linkUrl;
    private String linkText;

    @Lob
    private String body;

    private String exportFileName;

    private Integer score;

    private String customerToken;

    private String customerNotes;

    private String adminNotes;

    @Enumerated(EnumType.STRING)
    private Month monthUse;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deliveryDate;

    @CreatedDate
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdDate;

    @LastModifiedDate
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastModifiedDate;

    @Enumerated(EnumType.STRING)
    private ContentStatus contentStatus;

    @ManyToOne
    private User editor;

    @ManyToOne
    private Project project;

    @ManyToOne
    private Customer customer;

    @ManyToOne
    private Newspaper newspaper;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private ContentRules contentRules;

    @Transient
    private RuleSatisfation ruleSatisfation;

}
