package it.xtreamdev.gflbe.controller;

import it.xtreamdev.gflbe.dto.AccessTokenDto;
import it.xtreamdev.gflbe.dto.SigninDTO;
import it.xtreamdev.gflbe.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("signin")
    public AccessTokenDto login(@RequestBody SigninDTO signinDTO) {
        return this.userService.signin(signinDTO);
    }
}
