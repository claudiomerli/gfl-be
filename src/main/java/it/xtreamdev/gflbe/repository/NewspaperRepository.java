package it.xtreamdev.gflbe.repository;

import it.xtreamdev.gflbe.dto.FinanceDTO;
import it.xtreamdev.gflbe.dto.MaxMinRangeNewspaperAttributesDTO;
import it.xtreamdev.gflbe.model.Newspaper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface NewspaperRepository extends JpaRepository<Newspaper, Integer>, JpaSpecificationExecutor<Newspaper> {

    @SuppressWarnings("ALL")
    //Aggiungo alla somma dei sold content i prezzi dei pacchetti venduti
    @Query(value = "select " +
            "new it.xtreamdev.gflbe.dto.FinanceDTO(" +
            "sum(costEach*purchasedContent)," +
            "sum(costSell*soldContent) + (select COALESCE(sum(o.orderPackPrice),0) from Order o where o.status = 'CONFIRMED' and o.orderPackPrice is not null)) " +
            "from Newspaper")
    FinanceDTO finance();

    @Query(value = "select new it.xtreamdev.gflbe.dto.MaxMinRangeNewspaperAttributesDTO(" +
            "min (n.za)," +
            "max (n.za)," +
            "min (n.purchasedContent)," +
            "max (n.purchasedContent)," +
            "min (n.leftContent)," +
            "max (n.leftContent)," +
            "min (n.costEach)," +
            "max (n.costEach)," +
            "min (n.costSell)," +
            "max (n.costSell)" +
            ") from Newspaper n")
    MaxMinRangeNewspaperAttributesDTO getMaxMinRangeNewspaperAttributes();


}
