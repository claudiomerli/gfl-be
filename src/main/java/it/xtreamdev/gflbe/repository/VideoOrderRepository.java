package it.xtreamdev.gflbe.repository;

import it.xtreamdev.gflbe.model.VideoOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface VideoOrderRepository extends JpaRepository<VideoOrder, Integer>, JpaSpecificationExecutor<VideoOrder> {
}
