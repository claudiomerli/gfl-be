package it.xtreamdev.gflbe.repository;

import it.xtreamdev.gflbe.model.GenericOrder;
import it.xtreamdev.gflbe.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

public interface GenericOrderRepository extends JpaRepository<GenericOrder, Integer>, JpaSpecificationExecutor<GenericOrder> {


    @Transactional
    @Modifying
    @Query("update GenericOrder set status = 'CONFIRMED' where id = :id")
    void confirmOrder(@Param("id") Integer id);

    @Transactional
    @Modifying
    @Query("update GenericOrder set status = 'CANCELED' where id = :id")
    void refuseOrder(@Param("id") Integer id);
}
