package it.xtreamdev.gflbe.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import it.xtreamdev.gflbe.model.enumerations.OrderStatus;
import lombok.*;
import org.hibernate.annotations.Formula;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "booking")
@EntityListeners({AuditingEntityListener.class})
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "note")
    private String note;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;

    @ManyToOne
    @JoinColumn(name = "order_pack_id")
    private OrderPack orderPack;

    @Column(name = "order_pack_price")
    private Double orderPackPrice;

    @Formula("(select IF(order_pack_price is null, (select sum(oe.content_number*n.cost_sell) from booking b inner join order_element oe on b.id = oe.order_id inner join newspaper n on n.id = oe.newspaper_id where b.id = id), order_pack_price))")
    private Double total;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "order", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    private List<OrderElement> orderElements;


    @CreatedDate
    @Column(name = "created_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdDate;

    @LastModifiedDate
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

}
