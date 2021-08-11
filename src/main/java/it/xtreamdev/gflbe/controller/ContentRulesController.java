package it.xtreamdev.gflbe.controller;

import it.xtreamdev.gflbe.dto.SaveContentRulesDTO;
import it.xtreamdev.gflbe.model.ContentRules;
import it.xtreamdev.gflbe.repository.ContentRulesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/content-rules")
public class ContentRulesController {
    private final ContentRulesRepository contentRulesRepository;

    @GetMapping
    public ResponseEntity<List<ContentRules>> findAll() {
        return ResponseEntity.ok(
                contentRulesRepository.findAll()
        );
    }

    @PostMapping
    public ResponseEntity<ContentRules> save(@RequestBody SaveContentRulesDTO saveContentRulesDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                contentRulesRepository.save(ContentRules.builder()
                        .title(saveContentRulesDTO.getTitle())
                        .linkUrl(saveContentRulesDTO.getLinkUrl())
                        .linkText(saveContentRulesDTO.getLinkText())
                        .body(saveContentRulesDTO.getBody())
                        .maxCharacterBodyLength(saveContentRulesDTO.getMaxCharacterBodyLength())
                        .build())
        );
    }
}
