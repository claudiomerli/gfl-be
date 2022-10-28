package it.xtreamdev.gflbe.service;

import it.xtreamdev.gflbe.dto.SaveCustomerRestDTO;
import it.xtreamdev.gflbe.dto.user.SaveUserDTO;
import it.xtreamdev.gflbe.model.ContentRules;
import it.xtreamdev.gflbe.model.Customer;
import it.xtreamdev.gflbe.model.Project;
import it.xtreamdev.gflbe.model.User;
import it.xtreamdev.gflbe.model.enumerations.RoleName;
import it.xtreamdev.gflbe.repository.ContentRulesRepository;
import it.xtreamdev.gflbe.repository.CustomerRepository;
import it.xtreamdev.gflbe.repository.ProjectRepository;
import it.xtreamdev.gflbe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

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


    public Page<Customer> search(String globalSearch, PageRequest pageRequest) {
        return this.customerRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Optional.ofNullable(globalSearch)
                    .ifPresent(globalSearchValue ->
                            predicates.add(criteriaBuilder.like(criteriaBuilder.upper(root.get("name")), "%" + globalSearchValue.toUpperCase() + "%"))
                    );

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        }, pageRequest);
    }

    @Transactional
    public Customer save(SaveCustomerRestDTO saveCustomerRestDTO) {
        Customer customer = this.customerRepository.save(
                Customer.builder()
                        .name(saveCustomerRestDTO.getName())
                        .contentRules(
                                ContentRules
                                        .builder()
                                        .body(saveCustomerRestDTO.getContentRules().getBody())
                                        .linkText(saveCustomerRestDTO.getContentRules().getLinkText())
                                        .linkUrl(saveCustomerRestDTO.getContentRules().getLinkUrl())
                                        .maxCharacterBodyLength(saveCustomerRestDTO.getContentRules().getMaxCharacterBodyLength())
                                        .title(saveCustomerRestDTO.getContentRules().getTitle())
                                        .build()
                        )
                        .build()
        );


        SaveUserDTO saveUserDTO = new SaveUserDTO();
        saveUserDTO.setRole(RoleName.CUSTOMER);
        saveUserDTO.setUsername(saveCustomerRestDTO.getUsername());
        saveUserDTO.setFullname(saveCustomerRestDTO.getName());
        saveUserDTO.setEmail(saveCustomerRestDTO.getEmail());
        saveUserDTO.setMobilePhone(saveCustomerRestDTO.getMobile());
        saveUserDTO.setPassword(saveCustomerRestDTO.getPassword());

        User user = this.userService.createUser(saveUserDTO);
        user.setCustomer(customer);

        this.userRepository.save(user);
        
        return customer;
    }

    public Customer findById(Integer id) {
        return this.customerRepository.findById(id)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Customer not found"));
    }

//    public List<Project> findProjectByIdCustomer(Integer idCustomer) {
//        return projectRepository.findAllByCustomer_Id(idCustomer);
//    }

    public Customer update(Integer id, SaveCustomerRestDTO saveCustomerRestDTO) {
        Customer customer = this.customerRepository.findById(id)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Customer not found"));
        customer.setName(saveCustomerRestDTO.getName());
        customer.setContentRules(ContentRules
                .builder()
                .body(saveCustomerRestDTO.getContentRules().getBody())
                .linkText(saveCustomerRestDTO.getContentRules().getLinkText())
                .linkUrl(saveCustomerRestDTO.getContentRules().getLinkUrl())
                .maxCharacterBodyLength(saveCustomerRestDTO.getContentRules().getMaxCharacterBodyLength())
                .title(saveCustomerRestDTO.getContentRules().getTitle())
                .build()
        );
        return this.customerRepository.save(customer);
    }

    public void delete(Integer id) {
        this.customerRepository.deleteById(id);
    }
}
