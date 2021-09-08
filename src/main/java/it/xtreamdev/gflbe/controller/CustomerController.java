package it.xtreamdev.gflbe.controller;

import it.xtreamdev.gflbe.dto.SaveCustomerRestDTO;
import it.xtreamdev.gflbe.model.Customer;
import it.xtreamdev.gflbe.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/customer")
public class CustomerController {
    private final CustomerService customerService;

    @GetMapping
    public ResponseEntity<Page<Customer>> search(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "ASC") String sortDirection,
            @RequestParam(value = "globalSearch", required = false) String globalSearch
    ) {
        return ResponseEntity.ok(
                customerService.search(globalSearch, PageRequest.of(page, pageSize, Sort.Direction.fromString(sortDirection), sortBy))
        );
    }

    @GetMapping("{id}")
    public ResponseEntity<Customer> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(
                customerService.findById(id)
        );
    }

    @PostMapping
    public ResponseEntity<Customer> save(@RequestBody SaveCustomerRestDTO saveCustomerRestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                customerService.save(saveCustomerRestDTO)
        );
    }

    @PutMapping("{id}")
    public ResponseEntity<Customer> update(@PathVariable Integer id, @RequestBody SaveCustomerRestDTO saveCustomerRestDTO) {
        return ResponseEntity.ok(
                customerService.update(id, saveCustomerRestDTO)
        );
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        customerService.delete(id);
        return ResponseEntity.ok().build();
    }
}
