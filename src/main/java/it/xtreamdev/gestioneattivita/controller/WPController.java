package it.xtreamdev.gestioneattivita.controller;

import it.xtreamdev.gestioneattivita.dto.PublishArticleOnWordpressDTO;
import it.xtreamdev.gestioneattivita.service.WordpressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("wp")
public class WPController {

    private final WordpressService wordpressService;

    @GetMapping("test")
    public ResponseEntity<Void> testCreate() {
        wordpressService.publishArticle(
                PublishArticleOnWordpressDTO.builder()
                        .siteUrl("http://tucame.hopto.org:8080")
                        .username("test")
                        .password("crlh&g%uHGG*b6@3kl5bH)Zm")
                        .title("title")
                        .content("content")
                        .build()
        );
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
