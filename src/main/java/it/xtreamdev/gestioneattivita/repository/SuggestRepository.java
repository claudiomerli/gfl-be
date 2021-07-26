package it.xtreamdev.gestioneattivita.repository;

import it.xtreamdev.gestioneattivita.model.Suggest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuggestRepository extends JpaRepository<Suggest,Integer> {
}
