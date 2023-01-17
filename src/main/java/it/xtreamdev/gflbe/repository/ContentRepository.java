package it.xtreamdev.gflbe.repository;

import it.xtreamdev.gflbe.model.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ContentRepository extends JpaRepository<Content, Integer>, JpaSpecificationExecutor<Content> {

}
