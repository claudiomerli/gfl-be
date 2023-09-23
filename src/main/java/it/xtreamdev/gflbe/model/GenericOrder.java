package it.xtreamdev.gflbe.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import it.xtreamdev.gflbe.model.enumerations.GenericOrderType;
import it.xtreamdev.gflbe.model.enumerations.GenericOrderLevel;
import it.xtreamdev.gflbe.model.enumerations.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "generic_booking")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "order_type")
@EntityListeners({AuditingEntityListener.class})
public class GenericOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "order_type", insertable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private GenericOrderType type;

    @Column(name = "order_level")
    @Enumerated(EnumType.STRING)
    private GenericOrderLevel level;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;

    @CreatedDate
    @Column(name = "created_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdDate;

    @LastModifiedDate
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

}
