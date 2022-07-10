package it.xtreamdev.gflbe.model;


import it.xtreamdev.gflbe.model.enumerations.OrderStatus;
import lombok.*;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "booking")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "note")
    private String note;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;

    @Formula("(select sum(oe.content_number*n.cost_sell) from booking b inner join order_element oe on b.id = oe.order_id inner join newspaper n on n.id = oe.newspaper_id where b.id = id)")
    private Double total;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "order", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<OrderElement> orderElements;

}
