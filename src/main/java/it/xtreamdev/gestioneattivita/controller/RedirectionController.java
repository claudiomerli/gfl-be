package it.xtreamdev.gestioneattivita.controller;

import it.xtreamdev.gestioneattivita.model.User;
import it.xtreamdev.gestioneattivita.model.enumerations.RoleName;
import it.xtreamdev.gestioneattivita.security.UserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.HttpClientErrorException;

@Deprecated
@Controller
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
