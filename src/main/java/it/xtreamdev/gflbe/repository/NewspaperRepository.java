package it.xtreamdev.gflbe.repository;

import it.xtreamdev.gflbe.dto.FinanceDTO;
import it.xtreamdev.gflbe.model.Newspaper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface NewspaperRepository extends JpaRepository<Newspaper, Integer>, JpaSpecificationExecutor<Newspaper> {

    @Query(value = "select sum(valore_acquisti*(articoli_acquistati-numero_articoli)) purchasesValue, sum(valore_vendite*(articoli_acquistati-numero_articoli)) salesValue\n" +
            "from (\n" +
            "         select sum(n.cost_sell) valore_vendite,\n" +
            "                sum(cost_each) valore_acquisti,\n" +
            "                count(c.newspaper_id) numero_articoli,\n" +
            "                n.purchased_content articoli_acquistati\n" +
            "         from newspaper n left join content c on n.id = c.newspaper_id\n" +
            "         group by n.id, n.cost_sell, n.cost_each, c.newspaper_id) a", nativeQuery = true)
    FinanceDTO finance();
}
