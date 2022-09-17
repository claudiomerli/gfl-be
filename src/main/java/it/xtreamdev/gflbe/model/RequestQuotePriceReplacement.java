package it.xtreamdev.gflbe.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "request_quote_price_replacement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestQuotePriceReplacement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "price_replacement")
    private Double priceReplacement;

    @ManyToOne
    @JoinColumn(name = "newspaper_id")
    private Newspaper newspaper;

    @ManyToOne
    @JoinColumn(name = "request_quote_id")
    @JsonIgnore
    private RequestQuote requestQuote;

}
