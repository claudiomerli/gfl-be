package it.xtreamdev.gflbe.controller;

import it.xtreamdev.gflbe.dto.AccessTokenDto;
import it.xtreamdev.gflbe.dto.SigninDTO;
import it.xtreamdev.gflbe.model.User;
import it.xtreamdev.gflbe.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("signin")
    public ResponseEntity<AccessTokenDto> login(@RequestBody SigninDTO signinDTO) {
        return ResponseEntity.ok(this.userService.signin(signinDTO));
    }

    @GetMapping("userInfo")
    public ResponseEntity<User> userInfo() {
        return ResponseEntity.ok(this.userService.userInfo());
    }

}
