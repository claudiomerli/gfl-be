package it.xtreamdev.gflbe.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "newspaper_discount")
public class NewspaperDiscount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer discountPercentage;

    @ManyToOne
    private Newspaper newspaper;

    private Boolean allNewspaper;

    @ManyToOne
    private User customer;

}
