package it.xtreamdev.gflbe.controller;

import it.xtreamdev.gflbe.service.AttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/attachment")
public class AttachmentController {

    @Autowired
    private AttachmentService attachmentService;

    @GetMapping("{id}")
    public byte[] downloadAttachment(@PathVariable Integer id) {
        return this.attachmentService.findById(id);
    }


}
