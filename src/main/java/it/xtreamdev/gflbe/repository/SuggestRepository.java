package it.xtreamdev.gflbe.repository;

import it.xtreamdev.gflbe.model.Suggest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuggestRepository extends JpaRepository<Suggest,Integer> {
}
