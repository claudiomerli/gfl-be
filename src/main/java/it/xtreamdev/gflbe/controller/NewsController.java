package it.xtreamdev.gflbe.controller;

import it.xtreamdev.gflbe.dto.FindNewsDTO;
import it.xtreamdev.gflbe.dto.SaveNewsDTO;
import it.xtreamdev.gflbe.model.News;
import it.xtreamdev.gflbe.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api/news")
@RestController
public class NewsController {

    @Autowired
    private NewsService newsService;

    @GetMapping
    public Page<News> find(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "ASC") String sortDirection,
            FindNewsDTO findNewsDTO
    ) {
        return this.newsService.findNews(findNewsDTO, PageRequest.of(page, pageSize, Sort.Direction.fromString(sortDirection), sortBy));
    }

    @GetMapping("{id}")
    private News findById(@PathVariable Integer id) {
        return this.newsService.findById(id);
    }

    @PostMapping
    private News save(@RequestBody SaveNewsDTO saveNewsDTO) {
        return this.newsService.save(saveNewsDTO);
    }

    @PutMapping("{id}")
    private News update(@PathVariable Integer id, @RequestBody SaveNewsDTO saveNewsDTO) {
        return this.newsService.update(id, saveNewsDTO);
    }

    @DeleteMapping("{id}")
    private void delete(@PathVariable Integer id) {
        this.newsService.delete(id);
    }

}
