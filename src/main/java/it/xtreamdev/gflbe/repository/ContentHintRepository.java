package it.xtreamdev.gflbe.repository;

import it.xtreamdev.gflbe.model.ContentHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ContentHintRepository extends JpaRepository<ContentHint, Integer>, JpaSpecificationExecutor<ContentHint> {
}
