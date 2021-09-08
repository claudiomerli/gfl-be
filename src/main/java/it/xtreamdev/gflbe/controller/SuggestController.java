package it.xtreamdev.gflbe.controller;

import it.xtreamdev.gflbe.dto.SuggestsDTO;
import it.xtreamdev.gflbe.dto.RequestSuggest;
import it.xtreamdev.gflbe.service.SuggestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/suggests")
public class SuggestController {


    @Autowired
    private SuggestService suggestService;

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody RequestSuggest requestSuggest) {

        suggestService.saveKeyWord(requestSuggest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<SuggestsDTO>> findAll() {
        return ResponseEntity.ok(suggestService.getAll());
    }

    @DeleteMapping(value = "/{idSuggest}")
    public ResponseEntity<Void> deleteById(@PathVariable(value = "idSuggest") String idSuggest) {
        suggestService.deleteById(idSuggest);
        return ResponseEntity.ok().build();
    }

}
