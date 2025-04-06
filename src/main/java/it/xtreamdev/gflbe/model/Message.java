package it.xtreamdev.gflbe.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import it.xtreamdev.gflbe.model.enumerations.RoleName;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    private RoleName sourceRole;

    @OneToOne
    private User sourceUser;

    @Enumerated(EnumType.STRING)
    private RoleName targetRole;

    @OneToOne
    private User targetUser;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime timestamp;

    @Column(name = "visualized")
    private boolean read;

    private String topicId;

    private String topicType;
}
