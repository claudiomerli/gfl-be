package it.xtreamdev.gflbe.model;


import it.xtreamdev.gflbe.model.enumerations.CurrentlyMonthCustomerMonitorStatus;
import it.xtreamdev.gflbe.model.enumerations.CustomerMonitorStatus;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerMonitor {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    private User customer;

    @OneToOne
    private Project project;

    @Enumerated(EnumType.STRING)
    private CustomerMonitorStatus status;

    @Enumerated(EnumType.STRING)
    private CurrentlyMonthCustomerMonitorStatus currentlyMonthStatus;

    private String lastWork;

}
