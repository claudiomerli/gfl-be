package it.xtreamdev.gflbe.controller;

import it.xtreamdev.gflbe.dto.user.EditUserDTO;
import it.xtreamdev.gflbe.dto.user.SaveUserDTO;
import it.xtreamdev.gflbe.model.User;
import it.xtreamdev.gflbe.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<Page<User>> findUsers(
            @RequestParam(value = "sortBy", required = false, defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDirection", required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(value = "globalSearch", required = false) String gloabalSearch,
            @RequestParam(value = "role", required = false) String role
    ) {
        Page<User> result = this.userService
                .findUsers(gloabalSearch,role, PageRequest.of(page, pageSize, Sort.Direction.fromString(sortDirection), sortBy.split(",")));

        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Void> createUser(@RequestBody SaveUserDTO user) {
        this.userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        this.userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("{id}")
    public ResponseEntity<User> editUser(@PathVariable Integer id, @RequestBody EditUserDTO editUserDTO) {
        return ResponseEntity.ok(this.userService.updateUser(id, editUserDTO));
    }

    @GetMapping("{id}")
    public ResponseEntity<User> getUser(@PathVariable Integer id) {
        return ResponseEntity.ok(this.userService.findById(id));
    }


}
