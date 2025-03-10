package it.xtreamdev.gflbe.repository;

import it.xtreamdev.gflbe.model.Newspaper;
import it.xtreamdev.gflbe.model.NewspaperDiscount;
import it.xtreamdev.gflbe.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NewspaperDiscountRepository extends JpaRepository<NewspaperDiscount, Integer>, JpaSpecificationExecutor<NewspaperDiscount> {

    @Query("from NewspaperDiscount where customer = :user and (newspaper = :newspaper or allNewspaper is true)")
    Optional<NewspaperDiscount> findByCustomerAndNewspaper(
            @Param("user") User user,
            @Param("newspaper") Newspaper newspaper
    );

}
