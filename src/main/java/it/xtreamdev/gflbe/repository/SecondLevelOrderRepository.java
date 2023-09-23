
package it.xtreamdev.gflbe.repository;

import it.xtreamdev.gflbe.model.GenericOrder;
import it.xtreamdev.gflbe.model.Order;
import it.xtreamdev.gflbe.model.SecondLevelOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

public interface SecondLevelOrderRepository extends JpaRepository<SecondLevelOrder, Integer>, JpaSpecificationExecutor<SecondLevelOrder> {

}
