package it.xtreamdev.gflbe.controller;

import it.xtreamdev.gflbe.dto.RequestSuggest;
import it.xtreamdev.gflbe.service.SuggestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/suggests")
public class SuggestController {


    @Autowired
    private SuggestService suggestService;

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody RequestSuggest requestSuggest){

        suggestService.saveKeyWord(requestSuggest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
