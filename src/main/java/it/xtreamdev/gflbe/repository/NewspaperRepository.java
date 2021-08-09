package it.xtreamdev.gflbe.repository;

import it.xtreamdev.gflbe.model.Newspaper;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewspaperRepository extends JpaRepository<Newspaper, Integer> {
}
