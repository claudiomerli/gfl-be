package it.xtreamdev.gflbe.repository;

import it.xtreamdev.gflbe.model.Newspaper;
import it.xtreamdev.gflbe.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<Order, Integer>, JpaSpecificationExecutor<Order> {


    @Query("select sum(o.orderPackPrice) from Order o where o.status = 'CONFIRMED' and o.orderPackPrice is not null")
    Double totalSalesOrderPack();
}
