package it.xtreamdev.gestioneattivita.repository;

import it.xtreamdev.gestioneattivita.model.Newspaper;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewspaperRepository extends JpaRepository<Newspaper, Integer> {
}
