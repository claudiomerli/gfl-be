package it.xtreamdev.gflbe.repository;

import it.xtreamdev.gflbe.dto.newspaper.FinanceDTO;
import it.xtreamdev.gflbe.dto.newspaper.MaxMinRangeNewspaperAttributesDTO;
import it.xtreamdev.gflbe.model.Newspaper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface NewspaperRepository extends JpaRepository<Newspaper, Integer>, JpaSpecificationExecutor<Newspaper> {

    @Query(value = "select sum(cp.amount) from ContentPurchase cp")
    Double totalCost();

    @Query(value = "select sum(o.total) from Order o where o.status = 'CONFIRMED'")
    Double totalSell();


    @Query(value = "select new it.xtreamdev.gflbe.dto.newspaper.MaxMinRangeNewspaperAttributesDTO(" +
            "min (n.za)," +
            "max (n.za)," +
            "min (n.leftContent)," +
            "max (n.leftContent)," +
            "min (n.costEach)," +
            "max (n.costEach)," +
            "min (n.costSell)," +
            "max (n.costSell)" +
            ") from Newspaper n")
    MaxMinRangeNewspaperAttributesDTO getMaxMinRangeNewspaperAttributes();


}
