package it.xtreamdev.gflbe.repository;

import it.xtreamdev.gflbe.model.Domain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DomainRepository extends JpaRepository<Domain, Integer>, JpaSpecificationExecutor<Domain> {
}
