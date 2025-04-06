package it.xtreamdev.gflbe.repository;

import it.xtreamdev.gflbe.dto.project.CustomerMonitorListDTO;
import it.xtreamdev.gflbe.model.CustomerMonitor;
import it.xtreamdev.gflbe.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomerMonitorRepository extends JpaRepository<CustomerMonitor, Integer>, JpaSpecificationExecutor<CustomerMonitor> {

    @Query("select cm.customer, count(*) as monitors from CustomerMonitor cm " +
            "where :userId is null or cm.customer.id = :userId " +
            "group by cm.customer")
    Page<CustomerMonitorListDTO> findCustomerMonitorList(@Param("userId") Integer userId,Pageable pageable);

    List<CustomerMonitor> findCustomerMonitorByCustomer(User customer);

}
