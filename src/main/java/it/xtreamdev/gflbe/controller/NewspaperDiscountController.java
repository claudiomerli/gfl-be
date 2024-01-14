package it.xtreamdev.gflbe.controller;

import it.xtreamdev.gflbe.dto.newspaperdiscount.SaveNewspaperDiscount;
import it.xtreamdev.gflbe.dto.newspaperdiscount.SearchNewspaperDiscount;
import it.xtreamdev.gflbe.model.NewspaperDiscount;
import it.xtreamdev.gflbe.service.NewspaperDiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/newspaper-discounts")
public class NewspaperDiscountController {

    @Autowired
    private NewspaperDiscountService newspaperDiscountService;

    @PostMapping
    public ResponseEntity<NewspaperDiscount> save(@RequestBody SaveNewspaperDiscount saveNewspaperDiscount) {
        return ResponseEntity.ok(newspaperDiscountService.save(saveNewspaperDiscount));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NewspaperDiscount> update(@PathVariable Integer id, @RequestBody SaveNewspaperDiscount saveNewspaperDiscount) {
        return ResponseEntity.ok(newspaperDiscountService.update(id, saveNewspaperDiscount));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        newspaperDiscountService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<NewspaperDiscount> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(newspaperDiscountService.findById(id));
    }

    @GetMapping
    public ResponseEntity<Page<NewspaperDiscount>> find(SearchNewspaperDiscount searchNewspaperDiscount,
                                                        @RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
                                                        @RequestParam(name = "pageSize", defaultValue = "10", required = false) Integer pageSize,
                                                        @RequestParam(name = "sortBy", defaultValue = "id", required = false) String sortBy,
                                                        @RequestParam(name = "sortDirection", defaultValue = "DESC", required = false) String sortDirection
    ) {
        return ResponseEntity.ok(newspaperDiscountService.find(searchNewspaperDiscount, PageRequest
                .of(page, pageSize, Sort.Direction.fromString(sortDirection), sortBy.split(","))));
    }
}
