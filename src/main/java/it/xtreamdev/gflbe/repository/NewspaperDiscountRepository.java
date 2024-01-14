package it.xtreamdev.gflbe.repository;

import it.xtreamdev.gflbe.model.Newspaper;
import it.xtreamdev.gflbe.model.NewspaperDiscount;
import it.xtreamdev.gflbe.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface NewspaperDiscountRepository extends JpaRepository<NewspaperDiscount, Integer>, JpaSpecificationExecutor<NewspaperDiscount> {

    boolean existsByCustomerAndNewspaper(User customer, Newspaper newspaper);

}
