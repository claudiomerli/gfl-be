package it.xtreamdev.gflbe.repository;

import it.xtreamdev.gflbe.model.Newspaper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface NewspaperRepository extends JpaRepository<Newspaper, Integer>, JpaSpecificationExecutor<Newspaper> {
}
