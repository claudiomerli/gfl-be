package it.xtreamdev.gflbe.controller;

import it.xtreamdev.gflbe.dto.SaveCustomerRestDTO;
import it.xtreamdev.gflbe.exception.GLFException;
import it.xtreamdev.gflbe.model.Customer;
import it.xtreamdev.gflbe.repository.ContentRulesRepository;
import it.xtreamdev.gflbe.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/customer")
public class CustomerController {
    private final CustomerRepository customerRepository;
    private final ContentRulesRepository contentRulesRepository;

    @GetMapping
    public ResponseEntity<List<Customer>> findAll() {
        return ResponseEntity.ok(
                customerRepository.findAll()
        );
    }

    @PostMapping
    public ResponseEntity<Customer> save(@RequestBody SaveCustomerRestDTO saveCustomerRestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                customerRepository.save(Customer.builder()
                        .name(saveCustomerRestDTO.getName())
                        .contentRules(
                                contentRulesRepository.findById(saveCustomerRestDTO.getContentRulesId())
                                        .orElseThrow(() -> new GLFException("customer not found", HttpStatus.UNPROCESSABLE_ENTITY))
                        )
                        .build())
        );
    }
}
