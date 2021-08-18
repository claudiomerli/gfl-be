package it.xtreamdev.gflbe.controller;

import it.xtreamdev.gflbe.dto.newspaper.SaveNewspaperDTO;
import it.xtreamdev.gflbe.model.Newspaper;
import it.xtreamdev.gflbe.model.User;
import it.xtreamdev.gflbe.service.NewspaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/newspaper")
public class NewspaperController {

    @Autowired
    private NewspaperService newspaperService;

    @GetMapping
    public ResponseEntity<List<Newspaper>> find(@RequestParam(value = "globalSearch", required = false) String globalSearch) {
        return ResponseEntity.ok(this.newspaperService.findAll(globalSearch));
    }

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody SaveNewspaperDTO saveNewspaperDTO) {
        this.newspaperService.save(saveNewspaperDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("{id}")
    public ResponseEntity<Void> update(@PathVariable Integer id, @RequestBody SaveNewspaperDTO saveNewspaperDTO) {
        this.newspaperService.update(id, saveNewspaperDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        this.newspaperService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("{id}")
    public ResponseEntity<Newspaper> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(this.newspaperService.findById(id));
    }

}
