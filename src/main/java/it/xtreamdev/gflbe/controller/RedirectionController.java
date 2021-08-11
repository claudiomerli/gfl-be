package it.xtreamdev.gflbe.controller;

import it.xtreamdev.gflbe.model.User;
import it.xtreamdev.gflbe.model.enumerations.RoleName;
import it.xtreamdev.gflbe.security.UserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.HttpClientErrorException;

@Deprecated
@Controller
@RequestMapping("old")
public class RedirectionController {

    @GetMapping
    public String redirectionController(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        User user = userPrincipal.getUser();

        if (user.getRole() == RoleName.ADMIN) {
            return "redirect:/admin/dashboard";
        }

        if (user.getRole() == RoleName.EDITOR) {
            return "redirect:/editor/contents";
        }

        throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
    }

}
