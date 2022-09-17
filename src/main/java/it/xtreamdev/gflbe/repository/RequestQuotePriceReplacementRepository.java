package it.xtreamdev.gflbe.repository;

import it.xtreamdev.gflbe.model.RequestQuote;
import it.xtreamdev.gflbe.model.RequestQuotePriceReplacement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RequestQuotePriceReplacementRepository extends JpaRepository<RequestQuotePriceReplacement, Integer>, JpaSpecificationExecutor<RequestQuotePriceReplacement> {
    void deleteByRequestQuote(RequestQuote requestQuote);
}
