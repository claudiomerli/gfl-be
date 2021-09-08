package it.xtreamdev.gflbe.service;

import it.xtreamdev.gflbe.dto.SuggestsDTO;
import it.xtreamdev.gflbe.dto.RequestSuggest;
import it.xtreamdev.gflbe.model.Suggest;
import it.xtreamdev.gflbe.repository.SuggestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public List<SuggestsDTO> getAll() {
        return suggestRepository.findAll()
                .stream()
                .map(rs -> SuggestsDTO.builder()
                        .id(rs.getId())
                        .keyword(rs.getKeyword())
                        .suggests(rs.getSuggests())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteById(String idSuggest) {
        suggestRepository.deleteById(Integer.parseInt(idSuggest));
    }
}
