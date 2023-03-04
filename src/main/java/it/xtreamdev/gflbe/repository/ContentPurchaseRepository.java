package it.xtreamdev.gflbe.repository;

import it.xtreamdev.gflbe.model.ContentPurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ContentPurchaseRepository extends JpaRepository<ContentPurchase, Integer>, JpaSpecificationExecutor<ContentPurchase> {
}
