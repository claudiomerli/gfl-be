package it.xtreamdev.gflbe.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import it.xtreamdev.gflbe.model.enumerations.NotificationType;
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
@Table(name = "notification")
@EntityListeners({AuditingEntityListener.class})
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JsonIgnore
    private User user;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column(name = "description")
    private String description;

    @Column(name = "route")
    private String route;

    @Column(name = "dismissed")
    @Builder.Default
    private Boolean dismissed = false;

    @CreatedDate
    @Column(name = "created_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdDate;
}
