package it.xtreamdev.gflbe.controller;

import it.xtreamdev.gflbe.dto.topic.SaveTopicDTO;
import it.xtreamdev.gflbe.dto.topic.TopicDTO;
import it.xtreamdev.gflbe.model.Topic;
import it.xtreamdev.gflbe.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("api/topic")
public class TopicController {

    @Autowired
    private TopicService topicService;

    @GetMapping
    public ResponseEntity<Page<Topic>> find(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "ASC") String sortDirection,
            @RequestParam(value = "globalSearch", required = false) String globalSearch
    ) {
        return ResponseEntity.ok(this.topicService.findAll(globalSearch, PageRequest.of(page, pageSize, Sort.Direction.fromString(sortDirection), sortBy)));
    }

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody SaveTopicDTO saveTopicDTO) {
        this.topicService.save(saveTopicDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("{id}")
    public ResponseEntity<Void> update(@PathVariable Integer id, @RequestBody SaveTopicDTO saveTopicDTO) {
        this.topicService.update(id, saveTopicDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        this.topicService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("{id}")
    public ResponseEntity<Topic> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(this.topicService.findById(id));
    }
    @GetMapping("/all")
    public ResponseEntity<Set<TopicDTO>> findAll() {
        return ResponseEntity.ok(this.topicService.findAll());
    }

}
