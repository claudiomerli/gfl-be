package it.xtreamdev.gflbe.controller;

import it.xtreamdev.gflbe.dto.news.FindNewsDTO;
import it.xtreamdev.gflbe.dto.news.SaveNewsDTO;
import it.xtreamdev.gflbe.dto.purchasecontent.FindContentPurchaseDTO;
import it.xtreamdev.gflbe.dto.purchasecontent.SaveContentPurchaseDTO;
import it.xtreamdev.gflbe.model.ContentPurchase;
import it.xtreamdev.gflbe.model.News;
import it.xtreamdev.gflbe.service.ContentPurchaseService;
import it.xtreamdev.gflbe.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api/content-purchase")
@RestController
public class ContentPurchaseController {

    @Autowired
    private ContentPurchaseService contentPurchaseService;

    @GetMapping
    public Page<ContentPurchase> find(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "ASC") String sortDirection,
            FindContentPurchaseDTO contentPurchaseDTO
    ) {
        return this.contentPurchaseService.findContentPurchase(contentPurchaseDTO, PageRequest.of(page, pageSize, Sort.Direction.fromString(sortDirection), sortBy));
    }

    @GetMapping("{id}")
    private ContentPurchase findById(@PathVariable Integer id) {
        return this.contentPurchaseService.findById(id);
    }

    @PostMapping
    private ContentPurchase save(@RequestBody SaveContentPurchaseDTO saveContentPurchaseDTO) {
        return this.contentPurchaseService.save(saveContentPurchaseDTO);
    }

    @PutMapping("{id}")
    private ContentPurchase update(@PathVariable Integer id, @RequestBody SaveContentPurchaseDTO saveContentPurchaseDTO) {
        return this.contentPurchaseService.update(id, saveContentPurchaseDTO);
    }

    @DeleteMapping("{id}")
    private void delete(@PathVariable Integer id) {
        this.contentPurchaseService.delete(id);
    }

}
