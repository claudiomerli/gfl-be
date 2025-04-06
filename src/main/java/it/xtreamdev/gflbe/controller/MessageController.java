package it.xtreamdev.gflbe.controller;

import it.xtreamdev.gflbe.dto.message.SaveMessageDTO;
import it.xtreamdev.gflbe.dto.message.SearchMessageDTO;
import it.xtreamdev.gflbe.model.Message;
import it.xtreamdev.gflbe.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping()
    public ResponseEntity<Message> saveMessage(@RequestBody SaveMessageDTO saveMessageDTO) {
        return ResponseEntity.ok(messageService.saveMessage(saveMessageDTO));
    }

    @GetMapping
    public ResponseEntity<Page<Message>> searchMessages(SearchMessageDTO searchMessageDTO, Pageable pageable) {
        return ResponseEntity.ok(messageService.find(searchMessageDTO, pageable));
    }

    @PutMapping("{id}")
    public ResponseEntity<Message> readMessage(@PathVariable Integer id) {
        return ResponseEntity.ok(messageService.read(id));
    }
}
