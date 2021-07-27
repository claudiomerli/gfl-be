package it.xtreamdev.gestioneattivita.service;

import it.xtreamdev.gestioneattivita.dto.PublishArticleOnWordpressDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class WordpressService {

    private final Validator validator;

    private final RestTemplate restTemplate;

    public void publishArticle(PublishArticleOnWordpressDTO publishArticleOnWordpressDTO) {
        Set<ConstraintViolation<PublishArticleOnWordpressDTO>> violations = validator.validate(publishArticleOnWordpressDTO);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(
                    violations.stream()
                            .map(ConstraintViolation::getMessage)
                            .collect(Collectors.joining(" | ")),
                    violations
            );
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(publishArticleOnWordpressDTO.getUsername(), publishArticleOnWordpressDTO.getPassword());

        Map<String, String> params = new HashMap<>();
        params.put("title", publishArticleOnWordpressDTO.getTitle());
        params.put("content", publishArticleOnWordpressDTO.getContent());
        params.put("status", publishArticleOnWordpressDTO.getStatus().getStatus());
        if (Objects.nonNull(publishArticleOnWordpressDTO.getCategories())) {
            params.put("categories", publishArticleOnWordpressDTO.getCategories());
        }
        if (Objects.nonNull(publishArticleOnWordpressDTO.getTags())) {
            params.put("tags", publishArticleOnWordpressDTO.getTags());
        }
        if (Objects.nonNull(publishArticleOnWordpressDTO.getDate())) {
            params.put("date", publishArticleOnWordpressDTO.getDate().format(DateTimeFormatter.ISO_DATE_TIME));
        }
        if (Objects.nonNull(publishArticleOnWordpressDTO.getExcerpt())) {
            params.put("excerpt", publishArticleOnWordpressDTO.getExcerpt());
        }

        ResponseEntity<String> response = restTemplate.postForEntity(
                publishArticleOnWordpressDTO.getSiteUrl() + "/wp-json/wp/v2/posts",
                new HttpEntity<>(params, headers),
                String.class
        );
        log.info("" + response.getStatusCode());
        log.info(response.getBody());
    }

}
