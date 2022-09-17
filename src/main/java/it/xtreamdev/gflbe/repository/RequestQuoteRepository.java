package it.xtreamdev.gflbe.repository;

import it.xtreamdev.gflbe.model.RequestQuote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface RequestQuoteRepository extends JpaRepository<RequestQuote, Integer>, JpaSpecificationExecutor<RequestQuote> {
    List<RequestQuote> findByOrder_Id(Integer id);
}
