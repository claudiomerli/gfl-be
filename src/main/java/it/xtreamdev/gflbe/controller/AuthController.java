package it.xtreamdev.gflbe.controller;

import it.xtreamdev.gflbe.dto.auth.*;
import it.xtreamdev.gflbe.model.User;
import it.xtreamdev.gflbe.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("signin")
    public ResponseEntity<AccessTokenDTO> signin(@RequestBody SigninDTO signinDTO) {
        return ResponseEntity.ok(this.userService.signin(signinDTO));
    }

    @PostMapping("signin-customer")
    public ResponseEntity<AccessTokenDTO> signinCustomer(@RequestBody SigninDTO signinDTO) {
        return ResponseEntity.ok(this.userService.signinCustomer(signinDTO));
    }

    @PostMapping("signup")
    public void signup(@RequestBody @Valid SaveCustomerDTO saveCustomerDTO) {
        this.userService.createCustomer(saveCustomerDTO);
    }

    @GetMapping("check-username-availability/{username}")
    public ResponseEntity<Void> checkUsernameAvailability(@PathVariable String username) {
        this.userService.checkUsernameAvailability(username);
        return ResponseEntity.ok().build();
    }

    @GetMapping("check-email-availability/{email}")
    public ResponseEntity<Void> checkEmailAvailability(@PathVariable String email) {
        this.userService.checkEmailAvailability(email);
        return ResponseEntity.ok().build();
    }

    @GetMapping("confirm")
    public void confirm(@RequestParam String code) {
        this.userService.confirm(code);
    }

    @GetMapping("confirm-email")
    public void confirmEmail(@RequestParam String code) {
        this.userService.confirmEmail(code);
    }

    @PutMapping("request-reset-password")
    public void requestResetPassword(@RequestBody @Valid ResetPasswordEmailDTO resetPasswordEmailDTO) {
        this.userService.requestResetPassword(resetPasswordEmailDTO);
    }

    @PutMapping("reset-password")
    public void resetPassword(@RequestBody @Valid ResetPasswordDTO resetPasswordDTO) {
        this.userService.resetPassword(resetPasswordDTO);
    }

    //PRIVATES

    @GetMapping("userInfo")
    public ResponseEntity<User> userInfo() {
        return ResponseEntity.ok(this.userService.userInfo());
    }


    @PutMapping("update-customer-info/{id}")
    public void updateCustomerInfo(@PathVariable Integer id, @RequestBody @Valid SaveCustomerInfoDTO saveCustomerDTO) {
        this.userService.updateCustomerInfo(id, saveCustomerDTO);
    }

    @PutMapping("update-customer-email/{id}")
    public void updateCustomerEmail(@PathVariable Integer id, @RequestBody @Valid SaveCustomerEmailDTO saveCustomerDTO) {
        this.userService.updateCustomerEmail(id, saveCustomerDTO);
    }

    @PutMapping("update-customer-password/{id}")
    public void updateCustomerPassword(@PathVariable Integer id, @RequestBody @Valid SaveCustomerPasswordDTO saveCustomerDTO) {
        this.userService.updateCustomerPassword(id, saveCustomerDTO);
    }

    @PutMapping("update-customer-competitors/{id}")
    public void updateCustomerPassword(@PathVariable Integer id, @RequestBody @Valid SaveCustomerStatisticDomainDTO saveCustomerStatisticDomainDTO) {
        this.userService.updateCustomerCompetitors(id, saveCustomerStatisticDomainDTO);
    }

}
