package it.xtreamdev.gflbe.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "order_pack")
public class OrderPack {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "orderPack", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<OrderElement> orderElements;

    @Column(name = "price")
    private Double price;

    @Builder.Default
    private Boolean deleted = false;

}
