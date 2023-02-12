package it.xtreamdev.gflbe.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import it.xtreamdev.gflbe.model.enumerations.ContentStatus;
import lombok.*;
import org.hibernate.annotations.Formula;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "content")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners({AuditingEntityListener.class})
public class Content {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Lob
    @Column(name = "body")
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_status")
    private ContentStatus contentStatus;

    private String wordpressId;

    private String wordpressUrl;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime wordpressPublicationDate;

    @OneToOne
    private ProjectCommission projectCommission;

    @ManyToOne
    private User editor;

    @OneToOne(cascade = CascadeType.ALL)
    private ContentHint hint;

    @Column(length = 1024)
    private String wordpressFeaturedMediaUrl;

    private Integer wordpressFeaturedMediaId;

    @CreatedDate
    @Column(name = "created_date")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdDate;

    @LastModifiedDate
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    @Formula("(SELECT d.id from domain d inner join newspaper n on d.newspaper_id = n.id inner JOIN project_commission pc on pc.newspaper_id = n.id INNER JOIN content c on c.project_commission_id = pc.id where c.id = project_commission_id) is not null")
    private Boolean isDomainContent;

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContentWordpressCategory> wordpressCategories;



}
