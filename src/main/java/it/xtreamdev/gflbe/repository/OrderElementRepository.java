package it.xtreamdev.gflbe.repository;

import it.xtreamdev.gflbe.model.Order;
import it.xtreamdev.gflbe.model.OrderElement;
import it.xtreamdev.gflbe.model.OrderPack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderElementRepository extends JpaRepository<OrderElement, Integer>, JpaSpecificationExecutor<OrderElement> {

    void deleteByOrder(Order order);
    void deleteByOrderPack(OrderPack orderPack);

}
