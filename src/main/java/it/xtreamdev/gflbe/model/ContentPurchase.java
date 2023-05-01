package it.xtreamdev.gflbe.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Formula;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "content_purchase")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners({AuditingEntityListener.class})
public class ContentPurchase {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Double amount;

    @Formula("(amount / content_number)*(content_number - (select count(*) from project_commission pc where pc.content_purchase_id = id))")
    private Double amountRemaining;

    private Integer contentNumber;

    @Formula("(select count(*) from project_commission pc where pc.content_purchase_id = id)")
    private Integer contentUsed;

    @Formula("content_number - (select count(*) from project_commission pc where pc.content_purchase_id = id)")
    private Integer contentRemaining;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiration;

    @Lob
    private String note;

    @OneToMany(mappedBy = "contentPurchase")
    @JsonIgnore
    private List<ProjectCommission> projectCommissions;

    @ManyToMany
    @JoinTable(name = "content_purchase_newspaper")
    private List<Newspaper> newspapers;

    @CreatedDate
    @Column(name = "created_date")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdDate;

    @LastModifiedDate
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;
}
