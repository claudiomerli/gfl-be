package it.xtreamdev.gflbe.service;

import it.xtreamdev.gflbe.dto.RequestSuggest;
import it.xtreamdev.gflbe.model.Suggest;
import it.xtreamdev.gflbe.repository.SuggestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SuggestService {

    @Autowired
    private SuggestRepository suggestRepository;

    public void saveKeyWord(RequestSuggest requestSuggest) {

        Optional.ofNullable(requestSuggest.getKeywords())
                .ifPresent(rs -> rs.forEach(suggestRequest -> suggestRepository.save(
                        Suggest.builder()
                                .keyword(suggestRequest)
                                .build()
                        ))
                );
    }
}
