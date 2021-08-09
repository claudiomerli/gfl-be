package it.xtreamdev.gflbe.repository;

import it.xtreamdev.gflbe.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

}
