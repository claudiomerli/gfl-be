package it.xtreamdev.gflbe.service;

import it.xtreamdev.gflbe.dto.SaveCustomerDTO;
import it.xtreamdev.gflbe.dto.SaveCustomerRestDTO;
import it.xtreamdev.gflbe.model.ContentRules;
import it.xtreamdev.gflbe.model.Customer;
import it.xtreamdev.gflbe.repository.ContentRulesRepository;
import it.xtreamdev.gflbe.repository.CustomerRepository;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ContentRulesRepository contentRulesRepository;

    public Long count() {
        return this.customerRepository.count();
    }

    public List<Customer> findAll() {
        return this.customerRepository.findAll();
    }

    public Page<Customer> findAll(Pageable pageable) {
        return this.customerRepository.findAll(pageable);
    }

    public List<Customer> search(String globalSearch) {
        return this.customerRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Optional.ofNullable(globalSearch)
                    .filter(Strings::isNotBlank)
                    .ifPresent(globalSearchValue ->
                            predicates.add(criteriaBuilder.like(criteriaBuilder.upper(root.get("name")), "%" + globalSearchValue.toUpperCase() + "%"))
                    );
            Optional.ofNullable(globalSearch)
                    .filter(Strings::isNotBlank)
                    .ifPresent(customerIdValue -> {
                        Join<Customer, ContentRules> customerJoin = root.join("contentRules");
                        predicates.add(criteriaBuilder.like(criteriaBuilder.upper(customerJoin.get("title")), "%" + globalSearch.toUpperCase() + "%"));
                    });

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        });
    }

    public Page<Customer> search(String globalSearch, PageRequest pageRequest) {
        return this.customerRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Optional.ofNullable(globalSearch)
                    .ifPresent(globalSearchValue ->
                            predicates.add(criteriaBuilder.like(criteriaBuilder.upper(root.get("name")), "%" + globalSearchValue.toUpperCase() + "%"))
                    );
            Optional.ofNullable(globalSearch)
                    .ifPresent(customerIdValue -> {
                        Join<Customer, ContentRules> customerJoin = root.join("content-rules");
                        predicates.add(criteriaBuilder.like(criteriaBuilder.upper(customerJoin.get("title")), "%" + globalSearch.toUpperCase() + "%"));
                    });

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        }, pageRequest);
    }

    public Customer save(SaveCustomerDTO saveCustomerDTO) {
        ContentRules contentRules = this.contentRulesRepository.save(saveCustomerDTO.getContentRules());
        Customer customer = saveCustomerDTO.getCustomer();
        customer.setContentRules(contentRules);
        return this.customerRepository.save(customer);
    }

    public Customer save(SaveCustomerRestDTO saveCustomerRestDTO) {
        return this.customerRepository.save(
                Customer.builder()
                        .name(saveCustomerRestDTO.getName())
                        .contentRules(
                                this.contentRulesRepository.findById(saveCustomerRestDTO.getContentRulesId())
                                        .orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "ContentRules not found"))
                        )
                        .build()
        );
    }

    public SaveCustomerDTO loadSaveCustomerDtoFromCustomer(Integer id) {
        Customer customer = this.customerRepository.findById(id).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Id not found"));
        return SaveCustomerDTO.builder().customer(customer).contentRules(customer.getContentRules()).build();
    }

    public Customer findById(Integer id) {
        return this.customerRepository.findById(id)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Customer not found"));
    }

    public SaveCustomerDTO updateCustomer(Integer id, SaveCustomerDTO saveCustomerDTO) {
        Customer customer = this.customerRepository.findById(id).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Id not found"));
        customer.setName(saveCustomerDTO.getCustomer().getName());

        ContentRules contentRules = customer.getContentRules();
        contentRules.setBody(saveCustomerDTO.getContentRules().getBody());
        contentRules.setLinkText(saveCustomerDTO.getContentRules().getLinkText());
        contentRules.setLinkUrl(saveCustomerDTO.getContentRules().getLinkUrl());
        contentRules.setTitle(saveCustomerDTO.getContentRules().getTitle());
        contentRules.setMaxCharacterBodyLength(saveCustomerDTO.getContentRules().getMaxCharacterBodyLength());

        this.contentRulesRepository.save(contentRules);
        this.customerRepository.save(customer);

        saveCustomerDTO.getContentRules().setId(contentRules.getId());
        saveCustomerDTO.getCustomer().setId(id);

        return saveCustomerDTO;
    }

    public Customer update(Integer id, SaveCustomerRestDTO saveCustomerRestDTO) {
        Customer customer = this.customerRepository.findById(id)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Customer not found"));
        customer.setName(saveCustomerRestDTO.getName());
        customer.setContentRules(
                this.contentRulesRepository.findById(saveCustomerRestDTO.getContentRulesId())
                        .orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "ContentRules not found"))
        );
        return this.customerRepository.save(customer);
    }

    public void delete(Integer id) {
        this.customerRepository.deleteById(id);
    }
}
