package it.xtreamdev.gestioneattivita.controller;

import it.xtreamdev.gestioneattivita.dto.AccessTokenDto;
import it.xtreamdev.gestioneattivita.dto.SigninDTO;
import it.xtreamdev.gestioneattivita.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("login")
    public AccessTokenDto login(@RequestBody SigninDTO signinDTO) {
        return this.userService.signin(signinDTO);
    }
}
