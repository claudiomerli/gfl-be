package it.xtreamdev.gestioneattivita.model;

import it.xtreamdev.gestioneattivita.model.entitylisteners.ContentEntityListener;
import it.xtreamdev.gestioneattivita.model.enumerations.ContentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@EntityListeners({AuditingEntityListener.class, ContentEntityListener.class})
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate deliveryDate;

    @CreatedDate
    private LocalDate createdDate;

    @LastModifiedDate
    private LocalDate lastModifiedDate;

    @Enumerated(EnumType.STRING)
    private ContentStatus contentStatus;

    @ManyToOne
    private User editor;

    @ManyToOne
    private Customer customer;

    @ManyToOne
    private Newspaper newspaper;

    @OneToOne
    private ContentRules contentRules;

    @Transient
    private RuleSatisfation ruleSatisfation;

}
