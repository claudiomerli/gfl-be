package it.xtreamdev.gestioneattivita.repository;

import it.xtreamdev.gestioneattivita.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

}
