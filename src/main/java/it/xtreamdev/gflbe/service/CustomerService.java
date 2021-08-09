package it.xtreamdev.gflbe.service;

import it.xtreamdev.gflbe.dto.SaveCustomerDTO;
import it.xtreamdev.gflbe.model.ContentRules;
import it.xtreamdev.gflbe.model.Customer;
import it.xtreamdev.gflbe.repository.ContentRulesRepository;
import it.xtreamdev.gflbe.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

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

    public Customer save(SaveCustomerDTO saveCustomerDTO) {
        ContentRules contentRules = this.contentRulesRepository.save(saveCustomerDTO.getContentRules());
        Customer customer = saveCustomerDTO.getCustomer();
        customer.setContentRules(contentRules);
        return this.customerRepository.save(customer);
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

    public void delete(Integer id) {
        this.customerRepository.deleteById(id);
    }
}
