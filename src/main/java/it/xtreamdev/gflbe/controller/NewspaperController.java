package it.xtreamdev.gflbe.controller;

import it.xtreamdev.gflbe.dto.newspaper.SaveNewspaperDTO;
import it.xtreamdev.gflbe.model.Newspaper;
import it.xtreamdev.gflbe.service.NewspaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/newspaper")
public class NewspaperController {

    @Autowired
    private NewspaperService newspaperService;

    @GetMapping
    public ResponseEntity<Page<Newspaper>> find(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "ASC") String sortDirection,
            @RequestParam(value = "globalSearch", required = false) String globalSearch
    ) {
        return ResponseEntity.ok(this.newspaperService.findAll(globalSearch, PageRequest.of(page, pageSize, Sort.Direction.fromString(sortDirection), sortBy)));
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
