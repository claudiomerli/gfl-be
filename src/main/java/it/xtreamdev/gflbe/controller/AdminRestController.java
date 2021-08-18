package it.xtreamdev.gflbe.controller;

import it.xtreamdev.gflbe.dto.AdminStatusDTO;
import it.xtreamdev.gflbe.dto.SaveCustomerDTO;
import it.xtreamdev.gflbe.model.Customer;
import it.xtreamdev.gflbe.model.Newspaper;
import it.xtreamdev.gflbe.model.User;
import it.xtreamdev.gflbe.service.ContentService;
import it.xtreamdev.gflbe.service.CustomerService;
import it.xtreamdev.gflbe.service.NewspaperService;
import it.xtreamdev.gflbe.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("admin")
public class AdminRestController {

    @Autowired
    private UserService userService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ContentService contentService;

    @Autowired
    private NewspaperService newspaperService;

    @GetMapping("dashboard")
    public ResponseEntity<AdminStatusDTO> dashboard() {
        AdminStatusDTO adminStatusDTO = AdminStatusDTO.builder()
                .contentCount(this.contentService.count())
                .customersCount(this.customerService.count())
                .editorCount(this.userService.countEditors())
                .build();

        return ResponseEntity.ok(adminStatusDTO);
    }


//    @PostMapping("editors/create")
//    public ResponseEntity<Void> createEditor(@Valid @RequestBody User editor) {
//        this.userService.validateCreateEditor(editor);
//        this.userService.createEditor(editor);
//        return ResponseEntity.status(HttpStatus.CREATED).build();
//    }

//    @DeleteMapping("editors/{id}/delete")
//    public ResponseEntity<Void> deleteEditor(@PathVariable Integer id) {
//        this.userService.delete(id);
//        return ResponseEntity.ok().build();
//    }

//    @GetMapping("editors/{id}/edit")
//    public ResponseEntity<User> editEditor(@PathVariable Integer id) {
//        User result = this.userService.findById(id);
//        return ResponseEntity.ok(result);
//    }

//    @PutMapping("editors/{id}/edit")
//    public ResponseEntity<User> editEditor(@PathVariable Integer id, @RequestBody User user) {
//        return ResponseEntity.ok(this.userService.updateEditor(id, user));
//    }

    @GetMapping("customers")
    public ResponseEntity<Page<Customer>> findCustomers(
            @RequestParam(value = "sortBy", required = false, defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDirection", required = false, defaultValue = "ASC") String sortDirection
    ) {

        return ResponseEntity.ok(this.customerService.findAll(
                PageRequest.of(0, Integer.MAX_VALUE, Direction.fromString(sortDirection), sortBy.split(","))));
    }


    @PostMapping("customers/create")
    public ResponseEntity<Void> createCustomer(@Valid @RequestBody SaveCustomerDTO saveCustomerDTO) {

        this.customerService.save(saveCustomerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("customers/{id}/delete")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Integer id) {
        this.customerService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("customers/{id}/edit")
    public ResponseEntity<SaveCustomerDTO> editCustomer(@PathVariable Integer id) {
        return ResponseEntity.ok(this.customerService.loadSaveCustomerDtoFromCustomer(id));
    }

    @PutMapping("customers/{id}/edit")
    public ResponseEntity<SaveCustomerDTO> editCustomer(@PathVariable Integer id, @Valid @RequestBody SaveCustomerDTO saveCustomerDTO) {
        return ResponseEntity.ok(this.customerService.updateCustomer(id, saveCustomerDTO));
    }

    @GetMapping("newspapers")
    public ResponseEntity<List<Newspaper>> newspaperList() {
        return ResponseEntity.ok(this.newspaperService.findAll());
    }

    @PostMapping("newspapers")
    public ResponseEntity<Void> newspaperSave(@RequestBody Newspaper newspaper) {
        this.newspaperService.save(newspaper);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("newspapers/{id}/delete")
    public ResponseEntity<Void> newspaperList(@PathVariable Integer id) {
        this.newspaperService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
