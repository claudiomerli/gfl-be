package it.xtreamdev.gflbe.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Table(name = "customer_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners({AuditingEntityListener.class})
public class CustomerInfo {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JsonIgnore
    private User customer;

    private String companyName;
    private String url;
    private String companyDimension;
    private String businessArea;
    private String address;

    private String principalDomain;
    private String competitor1;
    private String competitor2;

}
